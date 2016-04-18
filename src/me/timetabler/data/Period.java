package me.timetabler.data;

import java.time.LocalTime;

/**
 * A period of time for a lesson to be taught.
 */
public class Period implements DataType {
    /**
     * The id of the period. This must be unique across all periods.
     */
    public int id;

    /**
     * The day of the week this period is in.
     */
    public Day day;

    /**
     * The time the period begins.
     */
    public LocalTime startTime;

    /**
     * The time the period ends.
     */
    public LocalTime endTime;

    /**
     * The default constructor, which does not initialise the members fo the object.
     */
    public Period() {
    }

    /**
     * A constructor which initialises all the members of the object with the given parameters.
     * @param id The id of the period.
     * @param day The day of the week this period is in.
     * @param startTime The time the period begins.
     * @param endTime The time the period ends.
     */
    public Period(int id, Day day, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
