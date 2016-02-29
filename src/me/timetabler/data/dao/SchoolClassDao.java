package me.timetabler.data.dao;

import me.timetabler.data.SchoolClass;
import me.timetabler.data.Subject;

import java.util.List;
import java.util.Optional;

/**
 * Created by stuart on 28/02/16.
 */
public interface SchoolClassDao {
    List<SchoolClass> getAllClasses();
    List<SchoolClass> getAllBySubject(Subject subject);
    Optional<SchoolClass> getById(int id);
    Optional<SchoolClass> getByName(String name);

    boolean insertClass(SchoolClass schoolClass);
    boolean updateClass(SchoolClass schoolClass);
    boolean deleteClass(SchoolClass schoolClass);
}
