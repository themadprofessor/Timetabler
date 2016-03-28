package me.timetabler.data.dao;

import me.timetabler.data.LearningSet;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;

import java.util.List;
import java.util.Optional;

/**
 * The interface between a data source and the program. This dao will manipulate subject data.
 */
public interface LearningSetDao extends Dao {
    /**
     * Returns a list of all the learning sets. If there are no learning sets, an empty list will be returned. The type
     * of list is to be determined by the implementation.
     *
     * @return A list of all the learning sets, which can be empty.
     */
    List<LearningSet> getAll() throws DataAccessException;

    /**
     * Returns the learning set which has the given id. The optional will be empty if the id does not reference any
     * learning sets.
     *
     * @param id The id of the learning set to be found.
     * @return An optional containing the learning set if it exists, or empty if it does not.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<LearningSet> getById(int id) throws DataAccessException;

    /**
     * Inserts the given learning set in to the data store. If the learning set was successfully entered in to the data
     * store, it will return true, and false if it could not.
     *
     * @param set The learning set to be added to the data store.
     * @return Returns the id of the learning set, or -1 if it failed to add the learning set.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    int insert(LearningSet set) throws DataAccessException, DataUpdateException;

    /**
     * Updates the given learning set in the data store. The id of the given learning set will be the entry in the
     * store to be updated with the name of the given learning set It returns true if the entry was successfully
     * updated and false if it failed.
     *
     * @param set The entry to be updated. The entry with the id of the given learning set will be updated with the
     *                given learning set's name.
     * @return True if the entry successfully updated and false if it failed.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean update(LearningSet set) throws DataAccessException, DataUpdateException;

    /**
     * Deletes the given entry from the data source. This returns true if the entry as successfully removed from the
     * data source and false if it failed.
     *
     * @param set The entry to to be deleted from the data source.
     * @return Returns true if the entry was successfully removed from the data source and false if it fails.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean delete(LearningSet set) throws DataAccessException, DataUpdateException;
}
