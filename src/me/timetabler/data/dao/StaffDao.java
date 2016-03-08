package me.timetabler.data.dao;

import me.timetabler.data.Staff;
import me.timetabler.data.Subject;

import java.util.List;
import java.util.Optional;

/**
 * The interface between a data source and the program. The dao will manipulate school class data.
 */
public interface StaffDao {
    /**
     * Returns a list of all the staff. If there are no staff, an empty list will be returned. The type of list is to be
     * determined by the implementation.
     *
     * @return A list of all the staff, which can be empty.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<Staff> getAllStaff();

    /**
     * Returns a list of all the staff who teach the give subject. If there are no staff who teach the given subject, an
     * empty list will be returned. The type of list is to be determined by the implementation.
     *
     * @param subject The subject staff to find.
     * @return A list of all the staff who teach the given subject, which can be empty.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<Staff> getAllBySubject(Subject subject);

    /**
     * Returns the staff which has the given id. The optional will be empty if the id does not reference any staffs.
     *
     * @param id The id of the staff to be found.
     * @return An optional containing the staff if it exists, or empty if it does not.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<Staff> getById(int id);

    /**
     * Inserts the given staff in to the data store. If the staff was successfully entered in to the data store, it
     * will return true, and false if it could not.
     *
     * @param staff The staff to be added to the data store.
     * @return Returns the id of the staff, or -1 if it failed to add the staff.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    int insertStaff(Staff staff);

    /**
     * Updates the given staff in the data store. The id of the given staff will be the entry in the store to be
     * updated with the name of the given staff.
     * It returns true if the entry was successfully updated and false if it failed.
     *
     * @param staff The entry to be updated. The entry with the id of the given staff will be updated with the
     *                given staff's name.
     * @return True if the entry successfully updated and false if it failed.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean updateStaff(Staff staff);

    /**
     * Deletes the given staff from the data source. This returns true if the subject was successfully removed from the
     * data source and false if it failed.
     *
     * @param staff The entry to to be deleted from the data source.
     * @return Returns true if the entry was successfully removed from the data source and false if it fails.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     * @throws me.timetabler.data.exceptions.DataUpdateException Thrown if the data cannot be modified.
     */
    boolean deleteStaff(Staff staff);
}
