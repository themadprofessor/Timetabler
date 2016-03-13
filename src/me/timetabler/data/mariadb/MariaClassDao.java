package me.timetabler.data.mariadb;

import me.timetabler.data.SchoolClass;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.SchoolClassDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.timetabler.data.sql.JoinClause;
import me.timetabler.data.sql.JoinType;
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
public class MariaClassDao implements SchoolClassDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectAllSubject;
    private PreparedStatement selectId;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    public MariaClassDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SchoolClass> getAllClasses() throws DataAccessException {
        ArrayList<SchoolClass> classes = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("class", StatementType.SELECT)
                        .addColumns("class.id,class.className,subject.id,subject.subjectName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "class.subjectId=subject.id"));
                selectAll = connection.prepareStatement(builder.build());
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                SchoolClass schoolClass = new SchoolClass(set.getInt(1), set.getString(2), new Subject(set.getInt(3), set.getString(4)));
                classes.add(schoolClass);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
        }

        return classes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SchoolClass> getAllBySubject(Subject subject) throws DataUpdateException, DataAccessException {
        ArrayList<SchoolClass> classes = new ArrayList<>();

        try {
            if (selectAllSubject == null || selectAllSubject.isClosed()) {
                SqlBuilder builder = new SqlBuilder("class", StatementType.SELECT)
                        .addColumns("id,className")
                        .addWhereClause("subjectId=?");
                selectAllSubject = connection.prepareStatement(builder.build());
            }

            selectAllSubject.setInt(1, subject.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            ResultSet set = selectAllSubject.executeQuery();
            while (set.next()) {
                SchoolClass schoolClass = new SchoolClass(set.getInt(1), set.getString(2), subject);
                classes.add(schoolClass);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return classes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SchoolClass> getById(int id) throws DataUpdateException, DataAccessException {
        SchoolClass schoolClass;

        try {
            if (selectId == null || selectId.isClosed()) {
                SqlBuilder builder = new SqlBuilder("class", StatementType.SELECT)
                        .addColumns("class.className,subject.id,subject.subjectName")
                        .addWhereClause("class.id=?")
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "class.subjectId=subject.id"));
                selectId = connection.prepareStatement(builder.build());
            }

            selectId.setInt(1, id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            ResultSet set = selectId.executeQuery();
            set.next();
            schoolClass = new SchoolClass(id, set.getString(1), new Subject(set.getInt(2), set.getString(3)));
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        if (schoolClass == null) {
            return Optional.empty();
        } else {
            return Optional.of(schoolClass);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int insertClass(SchoolClass schoolClass) throws DataAccessException, DataUpdateException {
        int id = -1;

        try {
            if (insert == null || insert.isClosed()) {
                SqlBuilder builder = new SqlBuilder("class", StatementType.INSERT)
                        .addColumns("className,subjectId")
                        .addValues("?", "?");
                insert = connection.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
            }

            insert.setString(1, schoolClass.name);
            insert.setInt(2, schoolClass.subject.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            insert.executeUpdate();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
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
    public boolean updateClass(SchoolClass schoolClass) throws DataUpdateException, DataAccessException {
        boolean success;

        try {
            if (update == null || update.isClosed()) {
                SqlBuilder builder = new SqlBuilder("class", StatementType.UPDATE)
                        .addSetClauses("className=?", "subjectId=?")
                        .addWhereClause("id=?");
                update = connection.prepareStatement(builder.build());
            }

            update.setString(1, schoolClass.name);
            update.setInt(2, schoolClass.subject.id);
            update.setInt(3, schoolClass.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            update.executeUpdate();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteClass(SchoolClass schoolClass) throws DataUpdateException, DataAccessException {
        boolean success;

        try {
            if (delete == null || delete.isClosed()) {
                SqlBuilder builder = new SqlBuilder("class", StatementType.DELETE)
                        .addWhereClause("id=?");
                delete = connection.prepareStatement(builder.build());
            }

            delete.setInt(1, schoolClass.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
        }

        try {
            delete.executeQuery();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return success;
    }


}
