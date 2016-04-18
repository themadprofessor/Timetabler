package me.timetabler.data.dao;

import me.timetabler.data.Building;
import me.timetabler.data.Classroom;
import me.timetabler.data.Subject;
import me.timetabler.data.exceptions.DataAccessException;

import java.util.List;

/**
 * {@inheritDoc}
 * This dao will manipulate classroom data.
 */
public interface ClassroomDao extends MutableDao<Classroom> {
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
}
