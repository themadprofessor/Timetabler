package me.timetabler.data;

import java.time.LocalTime;

/**
 * Created by stuart on 04/03/16.
 */
public class Period {
    public int id;
    public Day day;
    public LocalTime startTime;
    public LocalTime endTime;

    public Period() {
    }

    public Period(int id, Day day, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
