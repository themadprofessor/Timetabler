package me.timetabler.data;

/**
 * A member of staff at the school.
 */
public class Staff implements DataType {
    /**
     * The unique staff id. This must unique across all staff.
     */
    public int id;

    /**
     * The name of the member of staff.
     */
    public String name;

    /**
     * The subject taught by this staff member.
     */
    public Subject subject;

    /**
     * The max number of hours taught by this member of staff in one week.
     */
    public int hoursPerWeek;

    /**
     * The current hours this member fo staff teaches. <b>To be used by TimetableThread only!</b>
     */
    int currentHoursPerWeek;

    /**
     * The default constructor, which does not initialise any members of the object.
     */
    public Staff() {
    }

    /**
     * A constructor which initialises the members of the object with the given objects.
     * @param id The id of the member of staff.
     * @param name The name of the member of staff.
     * @param subject The subject taught by the member of staff.
     * @param hoursPerWeek The max number of hours taught by this member of staff.
     */
    public Staff(int id, String name, Subject subject, int hoursPerWeek) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.hoursPerWeek = hoursPerWeek;
    }
}
