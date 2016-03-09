package me.timetabler.data.mariadb;

import me.timetabler.data.SchoolClass;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.SchoolClassDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.util.Log;
import me.util.MapBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * {@inheritDoc}
 */
public class MariaClassDao implements SchoolClassDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectAllSubject;
    private PreparedStatement selectId;
    private PreparedStatement getLastId;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    public MariaClassDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SchoolClass> getAllClasses() throws DataAccessException {
        ArrayList<SchoolClass> classes = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                initStatement(StatementType.SELECT_ALL);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                SchoolClass schoolClass = new SchoolClass(set.getInt(1), set.getString(2), new Subject(set.getInt(3), set.getString(4)));
                classes.add(schoolClass);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
        }

        return classes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SchoolClass> getAllBySubject(Subject subject) throws DataUpdateException, DataAccessException {
        ArrayList<SchoolClass> classes = new ArrayList<>();

        try {
            if (selectAllSubject == null || selectAllSubject.isClosed()) {
                initStatement(StatementType.SELECT);
            }

            selectAllSubject.setInt(1, subject.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            ResultSet set = selectAllSubject.executeQuery();
            while (set.next()) {
                SchoolClass schoolClass = new SchoolClass(set.getInt(1), set.getString(2), subject);
                classes.add(schoolClass);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return classes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SchoolClass> getById(int id) throws DataUpdateException, DataAccessException {
        SchoolClass schoolClass;

        try {
            if (selectId == null || selectId.isClosed()) {
                initStatement(StatementType.SELECT);
            }

            selectId.setInt(1, id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            ResultSet set = selectId.executeQuery();
            set.next();
            schoolClass = new SchoolClass(id, set.getString(1), new Subject(set.getInt(2), set.getString(3)));
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        if (schoolClass == null) {
            return Optional.empty();
        } else {
            return Optional.of(schoolClass);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int insertClass(SchoolClass schoolClass) throws DataAccessException, DataUpdateException {
        int id = -1;

        try {
            if (insert == null || insert.isClosed()) {
                initStatement(StatementType.INSERT);
            }

            insert.setString(1, schoolClass.name);
            insert.setInt(2, schoolClass.subject.id);
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
            if (getLastId == null || getLastId.isClosed()) {
                initStatement(StatementType.GET_LAST_AUTO_INCRE);
            }

            ResultSet set = getLastId.executeQuery();
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
    public boolean updateClass(SchoolClass schoolClass) throws DataUpdateException, DataAccessException {
        boolean success;

        try {
            if (update == null || update.isClosed()) {
                initStatement(StatementType.UPDATE);
            }

            update.setString(1, schoolClass.name);
            update.setInt(2, schoolClass.id);
            update.setInt(3, schoolClass.subject.id);
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
     */
    @Override
    public boolean deleteClass(SchoolClass schoolClass) throws DataUpdateException, DataAccessException {
        boolean success;

        try {
            if (delete == null || delete.isClosed()) {
                initStatement(StatementType.DELETE);
            }

            delete.setInt(1, schoolClass.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
        }

        try {
            delete.executeQuery();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return success;
    }

    private void initStatement(StatementType type) throws DataAccessException {
        MapBuilder<String, String> builder = new MapBuilder<>(new HashMap<>());

        try {
            switch (type) {
                case SELECT_JOIN:
                    selectId = connection.prepareStatement(type.getSql(builder.put("table", "class")
                            .put("columns", "class.className,subject.id,subject.subjectName")
                            .put("where", "id=?")
                            .put("table2", "subject")
                            .put("join_key", "class.subjectId=subject.id")
                            .build()));
                    break;
                case SELECT:
                    selectAllSubject = connection.prepareStatement(type.getSql(builder.put("table", "class").put("columns", "id,className,subjectId").put("where", "subjectId=?").build()));
                    break;
                case UPDATE:
                    update = connection.prepareStatement(type.getSql(builder.put("table", "class").put("set", "className=?,subjectId=?").put("where", "id=?").build()));
                    break;
                case INSERT:
                    insert = connection.prepareStatement(type.getSql(builder.put("table", "class").put("columns", "className,subjectId").put("values", "?,?").build()));
                    break;
                case DELETE:
                    delete = connection.prepareStatement(type.getSql(builder.put("table", "class").put("where", "id=?").build()));
                    break;
                case SELECT_ALL:
                    selectAll = connection.prepareStatement(type.getSql(builder.put("table", "class").put("columns", "id,className,subjectId").build()));
                    break;
                case GET_LAST_AUTO_INCRE:
                    getLastId = connection.prepareStatement(type.getSql(null));
                    break;
                case SELECT_ALL_JOIN:
                    selectAll = connection.prepareStatement(type.getSql(builder.put("table", "class")
                            .put("columns", "class.id,class.className,subject.id,subject.name")
                            .put("table2", "subject")
                            .put("join_key", "class.subjectId=subject.id")
                            .build()));
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }
    }
}
