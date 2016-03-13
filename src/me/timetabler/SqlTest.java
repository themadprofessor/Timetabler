package me.timetabler;

import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.data.mariadb.MariaDaoManager;
import me.util.Log;
import me.util.LogLevel;
import me.util.MapBuilder;

import java.util.HashMap;

/**
 * Created by stuart on 29/02/16.
 */
public class SqlTest {
    public static void main(String[] args) throws DataConnectionException, DataAccessException {
        Log.LEVEL = LogLevel.VERBOSE;

        MapBuilder<String, String> config = new MapBuilder<>(new HashMap<>());
        DaoManager manager = new MariaDaoManager(config.put("type", "MARIADB").put("addr", "127.0.0.1").put("port", "3306").put("database", "school").build());

    }
}
