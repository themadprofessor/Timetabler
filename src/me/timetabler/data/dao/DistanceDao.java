package me.timetabler.data.dao;

import me.timetabler.data.Classroom;
import me.timetabler.data.Distance;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;

import java.util.List;
import java.util.Optional;

/**
 * The interface between a data source and the program. This dao will manipulate distance data.
 */
public interface DistanceDao extends Dao {
    /**
     * Returns a list of all the distances. If there are no distances, an empty list will be returned. The type of list
     * is to be determined by the implementation.
     *
     * @return A list of all the distances, which can be empty.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<Distance> getAll() throws DataAccessException;

    /**
     * Returns the distance which has the given id. The optional will be empty if the id does not reference any distances.
     *
     * @param id The id of the distance to be found.
     * @return An optional containing the distance if it exists, or empty if it does not.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<Distance> getById(int id) throws DataAccessException;

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

    /**
     * Inserts the given distance in to the data store. If the distance was successfully entered in to the data store, it
     * will return true, and false if it could not.
     *
     * @param distance The distance to be added to the data store.
     * @return Returns the id of the distance, or -1 if it failed to add the distance.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    int insertDistance(Distance distance) throws DataUpdateException, DataAccessException;

    /**
     * Updates the given distance in the data store. The id of the given distance will be the entry in the store to be
     * updated with the name of the given distance
     * It returns true if the entry was successfully updated and false if it failed.
     *
     * @param distance The entry to be updated. The entry with the id of the given distance will be updated with the
     *                given distance's name.
     * @return True if the entry successfully updated and false if it failed.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean updateDistance(Distance distance) throws DataUpdateException, DataAccessException;

    /**
     * Deletes the given distance from the data source. This returns true if the distance was successfully removed from the
     * data source and false if it failed.
     *
     * @param distance The entry to to be deleted from the data source.
     * @return Returns true if the entry was successfully removed from the data source and false if it fails.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean deleteDistance(Distance distance) throws DataUpdateException, DataAccessException;
}
