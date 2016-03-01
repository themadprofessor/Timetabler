package me.timetabler.data.mariadb;

import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.dao.SchoolClassDao;
import me.timetabler.data.dao.StaffDao;
import me.timetabler.data.dao.SubjectDao;
import me.timetabler.data.exceptions.DatabaseAccessException;
import me.timetabler.data.exceptions.DatabaseConnectionException;
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

    public MariaDaoManager(Map<String, String> config) {
        try {
            MariaDbDataSource source = new MariaDbDataSource(config.get("addr"), Integer.parseInt(config.get("port")), config.get("database"));
            source.setUser("root");
            source.setPassword("root");
            this.source = source;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseConnectionException!");
            throw new DatabaseConnectionException(config.get("addr"), e);
        }
    }

    @Override
    public StaffDao getStaffDao() {
        return null;
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
            Log.debug("Caught [" + e + "] so throwing a DatabaseAccessException!");
            throw new DatabaseAccessException(e);
        }
    }

    @Override
    public SchoolClassDao getSchoolClassDao() {
        return null;
    }
}
