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
 * Created by stuart on 29/02/16.
 */
public class MariaDaoManager implements DaoManager {
    private DataSource source;
    private Connection connection;
    private MariaSubjectDao subjectDao;
    private MariaStaffDao staffDao;
    private MariaClassDao classDao;
    private MariaDayDao dayDao;
    private MariaClassroomDao classroomDao;

    public MariaDaoManager(Map<String, String> config) {
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
     * {@inheritdoc}
     */
    @Override
    public StaffDao getStaffDao() {
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
     * {@inheritdoc}
     */
    @Override
    public SubjectDao getSubjectDao() {
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
     * {@inheritdoc}
     */
    @Override
    public SchoolClassDao getSchoolClassDao() {
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
     * {@inheritdoc}
     */
    @Override
    public DayDao getDayDao() {
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
     * {@inheritdoc}
     */
    @Override
    public ClassroomDao getClassroomDao() {
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
}
