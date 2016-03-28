package me.timetabler.data.dao;

import me.timetabler.data.Building;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;

import java.util.List;
import java.util.Optional;

/**
 * The interface between a data source and the program. This dao will manipulate building data.
 */
public interface BuildingDao extends Dao {
    /**
     * Returns a list of all the buildings. If there are no buildings, an empty list will be returned. The type of list
     * is to be determined by the implementation.
     *
     * @return A list of all the buildings, which can be empty.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<Building> getAll() throws DataAccessException;

    /**
     * Returns the building which has the given id. The optional will be empty if the id does not reference any buildings.
     *
     * @param id The id of the building to be found.
     * @return An optional containing the building if it exists, or empty if it does not.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<Building> getById(int id) throws DataUpdateException, DataAccessException;

    /**
     * Inserts the given building in to the data store. If the building was successfully entered in to the data store, it
     * will return true, and false if it could not.
     *
     * @param building The building to be added to the data store.
     * @return Returns the id of the building, or -1 if it failed to add the building.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    int insert(Building building) throws DataUpdateException, DataAccessException;

    /**
     * Updates the given building in the data store. The id of the given building will be the entry in the store to be
     * updated with the name of the given building
     * It returns true if the entry was successfully updated and false if it failed.
     *
     * @param building The entry to be updated. The entry with the id of the given building will be updated with the
     *                given building's name.
     * @return True if the entry successfully updated and false if it failed.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean update(Building building) throws DataUpdateException, DataAccessException;

    /**
     * Deletes the given building from the data source. This returns true if the building was successfully removed from the
     * data source and false if it failed.
     *
     * @param building The entry to to be deleted from the data source.
     * @return Returns true if the entry was successfully removed from the data source and false if it fails.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean delete(Building building) throws DataUpdateException, DataAccessException;
}
