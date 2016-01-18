package me.timetabler;

/**
 * Created by stuart on 14/01/16.
 */
public class Timetable {
    private Lesson[][] timetable;

    public Timetable(int numberOfDays, int numberOfPeriods) {
        timetable = new Lesson[numberOfDays][numberOfPeriods];
    }

    public Timetable putLesson(int day, int period, Lesson lesson) {
        timetable[day - 1][period - 1] = lesson;
        return this;
    }
}
