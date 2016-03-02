package me.timetabler;

import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.mariadb.MariaDaoManager;
import me.util.Log;
import me.util.LogLevel;
import me.util.MapBuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Created by stuart on 29/02/16.
 */
public class SqlTest {
    public static void main(String[] args) {
        Log.LEVEL = LogLevel.VERBOSE;

        MapBuilder<String, String> config = new MapBuilder<>(new HashMap<>());
        DaoManager manager = new MariaDaoManager(config.put("type", "MARIADB").put("addr", "127.0.0.1").put("port", "3306").put("database", "school").build());
        Subject maths = new Subject();
        maths.name = "Maths";
        maths.id = manager.getSubjectDao().insertSubject(maths);

        Staff ew = new Staff();
        ew.name = "Mr Smith";
        ew.subjectId = maths.id;
        ew.id = manager.getStaffDao().insertStaff(ew);

        List<Staff> all = manager.getStaffDao().getAllStaff();
        List<Staff> allMaths = manager.getStaffDao().getAllBySubject(maths);
        ew.name = "Mr Joe";
        manager.getStaffDao().updateStaff(ew);
        manager.getStaffDao().deleteStaff(ew);
    }
}
