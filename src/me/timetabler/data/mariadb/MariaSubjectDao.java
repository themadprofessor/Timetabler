package me.timetabler.data.mariadb;

import me.timetabler.data.Subject;
import me.timetabler.data.dao.SubjectDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.timetabler.data.sql.SqlBuilder;
import me.timetabler.data.sql.StatementType;
import me.util.Log;

import java.sql.*;
import java.util.ArrayList;
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
                SqlBuilder builder = new SqlBuilder("subject", StatementType.SELECT);
                selectAll = connection.prepareStatement(builder.build());
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
    public Optional<Subject> getById(int id) throws DataAccessException {
        Subject subject;

        try {
            if (selectId == null || selectId.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subject", StatementType.SELECT)
                        .addColumn("subjectName")
                        .addWhereClause("id=?");
                selectId = connection.prepareStatement(builder.build());
            }

            selectId.setInt(1, id);
            ResultSet set = selectId.executeQuery();
            set.next();
            subject = new Subject(id, set.getString(1));
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
                SqlBuilder builder = new SqlBuilder("subject", StatementType.INSERT)
                        .addColumn("subjectName")
                        .addValue("?");
                insert = connection.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
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
                SqlBuilder builder = new SqlBuilder("subject", StatementType.UPDATE)
                        .addSetClause("subject=?")
                        .addWhereClause("id=?");
                update = connection.prepareStatement(builder.build());
            }

            update.setInt(2, subject.id);
            update.setString(1, subject.name);
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
                SqlBuilder builder = new SqlBuilder("subject", StatementType.DELETE)
                        .addWhereClause("id=?");
                delete = connection.prepareStatement(builder.build());
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
}
