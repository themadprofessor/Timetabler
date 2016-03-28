package me.timetabler.data.dao;

import me.timetabler.data.Building;
import me.timetabler.data.Classroom;
import me.timetabler.data.Subject;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;

import java.util.List;
import java.util.Optional;

/**
 * The interface between a data source and the program. This dao will manipulate classroom data.
 */
public interface ClassroomDao extends Dao {
    /**
     * Returns a list of all the classrooms. If there are no classrooms, an empty list will be returned. The type of list
     * is to be determined by the implementation.
     *
     * @return A list of all the classrooms, which can be empty.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<Classroom> getAll() throws DataAccessException;

    /**
     * Returns a list of all the classrooms where a given subject is taught. If there are no classrooms where the given
     * subject is taught, an empty list will be returned. The type of list is to be determined by the implementation.
     *
     * @param subject The subject classrooms to find.
     * @return A list of all the classrooms where the given subject is taught, which can be empty.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<Classroom> getBySubject(Subject subject) throws DataAccessException;

    /**
     * Returns a list of all the classrooms in the given building. If there are no classrooms in the given building or
     * if the building does not exist, an empty list will be returned. The type of list is to be determined by the
     * implementation.
     * @param building The building to find the classrooms within.
     * @return A list of all the classrooms which are in the given building, which can be empty.
     * @throws DataAccessException Thrown if the data cannot be accessed.
     */
    List<Classroom> getByBuilding(Building building) throws DataAccessException;

    /**
     * Returns the classroom which has the given id. The optional will be empty if the id does not reference any classrooms.
     *
     * @param id The id of the classroom to be found.
     * @return An optional containing the classroom if it exists, or empty if it does not.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<Classroom> getById(int id) throws DataAccessException;

    /**
     * Inserts the given classroom in to the data store. If the classroom was successfully entered in to the data store, it
     * will return true, and false if it could not.
     *
     * @param classroom The classroom to be added to the data store.
     * @return Returns the id of the classroom, or -1 if it failed to add the classroom.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    int insert(Classroom classroom) throws DataAccessException, DataUpdateException;


    /**
     * Updates the given classroom in the data store. The id of the given classroom will be the entry in the store to be
     * updated with the name of the given classroom
     * It returns true if the entry was successfully updated and false if it failed.
     *
     * @param classroom The entry to be updated. The entry with the id of the given classroom will be updated with the
     *                given classroom's name.
     * @return True if the entry successfully updated and false if it failed.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean update(Classroom classroom) throws DataAccessException, DataUpdateException;

    /**
     * Updates the given classroom in the data store. The id of the given classroom will be the entry in the store to be
     * updated with the name of the given classroom
     * It returns true if the entry was successfully updated and false if it failed.
     *
     * @param classroom The entry to be updated. The entry with the id of the given classroom will be updated with the
     *                given classroom's name.
     * @return True if the entry successfully updated and false if it failed.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean delete(Classroom classroom) throws DataAccessException, DataUpdateException;
}
