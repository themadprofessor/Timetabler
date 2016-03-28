package me.timetabler.data.dao;

import me.timetabler.data.SchoolYear;
import me.timetabler.data.Subject;
import me.timetabler.data.SubjectSet;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;

import java.util.List;
import java.util.Optional;

/**
 * Created by stuart on 08/03/16.
 */
public interface SubjectSetDao extends Dao {
    List<SubjectSet> getAll() throws DataAccessException;
    List<SubjectSet> getAllBySubject(Subject subject) throws DataAccessException;
    List<SubjectSet> getAllByYearGroup(SchoolYear schoolYear) throws DataAccessException;
    Optional<SubjectSet> getById(int id) throws DataAccessException;

    int insert(SubjectSet set) throws DataAccessException, DataUpdateException;
    boolean update(SubjectSet set) throws DataAccessException, DataUpdateException;
    boolean delete(SubjectSet set) throws DataAccessException, DataUpdateException;
}
