package me.timetabler.data.mariadb;

import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.StaffDao;
import me.timetabler.data.exceptions.DatabaseAccessException;
import me.timetabler.data.exceptions.DatabaseUpdateException;
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
 * An implementation of StaffDao which handles a MariaDB data source.
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
    public List<Staff> getAllStaff() {
        ArrayList<Staff> staff = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                initStatement(StatementType.SELECT_ALL, false);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a a DatabaseAccessException!");
            throw new DatabaseAccessException(e);
        }

        try {
            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Staff s = new Staff(set.getInt(1), set.getString(2), set.getInt(3));
                staff.add(s);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a a DatabaseUpdateException!");
            throw new DatabaseUpdateException(e);
        }

        return staff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Staff> getAllBySubject(Subject subject) {
        ArrayList<Staff> staff = new ArrayList<>();

        try {
            if (selectAllSubject == null || selectAllSubject.isClosed()) {
                initStatement(StatementType.SELECT, true);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DatabaseAccessException!");
            throw new DatabaseAccessException(e);
        }

        try {
            ResultSet set = selectAllSubject.executeQuery();
            while (set.next()) {
                Staff s = new Staff(set.getInt(1), set.getString(2), set.getInt(3));
                staff.add(s);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a a DatabaseUpdateException!");
        }

        return staff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Staff> getById(int id) {
        Staff staff = null;

        try {
            if (selectId == null || selectId.isClosed()) {
                initStatement(StatementType.SELECT, false);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DatabaseAccessException!");
            throw new DatabaseAccessException(e);
        }

        try {
            ResultSet set = selectId.executeQuery();
            set.next();
            staff = new Staff(set.getInt(1), set.getString(2), set.getInt(3));
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DatabaseUpdateException!");
            throw new DatabaseUpdateException(e);
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
    public int insertStaff(Staff staff) {
        int id = -1;

        try {
            if (insert == null || insert.isClosed()) {
                initStatement(StatementType.INSERT, false);
            }
            if (getLastId == null || insert.isClosed()) {
                initStatement(StatementType.GET_LAST_AUTO_INCRE, false);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DatabaseAccessException!");
            throw new DatabaseAccessException(e);
        }

        try {
            insert.setString(1, staff.name);
            insert.setInt(2, staff.subjectId);

            ResultSet set = insert.executeQuery();
            set.next();
            id = set.getInt(1);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DatabaseUpdateException!");
        }

        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateStaff(Staff staff) {
        boolean success = false;

        try {
            if (update == null || update.isClosed()) {
                initStatement(StatementType.UPDATE, false);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DatabaseAccessException!");
            throw new DatabaseAccessException(e);
        }

        try {
            update.executeUpdate();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DatabaseUpdateException!");
            throw new DatabaseUpdateException(e);
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteStaff(Staff staff) {
        boolean success = false;

        try {
            if (delete == null || delete.isClosed()) {
                initStatement(StatementType.DELETE, false);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DatabaseAccessException!");
            throw new DatabaseAccessException(e);
        }

        try {
            delete.executeUpdate();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DatabaseUpdateException!");
            throw new DatabaseUpdateException(e);
        }

        return success;
    }

    private void initStatement(StatementType type, boolean subject) {
        MapBuilder<String, String> builder = new MapBuilder<>(new HashMap<>());
        try {
            switch (type) {
                case SELECT:
                    if (!subject) {
                        selectId = connection.prepareStatement(type.getSql(builder.put("table", "staff").put("columns", "staffName,subjectId").put("where", "id=?").build()));
                    } else {
                        selectAllSubject = connection.prepareStatement(type.getSql(builder.put("table", "staff").put("columns", "staffName,subjectId").put("where", "subjectId=?").build()));
                    }
                    break;
                case UPDATE:
                    update = connection.prepareStatement(type.getSql(builder.put("table", "staff").put("set", "staffName=?,subjectId=?").put("where", "id=?").build()));
                    break;
                case INSERT:
                    insert = connection.prepareStatement(type.getSql(builder.put("table", "staff").put("columns", "staffName,subjectId").put("values", "?,?").build()));
                    break;
                case DELETE:
                    delete = connection.prepareStatement(type.getSql(builder.put("table", "staff").put("where", "id=?").build()));
                    break;
                case SELECT_ALL:
                    selectAll = connection.prepareStatement(type.getSql(builder.put("table", "staff").put("columns", "staffName,subjectId").build()));
                    break;
                case GET_LAST_AUTO_INCRE:
                    getLastId = connection.prepareStatement(type.getSql(null));
                    break;
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DatabaseAccessException!");
            throw new DatabaseAccessException(e);
        }
    }
}
