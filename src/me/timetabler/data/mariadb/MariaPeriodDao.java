package me.timetabler.data.mariadb;

import me.timetabler.data.Day;
import me.timetabler.data.Period;
import me.timetabler.data.dao.PeriodDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.sql.JoinClause;
import me.timetabler.data.sql.JoinType;
import me.timetabler.data.sql.SqlBuilder;
import me.timetabler.data.sql.StatementType;
import me.util.Log;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@inheritDoc}
 * The dao will utilise a MariaDB database as it data source.
 */
public class MariaPeriodDao implements PeriodDao {
    /**
     * The connection to the database, which all the PreparedStatements rely on.
     */
    protected Connection connection;

    /**
     * A PreparedStatement which is used to select all periods from the database.
     */
    private PreparedStatement selectAll;

    /**
     * A PreparedStatement which is used to select a period with a given id from the database.
     */
    private PreparedStatement selectId;

    /**
     * A PreparedStatement which is used to select all periods on a given day from the database.
     */
    private PreparedStatement selectDay;

    /**
     * A PreparedStatement which is used to select all periods with a given start time from the database.
     */
    private PreparedStatement selectStart;

    /**
     * A PreparedStatement which is used to select all periods with a given end time from the database.
     */
    private PreparedStatement selectEnd;


    /**
     * Initialises the dao with the given connection. The statements are initialised when required.
     * @param connection The connection to the database.
     */
    public MariaPeriodDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     * This method will get the period data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<Period> getAll() throws DataAccessException {
        ArrayList<Period> periods = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("period", StatementType.SELECT)
                        .addColumns("period.id", "dayOfWeek.id", "dayOfWeek.dayOfWeek", "period.startTime", "period.endTime")
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"));
                selectAll = connection.prepareStatement(builder.build());
            }
            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Period period = new Period(set.getInt(1), new Day(set.getInt(4), set.getString(5)), set.getTime(2).toLocalTime(), set.getTime(3).toLocalTime());
                periods.add(period);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return periods;
    }

    /**
     * {@inheritDoc}
     * This method will get the period data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public Optional<Period> getById(int id) throws DataAccessException {
        Period period = null;

        try {
            if (selectId == null || selectId.isClosed()) {
                SqlBuilder builder = new SqlBuilder("period", StatementType.SELECT)
                        .addColumns("period.id", "dayOfWeek.id", "dayOfWeek.dayOfWeek", "period.startTime", "period.endTime")
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"));
                selectId = connection.prepareStatement(builder.build());
            }
            ResultSet set = selectId.executeQuery();
            if (set.next()) {
                period = new Period(set.getInt(1), new Day(set.getInt(4), set.getString(5)), set.getTime(2).toLocalTime(), set.getTime(3).toLocalTime());
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return Optional.ofNullable(period);
    }

    /**
     * {@inheritDoc}
     * This method will get the period data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<Period> getAllByDay(Day day) throws DataAccessException {
        ArrayList<Period> periods = new ArrayList<>();

        try {
            if (selectDay == null || selectDay.isClosed()) {
                SqlBuilder builder = new SqlBuilder("period", StatementType.SELECT)
                        .addColumns("id", "startTime", "endTime")
                        .addWhereClause("dayId=?");
                selectDay = connection.prepareCall(builder.build());
            }
            selectDay.setInt(1, day.id);
            ResultSet set = selectDay.executeQuery();
            while (set.next()) {
                Period period = new Period(set.getInt(1), day, set.getTime(2).toLocalTime(), set.getTime(3).toLocalTime());
                periods.add(period);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return periods;
    }

    /**
     * {@inheritDoc}
     * This method will get the period data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<Period> getByStartTime(LocalTime time) throws DataAccessException {
        ArrayList<Period> periods = new ArrayList<>();

        try {
            if (selectStart == null || selectStart.isClosed()) {
                SqlBuilder builder = new SqlBuilder("period", StatementType.SELECT)
                        .addColumns("period.id", "dayOfWeek.id", "dayOfWeek.dayOfWeek", "period.endTime")
                        .addWhereClause("period.startTime=?")
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"));
                selectStart = connection.prepareStatement(builder.build());
            }

            selectStart.setTime(1, Time.valueOf(time));
            ResultSet set = selectStart.executeQuery();
            while (set.next()) {
                Period period = new Period(set.getInt(1), new Day(set.getInt(2), set.getString(3)), time, set.getTime(4).toLocalTime());
                periods.add(period);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return periods;
    }


    /**
     * {@inheritDoc}
     * This method will get the period data from a MariaDB database.
     * This method assumes the connection member is not null and open. Therefore, should be called through MariaDaoManager.
     */
    @Override
    public List<Period> getByEndTime(LocalTime time) throws DataAccessException {
        ArrayList<Period> periods = new ArrayList<>();

        try {
            if (selectEnd == null || selectEnd.isClosed()) {
                 SqlBuilder builder = new SqlBuilder("period", StatementType.SELECT)
                        .addColumns("period.id", "dayOfWeek.id", "dayOfWeek.dayOfWeek", "period.startTime")
                        .addWhereClause("period.endTime=?")
                        .addJoinClause(new JoinClause(JoinType.INNER, "dayOfWeek", "period.dayId=dayOfWeek.id"));
                 selectEnd = connection.prepareStatement(builder.build());
            }

            selectEnd.setTime(1, Time.valueOf(time));
            ResultSet set = selectEnd.executeQuery();
            while (set.next()) {
                Period period = new Period(set.getInt(1), new Day(set.getInt(2), set.getString(3)), set.getTime(4).toLocalTime(), time);
                periods.add(period);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return periods;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            if (selectAll != null) selectAll.close();
            if (selectDay != null) selectDay.close();
            if (selectEnd != null) selectEnd.close();
            if (selectStart != null) selectStart.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            Log.error(e);
        }
    }
}
