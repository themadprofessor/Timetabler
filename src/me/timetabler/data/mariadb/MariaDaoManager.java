package me.timetabler.data.mariadb;

import me.timetabler.data.dao.*;
import me.timetabler.data.exceptions.DataConnectionException;
import me.util.Log;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * {@inheritDoc}
 */
public class MariaDaoManager implements DaoManager {
    private DataSource source;
    private Connection connection;
    private MariaSubjectDao subjectDao;
    private MariaStaffDao staffDao;
    private MariaClassDao classDao;
    private MariaDayDao dayDao;
    private MariaClassroomDao classroomDao;
    private MariaBuildingDao buildingDao;
    private MariaDistanceDao distanceDao;
    private MariaLearningSetDao learningSetDao;
    private MariaLessonPlanDao lessonPlanDao;
    private MariaPeriodDao periodDao;
    private MariaSchoolYearDao schoolYearDao;
    private MariaSubjectSetDao subjectSetDao;

    public MariaDaoManager(Map<String, String> config) throws DataConnectionException {
        try {
            MariaDbDataSource source = new MariaDbDataSource(config.get("addr"), Integer.parseInt(config.get("port")), config.get("database"));
            source.setUser("root");
            source.setPassword("root");
            this.source = source;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataConnectionException!");
            throw new DataConnectionException(config.get("addr"), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StaffDao getStaffDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = source.getConnection();
            }
            if (staffDao == null) {
                staffDao = new MariaStaffDao(connection);
            } else if (staffDao.connection != connection) {
                staffDao.connection = connection;
            }
            return staffDao;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataConnectionException!");
            throw new DataConnectionException("MariaDB", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SubjectDao getSubjectDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = source.getConnection();
            }
            if (subjectDao == null) {
                subjectDao = new MariaSubjectDao(connection);
            } else if (subjectDao.connection != connection) {
                subjectDao.connection = connection;
            }
            return subjectDao;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataConnectionException!");
            throw new DataConnectionException("MariaDB", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public SchoolClassDao getSchoolClassDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = source.getConnection();
            }
            if (classDao == null) {
                classDao = new MariaClassDao(connection);
            } else if (classDao.connection != connection) {
                classDao.connection = connection;
            }
            return classDao;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataConnectionException!");
            throw new DataConnectionException("MariaDB", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DayDao getDayDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                 connection = source.getConnection();
            }
            if (dayDao == null) {
                dayDao = new MariaDayDao(connection);
            } else if (dayDao.connection != connection) {
                dayDao.connection = connection;
            }
            return dayDao;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataConnectionException!");
            throw new DataConnectionException("MariaDB", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassroomDao getClassroomDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = source.getConnection();
            }
            if (classroomDao == null) {
                classroomDao = new MariaClassroomDao(connection);
            } else if (classroomDao.connection != connection) {
                classroomDao.connection = connection;
            }
            return classroomDao;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataConnectionException!");
            throw new DataConnectionException("MariaDB", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BuildingDao getBuildingDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = source.getConnection();
            }
            if (buildingDao == null) {
                buildingDao = new MariaBuildingDao(connection);
            } else if (buildingDao.connection != connection) {
                buildingDao.connection = connection;
            }
            return buildingDao;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataConnectionException!");
            throw new DataConnectionException("MariaDB", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DistanceDao getDistanceDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = source.getConnection();
            }
            if (distanceDao == null) {
                distanceDao = new MariaDistanceDao(connection);
            } else if (distanceDao.connection != connection) {
                distanceDao.connection = connection;
            }
            return distanceDao;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataConnectionException!");
            throw new DataConnectionException("MariaDB", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LearningSetDao getLearningSetDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = source.getConnection();
            }
            if (learningSetDao == null) {
                learningSetDao = new MariaLearningSetDao(connection);
            } else if (learningSetDao.connection != connection) {
                learningSetDao.connection = connection;
            }
            return learningSetDao;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataConnectionException!");
            throw new DataConnectionException("MariaDB", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LessonPlanDao getLessonPlanDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = source.getConnection();
            }
            if (lessonPlanDao == null) {
                lessonPlanDao = new MariaLessonPlanDao(connection);
            } else if (lessonPlanDao.connection != connection) {
                lessonPlanDao.connection = connection;
            }
            return lessonPlanDao;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataConnectionException!");
            throw new DataConnectionException("MariaDB", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PeriodDao getPeriodDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = source.getConnection();
            }
            if (periodDao == null) {
                periodDao = new MariaPeriodDao(connection);
            } else if (periodDao.connection != connection) {
                periodDao.connection = connection;
            }
            return periodDao;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataConnectionException!");
            throw new DataConnectionException("MariaDB", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchoolYearDao getSchoolYearDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = source.getConnection();
            }
            if (schoolYearDao == null) {
                schoolYearDao = new MariaSchoolYearDao(connection);
            } else if (schoolYearDao.connection != connection) {
                schoolYearDao.connection = connection;
            }
            return schoolYearDao;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataConnectionException!");
            throw new DataConnectionException("MariaDB", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SubjectSetDao getSubjectSetDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = source.getConnection();
            }
            if (subjectSetDao == null) {
                subjectSetDao = new MariaSubjectSetDao(connection);
            } else if (subjectSetDao.connection != connection) {
                subjectSetDao.connection = connection;
            }
            return subjectSetDao;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataConnectionException!");
            throw new DataConnectionException("MariaDB", e);
        }
    }
}
