package me.timetabler.data.mariadb;

import me.timetabler.data.*;
import me.timetabler.data.dao.LessonPlanDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.timetabler.data.sql.JoinClause;
import me.timetabler.data.sql.JoinType;
import me.timetabler.data.sql.SqlBuilder;
import me.timetabler.data.sql.StatementType;
import me.util.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stuart on 14/03/16.
 */
public class MariaLessonPlanDao implements LessonPlanDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectAllStaff;
    private PreparedStatement selectAllClassroom;
    private PreparedStatement selectAllPeriod;
    private PreparedStatement selectAllSubjectSet;
    private PreparedStatement selectAllClass;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    public MariaLessonPlanDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<LessonPlan> getAll() throws DataAccessException {
        ArrayList<LessonPlan> lessonPlans = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("lessonPlan.id", "class.id", "class.className", "subject.id", "subject.subjectName",
                                "staff.id", "staff.staffName", "classroom.id", "classroom.roomName",
                                "building.id", "building.buildingName", "period.id", "dayOfWeek.id",
                                "dayOfWeek.dayOfWeek", "period.startTime", "period.endTime", "subjectSet.id",
                                "learningSet.id", "learningSet.setName", "schoolYear.id", "schoolYear.schoolYearName",
                                "subjectSet.hoursPerWeek")
                        .addJoinClause(new JoinClause(JoinType.INNER, "class", "lessonPlan.classId=class.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "class.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "staff", "lessonPlan.staffId=staff.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom", "lessonPlan.classroomId=classroom.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "period", "lessonPlan.periodId=period.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subjectSet", "lessonPlan.subjectSetId=subjectSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.schoolYearId=schoolYear.id"));
                selectAll = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Subject subject = new Subject(set.getInt(4), set.getString(5));
                SchoolClass schoolClass = new SchoolClass(set.getInt(2), set.getString(3), subject);
                Staff staff = new Staff(set.getInt(6), set.getString(7), subject);
                Classroom classroom = new Classroom(set.getInt(8), set.getString(9),
                        new Building(set.getInt(10), set.getString(11)), subject);
                Period period = new Period(set.getInt(12),
                        new Day(set.getInt(13), set.getString(14)), set.getTime(15).toLocalTime(), set.getTime(16).toLocalTime());
                SubjectSet subjectSet = new SubjectSet(set.getInt(17), subject, new LearningSet(set.getInt(18), set.getString(19)),
                        new SchoolYear(set.getInt(20), set.getString(21)), set.getInt(22));

                LessonPlan lessonPlan = new LessonPlan(set.getInt(1), schoolClass, staff, classroom, period, subjectSet);
                lessonPlans.add(lessonPlan);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return lessonPlans;
    }

    @Override
    public List<LessonPlan> getAllByStaff(Staff staff) throws DataAccessException {
        ArrayList<LessonPlan> lessonPlans = new ArrayList<>();

        try {
            if (selectAllStaff == null || selectAllStaff.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("lessonPlan.id", "class.id", "class.className", "subject.id", "subject.subjectName",
                                "classroom.id", "classroom.roomName", "building.id", "building.buildingName", "period.id",
                                "dayOfWeek.id", "dayOfWeek.dayOfWeek", "period.startTime", "period.endTime", "subjectSet.id",
                                "learningSet.id", "learningSet.setName", "schoolYear.id", "schoolYear.schoolYearName",
                                "subjectSet.hoursPerWeek")
                        .addJoinClause(new JoinClause(JoinType.INNER, "class", "lessonPlan.classId=class.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "class.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom", "lessonPlan.classroomId=classroom.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "period", "lessonPlan.periodId=period.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subjectSet", "lessonPlan.subjectSetId=subjectSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.schoolYearId=schoolYear.id"));
                selectAllStaff = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAllStaff.executeQuery();
            while (set.next()) {
                Subject subject = new Subject(set.getInt(4), set.getString(5));
                SchoolClass schoolClass = new SchoolClass(set.getInt(2), set.getString(3), subject);
                Classroom classroom = new Classroom(set.getInt(6), set.getString(7),
                        new Building(set.getInt(8), set.getString(9)), subject);
                Period period = new Period(set.getInt(10),
                        new Day(set.getInt(11), set.getString(12)), set.getTime(13).toLocalTime(), set.getTime(14).toLocalTime());
                SubjectSet subjectSet = new SubjectSet(set.getInt(15), subject, new LearningSet(set.getInt(16), set.getString(17)),
                        new SchoolYear(set.getInt(18), set.getString(19)), set.getInt(20));

                LessonPlan lessonPlan = new LessonPlan(set.getInt(1), schoolClass, staff, classroom, period, subjectSet);
                lessonPlans.add(lessonPlan);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return lessonPlans;
    }

    @Override
    public List<LessonPlan> getAllByClassroom(Classroom classroom) throws DataAccessException {
        ArrayList<LessonPlan> lessonPlans = new ArrayList<>();

        try {
            if (selectAllClassroom == null || selectAllClassroom.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("lessonPlan.id", "class.id", "class.className", "subject.id", "subject.subjectName",
                                "staff.id", "staff.staffName", "period.id", "dayOfWeek.id",
                                "dayOfWeek.dayOfWeek", "period.startTime", "period.endTime", "subjectSet.id",
                                "learningSet.id", "learningSet.setName", "schoolYear.id", "schoolYear.schoolYearName",
                                "subjectSet.hoursPerWeek")
                        .addJoinClause(new JoinClause(JoinType.INNER, "class", "lessonPlan.classId=class.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "class.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "staff", "lessonPlan.staffId=staff.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "period", "lessonPlan.periodId=period.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subjectSet", "lessonPlan.subjectSetId=subjectSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.schoolYearId=schoolYear.id"));
                selectAllClassroom = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAllClassroom.executeQuery();
            while (set.next()) {
                Subject subject = new Subject(set.getInt(4), set.getString(5));
                SchoolClass schoolClass = new SchoolClass(set.getInt(2), set.getString(3), subject);
                Staff staff = new Staff(set.getInt(6), set.getString(7), subject);
                Period period = new Period(set.getInt(8),
                        new Day(set.getInt(9), set.getString(10)), set.getTime(11).toLocalTime(), set.getTime(12).toLocalTime());
                SubjectSet subjectSet = new SubjectSet(set.getInt(13), subject, new LearningSet(set.getInt(14), set.getString(15)),
                        new SchoolYear(set.getInt(16), set.getString(17)), set.getInt(18));

                LessonPlan lessonPlan = new LessonPlan(set.getInt(1), schoolClass, staff, classroom, period, subjectSet);
                lessonPlans.add(lessonPlan);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return lessonPlans;
    }

    @Override
    public List<LessonPlan> getAllByPeriod(Period period) throws DataAccessException {
        ArrayList<LessonPlan> lessonPlans = new ArrayList<>();

        try {
            if (selectAllPeriod == null || selectAllPeriod.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("lessonPlan.id", "class.id", "class.className", "subject.id", "subject.subjectName",
                                "staff.id", "staff.staffName", "classroom.id", "classroom.roomName",
                                "building.id", "building.buildingName", "subjectSet.id", "learningSet.id",
                                "learningSet.setName", "schoolYear.id", "schoolYear.schoolYearName",
                                "subjectSet.hoursPerWeek")
                        .addJoinClause(new JoinClause(JoinType.INNER, "class", "lessonPlan.classId=class.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "class.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "staff", "lessonPlan.staffId=staff.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom", "lessonPlan.classroomId=classroom.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subjectSet", "lessonPlan.subjectSetId=subjectSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.schoolYearId=schoolYear.id"));
                selectAllPeriod = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAllPeriod.executeQuery();
            while (set.next()) {
                Subject subject = new Subject(set.getInt(4), set.getString(5));
                SchoolClass schoolClass = new SchoolClass(set.getInt(2), set.getString(3), subject);
                Staff staff = new Staff(set.getInt(6), set.getString(7), subject);
                Classroom classroom = new Classroom(set.getInt(8), set.getString(9),
                        new Building(set.getInt(10), set.getString(11)), subject);
                SubjectSet subjectSet = new SubjectSet(set.getInt(12), subject, new LearningSet(set.getInt(13), set.getString(14)),
                        new SchoolYear(set.getInt(15), set.getString(16)), set.getInt(17));

                LessonPlan lessonPlan = new LessonPlan(set.getInt(1), schoolClass, staff, classroom, period, subjectSet);
                lessonPlans.add(lessonPlan);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return lessonPlans;
    }

    @Override
    public List<LessonPlan> getAllBySubjectSet(SubjectSet subjectSet) throws DataAccessException {
        ArrayList<LessonPlan> lessonPlans = new ArrayList<>();

        try {
            if (selectAllSubjectSet == null || selectAllSubjectSet.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("lessonPlan.id", "class.id", "class.className", "subject.id", "subject.subjectName",
                                "staff.id", "staff.staffName", "classroom.id", "classroom.roomName",
                                "building.id", "building.buildingName", "period.id", "dayOfWeek.id",
                                "dayOfWeek.dayOfWeek", "period.startTime", "period.endTime")
                        .addJoinClause(new JoinClause(JoinType.INNER, "class", "lessonPlan.classId=class.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "class.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "staff", "lessonPlan.staffId=staff.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom", "lessonPlan.classroomId=classroom.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "period", "lessonPlan.periodId=period.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"));
                selectAllSubjectSet = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAllSubjectSet.executeQuery();
            while (set.next()) {
                Subject subject = new Subject(set.getInt(4), set.getString(5));
                SchoolClass schoolClass = new SchoolClass(set.getInt(2), set.getString(3), subject);
                Staff staff = new Staff(set.getInt(6), set.getString(7), subject);
                Classroom classroom = new Classroom(set.getInt(8), set.getString(9),
                        new Building(set.getInt(10), set.getString(11)), subject);
                Period period = new Period(set.getInt(12),
                        new Day(set.getInt(13), set.getString(14)), set.getTime(15).toLocalTime(), set.getTime(16).toLocalTime());

                LessonPlan lessonPlan = new LessonPlan(set.getInt(1), schoolClass, staff, classroom, period, subjectSet);
                lessonPlans.add(lessonPlan);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return lessonPlans;
    }

    @Override
    public List<LessonPlan> getAllByClass(SchoolClass schoolClass) throws DataAccessException {
        ArrayList<LessonPlan> lessonPlans = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("lessonPlan.id", "subject.id", "subject.subjectName",
                                "staff.id", "staff.staffName", "classroom.id", "classroom.roomName",
                                "building.id", "building.buildingName", "period.id", "dayOfWeek.id",
                                "dayOfWeek.dayOfWeek", "period.startTime", "period.endTime", "subjectSet.id",
                                "learningSet.id", "learningSet.setName", "schoolYear.id", "schoolYear.schoolYearName",
                                "subjectSet.hoursPerWeek")
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "class.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "staff", "lessonPlan.staffId=staff.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom", "lessonPlan.classroomId=classroom.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "period", "lessonPlan.periodId=period.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subjectSet", "lessonPlan.subjectSetId=subjectSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.schoolYearId=schoolYear.id"));
                selectAll = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Subject subject = new Subject(set.getInt(2), set.getString(3));
                Staff staff = new Staff(set.getInt(4), set.getString(5), subject);
                Classroom classroom = new Classroom(set.getInt(6), set.getString(7),
                        new Building(set.getInt(8), set.getString(9)), subject);
                Period period = new Period(set.getInt(10),
                        new Day(set.getInt(11), set.getString(12)), set.getTime(13).toLocalTime(), set.getTime(14).toLocalTime());
                SubjectSet subjectSet = new SubjectSet(set.getInt(15), subject, new LearningSet(set.getInt(16), set.getString(17)),
                        new SchoolYear(set.getInt(18), set.getString(19)), set.getInt(20));

                LessonPlan lessonPlan = new LessonPlan(set.getInt(1), schoolClass, staff, classroom, period, subjectSet);
                lessonPlans.add(lessonPlan);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return lessonPlans;
    }

    @Override
    public int insert(LessonPlan lessonPlan) throws DataUpdateException, DataAccessException {
        int id = -1;

        try {
            if (insert == null || insert.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.INSERT)
                        .addColumns("classId", "staffId", "classroomId", "periodId", "subjectSetId")
                        .addValues("?", "?", "?", "?", "?");
                insert = connection.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
            }

            insert.setInt(1, lessonPlan.schoolClass.id);
            insert.setInt(2, lessonPlan.staff.id);
            insert.setInt(3, lessonPlan.classroom.id);
            insert.setInt(4, lessonPlan.period.id);
            insert.setInt(5, lessonPlan.subjectSet.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!-");
            throw new DataAccessException(e);
        }

        try {
            insert.executeUpdate();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        try {
            ResultSet set = insert.getGeneratedKeys();
            set.next();
            id = set.getInt(1);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return id;
    }

    @Override
    public boolean update(LessonPlan lessonPlan) throws DataUpdateException, DataAccessException {
        boolean success = false;

        try {
            if (update == null || update.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.UPDATE)
                        .addSetClauses("classId=?", "staffId=?", "classroomId=?", "periodId=?", "subjectSetId=?")
                        .addWhereClause("is=?");
                update = connection.prepareStatement(builder.build());
            }

            insert.setInt(1, lessonPlan.schoolClass.id);
            insert.setInt(2, lessonPlan.staff.id);
            insert.setInt(3, lessonPlan.classroom.id);
            insert.setInt(4, lessonPlan.period.id);
            insert.setInt(5, lessonPlan.subjectSet.id);
            insert.setInt(6, lessonPlan.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            update.executeUpdate();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return success;
    }

    @Override
    public boolean delete(LessonPlan lessonPlan) throws DataUpdateException, DataAccessException {
        boolean success = false;

        try {
            if (delete == null || delete.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.DELETE)
                        .addWhereClause("id=?");
                delete = connection.prepareStatement(builder.build());
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            delete.executeUpdate();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return success;
    }
}
