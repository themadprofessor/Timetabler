package me.timetabler.data.dao;

import me.timetabler.data.Subject;
import me.timetabler.data.SubjectSet;
import me.timetabler.data.YearGroup;

import java.util.List;
import java.util.Optional;

/**
 * Created by stuart on 08/03/16.
 */
public interface SubjectSetDao {
    List<SubjectSet> getAll();
    List<SubjectSet> getAllBySubject(Subject subject);
    List<SubjectSet> getAllByYearGroup(YearGroup yearGroup);
    Optional<SubjectSet> getById(int id);

    int insert(SubjectSet set);
    boolean update(SubjectSet set);
    boolean delete(SubjectSet set);
}
