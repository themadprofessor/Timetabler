package me.timetabler.data.mariadb;

import javafx.scene.control.Alert;
import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.StaffDao;
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
public class MariaStaffDao implements StaffDao {
    /**
     * The connection to the database, which all the PreparedStatements rely on.
     */
    protected Connection connection;

    /**
     * A PreparedStatement which is used to select all staffs from the database.
     */
    private PreparedStatement selectAll;

    /**
     * A PreparedStatement which is used to select all staffs of a given subject from the database.
     */
    private PreparedStatement selectAllSubject;

    /**
     * A PreparedStatement which is used to select a staff with a given id from the database.
     */
    private PreparedStatement selectId;

    /**
     * A PreparedStatement which is used to insert a staff into the database.
     */
    private PreparedStatement insert;

    /**
     * A PreparedStatement which is used to update a staff in the database.
     */
    private PreparedStatement update;

    /**
     * A PreparedStatement which is used to delete a staff from the database.
     */
    private PreparedStatement delete;

    /**
     * A PreparedStatement to load the staff data from a file into the database.
     */
    private PreparedStatement loadFile;

    public MariaStaffDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Staff> getAll() throws DataAccessException {
        ArrayList<Staff> staff = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("staff", StatementType.SELECT)
                        .addColumns("staff.id", "staff.staffName", "subject.id", "subject.subjectName", "staff.hoursPerWeek")
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "staff.subjectId=subject.id"));
                selectAll = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Staff s = new Staff(set.getInt(1), set.getString(2), new Subject(set.getInt(3), set.getString(4)), set.getInt(5));
                staff.add(s);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a a DataAccessException!");
            throw new DataAccessException(e);
        }

        return staff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Staff> getAllBySubject(Subject subject) throws DataAccessException {
        ArrayList<Staff> staff = new ArrayList<>();

        try {
            if (selectAllSubject == null || selectAllSubject.isClosed()) {
                SqlBuilder builder = new SqlBuilder("staff", StatementType.SELECT)
                        .addColumns("id", "staffName", "hoursPerWeek")
                        .addWhereClause("subjectId=?");
                selectAllSubject = connection.prepareStatement(builder.build());
            }

            selectAllSubject.setInt(1, subject.id);
            ResultSet set = selectAllSubject.executeQuery();
            while (set.next()) {
                Staff s = new Staff(set.getInt(1), set.getString(2), subject, set.getInt(3));
                staff.add(s);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return staff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Staff> getById(int id) throws DataAccessException {
        Staff staff = null;

        try {
            if (selectId == null || selectId.isClosed()) {
                SqlBuilder builder = new SqlBuilder("staff", StatementType.SELECT)
                        .addColumns("staff.staffName", "subject.id", "subject.subjectName", "staff.hoursPerWeek")
                        .addWhereClause("staff.id=?")
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "staff.subjectId=subject.id"));
                selectId = connection.prepareStatement(builder.build());
            }
            selectId.setInt(1, id);

            ResultSet set = selectId.executeQuery();
            set.next();
            Subject sub = new Subject(set.getInt(2), set.getString(3));
            staff = new Staff(id, set.getString(1), sub, set.getInt(4));
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        if (staff == null) {
             return Optional.empty();
        } else {
            return Optional.of(staff);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int insert(Staff staff) throws DataAccessException, DataUpdateException {
        int id = -1;

        try {
            if (insert == null || insert.isClosed()) {
                SqlBuilder builder = new SqlBuilder("staff", StatementType.INSERT)
                        .addColumns("staffName", "subjectId", "hoursPerWeek")
                        .addValues("?", "?", "?");
                insert = connection.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            insert.setString(1, staff.name);
            insert.setInt(2, staff.subject.id);
            insert.setInt(3, staff.hoursPerWeek);
            insert.executeUpdate();

        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        try {
            ResultSet set = insert.getGeneratedKeys();
            set.next();
            id = set.getInt(1);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Staff staff) throws DataAccessException, DataUpdateException {
        boolean success = false;

        try {
            if (update == null || update.isClosed()) {
                SqlBuilder builder = new SqlBuilder("staff", StatementType.UPDATE)
                        .addSetClauses("staffName=?", "subjectId=?", "hoursPerWeek=?")
                        .addWhereClause("id=?");
                update = connection.prepareStatement(builder.build());
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            update.setString(1, staff.name);
            update.setInt(2, staff.subject.id);
            update.setInt(3, staff.hoursPerWeek);
            update.setInt(4, staff.id);
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
     */
    @Override
    public boolean delete(Staff staff) throws DataAccessException, DataUpdateException {
        boolean success = false;

        try {
            if (delete == null || delete.isClosed()) {
                SqlBuilder builder = new SqlBuilder("staff", StatementType.DELETE)
                        .addWhereClause("id=?");
                delete = connection.prepareStatement(builder.build());
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            delete.setInt(1, staff.id);
            delete.executeUpdate();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
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
                SqlBuilder builder = new SqlBuilder("staff", StatementType.INSERT)
                        .addColumns("id", "staffName", "subjectId", "hoursPerWeek")
                        .addValues("?", "?", "?", "?");
                loadFile = connection.prepareStatement(builder.toString());
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
                                loadFile.setString(2, split[0]);
                                loadFile.setInt(3, Integer.parseInt(split[1]));
                                loadFile.setInt(4, Integer.parseInt(split[2]));
                                Log.verbose("Loaded staff entry without id [" + line + ']');
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
                                loadFile.setString(2, split[1]);
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
        } catch (BatchUpdateException e) {
            JavaFxBridge.createAlert(Alert.AlertType.WARNING, "Could not load file!", "Could not load all entries from file!", "The system successfully loaded [" + e.getUpdateCounts().length + "] entries from [" + file + ']', false);
            return false;
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
            if (selectId != null && !selectId.isClosed()) selectId.close();
            if (insert != null && !insert.isClosed()) insert.close();
            if (update != null && !update.isClosed()) update.close();
            if (delete != null && !delete.isClosed()) delete.close();
            if (loadFile != null && !loadFile.isClosed()) loadFile.close();
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            Log.error(e);
        }
    }
}
