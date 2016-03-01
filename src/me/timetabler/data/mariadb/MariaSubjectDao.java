package me.timetabler.data.mariadb;

import me.timetabler.data.Subject;
import me.timetabler.data.dao.SubjectDao;
import me.timetabler.data.exceptions.DatabaseAccessException;
import me.timetabler.data.exceptions.DatabaseUpdateException;
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
 * An implementation of SubjectDao which handles a MariaDB data source.
 */
public class MariaSubjectDao implements SubjectDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectId;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;
    private PreparedStatement getLastId;

    public MariaSubjectDao(Connection connection) {
        this.connection = connection;
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
    public int insertSubject(Subject subject) {
        int success = -1;

        try {
            if (insert == null || insert.isClosed()) {
                initStatement(StatementType.INSERT);
            }

            insert.setString(1, subject.name);
            insert.execute();

            if (getLastId == null || getLastId.isClosed()) {
                initStatement(StatementType.GET_LAST_AUTO_INCRE);
            }
            ResultSet set = getLastId.executeQuery();
            success = set.getInt(1);
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
        MapBuilder<String, String> builder = new MapBuilder<>(new HashMap<>());
        try {
            switch (type) {
                case SELECT:
                    selectId = connection.prepareStatement(type.getSql(builder.put("table", "subject").put("columns", "id,subjectName").build()));
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
                    selectAll = connection.prepareStatement(type.getSql(builder.put("table", "subject").build()));
                    break;
                case GET_LAST_AUTO_INCRE:
                    getLastId = connection.prepareStatement(type.getSql(null));
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
