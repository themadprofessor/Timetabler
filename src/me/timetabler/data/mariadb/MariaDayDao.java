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
 */
public class MariaDayDao implements DayDao {
    protected Connection connection;
    private PreparedStatement selectId;
    private PreparedStatement selectName;
    private PreparedStatement selectAll;

    public MariaDayDao(Connection connection) {
        this.connection = connection;
    }


    /**
     * {@inheritDoc}
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
     */
    @Override
    public Optional<Day> getById(int id) throws DataAccessException {
        Day day = null;

        try {
            if (selectId == null || selectId.isClosed()) {
                SqlBuilder builder = new SqlBuilder("dayOfWeek", StatementType.SELECT)
                        .addColumn("dayOfWeek")
                        .addWhereClause("id=?");
                selectId = connection.prepareStatement(builder.build());
            }
            selectId.setInt(1, id);

            ResultSet set = selectId.executeQuery();
            set.next();
            day = new Day(id, set.getString(1));
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        if (day == null) {
            return Optional.empty();
        } else {
            return Optional.of(day);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Day> getByName(String name) throws DataAccessException {
        Day day = null;

        try {
            if (selectName == null || selectName.isClosed()) {
                SqlBuilder builder = new SqlBuilder("dayOfWeek", StatementType.SELECT)
                        .addColumn("id")
                        .addWhereClause("dayOfWeek=?");
                selectName = connection.prepareStatement(builder.build());
            }
            selectName.setString(1, name);

            ResultSet set = selectName.executeQuery();
            set.next();
            day = new Day(set.getInt(1), name);
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        if (day == null) {
            return Optional.empty();
        } else {
            return Optional.of(day);
        }
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
