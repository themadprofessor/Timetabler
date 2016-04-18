package me.timetabler;

import me.timetabler.data.Building;
import me.timetabler.data.Classroom;
import me.timetabler.data.Distance;
import me.timetabler.data.Subject;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataConnectionException;
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
        //DaoManager manager = new MariaDaoManager(config.put("type", "MARIADB").put("addr", "127.0.0.1").put("port", "3306").put("database", "school").put("username", "root").put("password", "root").build());
        Classroom room1 = new Classroom(1, "24", new Building(1, "Main"), new Subject(1, "Maths"));
        Classroom room2 = new Classroom(2, "25", new Building(1, "Main"), new Subject(1, "Maths"));
        Distance distance1 = new Distance(0, room1, room2, 5);
        Distance distance2 = new Distance(0, room2, room1, 5);

        if (distance1.equals(distance2)) {
            Log.error("YAY");
        } else {
            Log.error("NO");
        }
    }
}
