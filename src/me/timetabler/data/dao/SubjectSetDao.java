package me.timetabler.data.dao;

import me.timetabler.data.SubjectSet;

import java.util.List;

/**
 * Created by stuart on 08/03/16.
 */
public interface SubjectSetDao {
    List<SubjectSet> getAll();
    List<SubjectSet> getAllBySubject();

}
