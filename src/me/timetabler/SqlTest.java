package me.timetabler;

import me.timetabler.sql.Database;
import me.timetabler.sql.DatabaseException;
import me.util.Log;
import me.util.LogLevel;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by stuart on 24/02/16.
 */
public class SqlTest {
    public static void main(String[] args) {
        try {
            Log.LEVEL = LogLevel.VERBOSE;
            Database manager = new Database("127.0.0.1", "school", "root", "root");
            manager.sendSQL("INSERT INTO subject VALUES (0,\"Maths\");");
            Log.info("Connected To Server!");
            ResultSet set = manager.sendSQL("SELECT * FROM subject;").get();
            Log.info("Sent SQL " + set.getFetchSize());
            while (set.next()) {
                Log.info("ID [" + set.getInt(1) + "] Name [" + set.getString(2) + ']');
            }
        } catch (DatabaseException | SQLException e) {
            Log.error(e);
        }
    }
}
