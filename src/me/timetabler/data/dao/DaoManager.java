package me.timetabler.data.dao;

import me.timetabler.data.exceptions.DataConnectionException;

/**
 * The manager of all the daos for a specific data source.
 */
public interface DaoManager {
    /**
     * Returns an implementation of StaffDao corresponding with this manager's data source.
     * @return A StaffDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    StaffDao getStaffDao() throws DataConnectionException;

    /**
     * Returns an implementation of SubjectDao corresponding with this manager's data source.
     * @return A SubjectDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    SubjectDao getSubjectDao() throws DataConnectionException;

    /**
     * Returns an implementation of SchoolClassDao corresponding with this manager's data source.
     * @return A SchoolClassDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    SchoolClassDao getSchoolClassDao() throws DataConnectionException;

    /**
     * Returns an implementation of DayDao corresponding with this manager's data source.
     * @return A DayDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    DayDao getDayDao() throws DataConnectionException;

    /**
     * Returns an implementation of ClassroomDao corresponding with this manager's data source.
     * @return A ClassroomDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    ClassroomDao getClassroomDao() throws DataConnectionException;
}
