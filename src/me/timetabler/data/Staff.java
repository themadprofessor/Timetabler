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
    public int name;

    /**
     * Initialises the class, but not its internal variables.
     */
    public Staff() {
    }

    /**
     * Initialises the class.
     * @param id The unique staff id.
     * @param name The name of the member of staff.
     */
    public Staff(int id, int name) {
        this.id = id;
        this.name = name;
    }
}
