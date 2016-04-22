package me.timetabler.data.mariadb;

import javafx.scene.control.Alert;
import me.timetabler.data.dao.*;
import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.ui.main.JavaFxBridge;
import me.util.Log;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * {@inheritDoc}
 */
public class MariaDaoManager implements DaoManager {
    /**
     * The DataSource wrapper object to the database.
     */
    private DataSource source;

    /**
     * The connection given to the daos.
     */
    private Connection connection;

    /**
     * The MariaDB SubjectDao implementation.
     */
    private MariaSubjectDao subjectDao;

    /**
     * The MariaDB StaffDao implementation.
     */
    private MariaStaffDao staffDao;

    /**
     * The MariaDB DayDao implementation.
     */
    private MariaDayDao dayDao;

    /**
     * The MariaDB ClassroomDao implementation.
     */
    private MariaClassroomDao classroomDao;

    /**
     * The MariaDB BuildingDao implementation.
     */
    private MariaBuildingDao buildingDao;

    /**
     * The MariaDB DistanceDao implementation.
     */
    private MariaDistanceDao distanceDao;

    /**
     * The MariaDB LearningSetDao implementation.
     */
    private MariaLearningSetDao learningSetDao;

    /**
     * The MariaDB LessonPlanDao implementation.
     */
    private MariaLessonPlanDao lessonPlanDao;

    /**
     * The MariaDB PeriodDao implementation.
     */
    private MariaPeriodDao periodDao;

    /**
     * The MariaDB SchoolYearDao implementation.
     */
    private MariaSchoolYearDao schoolYearDao;

    /**
     * The MariaDB SubjectSetDao implementation.
     */
    private MariaSubjectSetDao subjectSetDao;

    /**
     * The manager of the MariaDB server.
     */
    private MariaDbManager dbManager;

    /**
     * Initialises the DaoManager and establishes a DataSource object with the database.
     * @param config A map containing the keys 'addr', 'port', 'database', 'exec', 'args', 'username' and 'password'.
     * @throws DataConnectionException Thrown if the data source cannot be established.
     */
    public MariaDaoManager(Map<String, String> config) throws DataConnectionException {
        try {
            dbManager = new MariaDbManager(config);
        } catch (IOException e) {
            JavaFxBridge.createAlert(Alert.AlertType.ERROR, "Failed to start database!", null, "Failed to start the database due to a IO exception! [" + e.getMessage() + ']', true);
        }
        source = dbManager.getDataSource(config);
    }

    /**
     * {@inheritDoc}
     * Using the lazy initialisation pattern, the staffDao object is initialised when it is needed. Also, the
     * connection object is checked and passed to staffDao, if staffDao is initialised and its connection
     * object is uninitialised or closed.
     */
    @Override
    public StaffDao getStaffDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                Log.verbose("Establishing new connection");
                connection = source.getConnection();
            }
            if (staffDao == null) {
                Log.verbose("Creating new Staff Dao");
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
     * Using the lazy initialisation pattern, the subjectDao object is initialised when it is needed. Also, the
     * connection object is checked and passed to subjectDao, if subjectDao is initialised and its connection
     * object is uninitialised or closed.
     */
    @Override
    public SubjectDao getSubjectDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                Log.verbose("Establishing new connection");
                connection = source.getConnection();
            }
            if (subjectDao == null) {
                Log.verbose("Creating new Subject Dao");
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
     * Using the lazy initialisation pattern, the dayDao object is initialised when it is needed. Also, the
     * connection object is checked and passed to dayDao, if dayDao is initialised and its connection
     * object is uninitialised or closed.
     */
    @Override
    public DayDao getDayDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                Log.verbose("Establishing new connection");
                 connection = source.getConnection();
            }
            if (dayDao == null) {
                Log.verbose("Creating new Day Dao");
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
     * Using the lazy initialisation pattern, the classroomDao object is initialised when it is needed. Also, the
     * connection object is checked and passed to classroomDao, if classroomDao is initialised and its connection
     * object is uninitialised or closed.
     */
    @Override
    public ClassroomDao getClassroomDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                Log.verbose("Establishing new connection");
                connection = source.getConnection();
            }
            if (classroomDao == null) {
                Log.verbose("Creating new Classroom Dao");
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
     * Using the lazy initialisation pattern, the buildingDao object is initialised when it is needed. Also, the
     * connection object is checked and passed to buildingDao, if buildingDao is initialised and its connection
     * object is uninitialised or closed.
     */
    @Override
    public BuildingDao getBuildingDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                Log.verbose("Establishing new connection");
                connection = source.getConnection();
            }
            if (buildingDao == null) {
                Log.verbose("Creating new Building Dao");
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
     * Using the lazy initialisation pattern, the distanceDao object is initialised when it is needed. Also, the
     * connection object is checked and passed to distanceDao, if distanceDao is initialised and its connection
     * object is uninitialised or closed.
     */
    @Override
    public DistanceDao getDistanceDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                Log.verbose("Establishing new connection");
                connection = source.getConnection();
            }
            if (distanceDao == null) {
                Log.verbose("Creating new Distance Dao");
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
     * Using the lazy initialisation pattern, the lessonSetDao object is initialised when it is needed. Also, the
     * connection object is checked and passed to lessonSetDao, if lessonSetDao is initialised and its connection
     * object is uninitialised or closed.
     */
    @Override
    public LearningSetDao getLearningSetDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                Log.verbose("Establishing new connection");
                connection = source.getConnection();
            }
            if (learningSetDao == null) {
                Log.verbose("Creating new LearningSet Dao");
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
     * Using the lazy initialisation pattern, the lessonPlanDao object is initialised when it is needed. Also, the
     * connection object is checked and passed to lessonPlanDao, if lessonPlanDao is initialised and its connection
     * object is uninitialised or closed.
     */
    @Override
    public LessonPlanDao getLessonPlanDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                Log.verbose("Establishing new connection");
                connection = source.getConnection();
            }
            if (lessonPlanDao == null) {
                Log.verbose("Creating new LessonPlan Dao");
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
     * Using the lazy initialisation pattern, the periodDao object is initialised when it is needed. Also, the
     * connection object is checked and passed to periodDao, if periodDao is initialised and its connection
     * object is uninitialised or closed.
     */
    @Override
    public PeriodDao getPeriodDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                Log.verbose("Establishing new connection");
                connection = source.getConnection();
            }
            if (periodDao == null) {
                Log.verbose("Creating new Period Dao");
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
     * Using the lazy initialisation pattern, the schoolYearDao object is initialised when it is needed. Also, the
     * connection object is checked and passed to schoolYearDao, if schoolYearDao is initialised and its connection
     * object is uninitialised or closed.
     */
    @Override
    public SchoolYearDao getSchoolYearDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                Log.verbose("Establishing new connection");
                connection = source.getConnection();
            }
            if (schoolYearDao == null) {
                Log.verbose("Creating new SchoolYear Dao");
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
     * Using the lazy initialisation pattern, the subjectSetDao object is initialised when it is needed. Also, the
     * connection object is checked and passed to subjectSetDao, if subjectSetDao is initialised and its connection
     * object is uninitialised or closed.
     */
    @Override
    public SubjectSetDao getSubjectSetDao() throws DataConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                Log.verbose("Establishing new connection");
                connection = source.getConnection();
            }
            if (subjectSetDao == null) {
                Log.verbose("Creating new SubjectSet Dao");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (subjectDao != null) subjectDao.close();
        if (staffDao != null) staffDao.close();
        if (dayDao != null) dayDao.close();
        if (classroomDao != null) classroomDao.close();
        if (buildingDao != null) buildingDao.close();
        if (distanceDao != null) distanceDao.close();
        if (learningSetDao != null) learningSetDao.close();
        if (lessonPlanDao != null) lessonPlanDao.close();
        if (periodDao != null) periodDao.close();
        if (schoolYearDao != null) schoolYearDao.close();
        if (subjectSetDao != null) subjectSetDao.close();
        dbManager.close();
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            Log.error(e);
        }
    }

}
