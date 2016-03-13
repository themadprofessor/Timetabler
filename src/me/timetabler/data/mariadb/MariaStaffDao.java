package me.timetabler.data.mariadb;

import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.StaffDao;
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
                SqlBuilder builder = new SqlBuilder("staff", StatementType.SELECT)
                        .addColumns("staff.id", "staff.staffName", "subject.id", "subject.subjectName")
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "staff.subjectId=subject.id"));
                selectAll = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Staff s = new Staff(set.getInt(1), set.getString(2), new Subject(set.getInt(3), set.getString(4)));
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
    public List<Staff> getAllBySubject(Subject subject) throws DataAccessException, DataUpdateException {
        ArrayList<Staff> staff = new ArrayList<>();

        try {
            if (selectAllSubject == null || selectAllSubject.isClosed()) {
                SqlBuilder builder = new SqlBuilder("staff", StatementType.SELECT)
                        .addColumns("id", "staffName")
                        .addWhereClause("subjectId=?");
                selectAllSubject = connection.prepareStatement(builder.build());
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
                SqlBuilder builder = new SqlBuilder("staff", StatementType.SELECT)
                        .addColumns("staff.staffName", "subject.id", "subject.subjectName")
                        .addWhereClause("staff.id=?")
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "staff.subjectId=subject.id"));
                selectId = connection.prepareStatement(builder.build());
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
                SqlBuilder builder = new SqlBuilder("staff", StatementType.INSERT)
                        .addColumns("staffName", "subjectId")
                        .addValues("?", "?");
                insert = connection.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
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
                SqlBuilder builder = new SqlBuilder("staff", StatementType.UPDATE)
                        .addSetClauses("staffName=?", "subjectId=?")
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
}
