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
 * Created by stuart on 02/03/16.
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
     * {@inheritdoc}
     */
    @Override
    public List<SchoolClass> getAllClasses() {
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
     * {@inheritdoc}
     */
    @Override
    public List<SchoolClass> getAllBySubject(Subject subject) {
        ArrayList<SchoolClass> classes = new ArrayList<>();

        try {
            if (selectAllSubject == null || selectAllSubject.isClosed()) {
                initStatement(StatementType.SELECT);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
        }

        try {
            selectAllSubject.setInt(1, subject.id);

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
     * {@inheritdoc}
     */
    @Override
    public Optional<SchoolClass> getById(int id) {
        SchoolClass schoolClass;

        try {
            if (selectId == null || selectId.isClosed()) {
                initStatement(StatementType.SELECT);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            selectId.setInt(1, id);

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
     * {@inheritdoc}
     */
    @Override
    public int insertClass(SchoolClass schoolClass) {
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
     * {@inheritdoc}
     */
    @Override
    public boolean updateClass(SchoolClass schoolClass) {
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
     * {@inheritdoc}
     */
    @Override
    public boolean deleteClass(SchoolClass schoolClass) {
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

    private void initStatement(StatementType type) {
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
