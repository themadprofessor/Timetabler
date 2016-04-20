package me.timetabler.data.mariadb;

import me.timetabler.data.Subject;
import me.timetabler.data.dao.SubjectDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.timetabler.data.sql.SqlBuilder;
import me.timetabler.data.sql.StatementType;
import me.util.Log;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
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
                loadFile = connection.prepareStatement("TRUNCATE TABLE subject;LOAD DATA INFILE ? INTO TABLE subject FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\n';");
            }

            loadFile.setString(1, file.getAbsolutePath());
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            loadFile.executeUpdate();
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
