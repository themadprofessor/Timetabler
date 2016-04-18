package me.timetabler.data.dao;

import me.timetabler.data.Classroom;
import me.timetabler.data.Distance;
import me.timetabler.data.exceptions.DataAccessException;

import java.util.List;
import java.util.Optional;

/**
 * {@inheritDoc}
 * This dao will manipulate distance data.
 */
public interface DistanceDao extends MutableDao<Distance> {
    /**
     * Returns the distance between the two given rooms. The optional will be empty if either classroom does not exist.
     * @param classroom1 One of the classrooms to find the distance between.
     * @param classroom2 One of the classrooms to find the distance between.
     * @return An optional which contains the distance between the two classes, or empty if either classroom does not exist.
     * @throws DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<Distance> getDistanceBetween(Classroom classroom1, Classroom classroom2) throws DataAccessException;

    /**
     * Returns a list containing all the distances to every other classroom from the given classroom. The list will be
     * empty if the given classroom does not exist.
     * @param classroom The classroom to find all the distances for.
     * @return A list containing all distance between this classroom and all other classrooms, which can be empty.
     * @throws DataAccessException Thrown if the data cannot be accessed.
     */
    List<Distance> getAllDistancesFrom(Classroom classroom) throws DataAccessException;
}
