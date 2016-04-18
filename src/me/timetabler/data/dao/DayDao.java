package me.timetabler.data.dao;

import me.timetabler.data.Day;
import me.timetabler.data.exceptions.DataAccessException;

import java.util.Optional;

/**
 * {@inheritDoc}
 * This dao will return day data.
 */
public interface DayDao extends Dao<Day> {
    /**
     * Returns an optional which can contain a Day object which has the given name. This method should not be used if
     * getById can be used as ints are faster to compare than Strings.
     * @param name The name of the day.
     * @return An optional which can contain a Day object which has the given name.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<Day> getByName(String name) throws DataAccessException;
}