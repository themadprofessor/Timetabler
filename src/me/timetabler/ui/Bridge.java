package me.timetabler.ui;

import com.google.gson.Gson;
import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.util.Log;

import java.util.List;
import java.util.Map;

/**
 * Created by stuart on 18/09/15.
 */
public class Bridge {
    private Map<String, Subject> subjects;
    private Map<String, Staff> staff;
    private Gson gson;

    public Bridge(Map<String, Subject> subjects, Map<String, Staff> staff) {
        this.subjects = subjects;
        this.staff = staff;
        gson = new Gson();
    }

    public void out(String msg) {
        Log.out(msg);
    }

    public void err(String msg) {
        Log.err(msg);
    }

    public void addSubject(String json) {
        Subject subject = gson.fromJson(json, Subject.class);
        subjects.put(subject.id, subject);
    }

    public void addStaff(String json) {
        Staff staff = gson.fromJson(json, Staff.class);
        this.staff.put(staff.id, staff);
        Log.out(json);
    }
}
