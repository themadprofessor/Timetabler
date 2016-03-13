package me.timetabler.data.dao;

import me.timetabler.data.Building;
import me.timetabler.data.Classroom;
import me.timetabler.data.Subject;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;

import java.util.List;
import java.util.Optional;

/**
 * Created by stuart on 07/03/16.
 */
public interface ClassroomDao {
    List<Classroom> getAll() throws DataAccessException;
    List<Classroom> getBySubject(Subject subject) throws DataAccessException;
    List<Classroom> getByBuilding(Building building) throws DataAccessException;
    Optional<Classroom> getById(int id) throws DataAccessException;

    int insert(Classroom classroom) throws DataAccessException, DataUpdateException;
    boolean update(Classroom classroom) throws DataAccessException, DataUpdateException;
    boolean delete(Classroom classroom) throws DataAccessException, DataUpdateException;
}
