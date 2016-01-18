package me.timetabler;

import javafx.concurrent.Task;
import me.timetabler.data.SchoolClass;
import me.timetabler.data.Staff;
import me.timetabler.data.Subject;

import java.util.Map;

/**
 * Created by stuart on 14/01/16.
 */
public class TimetableGenerationThread extends Task<Timetable> {
    private Map<String, Subject> subjects;
    private Map<String, SchoolClass> classes;
    private Map<String, Staff> staff;

    public TimetableGenerationThread(Map<String, Subject> subjects, Map<String, SchoolClass> classes, Map<String, Staff> staff) {
        this.subjects = subjects;
        this.classes = classes;
        this.staff = staff;
    }

    @Override
    protected Timetable call() throws Exception {
        return null;
    }
}
