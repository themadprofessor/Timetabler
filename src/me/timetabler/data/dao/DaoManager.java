package me.timetabler.data.dao;

/**
 * The manager of all the daos for a specific data source.
 */
public interface DaoManager {
    /**
     * Returns an implementation of StaffDao corresponding with this manager's data source.
     * @return A StaffDao implementation.
     */
    StaffDao getStaffDao();

    /**
     * Returns an implementation of SubjectDao corresponding with this manager's data source.
     * @return A SubjectDao implementation.
     */
    SubjectDao getSubjectDao();

    /**
     * Returns an implementation of SchoolClassDao corresponding with this manager's data source.
     * @return A SchoolClassDao implementation.
     */
    SchoolClassDao getSchoolClassDao();

    /**
     * Returns an implementation of DayDao corresponding with this manager's data source.
     * @return A DayDao implementation.
     */
    DayDao getDayDao();
}
