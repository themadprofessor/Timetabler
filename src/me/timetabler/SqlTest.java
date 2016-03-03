package me.timetabler;

import me.timetabler.data.SchoolClass;
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

        SchoolClass schoolClass = new SchoolClass();
        schoolClass.name = "7Maths2";
        schoolClass.subjectId = maths.id;
        schoolClass.id = manager.getSchoolClassDao().insertClass(schoolClass);

        List<SchoolClass> all = manager.getSchoolClassDao().getAllClasses();
        List<SchoolClass> allMaths = manager.getSchoolClassDao().getAllBySubject(maths);
        schoolClass = manager.getSchoolClassDao().getById(schoolClass.id).get();
        schoolClass.name = "8Maths2";
        manager.getSchoolClassDao().updateClass(schoolClass);
        manager.getSchoolClassDao().deleteClass(schoolClass);
    }
}
