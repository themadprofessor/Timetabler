package me.timetabler.data.mariadb;

import me.timetabler.data.Building;
import me.timetabler.data.Classroom;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.ClassroomDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.timetabler.data.sql.JoinClause;
import me.timetabler.data.sql.JoinType;
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
public class MariaClassroomDao implements ClassroomDao {
    /**
     * The connection to the database, which all the PreparedStatements rely on.
     */
    protected Connection connection;

    /**
     * A PreparedStatement which is used to select all classrooms from the database.
     */
    private PreparedStatement selectAll;

    /**
     * A PreparedStatement which is used to select all classrooms of a given subject from the database.
     */
    private PreparedStatement selectSubject;

    /**
     * A PreparedStatement which is used to select all classrooms in a given building from the database.
     */
    private PreparedStatement selectBuilding;

    /**
     * A PreparedStatement which is used to select a classroom with a given id from the database.
     */
    private PreparedStatement selectId;

    /**
     * A PreparedStatement which is used to insert a classroom into the database.
     */
    private PreparedStatement insert;

    /**
     * A PreparedStatement which is used to update a classroom in the database.
     */
    private PreparedStatement update;

    /**
     * A PreparedStatement which is used to delete a classroom from the database.
     */
    private PreparedStatement delete;

    /**
     * A PreparedStatement to load the classroom data from a file into the database.
     */
    private PreparedStatement loadFile;

    /**
     * Initialises the dao with the given connection. The statements are initialised when required.
     * @param connection The connection to the database.
     */
    public MariaClassroomDao(Connection connection) {
        this.connection = connection;
    }


    /**
     * {@inheritDoc}
     * This method will get the classroom data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<Classroom> getAll() throws DataAccessException {
        ArrayList<Classroom> classrooms = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("classroom",  StatementType.SELECT)
                        .addColumns("classroom.id", "classroom.roomName", "building.id", "building.buildingName", "subject.id", "subject.subjectName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "classroom.subjectId=subject.id"));
                selectAll = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Classroom classroom = new Classroom(set.getInt(1), set.getString(2), new Building(set.getInt(3), set.getString(4)), new Subject(set.getInt(5), set.getString(6)));
                classrooms.add(classroom);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return classrooms;
    }


    /**
     * {@inheritDoc}
     * This method will get the classroom data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<Classroom> getBySubject(Subject subject) throws DataAccessException {
        ArrayList<Classroom> classrooms = new ArrayList<>();

        try {
            if (selectSubject == null || selectSubject.isClosed()) {
                SqlBuilder builder = new SqlBuilder("classroom", StatementType.SELECT)
                        .addColumns("classroom.id", "classroom.roomName", "building.id", "building.buildingName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addWhereClause("classroom.subjectId=?");
                selectSubject = connection.prepareStatement(builder.build());
            }

            selectSubject.setInt(1, subject.id);
            ResultSet set = selectSubject.executeQuery();
            while (set.next()) {
                Classroom classroom = new Classroom(set.getInt(1), set.getString(2), new Building(set.getInt(3), set.getString(4)), subject);
                classrooms.add(classroom);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return classrooms;
    }


    /**
     * {@inheritDoc}
     * This method will get the classroom data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<Classroom> getByBuilding(Building building) throws DataAccessException {
        ArrayList<Classroom> classrooms = new ArrayList<>();

        try {
            if (selectBuilding == null || selectBuilding.isClosed()) {
                SqlBuilder builder = new SqlBuilder("classroom", StatementType.SELECT)
                        .addColumns("classroom.id", "classroom.roomName", "subject.id", "subject.subjectName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "classroom.subjectId=subject.id"))
                        .addWhereClause("classroom.buildingId=?");
                selectBuilding = connection.prepareStatement(builder.build());
            }

            selectBuilding.setInt(1, building.id);
            ResultSet set = selectBuilding.executeQuery();
            while (set.next()) {
                Classroom classroom = new Classroom(set.getInt(1), set.getString(2), building, new Subject(set.getInt(3), set.getString(4)));
                classrooms.add(classroom);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return classrooms;
    }


    /**
     * {@inheritDoc}
     * This method will get the classroom data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public Optional<Classroom> getById(int id) throws DataAccessException {
        Classroom classroom = null;

        try {
            if (selectId == null || selectId.isClosed()) {
                SqlBuilder builder = new SqlBuilder("classroom", StatementType.SELECT)
                        .addColumns("classroom.roomName", "building.id", "building.buildingName", "subject.id", "subject.subjectName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "classroom.subjectId=subject.id"))
                        .addWhereClause("classroom.id=?");
                selectId = connection.prepareStatement(builder.build());
            }

            selectBuilding.setInt(1, id);
            ResultSet set = selectSubject.executeQuery();
            if (set.next()) {
                classroom = new Classroom(id, set.getString(1), new Building(set.getInt(2), set.getString(3)), new Subject(set.getInt(4), set.getString(5)));
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        if (classroom == null) {
            return Optional.empty();
        } else {
            return Optional.of(classroom);
        }
    }


    /**
     * {@inheritDoc}
     * This method will insert the classroom data into a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public int insert(Classroom classroom) throws DataAccessException, DataUpdateException {
        int id;

        try {
            if (classroom == null || classroom.name == null || classroom.subject == null || classroom.building == null) {
                return -1;
            }

            if (insert == null || insert.isClosed()) {
                SqlBuilder builder = new SqlBuilder("classroom", StatementType.INSERT)
                        .addColumns("roomName", "buildingId", "subjectId")
                        .addValues("?", "?", "?");
                insert = connection.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
            }

            insert.setString(1, classroom.name);
            insert.setInt(2, classroom.building.id);
            insert.setInt(3, classroom.subject.id);
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
            set.next();
            id = set.getInt(1);
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
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
    public boolean update(Classroom classroom) throws DataAccessException, DataUpdateException {
        try {
            if (classroom.name != null && classroom.building != null && classroom.subject != null && classroom.id > -1) {
                if (update == null || update.isClosed()) {
                    SqlBuilder builder = new SqlBuilder("classroom", StatementType.UPDATE)
                            .addSetClauses("roomName=?", "buildingId=?", "subjectId=?")
                            .addWhereClause("id=?");
                    update = connection.prepareStatement(builder.build());
                }

                update.setInt(4, classroom.id);
                update.setString(1, classroom.name);
                update.setInt(2, classroom.building.id);
                update.setInt(3, classroom.subject.id);
            } else {
                return false;
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            update.executeUpdate();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return true;
    }



    /**
     * {@inheritDoc}
     * This method will delete the classroom data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public boolean delete(Classroom classroom) throws DataAccessException, DataUpdateException {
        try {
            if (classroom != null && classroom.id > -1) {
                if (delete == null || delete.isClosed()) {
                    SqlBuilder builder = new SqlBuilder("classroom", StatementType.DELETE)
                            .addWhereClause("id=?");
                    delete = connection.prepareStatement(builder.build());
                }

                delete.setInt(1, classroom.id);
            } else {
                return false;
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            delete.executeUpdate();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return true;
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
                loadFile = connection.prepareStatement("LOAD DATA INFILE '?' INTO TABLE classroom FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\n';");
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
            if (selectAll != null) selectAll.close();
            if (selectBuilding != null) selectBuilding.close();
            if (selectId != null) selectId.close();
            if (selectSubject != null) selectSubject.close();
            if (loadFile != null) loadFile.close();
            if (insert != null) insert.close();
            if (update != null) update.close();
            if (delete != null) delete.close();
        } catch (SQLException e) {
            Log.error(e);
        }
    }
}
