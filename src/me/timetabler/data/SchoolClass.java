package me.timetabler.data;

/**
 * A class at the school.
 */
public class SchoolClass {
    /**
     * The unique subject name.
     */
    public int subjectId;

    /**
     * The name of the subject.
     */
    public String name;

    /**
     * The unique id of this class.
     */
    public int id;

    public SchoolClass() {
    }

    public SchoolClass(int subjectId, String name, int id) {
        this.subjectId = subjectId;
        this.name = name;
        this.id = id;
    }
}
