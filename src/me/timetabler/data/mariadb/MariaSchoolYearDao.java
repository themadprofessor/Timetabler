package me.timetabler.data.mariadb;

import me.timetabler.data.SchoolYear;
import me.timetabler.data.dao.SchoolYearDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.timetabler.data.sql.SqlBuilder;
import me.timetabler.data.sql.StatementType;
import me.util.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by stuart on 13/03/16.
 */
public class MariaSchoolYearDao implements SchoolYearDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectId;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SchoolYear> getAllSchoolYears() throws DataAccessException {
        ArrayList<SchoolYear> schoolYears = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("schoolYear", StatementType.SELECT);
                selectAll = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                SchoolYear schoolYear = new SchoolYear(set.getInt(1), set.getString(2));
                schoolYears.add(schoolYear);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException");
            throw new DataAccessException(e);
        }

        return schoolYears;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SchoolYear> getById(int id) throws DataUpdateException, DataAccessException {
        SchoolYear schoolYear;

        try {
            if (selectId == null || selectId.isClosed()) {
                SqlBuilder builder = new SqlBuilder("schoolYear", StatementType.SELECT)
                        .addColumn("schoolYearName")
                        .addWhereClause("id=?");
                selectId = connection.prepareStatement(builder.build());
            }

            selectId.setInt(1, id);
            ResultSet set = selectId.executeQuery();
            set.next();
            schoolYear = new SchoolYear(id, set.getString(1));
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        if (schoolYear == null) {
            return Optional.empty();
        } else {
            return Optional.of(schoolYear);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int insertSchoolYear(SchoolYear schoolYear) throws DataUpdateException, DataAccessException {
        int id = -1;

        try {
            if (insert == null || insert.isClosed()) {
                SqlBuilder builder = new SqlBuilder("schoolYear", StatementType.INSERT)
                        .addColumn("schoolYearName")
                        .addValue("?");
                insert = connection.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
            }

            insert.setString(1, schoolYear.schoolYearName);
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
    public boolean updateSchoolYear(SchoolYear schoolYear) throws DataUpdateException, DataAccessException {
        boolean success;

        try {
            if (update == null || update.isClosed()) {
                SqlBuilder builder = new SqlBuilder("schoolYear", StatementType.UPDATE)
                        .addSetClause("schoolYear=?")
                        .addWhereClause("id=?");
                update = connection.prepareStatement(builder.build());
            }

            update.setInt(2, schoolYear.id);
            update.setString(1, schoolYear.schoolYearName);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            update.execute();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteSchoolYear(SchoolYear schoolYear) throws DataUpdateException, DataAccessException {
        boolean success;

        try {
            if (delete == null || delete.isClosed()) {
                SqlBuilder builder = new SqlBuilder("schoolYear", StatementType.DELETE)
                        .addWhereClause("is=?");
                delete = connection.prepareStatement(builder.build());
            }

            delete.setInt(1, schoolYear.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
            throw new DataAccessException(e);
        }

        try {
            delete.execute();
            success = true;
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataUpdateException!");
            throw new DataUpdateException(e);
        }

        return success;
    }
}