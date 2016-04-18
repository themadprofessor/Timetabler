package me.timetabler.data.mariadb;

import me.timetabler.data.Day;
import me.timetabler.data.dao.DayDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.sql.SqlBuilder;
import me.timetabler.data.sql.StatementType;
import me.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * {@inheritDoc}
 * The dao will utilise a MariaDB database as it data source.
 */
public class MariaDayDao implements DayDao {
    /**
     * The connection to the database, which all the PreparedStatements rely on.
     */
    protected Connection connection;

    /**
     * A PreparedStatement which is used to select a classroom with a given id from the database.
     */
    private PreparedStatement selectId;

    /**
     * A PreparedStatement which is used to select a classroom with a given name from the database.
     */
    private PreparedStatement selectName;

    /**
     * A PreparedStatement which is used to select all classrooms from the database.
     */
    private PreparedStatement selectAll;

    public MariaDayDao(Connection connection) {
        this.connection = connection;
    }


    /**
     * {@inheritDoc}
     * This method will get the day data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager
     */
    @Override
    public List<Day> getAll() throws DataAccessException {
        ArrayList<Day> days = new ArrayList<>(7);

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("dayOfWeek", StatementType.SELECT)
                        .addColumns("id", "dayOfWeek");
                selectAll = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Day day = new Day(set.getInt(1), set.getString(2));
                days.add(day);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return Collections.unmodifiableList(days);
    }

    /**
     * {@inheritDoc}
     * This method will get the day data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager
     */
    @Override
    public Optional<Day> getById(int id) throws DataAccessException {
        Day day = null;

        if (id >= 0) {
            try {
                if (selectId == null || selectId.isClosed()) {
                    SqlBuilder builder = new SqlBuilder("dayOfWeek", StatementType.SELECT)
                            .addColumn("dayOfWeek")
                            .addWhereClause("id=?");
                    selectId = connection.prepareStatement(builder.build());
                }
                selectId.setInt(1, id);

                ResultSet set = selectId.executeQuery();
                if (set.next()) {
                    day = new Day(id, set.getString(1));
                }
                set.close();
            } catch (SQLException e) {
                Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
                throw new DataAccessException(e);
            }
        }

        return Optional.ofNullable(day);
    }


    /**
     * {@inheritDoc}
     * This method will get the day data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager
     */
    @Override
    public Optional<Day> getByName(String name) throws DataAccessException {
        Day day = null;

        if (name != null && !name.isEmpty()) {
            try {
                if (selectName == null || selectName.isClosed()) {
                    SqlBuilder builder = new SqlBuilder("dayOfWeek", StatementType.SELECT)
                            .addColumn("id")
                            .addWhereClause("dayOfWeek=?");
                    selectName = connection.prepareStatement(builder.build());
                }
                selectName.setString(1, name);

                ResultSet set = selectName.executeQuery();
                if (set.next()) {
                    day = new Day(set.getInt(1), name);
                }
                set.close();
            } catch (SQLException e) {
                Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
                throw new DataAccessException(e);
            }
        }

        return Optional.ofNullable(day);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            selectAll.close();
            selectId.close();
            selectName.close();
        } catch (SQLException e) {
            Log.error(e);
        }
    }
}
