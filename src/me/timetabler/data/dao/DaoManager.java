package me.timetabler.data.dao;

import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.data.mariadb.MariaDaoManager;

import java.util.Map;

/**
 * The manager of all the daos for a specific data source.
 */
public interface DaoManager extends AutoCloseable {
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

    /**
     * Returns an implementation of BuildingDao corresponding with this manager's data source.
     * @return A BuildingDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    BuildingDao getBuildingDao() throws DataConnectionException;

    /**
     * Returns an implementation of DistanceDao corresponding with this manager's data source.
     * @return A DistanceDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    DistanceDao getDistanceDao() throws DataConnectionException;

    /**
     * Returns an implementation of LearningSetDao corresponding with this manager's data source.
     * @return A LearningSetDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    LearningSetDao getLearningSetDao() throws DataConnectionException;

    /**
     * Returns an implementation of LessonPlanDao corresponding with this manager's data source.
     * @return A LessonPlanDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    LessonPlanDao getLessonPlanDao() throws DataConnectionException;

    /**
     * Returns an implementation of PeriodDao corresponding with this manager's data source.
     * @return A PeriodDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    PeriodDao getPeriodDao() throws DataConnectionException;

    /**
     * Returns an implementation of SchoolYearDao corresponding with this manager's data source.
     * @return A SchoolYearDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    SchoolYearDao getSchoolYearDao() throws DataConnectionException;

    /**
     * Returns an implementation of SubjectSetDao corresponding with this manager's data source.
     * @return A SubjectSetDao implementation.
     * @throws me.timetabler.data.exceptions.DataConnectionException Thrown if the data source cannot be connected to.
     */
    SubjectSetDao getSubjectSetDao() throws DataConnectionException;

    /**
     * Closes all the resources required by the program.
     */
    void close();

    /**
     * Returns a DaoManager to correspond with the given config's entry 'type'. The config will be given to the
     * DaoManager. If the config does not have a 'type' entry, or it does not correspond with the DaoManger
     * implementations, this will return null.
     * @param config The configuration map for 'data_type'
     * @return A DaoManager implementation or null.
     * @throws DataConnectionException Thrown if the DaoManager cannot establish a connection with the data source.
     */
    static DaoManager getManager(Map<String, String> config) throws DataConnectionException {
        String type = config.get("type");

        switch (type) {
            case "MARIADB": return new MariaDaoManager(config);
            default: return null;
        }
    }
}
