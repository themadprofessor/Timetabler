package me.timetabler.data.dao;

import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;

import java.io.File;

/**
 * {@inheritDoc}
 * The data which this dao manipulates is mutable.
 */
public interface MutableDao<T> extends Dao<T> {
    /**
     * Inserts the given data in to the data store. If the data was successfully entered in to the data store, it
     * will return the id of the data, and -1 if it could not.
     *
     * @param data The data to be added to the data store.
     * @return Returns the id of the data, or -1 if it failed to add the data.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    int insert(T data) throws DataAccessException, DataUpdateException;

    /**
     * Updates the given data in the data store. The id of the given data will be the entry in the store to be
     * updated with rest of the given data
     * It returns true if the entry was successfully updated and false if it failed.
     *
     * @param data The entry to be updated. The entry with the id of the given schoolYear will be updated with the
     *                given data's name.
     * @return True if the entry successfully updated and false if it failed.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean update(T data) throws DataAccessException, DataUpdateException;

    /**
     * Deletes the given data from the data store. This returns true if the data was successfully removed from the
     * data source and false if it failed.
     *
     * @param data The entry to to be deleted from the data store.
     * @return Returns true if the entry was successfully removed from the data store and false if it fails.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean delete(T data) throws DataAccessException, DataUpdateException;

    /**
     * Loads the data from the given file into the data store. This returns true if the data was successfully loaded,
     * and false if it failed.
     *
     * @param file The file to be loaded into the data store.
     * @return True if the data was successfully loaded into the data store, and false if it failed.
     * @throws DataAccessException Thrown if the data cannot be accessed.
     * @throws DataUpdateException Thrown if the data cannot be modified.
     */
    boolean loadFile(File file) throws DataAccessException, DataUpdateException;
}
