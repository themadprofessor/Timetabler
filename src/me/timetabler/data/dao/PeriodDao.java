package me.timetabler.data.dao;

import me.timetabler.data.Day;
import me.timetabler.data.Period;

import java.time.LocalTime;
import java.util.List;

/**
 * The interface between a data source and the program. This dao will return period data. All list implementations
 * are to be determined by the implementation.
 */
public interface PeriodDao {
    /**
     * Returns a list containing all periods present in the data source. The list implementation is to be determined by
     * the implementation.
     * @return A list of all periods.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<Period> getAll();

    /**
     * Returns a list containing all the periods in a given day. The list implementation is to be determined by the
     * implementation.
     * @param day The day to list the periods from.
     * @return A list of all periods on the given day.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<Period> getAllByDay(Day day);

    /**
     * Returns a list of a periods which starts at the given time. The list implementation is to be determined by the
     * implementation.
     * @param time The start time of the required periods.
     * @return A list of all periods which starts at the given time.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<Period> getByStartTime(LocalTime time);

    /**
     * Returns a list of a periods which ends at the given time. The list implementation is to be determined by the
     * implementation.
     * @param time The end time of the required periods.
     * @return A list of all periods which ends at the given time.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<Period> getByEndTime(LocalTime time);
}
