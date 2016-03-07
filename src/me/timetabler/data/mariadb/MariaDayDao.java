package me.timetabler.data.mariadb;

import me.timetabler.data.Day;
import me.timetabler.data.dao.DayDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.util.Log;
import me.util.MapBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by stuart on 04/03/16.
 */
public class MariaDayDao implements DayDao {
    protected Connection connection;
    private PreparedStatement getId;
    private PreparedStatement getName;

    public MariaDayDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Day> getById(int id) {
        Day day = null;

        try {
            if (getId == null || getId.isClosed()) {
                MapBuilder<String, String> builder = new MapBuilder<>(new HashMap<>());
                getId = connection.prepareStatement(StatementType.SELECT.getSql(builder.put("table", "dayOfWeek").put("columns", "dayOfWeek").put("where", "id=?").build()));
            }
            getId.setInt(1, id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            ResultSet set = getId.executeQuery();
            set.next();
            day = new Day(id, set.getString(1));
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataUpdateException(e);
        }

        if (day == null) {
            return Optional.empty();
        } else {
            return Optional.of(day);
        }
    }

    @Override
    public Optional<Day> getByName(String name) {
        Day day = null;

        try {
            if (getName == null || getName.isClosed()) {
                MapBuilder<String, String> builder = new MapBuilder<>(new HashMap<>());
                getName = connection.prepareStatement(StatementType.SELECT.getSql(builder.put("table", "dayOfWeek").put("columns", "id").put("where", "dayOfWeek=?").build()));
            }
            getName.setString(1, name);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            ResultSet set = getName.executeQuery();
            set.next();
            day = new Day(set.getInt(1), name);
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataUpdateException(e);
        }

        if (day == null) {
            return Optional.empty();
        } else {
            return Optional.of(day);
        }
    }
}
