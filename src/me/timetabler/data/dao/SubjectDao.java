package me.timetabler.data.dao;

import me.timetabler.data.Subject;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;

import java.util.List;
import java.util.Optional;

/**
 * The interface between a data source and the program. This dao will manipulate subject data.
 */
public interface SubjectDao extends Dao {
    /**
     * Returns a list of all the subjects. If there are no subjects, an empty list will be returned. The type of list
     * is to be determined by the implementation.
     *
     * @return A list of all the subjects, which can be empty.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<Subject> getAll() throws DataAccessException;

    /**
     * Returns the subject which has the given id. The optional will be empty if the id does not reference any subjects.
     *
     * @param id The id of the subject to be found.
     * @return An optional containing the subject if it exists, or empty if it does not.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<Subject> getById(int id) throws DataAccessException;

    /**
     * Returns the subject which has the given name. The optional will be empty if the name does not reference any subjects.
     * Use getById where possible as it is faster to compare and integer than a String.
     *
     * @param name The name of the subject to be found.
     * @return An optional containing the subject if it exists, or empty if it does not.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<Subject> getByName(String name) throws DataAccessException;

    /**
     * Inserts the given subject in to the data store. If the subject was successfully entered in to the data store, it
     * will return true, and false if it could not.
     *
     * @param subject The subject to be added to the data store.
     * @return Returns the id of the subject, or -1 if it failed to add the subject.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    int insertSubject(Subject subject) throws DataUpdateException, DataAccessException;

    /**
     * Updates the given subject in the data store. The id of the given subject will be the entry in the store to be
     * updated with the name of the given subject
     * It returns true if the entry was successfully updated and false if it failed.
     *
     * @param subject The entry to be updated. The entry with the id of the given subject will be updated with the
     *                given subject's name.
     * @return True if the entry successfully updated and false if it failed.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean updateSubject(Subject subject) throws DataUpdateException, DataAccessException;

    /**
     * Deletes the given subject from the data source. This returns true if the subject was successfully removed from the
     * data source and false if it failed.
     *
     * @param subject The entry to to be deleted from the data source.
     * @return Returns true if the entry was successfully removed from the data source and false if it fails.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean deleteSubject(Subject subject) throws DataUpdateException, DataAccessException;
}
