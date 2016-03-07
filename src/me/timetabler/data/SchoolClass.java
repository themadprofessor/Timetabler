package me.timetabler.data;

/**
 * A class at the school.
 */
public class SchoolClass {
    /**
     * The unique subject name.
     */
    public Subject subject;

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

    public SchoolClass(int id, String name, Subject subject) {
        this.subject = subject;
        this.name = name;
        this.id = id;
    }
}
