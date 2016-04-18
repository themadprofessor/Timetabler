package me.timetabler.data.dao;

import me.timetabler.data.*;
import me.timetabler.data.exceptions.DataAccessException;

import java.util.List;

/**
 * {@inheritDoc}
 * This dao will manipulate lessonPlan data.
 */
public interface LessonPlanDao extends MutableDao<LessonPlan> {
    /**
     * Returns a list containing all the lessonPlans taught by the given member of staff. If there are none taught by
     * the member of staff, the list will be empty.
     *
     * @param staff The member of staff to look for.
     * @return A list containing the applicable lessonPlans, which can be empty.
     * @throws DataAccessException Thrown if the data cannot be accessed.
     */
    List<LessonPlan> getAllByStaff(Staff staff) throws DataAccessException;

    /**
     * Returns a list containing all the lessonPlans taught in classroom. If there are none taught in classroom, the
     * list will be empty.
     *
     * @param classroom The classroom to look for.
     * @return A list containing the applicable lessonPlans, which can be empty.
     * @throws DataAccessException Thrown if the data cannot be accessed.
     */
    List<LessonPlan> getAllByClassroom(Classroom classroom) throws DataAccessException;

    /**
     * Returns a list containing all the lessonPlans taught in period. If there are none taught in period, the
     * list will be empty.
     *
     * @param period The period to look for.
     * @return A list containing the applicable lessonPlans, which can be empty.
     * @throws DataAccessException Thrown if the data cannot be accessed.
     */
    List<LessonPlan> getAllByPeriod(Period period) throws DataAccessException;

    /**
     * Returns a list containing all the lessonPlans teaching subjectSet. If there are none teaching subjectSet, the
     * list will be empty.
     *
     * @param subjectSet The subjectSet to look for.
     * @return A list containing the applicable lessonPlans, which can be empty.
     * @throws DataAccessException Thrown if the data cannot be accessed.
     */
    List<LessonPlan> getAllBySubjectSet(SubjectSet subjectSet) throws DataAccessException;

    /**
     * Returns a list containing all the lessonPlans teaching subject. If there are none teaching subject, the list will
     * be empty.
     *
     * @param subject The subject to look for.
     * @return A list containing the applicable lessonPlans, which can be empty.
     * @throws DataAccessException Thrown if the data cannot be accessed.
     */
    List<LessonPlan> getAllBySubject(Subject subject) throws DataAccessException;
}
