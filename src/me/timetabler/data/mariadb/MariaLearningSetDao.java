package me.timetabler.data.mariadb;

import me.timetabler.data.LearningSet;
import me.timetabler.data.dao.LearningSetDao;
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
public class MariaLearningSetDao implements LearningSetDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectId;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    public MariaLearningSetDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LearningSet> getAll() throws DataAccessException {
        ArrayList<LearningSet> sets = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("learningSet", StatementType.SELECT)
                        .addColumns("id", "setName");
                selectAll = connection.prepareStatement(builder.build());
            }

            ResultSet results = selectAll.executeQuery();
            while (results.next()) {
                LearningSet set = new LearningSet(results.getInt(1), results.getString(2));
                sets.add(set);
            }
            results.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return sets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<LearningSet> getById(int id) throws DataAccessException {
        LearningSet set = null;

        try {
            if (selectId == null || selectId.isClosed()) {
                SqlBuilder builder = new SqlBuilder("learningSet", StatementType.SELECT)
                        .addColumn("setName")
                        .addWhereClause("id=?");
                selectId = connection.prepareStatement(builder.build());
            }

            selectId.setInt(1, id);
            ResultSet results = selectId.executeQuery();
            set = new LearningSet(id, results.getString(1));
            results.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        if (set == null) {
            return Optional.empty();
        } else {
            return Optional.of(set);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int insert(LearningSet set) throws DataAccessException, DataUpdateException {
        int id = -1;

        try {
            if (insert == null || insert.isClosed()) {
                SqlBuilder builder = new SqlBuilder("learningSet", StatementType.INSERT)
                        .addColumn("setName")
                        .addValue("?");
                insert = connection.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
                Log.verbose("SQL Insert Statement [" + builder.build() + "] With [" + set.name + "] Parameters");
            }
            insert.setString(1, set.name);
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
            ResultSet idSet = insert.getGeneratedKeys();
            idSet.next();
            id = idSet.getInt(1);
            idSet.close();
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
    public boolean update(LearningSet set) throws DataAccessException, DataUpdateException {
        boolean success = false;

        try {
            if (update == null || update.isClosed()) {
                SqlBuilder builder = new SqlBuilder("learningSet", StatementType.UPDATE)
                        .addSetClause("setName=?")
                        .addWhereClause("id=?");
                update = connection.prepareStatement(builder.build());
            }
            update.setString(1, set.name);
            update.setInt(2, set.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            update.executeUpdate();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwigng a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(LearningSet set) throws DataAccessException, DataUpdateException {
        boolean success = false;

        try {
            if (delete == null || delete.isClosed()) {
                SqlBuilder builder = new SqlBuilder("learningSet", StatementType.DELETE)
                        .addWhereClause("id=?");
                delete = connection.prepareStatement(builder.build());
            }

            delete.setInt(1, set.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            delete.executeUpdate();
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
    public void close() {
        try {
            selectAll.close();
            selectId.close();
            insert.close();
            delete.close();
        } catch (SQLException e) {
            Log.error(e);
        }
    }
}
