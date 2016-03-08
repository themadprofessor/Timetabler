package me.timetabler.data.dao;

import me.timetabler.data.SchoolClass;
import me.timetabler.data.Subject;

import java.util.List;
import java.util.Optional;

/**
 * The interface between a data source and the program. The dao will manipulate school class data.
 */
public interface SchoolClassDao {
    /**
     * Returns a list of all the classes. If there are no classes, an empty list will be returned. The type of list
     * is to be determined by the implementation.
     *
     * @return A list of all the classes, which can be empty.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<SchoolClass> getAllClasses();

    /**
     * Returns a list of all the classes of the given subject. If there are no classes of the given subject, an empty
     * ist will be returned. The type of list is to be determined by the implementation.
     *
     * @param subject The subject of classes to find.
     * @return A list of all the classes of the give subject, which can be empty.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<SchoolClass> getAllBySubject(Subject subject);

    /**
     * Returns the school class which has the given id. The optional will be empty if the id does not reference any
     * school class.
     *
     * @param id The id of the class to be found.
     * @return An optional containing the school class if it exists, or empty if it does not.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<SchoolClass> getById(int id);

    /**
     * Inserts the given school class in to the data store. If the school class was successfully entered in to the data
     * store, it will return true, and false if it could not.
     *
     * @param schoolClass The school class to be added to the data store.
     * @return Returns the id of the school class, or -1 if it failed to add the school class.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    int insertClass(SchoolClass schoolClass);

    /**
     * Updates the given school class in the data store. The id of the given school class will be the entry in the store
     * to be updated with the name of the given school class. It returns true if the entry was successfully updated and
     * false if it failed.
     *
     * @param schoolClass The entry to be updated. The entry with the id of the given school class will be updated with
     *                    the given school class's name.
     * @return True if the entry successfully updated and false if it failed.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean updateClass(SchoolClass schoolClass);

    /**
     * Deletes the given school class from the data source. This returns true if the entry as successfully removed from the
     * data source and false if it failed.
     *
     * @param schoolClass The entry to to be deleted from the data source.
     * @return Returns true if the entry was successfully removed from the data source and false if it fails.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean deleteClass(SchoolClass schoolClass);
}
