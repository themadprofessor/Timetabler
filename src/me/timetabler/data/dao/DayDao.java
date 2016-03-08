package me.timetabler.data.dao;

import me.timetabler.data.Day;

import java.util.List;
import java.util.Optional;

/**
 * The interface between a data source and the program. This dao will return day data. All list implementations
 * are to be determined by the implementation.
 */
public interface DayDao {
    /**
     * Returns a list of all days of the week as Day objects. The list will be unmodifiable and the type will be
     * determined by the implementation.
     * @return A list of the days of the week.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<Day> getAll();

    /**
     * Returns an optional which can contain a Day object which is identified by the given id. This method should be
     * used over getByName where possible as ints are faster to compare than Strings.
     * @param id The id of the day.
     * @return An optional which can contain the Day object identified by the given id.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<Day> getById(int id);

    /**
     * Returns an optional which can contain a Day object which has the given name. This method should not be used if
     * getById can be used as ints are faster to compare than Strings.
     * @param name The name of the day.
     * @return An optional which can contain a Day object which has the given name.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<Day> getByName(String name);
}