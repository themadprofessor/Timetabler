package me.timetabler.data;

/**
 * Created by stuart on 08/03/16.
 */
public class SubjectSet {
    public int id;
    public Subject subject;
    public LearningSet learningSet;
    public SchoolYear schoolYear;

    public SubjectSet() {
    }

    public SubjectSet(int id, Subject subject, LearningSet learningSet, SchoolYear schoolYear) {
        this.id = id;
        this.subject = subject;
        this.learningSet = learningSet;
        this.schoolYear = schoolYear;
    }
}
