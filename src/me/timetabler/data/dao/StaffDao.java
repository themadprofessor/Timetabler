package me.timetabler.data.dao;

import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.timetabler.data.exceptions.DataAccessException;

import java.util.List;

/**
 * {@inheritDoc}
 * The dao will manipulate staff data.
 */
public interface StaffDao extends MutableDao<Staff> {
    /**
     * Returns a list of all the staff who teach the give subject. If there are no staff who teach the given subject, an
     * empty list will be returned. The type of list is to be determined by the implementation.
     *
     * @param subject The subject staff to find.
     * @return A list of all the staff who teach the given subject, which can be empty.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    List<Staff> getAllBySubject(Subject subject) throws DataAccessException;
}
