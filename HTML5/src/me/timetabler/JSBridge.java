package me.timetabler;

import me.util.Log;

/**
 * Created by stuart on 22/07/15.
 */
public class JSBridge {
    public void say(String tmp) {
        Log.out(tmp);
    }

    public void addSubject(String name, String id) {
        Subject subject = new Subject(name, id);
        Log.out(subject.name);
    }

    public void addTeacher(String name, String id) {
        Teacher teacher = new Teacher(name, id);
        Log.out(teacher.name);
    }
}
