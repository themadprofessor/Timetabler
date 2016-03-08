package me.timetabler.data.dao;

/**
 * The manager of all the daos for a specific data source.
 */
public interface DaoManager {
    /**
     * Returns an implementation of StaffDao corresponding with this manager's data source.
     * @return A StaffDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    StaffDao getStaffDao();

    /**
     * Returns an implementation of SubjectDao corresponding with this manager's data source.
     * @return A SubjectDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    SubjectDao getSubjectDao();

    /**
     * Returns an implementation of SchoolClassDao corresponding with this manager's data source.
     * @return A SchoolClassDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    SchoolClassDao getSchoolClassDao();

    /**
     * Returns an implementation of DayDao corresponding with this manager's data source.
     * @return A DayDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    DayDao getDayDao();

    /**
     * Returns an implementation of ClassroomDao corresponding with this manager's data source.
     * @return A ClassroomDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    ClassroomDao getClassroomDao();
}
