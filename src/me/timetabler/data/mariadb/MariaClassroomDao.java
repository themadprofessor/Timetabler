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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@inheritDoc}
 */
public class MariaClassroomDao implements ClassroomDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectSubject;
    private PreparedStatement selectBuilding;
    private PreparedStatement selectId;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    public MariaClassroomDao(Connection connection) {
        this.connection = connection;
    }


    /**
     * {@inheritDoc}
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
                selectSubject = connection.prepareStatement(builder.build());
            }

            selectBuilding.setInt(1, building.id);
            ResultSet set = selectSubject.executeQuery();
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
            set.next();
            classroom = new Classroom(id, set.getString(1), new Building(set.getInt(2), set.getString(3)), new Subject(set.getInt(4), set.getString(5)));
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
     */
    @Override
    public int insert(Classroom classroom) throws DataAccessException, DataUpdateException {
        int id = -1;

        try {
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
     */
    @Override
    public boolean update(Classroom classroom) throws DataAccessException, DataUpdateException {
        boolean success = false;

        try {
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
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
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
     */
    @Override
    public boolean delete(Classroom classroom) throws DataAccessException, DataUpdateException {
        boolean success = false;

        try {
            if (delete == null || delete.isClosed()) {
                SqlBuilder builder = new SqlBuilder("classroom", StatementType.DELETE)
                        .addWhereClause("id=?");
                delete = connection.prepareStatement(builder.build());
            }

            delete.setInt(1, classroom.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
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
     */
    @Override
    public void close() {
        try {
            selectAll.close();
            selectBuilding.close();
            selectId.close();
            selectSubject.close();
            insert.close();
            update.close();
            delete.close();
        } catch (SQLException e) {
            Log.error(e);
        }
    }
}
