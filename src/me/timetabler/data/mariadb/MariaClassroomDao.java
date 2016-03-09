package me.timetabler.data.mariadb;

import me.timetabler.data.Classroom;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.ClassroomDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.util.Log;
import me.util.MapBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
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
                initStatement(StatementType.SELECT_ALL_JOIN, false);
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Classroom classroom = new Classroom(set.getInt(1), set.getString(2), set.getString(3), new Subject(set.getInt(4), set.getString(5)));
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
                initStatement(StatementType.SELECT_ALL, false);
            }

            selectSubject.setInt(1, subject.id);
            ResultSet set = selectSubject.executeQuery();
            while (set.next()) {
                Classroom classroom = new Classroom(set.getInt(1), set.getString(2), set.getString(3), subject);
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
    public List<Classroom> getByBuilding(String building) throws DataAccessException {
        ArrayList<Classroom> classrooms = new ArrayList<>();

        try {
            if (selectBuilding == null || selectBuilding.isClosed()) {
                initStatement(StatementType.SELECT_JOIN, true);
            }

            selectBuilding.setString(1, building);
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
                initStatement(StatementType.SELECT_JOIN, false);
            }

            selectBuilding.setInt(1, id);
            ResultSet set = selectSubject.executeQuery();
            set.next();
            classroom = new Classroom(id, set.getString(1), set.getString(2), new Subject(set.getInt(3), set.getString(4)));
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
                initStatement(StatementType.INSERT, false);
            }

            insert.setString(1, classroom.name);
            insert.setString(2, classroom.buildingName);
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
                initStatement(StatementType.UPDATE, false);
            }
            update.setInt(4, classroom.id);
            update.setString(1, classroom.name);
            update.setString(2, classroom.buildingName);
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
                initStatement(StatementType.DELETE, false);
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

    private void initStatement(StatementType type, boolean building) throws DataAccessException {
        assert type != null;

        MapBuilder<String, String> builder = new MapBuilder<>(new HashMap<>());

        try {
            switch (type) {
                case SELECT_ALL_JOIN:
                    selectAll = connection.prepareStatement(type.getSql(builder.put("table", "classroom")
                            .put("columns", "classroom.id,classroom.roomName,classroom.buildingName,subject.id,subject.subjectName")
                            .put("table2", "subject")
                            .put("join_key", "classroom.subjectId=subject.id")
                            .build()));
                    break;
                case SELECT:
                    selectSubject = connection.prepareStatement(type.getSql(builder.put("table", "classroom")
                            .put("columns", "id,roomName,buildingName")
                            .put("where", "subjectId=?")
                            .build()));
                    break;
                case SELECT_JOIN:
                    if (building) {
                        selectBuilding = connection.prepareStatement(type.getSql(builder.put("table", "classroom")
                                .put("columns", "classroom.id,classroom.roomName,subject.id,subject.subjectName")
                                .put("table2", "subject")
                                .put("join_key", "classroom.subjectId=subject.id")
                                .put("where", "classroom.buildingName=?")
                                .build()));
                    } else {
                        selectId = connection.prepareStatement(type.getSql(builder.put("table", "classroom")
                                .put("columns", "classroom.roomName,classroom.buildingName,subject.id,subject.subjectName")
                                .put("table2", "subject")
                                .put("join_key", "classroom.subjectId=subject.id")
                                .put("where", "classroom.id=?").build()));
                    }
                    break;
                case INSERT:
                    insert = connection.prepareStatement(type.getSql(builder.put("table", "classroom")
                            .put("columns", "roomName,buildingName,subjectId")
                            .put("values", "?,?,?").build()), Statement.RETURN_GENERATED_KEYS);
                    break;
                case UPDATE:
                    update = connection.prepareStatement(type.getSql(builder.put("table", "classroom")
                            .put("set", "roomName=?,buildingName=?,subjectId=?")
                            .put("where", "id=?")
                            .build()));
                    break;
                case DELETE:
                    delete = connection.prepareStatement(type.getSql(builder.put("table", "classroom")
                            .put("where", "id=?")
                            .build()));
                    break;
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }
    }
}
