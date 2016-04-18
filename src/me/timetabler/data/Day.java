package me.timetabler.data;

/**
 * A day of the week.
 */
public class Day implements DataType {
    /**
     * The id of this day. This must be unique across all days.
     */
    public int id;

    /**
     * The name of the day.
     */
    public String name;

    /**
     * A constructor which initialises the members of the object with the given parameters.
     * @param id The id of the day.
     * @param name The name of the day.
     */
    public Day(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * The default constructor, which does not initialise the members of the object.
     */
    public Day() {
    }
}
