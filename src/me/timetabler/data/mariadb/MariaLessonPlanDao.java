package me.timetabler.data.mariadb;

import me.timetabler.data.*;
import me.timetabler.data.dao.LessonPlanDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.timetabler.data.sql.SqlBuilder;
import me.timetabler.data.sql.StatementType;
import me.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stuart on 14/03/16.
 */
public class MariaLessonPlanDao implements LessonPlanDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectAllStaff;
    private PreparedStatement selectAllClassroom;
    private PreparedStatement selectAllPeriod;
    private PreparedStatement selectAllSubjectSet;
    private PreparedStatement selectAllClass;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    @Override
    public List<LessonPlan> getAll() throws DataAccessException {
        ArrayList<LessonPlan> lessonPlen = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("lessonPlan", StatementType.SELECT)
                        .addColumns("lessonPlan.id", "class.id", "class.className", "subject.id", "subject.subjectName",
                                "staff.id", "staff.staffName", "classroom.id", "classroom.roomName",
                                "buliding.id", "building.buildingName", "period.id", "period.dayId", "period.startTime",
                                "period.endTime", "subjectSet.id", "learningSet.id", "learningSet.name", "subjectSet.schoolYear");
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return null;
    }

    @Override
    public List<LessonPlan> getAllByStaff(Staff staff) throws DataAccessException {
        return null;
    }

    @Override
    public List<LessonPlan> getAllByClassroom(Classroom classroom) throws DataAccessException {
        return null;
    }

    @Override
    public List<LessonPlan> getAllByPeriod(Period period) throws DataAccessException {
        return null;
    }

    @Override
    public List<LessonPlan> getAllBySubjectSet(SubjectSet subjectSet) throws DataAccessException {
        return null;
    }

    @Override
    public List<LessonPlan> getAllByClass(SchoolClass schoolClass) throws DataAccessException {
        return null;
    }

    @Override
    public int insert(LessonPlan lessonPlan) throws DataUpdateException, DataAccessException {
        return 0;
    }

    @Override
    public boolean update(LessonPlan lessonPlan) throws DataUpdateException, DataAccessException {
        return false;
    }

    @Override
    public boolean delete(LessonPlan lessonPlan) throws DataUpdateException, DataAccessException {
        return false;
    }
}
