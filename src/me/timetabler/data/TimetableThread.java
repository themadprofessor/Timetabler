package me.timetabler.data;

import javafx.concurrent.Task;
import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.data.exceptions.DataExceptionHandler;
import me.timetabler.data.exceptions.DataUpdateException;
import me.util.LambdaBreakException;
import me.util.Log;

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
     * @return Nothing.
     */
    @Override
    public Void call() {
        // get all subjects
        List<Subject> subjects;
        List<Distance> distances;
        StringBuilder failedLog = new StringBuilder();

        try {
            subjects = daoManager.getSubjectDao().getAll();
        } catch (DataAccessException e) {
            updateMessage("Failed to access subject data due to the error [" + e + "]\n" +
                    "Please restart the timetabler and send the logs to your system administrator if this occurs again.");
            return null;
        } catch (DataConnectionException e) {
            DataExceptionHandler.handleJavaFx(e, null, true);
            return null;
        }
        try {
            distances = daoManager.getDistanceDao().getAll();
        } catch (DataAccessException e) {
            updateMessage("Failed to access distance data due to the error [" + e + "]\n" +
                    "Please restart the timetabler and send the logs to your system administrator if this occurs again.");
            return null;
        } catch (DataConnectionException e) {
            DataExceptionHandler.handleJavaFx(e, null, true);
            return null;
        }

        try {
            subjects.forEach(subject -> {
                updateMessage("Timetabling [" + subject.name + ']');
                // for current subject find periods where multiple classes are taught during the same period for this subject
                //The id of the top list is the id of the period as it is static data
                List<Set<LessonPlan>> overloadedPeriod;
                overloadedPeriod = findOverloadedPeriods(subject, 31);
                Log.verbose("Found [" + overloadedPeriod.size() + "] overloaded periods");

                List<LessonPlan> lessonPlans = null;
                List<Staff> staff = null;
                List<Classroom> classrooms = null;

                try {
                    // get all lessonPlans for this subject
                    lessonPlans = daoManager.getLessonPlanDao().getAllBySubject(subject);
                } catch (DataAccessException e) {
                    failedLog.append("Could not access the lesson data!\nThe error was [")
                            .append(e)
                            .append("]\nPlease restart the timetabler and send the logs to your system administrator")
                            .append(" if this occurs again.");
                    Log.error(e);
                    throw new LambdaBreakException();
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, null, true);
                    throw new LambdaBreakException();
                }

                try {
                    // get all staff who teach the subject
                    staff = daoManager.getStaffDao().getAllBySubject(subject);
                } catch (DataAccessException e) {
                    failedLog.append("Could not access the staff data!\nThe error was [")
                            .append(e)
                            .append("]\nPlease restart the timetabler and send the logs to your system administrator")
                            .append(" if this occurs again.");
                    Log.error(e);
                    throw new LambdaBreakException();
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, null, true);
                    throw new LambdaBreakException();
                }

                try {
                    classrooms = daoManager.getClassroomDao().getAll();
                } catch (DataConnectionException e) {
                    failedLog.append("Could not access the classroom data!\nThe error was [")
                            .append(e)
                            .append("]\nPlease restart the timetabler and send the logs to your system administrator")
                            .append(" if this occurs again.");
                    Log.error(e);
                    throw new LambdaBreakException();
                } catch (DataAccessException e) {
                    DataExceptionHandler.handleJavaFx(e, null, true);
                    throw new LambdaBreakException();
                }

                Log.verbose("Found [" + classrooms.size() + "] classrooms and [" + lessonPlans.size() + "] lessons and [" + staff.size() + "] staff");

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
                //putLessonPlansIntoClassrooms(lessonPlans, subjectRooms, subjectDistance);
                lessonPlans.forEach(lessonPlan -> {
                    try {
                        daoManager.getLessonPlanDao().update(lessonPlan);
                    } catch (DataAccessException | DataUpdateException e) {
                        failedLog.append("Could not update the lesson data!\nThe error was [")
                                .append(e)
                                .append("]\nPlease restart the timetabler and send the logs to your system administrator")
                                .append(" if this occurs again.");
                        Log.error(e);
                        throw new LambdaBreakException();
                    } catch (DataConnectionException e) {
                        DataExceptionHandler.handleJavaFx(e, null, true);
                        throw new LambdaBreakException();
                    }
                });
            });
        } catch (LambdaBreakException e) {
            if (failedLog.length() > 0) {
                updateMessage(failedLog.toString());
            }
            return null;
        }

        updateMessage("Done");
        return null;
    }

    /**
     * Populates a list of lists with all the subjectSets of the given subject taught at any time. The inner set can
     * contain any of elements
     * @param subject The subject which the subjectSets will be taught
     */
    private List<Set<LessonPlan>> findOverloadedPeriods(Subject subject, int size) {
        List<Set<LessonPlan>> overloadedPeriod = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            overloadedPeriod.add(new HashSet<>());
        }

        try {
            List<LessonPlan> lessons = daoManager.getLessonPlanDao().getAllBySubject(subject);

            // add each lesson to the overloadedPeriod list
            // multiple lessons that are taught during same period will highlight period overloading.
            lessons.forEach(lessonPlan -> {
                overloadedPeriod.get(lessonPlan.period.id).add(lessonPlan);
            });

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

                if (lessonsForSubjectSet.get(0).staff.id == -1) { //Only check first as all will be set if the first is set
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
                            if (lessonPlan.staff.id != -1 && lessonPlan.id != staff.id) {
                                subjectSetsForStaff.add(lessonPlan.subjectSet);
                            }
                        });

                        if (Collections.disjoint(possibleConflicts, subjectSetsForStaff)) { //disjoint returns true if the collections has no elements in common
                            staff.currentHoursPerWeek += lessonsForSubjectSet.size();
                            for (LessonPlan lessonPlan : lessonPlans) {
                                if (lessonPlan.subjectSet.id == subjectSet.id) {
                                    LessonPlan lesson = new LessonPlan(lessonPlan.id, staff, lessonPlan.classroom, lessonPlan.period, lessonPlan.subjectSet);
                                    lessonPlans.set(lessonPlans.indexOf(lessonPlan), lesson);
                                }
                            }
                        }
                    }
                }
            }

            done = lessonPlans.stream().allMatch(lessonPlan -> lessonPlan.staff != null); //Checks if every lessonPlan has a member of staff teaching it
        }

        return true;
    }

    /**
     * Puts the given lesson plans into classrooms based on the distance the member fo staff will have to travel between
     * each lesson.
     * @param lessonPlans The lessonPlans to be put into classrooms.
     * @param classrooms The classrooms to put lessons plans into.
     * @param distances The distances between the classrooms.
     * @return The list of lessonPlans after they have been put into classrooms.
     */
    private boolean putLessonPlansIntoClassrooms(List<LessonPlan> lessonPlans, List<Classroom> classrooms, List<Distance> distances) {

        //Create a week of periods for this subject
        List<List<LessonPlan>> week = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            week.add(new ArrayList<>());
        }
        lessonPlans.stream()
                .forEach(lessonPlan -> week.get(lessonPlan.period.day.id - 1).add(lessonPlan));

        List<Period> periods;
        //Iterate through each day
        for (int dayNo = 0; dayNo < week.size(); dayNo++) {
            //Get the periods of this day
            try {
                periods = daoManager.getPeriodDao()
                        .getAllByDay(daoManager.getDayDao().getById(dayNo + 1)
                        .orElseThrow(() -> new IllegalStateException("Failed to find periods in a day")));
                //The above exception should never be thrown
                periods.sort((o1, o2) -> o1.startTime.getHour() - o2.startTime.getHour());
            } catch (DataAccessException e) {
                DataExceptionHandler.handleJavaFx(e, "period", false);
                return false;
            } catch (DataConnectionException e) {
                DataExceptionHandler.handleJavaFx(e, null, true);
                return false;
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
                    if (lessonPlan.classroom.id == -1) {
                        lessonPlan.classroom = classroom;
                        break;
                    }
                }
            });
            //Store the first period in the timetabled list
            firstPeriod.forEach(lessonPlan -> {
                int index = lessonPlans.indexOf(lessonPlan);
                lessonPlans.set(index, lessonPlan);
            });

            List<LessonPlan> previousPeriod = new ArrayList<>(firstPeriod);
            //Iterate through the other periods
            for (int periodNo = 1; periodNo < 5; periodNo++) {
                List<LessonPlan> period = new ArrayList<>();
                for (LessonPlan lessonPlan : day) {
                    if (lessonPlan.period.equals(periods.get(periodNo-1))) {
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

                previousPeriod = period;
            }
        }
        return true;
    }
}
