package me.timetabler.data;

/**
 * Created by stuart on 08/03/16.
 */
public class SubjectSet {
    public int id;
    public Subject subject;
    public LearningSet learningSet;
    public SchoolYear schoolYear;
    public int hoursPerWeek;

    public SubjectSet() {
    }

    public SubjectSet(int id, Subject subject, LearningSet learningSet, SchoolYear schoolYear, int hoursPerWeek) {
        this.id = id;
        this.subject = subject;
        this.learningSet = learningSet;
        this.schoolYear = schoolYear;
        this.hoursPerWeek = hoursPerWeek;
    }
}
