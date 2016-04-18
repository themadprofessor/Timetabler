package me.timetabler.data;

/**
 * A year group taught at the school.
 */
public class SchoolYear implements DataType {
    /**
     * The id of the year group. This must be unique across all schoolYears.
     */
    public int id;

    /**
     * The name of the year group.
     */
    public String schoolYearName;

    /**
     * The default constructor which does not initialise any members of the object.
     */
    public SchoolYear() {
    }

    /**
     * A constructor which initialises the members of the object with the given parameters.
     * @param id The id of the year group.
     * @param schoolYearName The name of the year group.
     */
    public SchoolYear(int id, String schoolYearName) {
        this.id = id;
        this.schoolYearName = schoolYearName;
    }
}
