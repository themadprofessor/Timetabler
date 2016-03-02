package me.timetabler.data;

/**
 * A member of staff at the school.
 */
public class Staff {
    /**
     * The unique staff id.
     */
    public int id;

    /**
     * The name of the member of staff.
     */
    public String name;

    public int subjectId;

    /**
     * Initialises the class, but not its internal variables.
     */
    public Staff() {
    }

    public Staff(int id, String name, int subjectId) {
        this.id = id;
        this.name = name;
        this.subjectId = subjectId;
    }
}
