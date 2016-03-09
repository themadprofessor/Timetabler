package me.timetabler.data.mariadb;

import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.StaffDao;
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
public class MariaStaffDao implements StaffDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectAllSubject;
    private PreparedStatement selectId;
    private PreparedStatement getLastId;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    public MariaStaffDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Staff> getAllStaff() throws DataAccessException, DataUpdateException {
        ArrayList<Staff> staff = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                initStatement(StatementType.SELECT_ALL);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Staff s = new Staff(set.getInt(1), set.getString(2), new Subject(set.getInt(3), set.getString(4)));
                staff.add(s);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return staff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Staff> getAllBySubject(Subject subject) throws DataAccessException, DataUpdateException {
        ArrayList<Staff> staff = new ArrayList<>();

        try {
            if (selectAllSubject == null || selectAllSubject.isClosed()) {
                initStatement(StatementType.SELECT);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            selectAllSubject.setInt(1, subject.id);
            ResultSet set = selectAllSubject.executeQuery();
            while (set.next()) {
                Staff s = new Staff(set.getInt(1), set.getString(2), subject);
                staff.add(s);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a a DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return staff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Staff> getById(int id) throws DataAccessException, DataUpdateException {
        Staff staff = null;

        try {
            if (selectId == null || selectId.isClosed()) {
                initStatement(StatementType.SELECT_JOIN);
            }
            selectId.setInt(1, id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            ResultSet set = selectId.executeQuery();
            set.next();
            Subject sub = new Subject(set.getInt(2), set.getString(3));
            staff = new Staff(id, set.getString(1), sub);
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataUpdateException!");
            throw new DataUpdateException(e);
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
    public int insertStaff(Staff staff) throws DataAccessException, DataUpdateException {
        int id = -1;

        try {
            if (insert == null || insert.isClosed()) {
                initStatement(StatementType.INSERT);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            insert.setString(1, staff.name);
            insert.setInt(2, staff.subject.id);
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
    public boolean updateStaff(Staff staff) throws DataAccessException, DataUpdateException {
        boolean success = false;

        try {
            if (update == null || update.isClosed()) {
                initStatement(StatementType.UPDATE);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            update.setString(1, staff.name);
            update.setInt(2, staff.subject.id);
            update.setInt(3, staff.id);
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
    public boolean deleteStaff(Staff staff) throws DataAccessException, DataUpdateException {
        boolean success = false;

        try {
            if (delete == null || delete.isClosed()) {
                initStatement(StatementType.DELETE);
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
     * Initialises a prepared statement base on the given statement type.
     * @param type The type of statement to initialise.
     */
    private void initStatement(StatementType type) throws DataAccessException {
        MapBuilder<String, String> builder = new MapBuilder<>(new HashMap<>());
        try {
            switch (type) {
                case SELECT_JOIN:
                    selectId = connection.prepareStatement(type.getSql(builder.put("table", "staff")
                            .put("columns", "staff.staffName,subject.id,subject.subjectName")
                            .put("where", "staff.id=?")
                            .put("table2", "subject")
                            .put("join_key", "staff.subjectId=subject.id")
                            .build()));
                    break;
                case SELECT:
                    selectAllSubject = connection.prepareStatement(type.getSql(builder.put("table", "staff").put("columns", "id,staffName,subjectId").put("where", "subjectId=?").build()));
                    break;
                case UPDATE:
                    update = connection.prepareStatement(type.getSql(builder.put("table", "staff").put("set", "staffName=?,subjectId=?").put("where", "id=?").build()));
                    break;
                case INSERT:
                    insert = connection.prepareStatement(type.getSql(builder.put("table", "staff").put("columns", "staffName,subjectId").put("values", "?,?").build()), Statement.RETURN_GENERATED_KEYS);
                    break;
                case DELETE:
                    delete = connection.prepareStatement(type.getSql(builder.put("table", "staff").put("where", "id=?").build()));
                    break;
                case SELECT_ALL_JOIN:
                    selectAll = connection.prepareStatement(type.getSql(builder.put("table", "staff")
                            .put("columns", "staff.id,staff.staffName,subject.id,subject.subjectName")
                            .put("table2", "subject")
                            .put("join_key", "staff.subjectId=subject.id")
                            .build()));
                    break;
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }
    }
}
