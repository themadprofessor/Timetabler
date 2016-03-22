package me.timetabler.data.mariadb;

import me.timetabler.data.LearningSet;
import me.timetabler.data.SchoolYear;
import me.timetabler.data.Subject;
import me.timetabler.data.SubjectSet;
import me.timetabler.data.dao.SubjectSetDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.timetabler.data.sql.JoinClause;
import me.timetabler.data.sql.JoinType;
import me.timetabler.data.sql.SqlBuilder;
import me.timetabler.data.sql.StatementType;
import me.util.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by stuart on 13/03/16.
 */
public class MariaSubjectSetDao implements SubjectSetDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectAllSubject;
    private PreparedStatement selectAllYear;
    private PreparedStatement selectId;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    public MariaSubjectSetDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<SubjectSet> getAll() throws DataAccessException {
        ArrayList<SubjectSet> sets = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subjectSet", StatementType.SELECT)
                        .addColumns("subjectSet.id", "subject.id", "subject.subjectName", "learningSet.id",
                                "learningSet.setName", "schoolYear.id", "schoolYear.schoolYearName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "subjectSet.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.schoolYearId=schoolYear.id"));
                selectAll = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                SubjectSet subjectSet = new SubjectSet(set.getInt(1), new Subject(set.getInt(2), set.getString(3)),
                        new LearningSet(set.getInt(4), set.getString(5)), new SchoolYear(set.getInt(6), set.getString(7)));
                sets.add(subjectSet);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return sets;
    }

    @Override
    public List<SubjectSet> getAllBySubject(Subject subject) throws DataAccessException {
        ArrayList<SubjectSet> sets = new ArrayList<>();

        try {
            if (selectAllSubject == null || selectAllSubject.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subjectSet", StatementType.SELECT)
                        .addColumns("subjectSet.id", "learningSet.id", "learningSet.setName", "schoolYear.id",
                                "schoolYear.schoolYearName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.yearGroupId=schoolYear.id"))
                        .addWhereClause("subjectSet.subjectId=?");
                selectAllSubject = connection.prepareStatement(builder.build());
            }

            selectAllSubject.setInt(1, subject.id);
            ResultSet set = selectAllSubject.executeQuery();
            while (set.next()) {
                SubjectSet subjectSet = new SubjectSet(set.getInt(1), subject, new LearningSet(set.getInt(2),
                        set.getString(3)), new SchoolYear(set.getInt(4), set.getString(5)));
                sets.add(subjectSet);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return sets;
    }

    @Override
    public List<SubjectSet> getAllByYearGroup(SchoolYear schoolYear) throws DataAccessException {
        ArrayList<SubjectSet> sets = new ArrayList<>();

        try {
            if (selectAllYear == null || selectAllYear.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subjectSet", StatementType.SELECT)
                        .addColumns("subjectSet.id", "subject.id", "subject.subjectName", "learningSet.id",
                                "learningSet.setName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "subjectSet.subjectId=subject.id"))
                        .addWhereClause("subjectSet.yearGroupId=?");
                selectAllYear = connection.prepareStatement(builder.build());
            }

            selectAllYear.setInt(1, schoolYear.id);
            ResultSet set = selectAllYear.executeQuery();
            while (set.next()) {
                SubjectSet subjectSet = new SubjectSet(set.getInt(1), new Subject(set.getInt(2), set.getString(3)),
                        new LearningSet(set.getInt(4), set.getString(5)), schoolYear);
                sets.add(subjectSet);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return sets;
    }

    @Override
    public Optional<SubjectSet> getById(int id) throws DataAccessException {
        SubjectSet subjectSet = null;

        try {
            if (selectId == null || selectId.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subjectSet", StatementType.SELECT)
                        .addColumns("subject.id", "subject.subjectName", "learningSet.id",
                                "learningSet.setName", "schoolYear.id", "schoolYear.schoolYearName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "subjectSet.subjectId=subject.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "learningSet", "subjectSet.setId=learningSet.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "schoolYear", "subjectSet.yearGroupId=schoolYear.id"))
                        .addWhereClause("subjectSet.id=?");
                selectId = connection.prepareStatement(builder.build());
            }

            selectId.setInt(1, id);
            ResultSet set = selectId.executeQuery();
            set.next();
            subjectSet = new SubjectSet(id, new Subject(set.getInt(1), set.getString(2)),
                    new LearningSet(set.getInt(3), set.getString(4)), new SchoolYear(set.getInt(5), set.getString(6)));
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        if (subjectSet == null) {
            return Optional.empty();
        } else {
            return Optional.of(subjectSet);
        }
    }

    @Override
    public int insert(SubjectSet set) throws DataAccessException, DataUpdateException {
        int id = -1;

        try {
            if (insert == null || insert.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subjectSet", StatementType.INSERT)
                        .addColumns("subjectId", "setId", "schoolYearId")
                        .addValues("?", "?", "?");
                insert = connection.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
            }

            insert.setInt(1, set.subject.id);
            insert.setInt(2, set.learningSet.id);
            insert.setInt(3, set.schoolYear.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            insert.executeUpdate();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        try {
            ResultSet results = insert.getGeneratedKeys();
            results.next();
            id = results.getInt(1);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return id;
    }

    @Override
    public boolean update(SubjectSet set) throws DataAccessException, DataUpdateException {
        boolean success = false;

        try {
            if (update == null || update.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subjectSet", StatementType.UPDATE)
                        .addSetClauses("subjectId=?", "learningSetId=?", "yearGroupId=?")
                        .addWhereClause("id=?");
                update = connection.prepareStatement(builder.build());
            }

            update.setInt(1, set.subject.id);
            update.setInt(2, set.learningSet.id);
            update.setInt(3, set.schoolYear.id);
            update.setInt(4, set.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            update.executeUpdate();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return success;
    }

    @Override
    public boolean delete(SubjectSet set) throws DataAccessException, DataUpdateException {
        boolean success = false;

        try {
            if (delete == null || delete.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subjectSet", StatementType.DELETE)
                        .addWhereClause("id=?");
                delete = connection.prepareStatement(builder.build());
            }

            delete.setInt(1, set.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            delete.executeUpdate();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throw a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return success;
    }
}
