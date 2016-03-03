package me.timetabler.data.mariadb;

import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.dao.SchoolClassDao;
import me.timetabler.data.dao.StaffDao;
import me.timetabler.data.dao.SubjectDao;
import me.timetabler.data.exceptions.DataAccessException;
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
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }
    }

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
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }
    }

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
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }
    }
}
