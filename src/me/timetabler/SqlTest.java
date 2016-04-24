package me.timetabler;

import me.timetabler.auth.Authenticator;
import me.timetabler.auth.MariaAuthenticator;
import me.timetabler.data.Building;
import me.timetabler.data.Classroom;
import me.timetabler.data.Distance;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.data.mariadb.MariaDaoManager;
import me.timetabler.data.mariadb.MariaDbManager;
import me.util.Log;
import me.util.LogLevel;
import me.util.MapBuilder;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by stuart on 29/02/16.
 */
public class SqlTest {
    public static void main(String[] args) throws DataConnectionException, DataAccessException {
        Log.LEVEL = LogLevel.VERBOSE;

        MapBuilder<String, String> config = new MapBuilder<>(new HashMap<>());
        config.put("addr", "127.0.0.1").put("port", "3306").put("database", "school").put("exec", "/usr/bin/mysqld")
                .put("args", "--no-defaults --basedir=/usr/ --datadir=./db --socket=./mysqld.sock --bind-address=127.0.0.1 --port=3306");

    }
}
