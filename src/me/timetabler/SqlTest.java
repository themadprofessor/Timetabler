package me.timetabler;

import me.timetabler.sql.DbManager;
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
            DbManager manager = new DbManager("jdbc:mysql://127.0.0.1/school", "root", "root", "org.mariadb.jdbc.Driver");
            manager.sendSQL("INSERT INTO subject VALUES (0,\"Maths\");");
            Log.info("Connected To Server!");
            ResultSet set = manager.sendSQL("SELECT * FROM subject;").get();
            Log.info("Sent SQL " + set.getFetchSize());
            while (set.next()) {
                Log.info("ID [" + set.getInt(1) + "] Name [" + set.getString(2) + ']');
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
