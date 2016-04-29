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

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@inheritDoc}
 * The dao will utilise a MariaDB database as it data source.
 */
public class MariaLessonPlanDao implements LessonPlanDao {
    /**
     * The connection to the database, which all the PreparedStatements rely on.
     */
    protected Connection connection;

    /**
     * A PreparedStatement which is used to select all lessonPlans from the database.
     */
    private PreparedStatement selectAll;

    /**
     * A PreparedStatement which is used to select all lessonPlans of a given staff from the database.
     */
    private PreparedStatement selectAllStaff;

    /**
     * A PreparedStatement which is used to select all lessonPlans of a given classroom from the database.
     */
    private PreparedStatement selectAllClassroom;

    /**
     * A PreparedStatement which is used to select all lessonPlans of a given period from the database.
     */
    private PreparedStatement selectAllPeriod;

    /**
     * A PreparedStatement which is used to select all lessonPlans of a given subjectSet from the database.
     */
    private PreparedStatement selectAllSubjectSet;

    /**
     * A PreparedStatement which is used to select all lessonPlans of a given subject from the database.
     */
    private PreparedStatement selectAllSubject;

    /**
     * A PreparedStatement which is used to select a lessonPlan with a given id from the database.
     */
    private PreparedStatement selectId;

    /**
     * A PreparedStatement which is used to insert a lessonPlan into the database.
     */
    private PreparedStatement insert;

    /**
     * A PreparedStatement which is used to update a lessonPlan in the database.
     */
    private PreparedStatement update;

    /**
     * A PreparedStatement which is used to delete a lessonPlan from the database.
     */
    private PreparedStatement delete;

    /**
     * A PreparedStatement to load the learningSet data from a file into the database.
     */
    private PreparedStatement loadFile;

    /**
     * Initialises the dao with the given connection. The statements are initialised when required.
     * @param connection The connection to the database.
     */
    public MariaLessonPlanDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     * This method will get the lessonPlan data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<LessonPlan> getAll() throws DataAccessException {
        ArrayList<LessonPlan> lessonPlans = new ArrayList<>();
        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("lessonPlan.id", "subject.id", "subject.subjectName",
                                "lessonPlan.staffId", "lessonPlan.classroomId", "period.id", "dayOfWeek.id",
                                "dayOfWeek.dayOfWeek", "period.startTime", "period.endTime", "subjectSet.id",
                                "learningSet.id", "learningSet.setName", "schoolYear.id", "schoolYear.schoolYearName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "period", "lessonPlan.periodId=period.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subjectSet", "lessonPlan.subjectSetId=subjectSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "subjectSet.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.schoolYearId=schoolYear.id"));
                selectAll = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Subject subject = new Subject(set.getInt(2), set.getString(3));

                int staffId = set.getInt(4);
                Staff staff;
                if (!set.wasNull()){
                    staff = new Staff();
                    staff.id = staffId;
                } else {
                    staff = new Staff();
                    staff.id = -1;
                }

                int classroomId = set.getInt(5);
                Classroom classroom;
                if (!set.wasNull()) {
                    classroom = new Classroom();
                    classroom.id = classroomId;
                } else {
                    classroom = new Classroom();
                    classroom.id = -1;
                }

                Period period = new Period(set.getInt(6),
                        new Day(set.getInt(7), set.getString(8)), set.getTime(9).toLocalTime(), set.getTime(10).toLocalTime());
                SubjectSet subjectSet = new SubjectSet(set.getInt(11), subject, new LearningSet(set.getInt(12), set.getString(13)),
                        new SchoolYear(set.getInt(14), set.getString(15)));

                LessonPlan lessonPlan = new LessonPlan(set.getInt(1), staff, classroom, period, subjectSet);
                lessonPlans.add(lessonPlan);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return lessonPlans;
    }

    /**
     * {@inheritDoc}
     * This method will get the lessonPlan data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<LessonPlan> getAllByStaff(Staff staff) throws DataAccessException {
        ArrayList<LessonPlan> lessonPlans = new ArrayList<>();

        try {
            if (selectAllStaff == null || selectAllStaff.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("lessonPlan.id", "subject.id", "subject.subjectName",
                                "classroom.id", "classroom.roomName", "building.id", "building.buildingName", "period.id",
                                "dayOfWeek.id", "dayOfWeek.dayOfWeek", "period.startTime", "period.endTime", "subjectSet.id",
                                "learningSet.id", "learningSet.setName", "schoolYear.id", "schoolYear.schoolYearName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom", "lessonPlan.classroomId=classroom.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "period", "lessonPlan.periodId=period.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subjectSet", "lessonPlan.subjectSetId=subjectSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "subjectSet.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.schoolYearId=schoolYear.id"));
                selectAllStaff = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAllStaff.executeQuery();
            while (set.next()) {
                Subject subject = new Subject(set.getInt(2), set.getString(3));
                Classroom classroom = new Classroom(set.getInt(4), set.getString(5),
                        new Building(set.getInt(6), set.getString(7)), subject);
                Period period = new Period(set.getInt(8),
                        new Day(set.getInt(9), set.getString(10)), set.getTime(11).toLocalTime(), set.getTime(12).toLocalTime());
                SubjectSet subjectSet = new SubjectSet(set.getInt(13), subject, new LearningSet(set.getInt(14), set.getString(15)),
                        new SchoolYear(set.getInt(16), set.getString(17)));

                LessonPlan lessonPlan = new LessonPlan(set.getInt(1), staff, classroom, period, subjectSet);
                lessonPlans.add(lessonPlan);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return lessonPlans;
    }

    /**
     * {@inheritDoc}
     * This method will get the lessonPlan data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<LessonPlan> getAllByClassroom(Classroom classroom) throws DataAccessException {
        ArrayList<LessonPlan> lessonPlans = new ArrayList<>();

        try {
            if (selectAllClassroom == null || selectAllClassroom.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("lessonPlan.id", "subject.id", "subject.subjectName",
                                "staff.id", "staff.staffName", "staff.hoursPerWeek", "period.id", "dayOfWeek.id",
                                "dayOfWeek.dayOfWeek", "period.startTime", "period.endTime", "subjectSet.id",
                                "learningSet.id", "learningSet.setName", "schoolYear.id", "schoolYear.schoolYearName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "staff", "lessonPlan.staffId=staff.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "period", "lessonPlan.periodId=period.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subjectSet", "lessonPlan.subjectSetId=subjectSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "subjectSet.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.schoolYearId=schoolYear.id"));
                selectAllClassroom = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAllClassroom.executeQuery();
            while (set.next()) {
                Subject subject = new Subject(set.getInt(2), set.getString(3));
                Staff staff = new Staff(set.getInt(4), set.getString(5), subject, set.getInt(6));
                Period period = new Period(set.getInt(7),
                        new Day(set.getInt(8), set.getString(9)), set.getTime(10).toLocalTime(), set.getTime(11).toLocalTime());
                SubjectSet subjectSet = new SubjectSet(set.getInt(12), subject, new LearningSet(set.getInt(13), set.getString(14)),
                        new SchoolYear(set.getInt(15), set.getString(16)));

                LessonPlan lessonPlan = new LessonPlan(set.getInt(1), staff, classroom, period, subjectSet);
                lessonPlans.add(lessonPlan);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return lessonPlans;
    }

    /**
     * {@inheritDoc}
     * This method will get the lessonPlan data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<LessonPlan> getAllByPeriod(Period period) throws DataAccessException {
        ArrayList<LessonPlan> lessonPlans = new ArrayList<>();

        try {
            if (selectAllPeriod == null || selectAllPeriod.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("lessonPlan.id", "subject.id", "subject.subjectName",
                                "staff.id", "staff.staffName", "staff.hoursPerWeek", "classroom.id", "classroom.roomName",
                                "building.id", "building.buildingName", "subjectSet.id", "learningSet.id",
                                "learningSet.setName", "schoolYear.id", "schoolYear.schoolYearName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "staff", "lessonPlan.staffId=staff.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom", "lessonPlan.classroomId=classroom.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subjectSet", "lessonPlan.subjectSetId=subjectSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "subjectSet.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.schoolYearId=schoolYear.id"));
                selectAllPeriod = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAllPeriod.executeQuery();
            while (set.next()) {
                Subject subject = new Subject(set.getInt(2), set.getString(3));
                Staff staff = new Staff(set.getInt(4), set.getString(5), subject, set.getInt(6));
                Classroom classroom = new Classroom(set.getInt(7), set.getString(8),
                        new Building(set.getInt(9), set.getString(10)), subject);
                SubjectSet subjectSet = new SubjectSet(set.getInt(11), subject, new LearningSet(set.getInt(12), set.getString(13)),
                        new SchoolYear(set.getInt(14), set.getString(15)));

                LessonPlan lessonPlan = new LessonPlan(set.getInt(1), staff, classroom, period, subjectSet);
                lessonPlans.add(lessonPlan);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return lessonPlans;
    }

    /**
     * {@inheritDoc}
     * This method will get the lessonPlan data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<LessonPlan> getAllBySubjectSet(SubjectSet subjectSet) throws DataAccessException {
        ArrayList<LessonPlan> lessonPlans = new ArrayList<>();

        try {
            if (selectAllSubjectSet == null || selectAllSubjectSet.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("lessonPlan.id", "class.id", "class.className", "subject.id", "subject.subjectName",
                                "staff.id", "staff.staffName", "staff.hoursPerWeek", "classroom.id", "classroom.roomName",
                                "building.id", "building.buildingName", "period.id", "dayOfWeek.id",
                                "dayOfWeek.dayOfWeek", "period.startTime", "period.endTime")
                        .addJoinClause(new JoinClause(JoinType.INNER, "staff", "lessonPlan.staffId=staff.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "staff.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom", "lessonPlan.classroomId=classroom.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "period", "lessonPlan.periodId=period.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"));
                selectAllSubjectSet = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAllSubjectSet.executeQuery();
            while (set.next()) {
                Subject subject = new Subject(set.getInt(2), set.getString(3));
                Staff staff = new Staff(set.getInt(4), set.getString(5), subject, set.getInt(6));
                Classroom classroom = new Classroom(set.getInt(7), set.getString(8),
                        new Building(set.getInt(9), set.getString(10)), subject);
                Period period = new Period(set.getInt(11),
                        new Day(set.getInt(12), set.getString(13)), set.getTime(14).toLocalTime(), set.getTime(15).toLocalTime());

                LessonPlan lessonPlan = new LessonPlan(set.getInt(1), staff, classroom, period, subjectSet);
                lessonPlans.add(lessonPlan);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return lessonPlans;
    }

    /**
     * {@inheritDoc}
     * This method will get the lessonPlan data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<LessonPlan> getAllBySubject(Subject subject) throws DataAccessException {
        ArrayList<LessonPlan> lessonPlans = new ArrayList<>();

        try {
            if (selectAllSubject == null || selectAllSubject.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("lessonPlan.id", "staff.id", "staff.staffName", "staff.hoursPerWeek", "classroom.id",
                                "classroom.roomName", "building.id", "building.buildingName", "period.id", "dayOfWeek.id",
                                "dayOfWeek.dayOfWeek", "period.startTime", "period.endTime", "subjectSet.id",
                                "learningSet.id", "learningSet.setName", "schoolYear.id", "schoolYear.schoolYearName", "subjectSet.subjectId")
                        .addJoinClause(new JoinClause(JoinType.INNER, "staff", "lessonPlan.staffId=staff.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom", "lessonPlan.classroomId=classroom.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "period", "lessonPlan.periodId=period.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subjectSet", "lessonPlan.subjectSetId=subjectSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.schoolYearId=schoolYear.id"))
                        .addWhereClause("subjectSet.subjectId=?");
                selectAllSubject = connection.prepareStatement(builder.build());
            }
            selectAllSubject.setInt(1, subject.id);

            ResultSet set = selectAllSubject.executeQuery();
            while (set.next()) {
                int staffId = set.getInt(2);
                Staff staff;
                if (!set.wasNull()){
                    staff = new Staff(staffId, set.getString(3), subject, set.getInt(4));
                } else {
                    staff = new Staff();
                    staff.id = -1;
                }

                int classroomId = set.getInt(5);
                Classroom classroom;
                if (!set.wasNull()) {
                    classroom = new Classroom(classroomId, set.getString(6),
                            new Building(set.getInt(7), set.getString(8)), subject);
                } else {
                    classroom = new Classroom();
                    classroom.id = -1;
                }

                Period period = new Period(set.getInt(9),
                        new Day(set.getInt(10), set.getString(11)), set.getTime(12).toLocalTime(), set.getTime(13).toLocalTime());
                SubjectSet subjectSet = new SubjectSet(set.getInt(14), subject, new LearningSet(set.getInt(15), set.getString(16)),
                        new SchoolYear(set.getInt(17), set.getString(18)));

                LessonPlan lessonPlan = new LessonPlan(set.getInt(1), staff, classroom, period, subjectSet);
                lessonPlans.add(lessonPlan);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return lessonPlans;
    }

    /**
     * {@inheritDoc}
     * This method will insert the lessonPlan data into a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public int insert(LessonPlan lessonPlan) throws DataUpdateException, DataAccessException {
        int id = -1;

        if (lessonPlan == null || lessonPlan.subjectSet == null || lessonPlan.period == null) {
            return id;
        }

        try {
            if (insert == null || insert.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.INSERT)
                        .addColumns("staffId", "classroomId", "periodId", "subjectSetId")
                        .addValues("?", "?", "?", "?");
                insert = connection.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
            }

            if (lessonPlan.staff == null) {
                insert.setNull(1, Types.INTEGER);
            } else {
                insert.setInt(1, lessonPlan.staff.id);
            }

            if (lessonPlan.classroom == null) {
                insert.setNull(2, Types.INTEGER);
            } else {
                insert.setInt(2, lessonPlan.classroom.id);
            }

            insert.setInt(3, lessonPlan.period.id);
            insert.setInt(4, lessonPlan.subjectSet.id);
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
            if (set.next()) {
                id = set.getInt(1);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return id;
    }

    /**
     * {@inheritDoc}
     * This method will update the lessonPlan data in a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public boolean update(LessonPlan lessonPlan) throws DataUpdateException, DataAccessException {
        if (lessonPlan == null || lessonPlan.period == null || lessonPlan.subjectSet == null || lessonPlan.staff == null || lessonPlan.classroom == null || lessonPlan.id < 0) {
            return false;
        }

        try {
            if (update == null || update.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.UPDATE)
                        .addSetClauses("staffId=?", "classroomId=?", "periodId=?", "subjectSetId=?")
                        .addWhereClause("id=?");
                update = connection.prepareStatement(builder.build());
            }

            insert.setInt(1, lessonPlan.staff.id);
            insert.setInt(2, lessonPlan.classroom.id);
            insert.setInt(3, lessonPlan.period.id);
            insert.setInt(4, lessonPlan.subjectSet.id);
            insert.setInt(5, lessonPlan.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            update.executeUpdate();
            return true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }
    }

    /**
     * {@inheritDoc}
     * This method will delete the lessonPlan data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public boolean delete(LessonPlan lessonPlan) throws DataUpdateException, DataAccessException {
        if (lessonPlan == null || lessonPlan.id < 0) {
            return false;
        }

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
            return true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }
    }

    /**
     * {@inheritDoc}
     * This method will get the lessonPlan data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public Optional<LessonPlan> getById(int id) throws DataAccessException {
        LessonPlan plan = null;

        try {
            if (id >= 0) {
                if (selectId == null || selectId.isClosed()) {
                    SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("subject.id", "subject.subjectName",
                                "classroom.id", "classroom.roomName", "building.id", "building.buildingName", "period.id",
                                "dayOfWeek.id", "dayOfWeek.dayOfWeek", "period.startTime", "period.endTime", "subjectSet.id",
                                "learningSet.id", "learningSet.setName", "schoolYear.id", "schoolYear.schoolYearName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom", "lessonPlan.classroomId=classroom.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "period", "lessonPlan.periodId=period.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subjectSet", "lessonPlan.subjectSetId=subjectSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "subjectSet.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.schoolYearId=schoolYear.id"))
                        .addWhereClause("lessonPlan.id=?");
                    selectId = connection.prepareStatement(builder.build());
                }

                selectId.setInt(1, id);

                ResultSet set = selectId.executeQuery();

                if (set.next()) {
                    Subject subject = new Subject(set.getInt(1), set.getString(2));
                    Staff staff = new Staff(set.getInt(3), set.getString(4), subject, set.getInt(5));
                    Classroom classroom = new Classroom(set.getInt(6), set.getString(7),
                            new Building(set.getInt(8), set.getString(9)), subject);
                    Period period = new Period(set.getInt(10),
                            new Day(set.getInt(11), set.getString(12)), set.getTime(13).toLocalTime(), set.getTime(14).toLocalTime());
                    SubjectSet subjectSet = new SubjectSet(set.getInt(15), subject, new LearningSet(set.getInt(16), set.getString(17)),
                            new SchoolYear(set.getInt(18), set.getString(19)));

                    plan = new LessonPlan(id, staff, classroom, period, subjectSet);
                }
                set.close();
            }
        } catch (SQLException e) {
            Log.error("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        if (plan == null) {
            return Optional.empty();
        } else {
            return Optional.of(plan);
        }
    }

    /**
     * {@inheritDoc}
     * This method will load the classroom data into the MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public boolean loadFile(File file) throws DataAccessException, DataUpdateException {
        if (file == null) {
            throw new NullPointerException("Data File Cannot Be Null!");
        } else if (!file.exists()) {
            throw new IllegalArgumentException("Data File [" + file.getAbsolutePath() + "] Must Exist!");
        } else if (file.isDirectory()) {
            throw new IllegalArgumentException("Data File [" + file.getAbsolutePath() + "] Must Not Be A Directory!");
        } else if (!file.canRead()) {
            throw new IllegalArgumentException("Data File [" + file.getAbsolutePath() + "] Must Have Read Permissions For User [" + System.getProperty("user.name") + "]!");
        }

        try {
            if (loadFile == null || loadFile.isClosed()) {
                loadFile = connection.prepareStatement("LOAD DATA INFILE '?' INTO TABLE classroom FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\n';");
            }

            loadFile.setString(1, file.getAbsolutePath());
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            loadFile.executeUpdate();
            return true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            if (selectAll != null && !selectAll.isClosed()) selectAll.close();
            if (selectAllStaff != null && !selectAllStaff.isClosed()) selectAllStaff.close();
            if (selectAllClassroom != null && !selectAllClassroom.isClosed()) selectAllClassroom.close();
            if (selectAllPeriod != null && !selectAllPeriod.isClosed()) selectAllPeriod.close();
            if (selectAllSubjectSet != null && !selectAllSubjectSet.isClosed()) selectAllSubjectSet.close();
            if (selectAllSubject != null && !selectAllSubject.isClosed()) selectAllSubject.close();
            if (insert != null && !insert.isClosed()) insert.close();
            if (update != null && !update.isClosed()) update.close();
            if (delete != null && !delete.isClosed()) delete.close();
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            Log.error(e);
        }
    }
}
