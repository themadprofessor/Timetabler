package me.timetabler.data.mariadb;

import me.timetabler.data.LearningSet;
import me.timetabler.data.dao.LearningSetDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.util.Log;
import me.util.MapBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Override
    public List<LearningSet> getAll() throws DataAccessException {
        ArrayList<LearningSet> sets = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                initStatement(StatementType.SELECT_ALL);
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

    @Override
    public Optional<LearningSet> getById(int id) throws DataAccessException {
        LearningSet set = null;

        try {
            if (selectId == null || selectId.isClosed()) {
                initStatement(StatementType.SELECT);
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

    @Override
    public int insert(LearningSet set) throws DataAccessException, DataUpdateException {
        int id = -1;

        try {
            if (insert == null || insert.isClosed()) {
                initStatement(StatementType.INSERT);
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

    @Override
    public boolean update(LearningSet set) throws DataAccessException, DataUpdateException {
        boolean success = false;

        try {
            if (update == null || update.isClosed()) {
                initStatement(StatementType.UPDATE);
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

    @Override
    public boolean delete(LearningSet set) throws DataAccessException {
        boolean success = false;

        try {
            if (delete == null || delete.isClosed()) {
                initStatement(StatementType.DELETE);
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
        }

        return success;
    }

    private void initStatement(StatementType type) throws DataAccessException {
        assert connection != null;
        MapBuilder<String, String>  builder = new MapBuilder<>(new HashMap<>());

        try {
            switch (type) {
                case SELECT_ALL:
                    selectAll = connection.prepareStatement(type.getSql(builder.put("table", "learningSet")
                            .put("columns", "id,setName")
                            .build()));
                    break;
                case SELECT:
                    selectId = connection.prepareStatement(type.getSql(builder.put("table", "learningSet")
                            .put("columns", "setName")
                            .put("where", "id=?")
                            .build()));
                    break;
                case INSERT:
                    insert = connection.prepareStatement(type.getSql(builder.put("table", "learningSet")
                            .put("columns", "setName")
                            .put("values", "?")
                            .build()), Statement.RETURN_GENERATED_KEYS);
                    break;
                case UPDATE:
                    update = connection.prepareStatement(type.getSql(builder.put("table", "learningSet")
                            .put("set", "setName=?")
                            .put("where", "id=?")
                            .build()));
                    break;
                case DELETE:
                    delete = connection.prepareStatement(type.getSql(builder.put("table", "learningSet")
                            .put("where", "id=?")
                            .build()));
                    break;
                default: throw new AssertionError("Unsupported StatementType [" + type + ']');
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }
    }
}
