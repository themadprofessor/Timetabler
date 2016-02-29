package me.timetabler.data.dao;

/**
 * Created by stuart on 29/02/16.
 */
public interface DaoManager {
    StaffDao getStaffDao();
    SubjectDao getSubjectDao();
    SchoolClassDao getSchoolClassDao();
}
