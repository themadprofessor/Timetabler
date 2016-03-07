package me.timetabler.data.mariadb;

import me.timetabler.data.Day;
import me.timetabler.data.Period;
import me.timetabler.data.dao.PeriodDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.util.Log;
import me.util.MapBuilder;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by stuart on 07/03/16.
 */
public class MariaPeriodDao implements PeriodDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectDay;
    private PreparedStatement selectStart;
    private PreparedStatement selectEnd;

    public MariaPeriodDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Period> getAll() {
        ArrayList<Period> periods = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                initStatement(StatementType.SELECT_ALL_JOIN, false);
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

    @Override
    public List<Period> getAllByDay(Day day) {
        ArrayList<Period> periods = new ArrayList<>();

        try {
            if (selectDay == null || selectDay.isClosed()) {
                initStatement(StatementType.SELECT, false);
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

    @Override
    public List<Period> getByStartTime(LocalTime time) {
        ArrayList<Period> periods = new ArrayList<>();

        try {
            if (selectStart == null || selectStart.isClosed()) {
                initStatement(StatementType.SELECT_JOIN, true);
            }

            selectStart.setTime(1, Time.valueOf(time));
            ResultSet set = selectStart.executeQuery();
            while (set.next()) {
                Period period = new Period(set.getInt(1), new Day(set.getInt(3), set.getString(4)), time, set.getTime(2).toLocalTime());
                periods.add(period);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return periods;
    }

    @Override
    public List<Period> getByEndTime(LocalTime time) {
        ArrayList<Period> periods = new ArrayList<>();

        try {
            if (selectEnd == null || selectEnd.isClosed()) {
                 initStatement(StatementType.SELECT_JOIN, false);
            }

            selectEnd.setTime(1, Time.valueOf(time));
            ResultSet set = selectEnd.executeQuery();
            while (set.next()) {
                Period period = new Period(set.getInt(1), new Day(set.getInt(3), set.getString(4)), set.getTime(2).toLocalTime(), time);
                periods.add(period);
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return periods;
    }

    private void initStatement(StatementType type, boolean start) {
        MapBuilder<String, String> builder = new MapBuilder<>(new HashMap<>());
        try {
            switch (type) {
                case SELECT_ALL_JOIN:
                    selectAll = connection.prepareStatement(type.getSql(builder.put("table", "period")
                            .put("columns", "period.id,period.startTime,period.endTime,dayOfWeek.id,dayOfWeek.dayOfWeek")
                            .put("table2", "dayOfWeek")
                            .put("join_key", "period.dayId=dayOfWeek.id")
                            .build()));
                    break;
                case SELECT:
                    selectDay = connection.prepareStatement(type.getSql(builder.put("table", "period")
                            .put("columns", "id,startTime,endTime")
                            .put("where", "dayId=?")
                            .build()));
                    break;
                case SELECT_JOIN:
                    if (start) {
                        selectStart = connection.prepareCall(type.getSql(builder.put("table", "period")
                                .put("columns", "period.id,period.endTime,dayOfWeek.id,dayOfWeek.dayOfWeek")
                                .put("table2", "dayOfWeek")
                                .put("join_key", "period.dayId=dayOfWeek.id")
                                .put("where", "startTime=?").build()));
                        break;
                    } else {
                        selectEnd = connection.prepareCall(type.getSql(builder.put("table", "period")
                                .put("columns", "period.id,period.startTime,dayOfWeek.id,dayOfWeek.dayOfWeek")
                                .put("table2", "dayOfWeek")
                                .put("join_key", "period.dayId=dayOfWeek.id")
                                .put("where", "endTime=?").build()));
                        break;
                    }
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }
    }
}
