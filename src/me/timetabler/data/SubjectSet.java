package me.timetabler.data;

/**
 * A class taught at the school. It is an individual set from a year group taught a single subject.
 */
public class SubjectSet implements DataType {
    /**
     * The id of the subjectSet. This must be unique across all subjectSets
     */
    public int id;

    /**
     * The subject taught to this subjectSet.
     */
    public Subject subject;

    /**
     * The set which is taught in this subjectSet.
     */
    public LearningSet learningSet;

    /**
     * The year group taught in this subject set.
     */
    public SchoolYear schoolYear;

    /**
     * The default constructor.
     */
    public SubjectSet() {
    }

    /**
     * A constructor which initialises all the members of the object with the given objects.
     * @param id The id of the subjectSet.
     * @param subject The subject taught in this subjectSet.
     * @param learningSet The set taught by this subjectSet.
     * @param schoolYear The year group taught in this subjectSet.
     */
    public SubjectSet(int id, Subject subject, LearningSet learningSet, SchoolYear schoolYear) {
        this.id = id;
        this.subject = subject;
        this.learningSet = learningSet;
        this.schoolYear = schoolYear;
    }
}
