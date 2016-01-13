package me.timetabler.ui;

import com.google.gson.Gson;
import me.timetabler.data.SchoolClass;
import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.util.Log;

import java.util.Map;

/**
 * The bridge between the Javascript and the Java objects.
 */
public class Bridge {
    private Map<String, Subject> subjects;
    private Map<String, Staff> staff;
    private Map<String, SchoolClass> classes;
    private Gson gson;

    public Bridge(Map<String, Subject> subjects, Map<String, Staff> staff, Map<String, SchoolClass> classes) {
        this.subjects = subjects;
        this.staff = staff;
        this.classes = classes;
        gson = new Gson();
    }

    public void out(String msg) {
        Log.info("[JAVASCRIPT ]" + msg);
    }

    public void debug(String msg) {
        Log.debug("[JAVASCRIPT] " + msg);
    }

    public void err(String msg) {
        Log.error("[JAVASCRIPT] " + msg);
    }

    public void removeSubject(String id) {
        subjects.remove(id);
    }

    public void add(String type, String json) {
        switch (type) {
            case "Class":
            case "class":
                SchoolClass clazz = gson.fromJson(json, SchoolClass.class);
                this.classes.put(clazz.id, clazz);
                break;
            case "Subject":
            case "subject":
                Subject subject = gson.fromJson(json, Subject.class);
                this.subjects.put(subject.id, subject);
                break;
            case "Staff":
            case "staff":
                Staff st = gson.fromJson(json, Staff.class);
                this.staff.put(st.id, st);
                break;
        }
    }

    public void remove(String type, String id) {
        switch (type) {
            case "Class":
            case "class":
                this.classes.remove(id);
                break;
            case "Subject":
            case "subject":
                this.subjects.remove(id);
                break;
            case "Staff":
            case "staff":
                this.staff.remove(id);
                break;
        }
    }
}
