package me.timetabler.data.mariadb;

import me.timetabler.data.Building;
import me.timetabler.data.dao.BuildingDao;
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
 * Created by stuart on 13/03/16.
 */
public class MariaBuildingDao implements BuildingDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectId;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    public MariaBuildingDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Building> getAll() throws DataAccessException {
        ArrayList<Building> buildings = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("building", StatementType.SELECT);
                selectAll = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Building building = new Building(set.getInt(1), set.getString(2));
                buildings.add(building);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException");
            throw new DataAccessException(e);
        }

        return buildings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Building> getById(int id) throws DataUpdateException, DataAccessException {
        Building building;

        try {
            if (selectId == null || selectId.isClosed()) {
                SqlBuilder builder = new SqlBuilder("building", StatementType.SELECT)
                        .addColumn("buildingName")
                        .addWhereClause("id=?");
                selectId = connection.prepareStatement(builder.build());
            }

            selectId.setInt(1, id);
            ResultSet set = selectId.executeQuery();
            set.next();
            building = new Building(id, set.getString(1));
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        if (building == null) {
            return Optional.empty();
        } else {
            return Optional.of(building);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int insert(Building building) throws DataUpdateException, DataAccessException {
        int id = -1;

        try {
            if (insert == null || insert.isClosed()) {
                SqlBuilder builder = new SqlBuilder("building", StatementType.INSERT)
                        .addColumn("buildingName")
                        .addValue("?");
                insert = connection.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
            }

            insert.setString(1, building.buildingName);
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
    public boolean update(Building building) throws DataUpdateException, DataAccessException {
        boolean success;

        try {
            if (update == null || update.isClosed()) {
                SqlBuilder builder = new SqlBuilder("building", StatementType.UPDATE)
                        .addSetClause("building=?")
                        .addWhereClause("id=?");
                update = connection.prepareStatement(builder.build());
            }

            update.setInt(2, building.id);
            update.setString(1, building.buildingName);
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
    public boolean delete(Building building) throws DataUpdateException, DataAccessException {
        boolean success;

        try {
            if (delete == null || delete.isClosed()) {
                SqlBuilder builder = new SqlBuilder("building", StatementType.DELETE)
                        .addWhereClause("is=?");
                delete = connection.prepareStatement(builder.build());
            }

            delete.setInt(1, building.id);
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
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            selectAll.close();
            selectId.close();
            insert.close();
            update.close();
            delete.close();
        } catch (SQLException e) {
            Log.error(e);
        }
    }
}
