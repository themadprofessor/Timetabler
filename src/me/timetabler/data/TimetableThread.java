package me.timetabler.data;

import javafx.concurrent.Task;
import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.data.exceptions.DataExceptionHandler;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A background thread which timetables staff members into lessons and lessons into classrooms.
 */
public class TimetableThread extends Task<Void> {
    private DaoManager daoManager;

    public TimetableThread(DaoManager daoManager) {
        this.daoManager = daoManager;
    }

    /**
     * Gives each lesson a staff member and classroom on a per subject basis, taking into account the distances between
     * each lesson for each member of staff.
     * @return
     */
    @Override
    public Void call() {
        try {
            // get all subjects
            List<Subject> subjects = daoManager.getSubjectDao().getAll();
            List<Distance> distances = daoManager.getDistanceDao().getAll();

            subjects.forEach(subject -> {
                // for current subject find periods where multiple classes are taught during the same period for this subject
                //The id of the top list is the id of the period as it is static data
                List<Set<LessonPlan>> overloadedPeriod;
                overloadedPeriod = findOverloadedPeriods(subject, 31);

                List<LessonPlan> lessonPlans = null;
                List<Staff> staff = null;
                List<Classroom> classrooms = null;

                try {
                    // get all lessonPlans for this subject
                    lessonPlans = daoManager.getLessonPlanDao().getAllBySubject(subject);
                } catch (DataAccessException e) {
                    DataExceptionHandler.handleJavaFx(e, "lessonPlan", false);
                    return;
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, null, true);
                    return;
                }

                try {
                    // get all staff who teach the subject
                    staff = daoManager.getStaffDao().getAllBySubject(subject);
                } catch (DataAccessException e) {
                    DataExceptionHandler.handleJavaFx(e, "staff", false);
                    return;
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, null, true);
                    return;
                }

                try {
                    classrooms = daoManager.getClassroomDao().getAll();
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, "classrooms", false);
                    return;
                } catch (DataAccessException e) {
                    DataExceptionHandler.handleJavaFx(e, null, true);
                    return;
                }

                // put staff into lessonPlans for this subject, avoiding teacher being scheduled twice for same period.
                putStaffIntoLessonPlan(lessonPlans, staff, overloadedPeriod);

                //Get the data needed for the final part of timetabling
                List<Distance> subjectDistance = distances.parallelStream()
                        .filter(distance -> distance.startRoom.subject.equals(subject))
                        .collect(Collectors.toList());
                List<Classroom> subjectRooms = classrooms.parallelStream()
                        .filter(classroom -> classroom.subject.equals(subject))
                        .collect(Collectors.toList());
                //Put classrooms into lessonPlans for this subject.
                putLessonPlansIntoClassrooms(lessonPlans, subjectRooms, subjectDistance);
            });
        } catch (DataAccessException e) {
            DataExceptionHandler.handleJavaFx(e, "Subject", false);
        } catch (DataConnectionException e) {
            DataExceptionHandler.handleJavaFx(e, "Subject", true);
        }
        return null;
    }

    /**
     * Populates a list of lists with all the subjectSets of the given subject taught at any time. The inner list can
     * contain any of elements
     * @param subject The subject which the subjectSets will be taught
     */
    private List<Set<LessonPlan>> findOverloadedPeriods(Subject subject, int size) {
        List<Set<LessonPlan>> overloadedPeriod = new ArrayList<>(size);
        Collections.fill(overloadedPeriod, new HashSet<>());

        try {
            List<LessonPlan> lessons = daoManager.getLessonPlanDao().getAllBySubject(subject);

            // add each lesson to the overloadedPeriod list
            // multiple lessons that are taught during same period will highlight period overloading.
            lessons.forEach(lessonPlan -> overloadedPeriod.get(lessonPlan.period.id).add(lessonPlan));

        } catch (DataAccessException e) {
            DataExceptionHandler.handleJavaFx(e, "LessonPlan", false);
        } catch (DataConnectionException e) {
            DataExceptionHandler.handleJavaFx(e, "LessonPlan", true);
        }

        return overloadedPeriod;
    }

    /**
     * Puts all the given staff into the given lessonPlans, avoiding all given lessonPlans. Returns true if it
     * successfully put the staff into the lessonPlans.
     * @param lessonPlans The lessonPlans to have staff put in.
     * @param staffList The staff to put into the lessonPlans.
     * @param overloadedPeriods The lessonPlans which overlap.
     * @return True if successfully put the staff into the lessonPlans.
     */
    private boolean putStaffIntoLessonPlan(List<LessonPlan> lessonPlans, List<Staff> staffList, List<Set<LessonPlan>> overloadedPeriods) {
        boolean done = false;

        while (!done) {
            Set<SubjectSet> subjectSets = new HashSet<>();
            lessonPlans.forEach(lessonPlan -> subjectSets.add(lessonPlan.subjectSet));

            for (SubjectSet subjectSet : subjectSets) {
                List<LessonPlan> lessonsForSubjectSet = lessonPlans.stream()                //Turn list into an iterating stream
                        .filter(lessonPlan -> lessonPlan.subjectSet.id == subjectSet.id)    //Only add lessonsPlans who's subjectSet is the subjectSet currently being processed.
                        .collect(Collectors.toCollection(ArrayList::new));                  //Collect all which meet the filter into an ArrayList and return it

                /*List<LessonPlan> lessonsForSubjectSet = new ArrayList<>();                //This is the equivalent of the above
                for (LessonPlan lessonPlan : lessonPlans) {
                    if (lessonPlan.subjectSet.id == subjectSet.id) {
                        lessonsForSubjectSet.add(lessonPlan);
                    }
                }*/

                if (lessonsForSubjectSet.get(0).staff == null) { //Only check first as all will be set if the first is set
                    Set<SubjectSet> possibleConflicts = new HashSet<>(); //A set containing every subjectSet which happens at the same time as the current subjectSet
                    for (Set<LessonPlan> overloadedPeriod : overloadedPeriods) {
                        if (overloadedPeriod.stream().anyMatch(lessonPlan -> lessonPlan.subjectSet.id == subjectSet.id)) {
                            //Only a set rather than List(Set) as do not need to know when the conflict is,
                            overloadedPeriod.forEach(lessonPlan -> possibleConflicts.add(lessonPlan.subjectSet)); //only there is a conflict between the current subjectSet and the subjectSets
                            //in the set
                        }
                    }

                    for (Staff staff : staffList) {
                        if (staff.currentHoursPerWeek >= staff.hoursPerWeek) {
                            break;
                        }

                        Set<SubjectSet> subjectSetsForStaff = new HashSet<>(); //A set containing the subjectSets taught by the current staff
                        lessonsForSubjectSet.forEach(lessonPlan -> {
                            if (lessonPlan.staff.id == staff.id) {
                                subjectSetsForStaff.add(lessonPlan.subjectSet);
                            }
                        });

                        if (Collections.disjoint(possibleConflicts, subjectSetsForStaff)) { //disjoint returns true if the collections has no elements in common
                            staff.currentHoursPerWeek += lessonsForSubjectSet.size();
                            for (LessonPlan lessonPlan : lessonPlans) {
                                if (lessonPlan.subjectSet.id == subjectSet.id) {
                                    lessonPlan.staff = staff;
                                }
                            }
                        }
                    }
                }
            }

            done = lessonPlans.stream().allMatch(lessonPlan -> lessonPlan.staff != null); //Checks if every lessonPlan has a member of staff teaching it
        }

        /*while (!done) {
            for (LessonPlan lesson : lessonPlans) {
                if (lesson.staff == null) {

                    for (Staff staff : staffList) {
                        if (staff.currentHoursPerWeek < staff.hoursPerWeek) {
                            break;
                        }

                        boolean conflicts = false;

                        //Gets all lessons currently taught by this member of staff
                        List<LessonPlan> lessonsByStaff = lessonPlans
                                .stream()                                               //Turn list into an iterating stream
                                .filter(lessonPlan -> lessonPlan.staff.id == staff.id)  //Only add lessons who's staff member is the currently processed staff member
                                .collect(Collectors.toCollection(ArrayList::new));      //Collect all which meet the filter into an ArrayList and return it

                        //Gets all the periods which contain the lessons currently being processed
                        List<List<LessonPlan>> containsThisLesson = overloadedPeriods
                                .stream()                                           //Turn the list into an iterating stream
                                .filter((period) -> period.contains(lesson))        //At each iteration, check if the current lesson is within the list
                                .collect(Collectors.toCollection(ArrayList::new));  //Collect all which meet the filter into an ArrayList and return it

                        for (List<LessonPlan> period : containsThisLesson) {
                            Optional<LessonPlan> conflict = lessonsByStaff
                                    .stream()                   //Turn list into iterating stream
                                    .filter(period::contains)   //At each iteration, test if the lesson is contained in the period overload list
                                    .findFirst();               //Only check if one exists, and return its optional
                            if (conflict.isPresent()) {         //If one was found, set conflicts to true
                                conflicts = true;
                            }
                        }

                        if (!conflicts) {
                            int count = 0;

                            for (LessonPlan lessonPlan : lessonPlans) {
                                if (lessonPlan.id == lesson.id) {
                                    lessonPlan.staff = staff;
                                    count++;
                                }
                            }

                            staff.currentHoursPerWeek += count;
                        }
                    }
                }
            }
        }*/

        return true;
    }

    private List<LessonPlan> putLessonPlansIntoClassrooms(List<LessonPlan> lessonPlans, List<Classroom> classrooms, List<Distance> distances) {
        //TODO: Iter through each subject, iter through each day, allocate period 1 randomly, iter through each period,
        //TODO: check if teacher teaching in period, if is find closest available room, if not move on
        List<LessonPlan> timetabledLessons = new ArrayList<>();

        //Create a week of periods for this subject
        List<List<LessonPlan>> week = new ArrayList<>(5);
        Collections.fill(week, new ArrayList<>());
        lessonPlans.stream()
                .forEach(lessonPlan -> week.get(lessonPlan.period.day.id - 1).add(lessonPlan));

        List<Period> periods;
        //Iterate through each day
        for (int dayNo = 0; dayNo < week.size(); dayNo++) {
            //Get the periods of this day
            try {
                periods = daoManager.getPeriodDao().getAllByDay(daoManager.getDayDao().getById(dayNo + 1).get());
                periods.sort((o1, o2) -> o1.startTime.getHour() - o2.startTime.getHour());
            } catch (DataAccessException e) {
                DataExceptionHandler.handleJavaFx(e, "period", false);
                return timetabledLessons;
            } catch (DataConnectionException e) {
                DataExceptionHandler.handleJavaFx(e, null, true);
                return timetabledLessons;
            }

            //Get this days lessons and randomly allocate the first period
            List<LessonPlan> day = week.get(dayNo);
            List<LessonPlan> firstPeriod = new ArrayList<>();
            day.forEach(lessonPlan -> {
                if (lessonPlan.period.startTime.equals(LocalTime.of(9, 10))) {
                    firstPeriod.add(lessonPlan);
                }
            });
            classrooms.stream().forEach(classroom -> {
                for (LessonPlan lessonPlan : firstPeriod) {
                    if (lessonPlan.classroom == null) {
                        lessonPlan.classroom = classroom;
                        break;
                    }
                }
            });
            //Store the first period in the timetabled list
            timetabledLessons.addAll(firstPeriod);

            List<LessonPlan> previousPeriod = new ArrayList<>(firstPeriod);
            //Iterate through the other periods
            for (int periodNo = 1; periodNo < day.size(); periodNo++) {
                List<LessonPlan> period = new ArrayList<>();
                for (LessonPlan lessonPlan : day) {
                    if (lessonPlan.period.equals(periods.get(periodNo))) {
                        period.add(lessonPlan);
                    }
                }

                //Timetable lessons where the staff member had a lesson previously
                for (LessonPlan lessonPlan : period) {
                    Staff staff = lessonPlan.staff;
                    Optional<LessonPlan> previousLesson = previousPeriod.stream()
                            .filter(lesson -> lesson.staff.equals(staff))
                            .findFirst();

                    if (previousLesson.isPresent()) {
                        Optional<LessonPlan> planOptional = period.stream()
                                .filter(plan -> plan.classroom.equals(previousLesson.get().classroom))
                                .findFirst();

                        if (planOptional.isPresent()) {
                            List<Distance> availableRooms = distances.stream()
                                    .filter(distance ->
                                            distance.startRoom.equals(planOptional.get().classroom)
                                                    || distance.endRoom.equals(planOptional.get().classroom))
                                    .collect(Collectors.toList());
                            availableRooms.removeIf(distance -> {
                                boolean remove = false;
                                for (LessonPlan plan : period) {
                                    if (plan.classroom.equals(distance.startRoom) || plan.classroom.equals(distance.endRoom)) {
                                        remove = true;
                                    }
                                }

                                return remove;
                            });

                            availableRooms.sort((o1, o2) -> o1.distance - o2.distance);
                            Distance nextTrip = availableRooms.get(0);

                            if (nextTrip.startRoom.equals(planOptional.get().classroom)) {
                                lessonPlan.classroom = nextTrip.endRoom;
                            } else if (nextTrip.endRoom.equals(planOptional.get().classroom)) {
                                lessonPlan.classroom = nextTrip.startRoom;
                            } else {
                                assert false : "The new classroom should be one from the shortest distance!";
                            }
                        } else {
                            lessonPlan.classroom = previousLesson.get().classroom;
                        }
                    }
                }

                //Timetable the remaining lessons randomly
                for (LessonPlan lessonPlan : period) {
                    if (lessonPlan.classroom != null) {
                        List<Classroom> availableRooms = new ArrayList<>();
                        availableRooms.addAll(classrooms);

                        availableRooms.removeIf(classroom -> {
                            boolean remove = false;
                            for (LessonPlan plan : period) {
                                if (classroom.equals(plan.classroom)) {
                                    remove = true;
                                }
                            }
                            return remove;
                        });
                        lessonPlan.classroom = availableRooms.get(0);
                    }
                }

                timetabledLessons.addAll(period);
                previousPeriod = period;
            }
        }
        return timetabledLessons;
    }


}
