package me.timetabler.data.mariadb;

import me.timetabler.data.Subject;
import me.timetabler.data.dao.SubjectDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.util.Log;
import me.util.MapBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * {@inheritDoc}
 */
public class MariaSubjectDao implements SubjectDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectId;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    public MariaSubjectDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Subject> getAllSubjects() throws DataAccessException {
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
            Log.debug("Caught [" + e + "] so throwing DataAccessException");
            throw new DataAccessException(e);
        }

        return subjects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Subject> getById(int id) throws DataUpdateException, DataAccessException {
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
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
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
    public int insertSubject(Subject subject) throws DataUpdateException, DataAccessException {
        int id = -1;

        try {
            if (insert == null || insert.isClosed()) {
                initStatement(StatementType.INSERT);
            }

            insert.setString(1, subject.name);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            insert.executeUpdate();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataUpdateException!");
            throw new DataUpdateException(e);
        }

        try {
            ResultSet set = insert.getGeneratedKeys();
            set.next();
            id = set.getInt(1);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateSubject(Subject subject) throws DataUpdateException, DataAccessException {
        boolean success;

        try {
            if (update == null || update.isClosed()) {
                initStatement(StatementType.UPDATE);
            }

            update.setInt(1, subject.id);
            update.setString(2, subject.name);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            update.execute();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteSubject(Subject subject) throws DataUpdateException, DataAccessException {
        boolean success;

        try {
            if (delete == null || delete.isClosed()) {
                initStatement(StatementType.DELETE);
            }

            delete.setInt(1, subject.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            delete.execute();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return success;
    }

    /**
     * If the connection is null or is closed, this will initialise it. It will then initialise the statement which corresponds the
     * given type.
     * @param type The type of statement to be initialised.
     * @throws DataAccessException Thrown if the database cannot be accessed to open the connection or if the statement cannot be prepared.
     */
    private void initStatement(StatementType type) throws DataAccessException {
        MapBuilder<String, String> builder = new MapBuilder<>(new HashMap<>());
        try {
            switch (type) {
                case SELECT:
                    selectId = connection.prepareStatement(type.getSql(builder.put("table", "subject").put("columns", "id,subjectName").put("where", "id=?").build()));
                    break;
                case UPDATE:
                    update = connection.prepareStatement(type.getSql(builder.put("table", "subject").put("set", "subjectName=?").put("where", "id=?").build()));
                    break;
                case INSERT:
                    insert = connection.prepareStatement(type.getSql(builder.put("table", "subject").put("columns", "subjectName").put("values", "?").build()));
                    break;
                case DELETE:
                    delete = connection.prepareStatement(type.getSql(builder.put("table", "subject").put("where", "id=?").build()));
                    break;
                case SELECT_ALL:
                    selectAll = connection.prepareStatement(type.getSql(builder.put("table", "subject").put("columns", "*").build()));
                    break;
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }
    }
}
