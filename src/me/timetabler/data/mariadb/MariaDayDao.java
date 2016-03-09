package me.timetabler.data.mariadb;

import me.timetabler.data.Day;
import me.timetabler.data.dao.DayDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.util.Log;
import me.util.MapBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
                MapBuilder<String, String> builder = new MapBuilder<>(new HashMap<>());
                selectAll = connection.prepareStatement(StatementType.SELECT_ALL.getSql(builder.put("table", "dayOfWeek").put("columns", "id,datOfWeek").build()));
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
                MapBuilder<String, String> builder = new MapBuilder<>(new HashMap<>());
                selectId = connection.prepareStatement(StatementType.SELECT.getSql(builder.put("table", "dayOfWeek").put("columns", "dayOfWeek").put("where", "id=?").build()));
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
                MapBuilder<String, String> builder = new MapBuilder<>(new HashMap<>());
                selectName = connection.prepareStatement(StatementType.SELECT.getSql(builder.put("table", "dayOfWeek").put("columns", "id").put("where", "dayOfWeek=?").build()));
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
}
