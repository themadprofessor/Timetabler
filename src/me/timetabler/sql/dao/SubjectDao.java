package me.timetabler.sql.dao;

import me.timetabler.data.Subject;

import java.util.List;
import java.util.Optional;

/**
 * Created by stuart on 28/02/16.
 */
public interface SubjectDao {
    List<Subject> getAllSubjects();
    Optional<Subject> getById(int id);
    Optional<Subject> getByName(String name);

    boolean insertSubject(Subject subject);
    boolean updateSubject(Subject subject);
    boolean deleteSubject(Subject subject);
}
