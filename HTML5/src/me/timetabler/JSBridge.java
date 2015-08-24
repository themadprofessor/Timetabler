package me.timetabler;

import me.util.Log;

import java.util.ArrayList;

/**
 * Created by stuart on 22/07/15.
 */
public class JSBridge {
    public ArrayList<String> strings = new ArrayList<>();

    public void say(String tmp) {
        Log.out(tmp);
    }

    public void addSubject(Subject subject) {
        //Subject subject = new Subject(name, id);
        Log.out(subject.name);
        Controller.subjects.add(subject);
    }

    public void addTeacher(String name, String id) {
        Teacher teacher = new Teacher(name, id);
        Log.out(teacher.name);
        Controller.teachers.add(teacher);
    }
}
