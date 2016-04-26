package me.timetabler.data.mariadb;

import javafx.scene.control.Alert;
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
import me.timetabler.ui.main.JavaFxBridge;
import me.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@inheritDoc}
 * The dao will utilise a MariaDB database as it data source.
 */
public class MariaSubjectSetDao implements SubjectSetDao {
    /**
     * The connection to the database, which all the PreparedStatements rely on.
     */
    protected Connection connection;

    /**
     * A PreparedStatement which is used to select all subjectSets from the database.
     */
    private PreparedStatement selectAll;

    /**
     * A PreparedStatement which is used to select all subjectSets of a given subject from the database.
     */
    private PreparedStatement selectAllSubject;

    /**
     * A PreparedStatement which is used to select all subjectSets of a given subject from the database.
     */
    private PreparedStatement selectAllYear;

    /**
     * A PreparedStatement which is used to select a subjectSet with a given id from the database.
     */
    private PreparedStatement selectId;

    /**
     * A PreparedStatement which is used to insert a subjectSet into the database.
     */
    private PreparedStatement insert;

    /**
     * A PreparedStatement which is used to update a subjectSet in the database.
     */
    private PreparedStatement update;

    /**
     * A PreparedStatement which is used to delete a subjectSet from the database.
     */
    private PreparedStatement delete;

    /**
     * A PreparedStatement to load the subjectSet data from a file into the database.
     */
    private PreparedStatement loadFile;

    public MariaSubjectSetDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     * This method will get the subjectSet data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
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

    /**
     * {@inheritDoc}
     * This method will get the subjectSet data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
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

    /**
     * {@inheritDoc}
     * This method will get the subjectSet data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
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

    /**
     * {@inheritDoc}
     * This method will get the subjectSet data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
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

    /**
     * {@inheritDoc}
     * This method will insert the subjectSet data into a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
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

    /**
     * {@inheritDoc}
     * This method will update the subjectSet data in a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
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

    /**
     * {@inheritDoc}
     * This method will delete the subjectSet data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
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

    /**
     * {@inheritDoc}
     * This method will load the classroom data into the MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public boolean loadFile(File file) throws DataAccessException, DataUpdateException {
        if (file == null) {
            throw new NullPointerException("Data File Cannot Be Null!");
        } else if (!file.exists()) {
            throw new IllegalArgumentException("Data File [" + file.getAbsolutePath() + "] Must Exist!");
        } else if (file.isDirectory()) {
            throw new IllegalArgumentException("Data File [" + file.getAbsolutePath() + "] Must Not Be A Directory!");
        } else if (!file.canRead()) {
            throw new IllegalArgumentException("Data File [" + file.getAbsolutePath() + "] Must Have Read Permissions For User [" + System.getProperty("user.name") + "]!");
        }

        try {
            if (loadFile == null || loadFile.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subjectSet", StatementType.INSERT)
                        .addColumns("id", "setId", "subjectId", "schoolYearId")
                        .addValues("?", "?", "?", "?");
                loadFile = connection.prepareStatement(builder.build());
            }

            StringBuilder failedBuilder = new StringBuilder();
            try {
                Log.info("Will load staff file [" + file + ']');
                Files.lines(file.toPath()).sequential().forEach(line -> {
                    try {
                        String[] split = line.split(",");
                        if (split.length == 3) {
                            if (!split[1].chars().allMatch(Character::isDigit)) {
                                failedBuilder.append("Ignoring line [")
                                        .append(line)
                                        .append("] as subjectId column is not an unsigned integer.\n");
                            } if (!split[2].chars().allMatch(Character::isDigit)) {
                                failedBuilder.append("Ignoring line [")
                                        .append(line)
                                        .append("] as hoursPerWeek column is not an unsigned integer.\n");
                            } else {
                                loadFile.setNull(1, Types.INTEGER);
                                loadFile.setInt(2, Integer.parseInt(split[0]));
                                loadFile.setInt(3, Integer.parseInt(split[1]));
                                loadFile.setInt(4, Integer.parseInt(split[2]));
                                Log.verbose("Loaded subjectSet entry without id [" + line + ']');
                            }
                        } else if (split.length == 4) {
                            if (!split[0].chars().allMatch(Character::isDigit)) {
                                failedBuilder.append("Ignoring line [")
                                        .append(line)
                                        .append("] as id column is not an unsigned integer.\n");
                            } else if (!split[2].chars().allMatch(Character::isDigit)) {
                                failedBuilder.append("Ignoring line [")
                                        .append(line)
                                        .append("] as subjectId column is not an unsigned integer.\n");
                            } else if (!split[3].chars().allMatch(Character::isDigit)) {
                                failedBuilder.append("Ignoring line [")
                                        .append(line)
                                        .append("] as hoursPerWeek column is not an unsigned integer.\n");
                            } else {
                                loadFile.setInt(1, Integer.parseInt(split[0]));
                                loadFile.setInt(2, Integer.parseInt(split[1]));
                                loadFile.setInt(3, Integer.parseInt(split[2]));
                                loadFile.setInt(4, Integer.parseInt(split[3]));
                                Log.verbose("Loaded line [" + line + ']');
                            }
                        } else {
                            failedBuilder.append("Ignoring line [")
                                    .append(line)
                                    .append("] as there is an incorrect number of columns. It should be 4.\n");
                        }

                        //Use batch statement as it is faster and easier
                        //The batch will be overwritten if this method is not called, e.g. exception thrown
                        loadFile.addBatch();
                    } catch (SQLException e) {
                        //Thrown only if data cannot be added
                        failedBuilder.append("Failed to load line [")
                                .append(line)
                                .append("]\n");
                    }
                });
                Log.info("Finished Loading File");
            } catch (IOException e) {
                //Should only happen if file goes missing during reading
                Log.error(e);
                JavaFxBridge.createAlert(Alert.AlertType.ERROR, "IO Exception", null, "Is [" + file.toString() + "] still there?\nAn IO Exception has occurred [" + e.toString() + "]", false);
            }

            if (failedBuilder.length() > 0) {
                Log.warning(failedBuilder);
                JavaFxBridge.createAlert(Alert.AlertType.WARNING, "Failed to load entries!", "Some entries could not be loaded!", failedBuilder.toString(), false);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            int[] results = loadFile.executeBatch();
            Log.debug("Loaded [" + results.length + "] subject entries");
            return true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            if (selectAll != null && !selectAll.isClosed()) selectAll.close();
            if (selectAllSubject != null && !selectAllSubject.isClosed()) selectAllSubject.close();
            if (selectAllYear != null && !selectAllYear.isClosed()) selectAllYear.close();
            if (selectId != null && !selectId.isClosed()) selectId.close();
            if (insert != null && !insert.isClosed()) insert.close();
            if (update != null && !update.isClosed()) update.close();
            if (delete != null && !delete.isClosed()) delete.close();
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            Log.error(e);
        }
    }
}
