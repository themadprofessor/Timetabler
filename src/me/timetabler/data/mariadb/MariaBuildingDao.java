package me.timetabler.data.mariadb;

import me.timetabler.data.Building;
import me.timetabler.data.dao.BuildingDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.timetabler.data.sql.SqlBuilder;
import me.timetabler.data.sql.StatementType;
import me.util.Log;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@inheritDoc}
 * The dao will utilise a MariaDB database as it data source.
 */
public class MariaBuildingDao implements BuildingDao {
    /**
     * The connection used by the dao to create and execute the PreparedStatements.
     */
    protected Connection connection;

    /**
     * A PreparedStatement to select all the buildings in the database.
     */
    private PreparedStatement selectAll;

    /**
     * A PreparedStatement to select a building with a given id from the database.
     */
    private PreparedStatement selectId;

    /**
     * A PreparedStatement to insert a given building into the database.
     */
    private PreparedStatement insert;

    /**
     * A PreparedStatement to update a given building in the database
     */
    private PreparedStatement update;

    /**
     * A PreparedStatement to delete a given building from the database.
     */
    private PreparedStatement delete;

    /**
     * A PreparedStatement to load the building data from a file into the database.
     */
    private PreparedStatement loadFile;

    /**
     * Initialises the dao with the given connection. The statements are initialised when required.
     * @param connection The connection for the dao to use.
     */
    public MariaBuildingDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     * This method will get the building data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
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
     * This method will get the building data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public Optional<Building> getById(int id) throws DataAccessException {
        Building building = null;

        try {
            if (id > -1) {
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
            }
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
     * This method will insert the building data into a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public int insert(Building building) throws DataUpdateException, DataAccessException {
        if (building == null || building.buildingName == null || building.buildingName.isEmpty()) {
            return -1;
        }

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
            return set.getInt(1);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

    }

    /**
     * {@inheritDoc}
     * This method will update the building data in a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public boolean update(Building building) throws DataUpdateException, DataAccessException {
        if (building == null || building.id < 0 || building.buildingName == null || "".equals(building.buildingName)) {
            return false;
        }

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
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * This method delete get the building data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public boolean delete(Building building) throws DataUpdateException, DataAccessException {
        try {
            if (building == null || building.id < 0) {
                return false;
            }

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
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * This method will load the building data into a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public boolean loadFile(File file) throws DataAccessException, DataUpdateException {
        if (file == null) {
            throw new NullPointerException("Data File Cannot Be Null!");
        } else if (!file.exists()) {
            throw new IllegalArgumentException("Data File [" + file.getAbsolutePath() + "] Must Exist!");
        } else if (file.isDirectory()) {
            throw new IllegalArgumentException("Data File [" + file.getAbsolutePath() + "] Must Not Be A Directory!");
        } else if (!file.canRead()) {
            throw new IllegalArgumentException("Data File [" + file.getAbsolutePath() + "] Must Have Read Permissions For User [" + System.getProperty("user.name") + "]!");
        }

        try {
            if (loadFile == null || loadFile.isClosed()) {
                loadFile = connection.prepareStatement("LOAD DATA INFILE '?' INTO TABLE building FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\n';");
            }

            loadFile.setString(1, file.getAbsolutePath());
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            loadFile.executeUpdate();
            return true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            if (selectAll != null) selectAll.close();
            if (selectId != null) selectId.close();
            if (loadFile != null) loadFile.close();
            if (insert != null) insert.close();
            if (update != null) update.close();
            if (delete != null) delete.close();
        } catch (SQLException e) {
            Log.error(e);
        }
    }
}
