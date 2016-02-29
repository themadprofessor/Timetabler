package me.timetabler.data.mariadb;

import me.timetabler.data.Subject;
import me.timetabler.data.dao.SubjectDao;
import me.timetabler.data.exceptions.DatabaseAccessException;
import me.timetabler.data.exceptions.DatabaseUpdateException;
import me.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An implementation of SubjectDao which handles a MariaDB data source.
 */
public class MariaSubjectDao implements SubjectDao {
    private Database database;
    private Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectId;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    public MariaSubjectDao(Map<String, String> config) {
        database = new Database(config.get("addr"), config.get("dbname"), config.get("username"), config.get("password"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Subject> getAllSubjects() {
        ArrayList<Subject> subjects = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                initStatement(StatementType.SELECT_ALL);
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Subject subject = new Subject(set.getInt(1), set.getString(2));
                subjects.add(subject);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseAccessException");
            throw new DatabaseAccessException(e);
        }

        return subjects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Subject> getById(int id) {
        Subject subject;

        try {
            if (selectId == null || selectId.isClosed()) {
                initStatement(StatementType.SELECT);
            }

            selectId.setInt(1, id);
            ResultSet set = selectId.executeQuery();
            set.next();
            subject = new Subject(set.getInt(1), set.getString(2));
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseUpdateException!");
            throw new DatabaseUpdateException(e);
        }

        if (subject == null) {
            return Optional.empty();
        } else {
            return Optional.of(subject);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertSubject(Subject subject) {
        boolean success;

        try {
            if (insert == null || insert.isClosed()) {
                initStatement(StatementType.SELECT);
            }

            insert.setInt(1, subject.id);
            insert.setString(2, subject.name);
            insert.execute();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseUpdateException!");
            throw new DatabaseUpdateException(e);
        }
        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateSubject(Subject subject) {
        boolean success;

        try {
            if (update == null || update.isClosed()) {
                initStatement(StatementType.UPDATE);
            }

            update.setInt(1, subject.id);
            update.setString(2, subject.name);
            update.execute();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseUpdateException!");
            throw new DatabaseUpdateException(e);
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteSubject(Subject subject) {
        boolean success;

        try {
            if (delete == null || delete.isClosed()) {
                initStatement(StatementType.DELETE);
            }

            delete.setInt(1, subject.id);
            delete.execute();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseUpdateException!");
            throw new DatabaseUpdateException(e);
        }

        return success;
    }

    /**
     * If the connection is null or is closed, this will initialise it. It will then initialise the statement which corresponds the
     * given type.
     * @param type The type of statement to be initialised.
     * @throws DatabaseAccessException Thrown if the database cannot be accessed to open the connection or if the statement cannot be prepared.
     */
    private void initStatement(StatementType type) {
        try {
            if (connection == null || connection.isClosed()) {
                connection = database.openConn();
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseAccessException!");
            throw new DatabaseAccessException(e);
        }

        try {
            switch (type) {
                case SELECT:
                    selectId = connection.prepareStatement(type.getSql("subject", "id=?", null, null, null));
                    break;
                case SELECT_ALL:
                    selectAll = connection.prepareStatement(type.getSql("subject", null, null, null, null));
                    break;
                case UPDATE:
                    update = connection.prepareStatement(type.getSql("subject", "id=?", "name=?", null, null));
                    break;
                case INSERT:
                    insert = connection.prepareStatement(type.getSql("subject", null, null, "id,name", "?,?"));
                    break;
                case DELETE:
                    delete = connection.prepareStatement(type.getSql("subject", "id=?", null, null, null));
                    break;
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseAccessException!");
            throw new DatabaseAccessException(e);
        }
    }

    @Override
    public void close() {
        try {
            selectId.close();
            selectAll.close();
            insert.close();
            delete.close();
            update.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
