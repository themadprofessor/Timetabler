package me.timetabler.data;

/**
 * Created by stuart on 08/03/16.
 */
public class LessonPlan {
    public int id;
    public SchoolClass schoolClass;
    public Staff staff;
    public Classroom classroom;
    public Period period;
    public SubjectSet subjectSet;

    public LessonPlan() {
    }

    public LessonPlan(int id, SchoolClass schoolClass, Staff staff, Classroom classroom, Period period, SubjectSet subjectSet) {
        this.id = id;
        this.schoolClass = schoolClass;
        this.staff = staff;
        this.classroom = classroom;
        this.period = period;
        this.subjectSet = subjectSet;
    }
}
