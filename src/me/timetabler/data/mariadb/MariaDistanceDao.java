package me.timetabler.data.mariadb;

import me.timetabler.data.Classroom;
import me.timetabler.data.Distance;
import me.timetabler.data.dao.DistanceDao;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataUpdateException;
import me.timetabler.data.sql.SqlBuilder;
import me.timetabler.data.sql.StatementType;
import me.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by stuart on 13/03/16.
 */
public class MariaDistanceDao implements DistanceDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectId;
    private PreparedStatement selectAllRoom;
    private PreparedStatement selectTwoRooms;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    @Override
    public List<Distance> getAllDistances() throws DataAccessException {
        ArrayList<Distance> distances = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("distance", StatementType.SELECT)
                        .addColumns("distance.id", "");
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return distances;
    }

    @Override
    public Optional<Distance> getById(int id) throws DataAccessException {
        Distance distance = null;

        try {
            if (selectId == null || selectId.isClosed()) {
                SqlBuilder builder = new SqlBuilder("distance", StatementType.SELECT);
            }
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        if (distance == null) {
            return Optional.empty();
        } else {
            return Optional.of(distance);
        }
    }

    @Override
    public Optional<Distance> getDistanceBetween(Classroom classroom1, Classroom classroom2) throws DataAccessException {
        Distance distance = null;

        try {
            if (selectTwoRooms == null || selectTwoRooms.isClosed()) {
                SqlBuilder builder = new SqlBuilder("distance", StatementType.SELECT)
                        .addColumns("id,distance")
                        .addWhereClause("(startRoomId=? AND endRoomId=?) OR (startRoomId=? AND endRoomId=?");
                selectTwoRooms = connection.prepareStatement(builder.build());
            }

            selectTwoRooms.setInt(1, classroom1.id);
            selectTwoRooms.setInt(4, classroom1.id);
            selectTwoRooms.setInt(2, classroom2.id);
            selectTwoRooms.setInt(3, classroom2.id);

            ResultSet set = selectTwoRooms.executeQuery();
            set.next();
            distance = new Distance(set.getInt(1), classroom1, classroom2, set.getInt(2));
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        if (distance == null) {
            return Optional.empty();
        } else {
            return Optional.of(distance);
        }
    }

    @Override
    public List<Distance> getAllDistancesFrom(Classroom classroom) throws DataAccessException {
        return null;
    }

    @Override
    public int insertDistance(Distance distance) throws DataUpdateException, DataAccessException {
        return 0;
    }

    @Override
    public boolean updateDistance(Distance distance) throws DataUpdateException, DataAccessException {
        return false;
    }

    @Override
    public boolean deleteDistance(Distance distance) throws DataUpdateException, DataAccessException {
        return false;
    }
}
