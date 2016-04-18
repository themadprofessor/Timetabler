package me.timetabler.data.dao;

import me.timetabler.data.Subject;
import me.timetabler.data.exceptions.DataAccessException;

import java.util.Optional;

/**
 * {@inheritDoc}
 * This dao will manipulate subject data.
 */
public interface SubjectDao extends MutableDao<Subject> {
    /**
     * Returns the subject which has the given name. The optional will be empty if the name does not reference any subjects.
     * Use getById where possible as it is faster to compare and integer than a String.
     *
     * @param name The name of the subject to be found.
     * @return An optional containing the subject if it exists, or empty if it does not.
     * @throws me.timetabler.data.exceptions.DataAccessException Thrown if the data cannot be accessed.
     */
    Optional<Subject> getByName(String name) throws DataAccessException;
}
