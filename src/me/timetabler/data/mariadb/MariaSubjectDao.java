package me.timetabler.data.mariadb;

import javafx.scene.control.Alert;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.SubjectDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataExceptionHandler;
import me.timetabler.data.exceptions.DataUpdateException;
import me.timetabler.data.sql.SqlBuilder;
import me.timetabler.data.sql.StatementType;
import me.timetabler.ui.main.JavaFxBridge;
import me.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * {@inheritDoc}
 * The dao will utilise a MariaDB database as it data source.
 */
public class MariaSubjectDao implements SubjectDao {
    /**
     * The connection to the database, which all the PreparedStatements rely on.
     */
    protected Connection connection;

    /**
     * A PreparedStatement which is used to select all subjects of a given subject from the database.
     */
    private PreparedStatement selectAll;

    /**
     * A PreparedStatement which is used to select a subject with a given id from the database.
     */
    private PreparedStatement selectId;

    /**
     * A PreparedStatement which is used to select a subject with a given id from the database.
     */
    private PreparedStatement selectName;

    /**
     * A PreparedStatement which is used to insert a subject into the database.
     */
    private PreparedStatement insert;

    /**
     * A PreparedStatement which is used to update a subject in the database.
     */
    private PreparedStatement update;

    /**
     * A PreparedStatement which is used to delete a subject from the database.
     */
    private PreparedStatement delete;

    /**
     * A PreparedStatement to load the subject data from a file into the database.
     */
    private PreparedStatement loadFile;

    /**
     * Initialises the dao with the given connection. The statements are initialised when required.
     * @param connection The connection to the database.
     */
    public MariaSubjectDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     * This method will get the subject data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<Subject> getAll() throws DataAccessException {
        ArrayList<Subject> subjects = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subject", StatementType.SELECT);
                selectAll = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Subject subject = new Subject(set.getInt(1), set.getString(2));
                subjects.add(subject);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException");
            throw new DataAccessException(e);
        }

        return subjects;
    }

    /**
     * {@inheritDoc}
     * This method will get the subject data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public Optional<Subject> getById(int id) throws DataAccessException {
        Subject subject = null;

        if (id < 0) {
            return Optional.empty();
        }

        try {
            if (selectId == null || selectId.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subject", StatementType.SELECT)
                        .addColumn("subjectName")
                        .addWhereClause("id=?");
                selectId = connection.prepareStatement(builder.build());
            }

            selectId.setInt(1, id);
            ResultSet set = selectId.executeQuery();
            if (set.next()) {
                subject = new Subject(id, set.getString(1));
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        return Optional.ofNullable(subject);
    }

    /**
     * {@inheritDoc}
     * This method will get the subject data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public Optional<Subject> getByName(String name) throws DataAccessException {
        Subject subject = null;

        if (name == null || name.isEmpty()) {
            return Optional.empty();
        }

        try {
            if (selectName == null || selectName.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subject", StatementType.SELECT)
                        .addColumn("id")
                        .addWhereClause("subjectName=?");
                selectName = connection.prepareStatement(builder.build());
            }

            selectName.setString(1, name);
            ResultSet set = selectName.executeQuery();
            if (set.next()) {
                subject = new Subject(set.getInt(1), name);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        return Optional.ofNullable(subject);
    }

    /**
     * {@inheritDoc}
     * This method will insert the subject data into a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public int insert(Subject subject) throws DataUpdateException, DataAccessException {
        int id = -1;

        if (subject == null || subject.name == null || subject.name.isEmpty()) {
            return id;
        }

        try {
            if (insert == null || insert.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subject", StatementType.INSERT)
                        .addColumn("subjectName")
                        .addValue("?");
                insert = connection.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
            }

            insert.setString(1, subject.name);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            insert.executeUpdate();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataUpdateException!");
            throw new DataUpdateException(e);
        }

        try {
            ResultSet set = insert.getGeneratedKeys();
            if (set.next()) {
                id = set.getInt(1);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return id;
    }

    /**
     * {@inheritDoc}
     * This method will update the classroom data in a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public boolean update(Subject subject) throws DataUpdateException, DataAccessException {
        if (subject == null || subject.name == null || subject.name.isEmpty()) {
            return false;
        }

        try {
            if (update == null || update.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subject", StatementType.UPDATE)
                        .addSetClause("subject=?")
                        .addWhereClause("id=?");
                update = connection.prepareStatement(builder.build());
            }

            update.setInt(2, subject.id);
            update.setString(1, subject.name);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            update.execute();
            return true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataUpdateException!");
            throw new DataUpdateException(e);
        }
    }

    /**
     * {@inheritDoc}
     * This method will delete the classroom data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public boolean delete(Subject subject) throws DataUpdateException, DataAccessException {
        if (subject == null || subject.id < 0) {
            return false;
        }

        try {
            if (delete == null || delete.isClosed()) {
                SqlBuilder builder = new SqlBuilder("subject", StatementType.DELETE)
                        .addWhereClause("id=?");
                delete = connection.prepareStatement(builder.build());
            }

            delete.setInt(1, subject.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            delete.execute();
            return true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataUpdateException!");
            throw new DataUpdateException(e);
        }
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
                SqlBuilder builder = new SqlBuilder("subject", StatementType.INSERT)
                        .addColumns("id", "subjectName")
                        .addValues("?", "?");
                loadFile = connection.prepareStatement(builder.build());
            }

            StringBuilder failedBuilder = new StringBuilder();
            try {
                Log.info("Will load subject file [" + file + ']');
                Files.lines(file.toPath()).sequential().forEach(line -> {
                    try {
                        String[] split = line.split(",");
                        if (split.length == 0) {
                            //Empty lines are to be ignored
                        } else if (split.length == 1) {
                            loadFile.setNull(1, Types.INTEGER);
                            loadFile.setString(2, split[0]);
                            Log.verbose("Loaded subject name entry without id [" + line + ']');
                        } else {
                            if (split.length > 2) {
                                //Line should have no more than two elements, but will still try to add first two elements.
                                failedBuilder.append("On line [")
                                        .append(line)
                                        .append("] ignoring [")
                                        .append(Arrays.toString(Arrays.copyOfRange(split, 2, split.length)))
                                        .append("]. Will try to add first two elements\n");
                                Log.verbose("Will partially load line [" + line + ']');
                            }

                            //Ensure the first entry is an unsigned integer, else set it as null and let the database
                            //set the id
                            if (split[0].chars().allMatch(Character::isDigit)) {
                                loadFile.setInt(1, Integer.parseInt(split[0]));
                            } else {
                                loadFile.setNull(1, Types.INTEGER);
                            }
                            loadFile.setString(2, split[1]);
                            Log.verbose("Loaded line [" + line + ']');
                        }

                        //Use batch statement as it is faster and easier
                        //The batch will be overwritten if this method is not called, e.g. exception thrown
                        loadFile.addBatch();
                    } catch (SQLException e) {
                        //Only thrown if the data cannot be added
                        failedBuilder.append("Failed to load line [")
                                .append(line)
                                .append("]\n");
                    }
                });
                Log.info("Finished Loading File");
            } catch (IOException e) {
                //Should only happen if the file goes missing during reading
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
            if (selectId != null && !selectId.isClosed()) selectId.close();
            if (selectName != null && !selectName.isClosed()) selectName.close();
            if (insert != null && !insert.isClosed()) insert.close();
            if (update != null && !update.isClosed()) update.close();
            if (delete != null && !delete.isClosed()) delete.close();
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            Log.error(e);
        }
    }
}
