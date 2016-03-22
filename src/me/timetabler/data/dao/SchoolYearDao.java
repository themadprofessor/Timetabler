package me.timetabler.data.dao;

import me.timetabler.data.SchoolYear;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;

import java.util.List;
import java.util.Optional;

/**
 * The interface between a data source and the program. This dao will manipulate school year data.
 */
public interface SchoolYearDao {
        /**
     * Returns a list of all the schoolYears. If there are no schoolYears, an empty list will be returned. The type of list
     * is to be determined by the implementation.
     *
     * @return A list of all the schoolYears, which can be empty.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<SchoolYear> getAllSchoolYears() throws DataAccessException;

    /**
     * Returns the schoolYear which has the given id. The optional will be empty if the id does not reference any schoolYears.
     *
     * @param id The id of the schoolYear to be found.
     * @return An optional containing the schoolYear if it exists, or empty if it does not.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<SchoolYear> getById(int id) throws DataUpdateException, DataAccessException;

    /**
     * Inserts the given schoolYear in to the data store. If the schoolYear was successfully entered in to the data store, it
     * will return true, and false if it could not.
     *
     * @param schoolYear The schoolYear to be added to the data store.
     * @return Returns the id of the schoolYear, or -1 if it failed to add the schoolYear.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    int insert(SchoolYear schoolYear) throws DataUpdateException, DataAccessException;

    /**
     * Updates the given schoolYear in the data store. The id of the given schoolYear will be the entry in the store to be
     * updated with the name of the given schoolYear
     * It returns true if the entry was successfully updated and false if it failed.
     *
     * @param schoolYear The entry to be updated. The entry with the id of the given schoolYear will be updated with the
     *                given schoolYear's name.
     * @return True if the entry successfully updated and false if it failed.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean update(SchoolYear schoolYear) throws DataUpdateException, DataAccessException;

    /**
     * Deletes the given schoolYear from the data source. This returns true if the schoolYear was successfully removed from the
     * data source and false if it failed.
     *
     * @param schoolYear The entry to to be deleted from the data source.
     * @return Returns true if the entry was successfully removed from the data source and false if it fails.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean delete(SchoolYear schoolYear) throws DataUpdateException, DataAccessException;
}
