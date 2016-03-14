package me.timetabler.data.dao;

import me.timetabler.data.*;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;

import java.util.List;

/**
 * Created by stuart on 14/03/16.
 */
public interface LessonPlanDao {
    List<LessonPlan> getAll() throws DataAccessException;
    List<LessonPlan> getAllByStaff(Staff staff) throws DataAccessException;
    List<LessonPlan> getAllByClassroom(Classroom classroom) throws DataAccessException;
    List<LessonPlan> getAllByPeriod(Period period) throws DataAccessException;
    List<LessonPlan> getAllBySubjectSet(SubjectSet subjectSet) throws DataAccessException;
    List<LessonPlan> getAllByClass(SchoolClass schoolClass) throws DataAccessException;

    int insert(LessonPlan lessonPlan) throws DataUpdateException, DataAccessException;
    boolean update(LessonPlan lessonPlan) throws DataUpdateException, DataAccessException;
    boolean delete(LessonPlan lessonPlan) throws DataUpdateException, DataAccessException;
}
