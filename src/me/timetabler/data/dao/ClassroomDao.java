package me.timetabler.data.dao;

import me.timetabler.data.Classroom;
import me.timetabler.data.Subject;

import java.util.List;
import java.util.Optional;

/**
 * Created by stuart on 07/03/16.
 */
public interface ClassroomDao {
    List<Classroom> getAll();
    List<Classroom> getBySubject(Subject subject);
    List<Classroom> getByBuilding(String building);
    Optional<Classroom> getById(int id);

    int insert(Classroom classroom);
    boolean update(Classroom classroom);
    boolean delete(Classroom classroom);
}
