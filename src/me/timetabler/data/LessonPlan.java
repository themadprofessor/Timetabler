package me.timetabler.data;

/**
 * A lesson which has an allocated member of staff, classroom, period and class(subjectSet).
 */
public class LessonPlan implements DataType {
    /**
     * The id of this lesson. The must be unique across all lessonPlans.
     */
    public int id;

    /**
     * The member of staff which will teach this lesson.
     */
    public Staff staff;

    /**
     * The classroom this lesson will be taught in.
     */
    public Classroom classroom;

    /**
     * The period when this lesson will be taught.
     */
    public Period period;

    /**
     * The subjectSet who will be taught in this lesson.
     */
    public SubjectSet subjectSet;

    /**
     * The default constructor, which does not initialise the members of the object.
     */
    public LessonPlan() {
    }

    /**
     * A constructor which initalises the members of the object with the given parameters.
     * @param id The if of the lesson.
     * @param staff The member of staff who will teach the lesson.
     * @param classroom The classroom the lesson will be taught in.
     * @param period The period when the lesson will be taught.
     * @param subjectSet The subjectSet who will be taught in the lesson.
     */
    public LessonPlan(int id, Staff staff, Classroom classroom, Period period, SubjectSet subjectSet) {
        this.id = id;
        this.staff = staff;
        this.classroom = classroom;
        this.period = period;
        this.subjectSet = subjectSet;
    }
}
