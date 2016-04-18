package me.timetabler.data.dao;

import me.timetabler.data.SchoolYear;
import me.timetabler.data.Subject;
import me.timetabler.data.SubjectSet;
import me.timetabler.data.exceptions.DataAccessException;

import java.util.List;

/**
 * {@inheritDoc}
 * This dao will manipulate subjectSet data.
 */
public interface SubjectSetDao extends MutableDao<SubjectSet> {
    /**
     * Returns a list containing all the subjectSets which are being taught the given subject. If there are no
     * subjectSets being taught tht given subject, the list will be empty.
     * @param subject The subject to look for.
     * @return A list containing the applicable subjectSets, which can be empty.
     * @throws DataAccessException Thrown if the data cannot be accessed.
     */
    List<SubjectSet> getAllBySubject(Subject subject) throws DataAccessException;

    /**
     * Returns a list containing all the subjectSets which are being taught the given schoolYear. If there are no
     * subjectSets being taught tht given schoolYear, the list will be empty.
     * @param schoolYear The schoolYear to look for.
     * @return A list containing the applicable subjectSets, which can be empty.
     * @throws DataAccessException Thrown if the data cannot be accessed.
     */
    List<SubjectSet> getAllByYearGroup(SchoolYear schoolYear) throws DataAccessException;
}
