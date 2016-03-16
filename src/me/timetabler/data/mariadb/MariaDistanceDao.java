package me.timetabler.data.mariadb;

import me.timetabler.data.Building;
import me.timetabler.data.Classroom;
import me.timetabler.data.Distance;
import me.timetabler.data.Subject;
import me.timetabler.data.dao.DistanceDao;
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
 * Created by stuart on 13/03/16.
 */
public class MariaDistanceDao implements DistanceDao {
    protected Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectId;
    private PreparedStatement selectAllRoomStart;
    private PreparedStatement selectAllRoomEnd;
    private PreparedStatement selectTwoRooms;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Distance> getAllDistances() throws DataAccessException {
        ArrayList<Distance> distances = new ArrayList<>();

        try {
            if (selectAll == null || selectAll.isClosed()) {
                SqlBuilder builder = new SqlBuilder("distance", StatementType.SELECT)
                        .addColumns("distance.id", "distance.startRoomId", "distance.endRoomId", "classroom1.roomName",
                                "classroom1.buildingId", "building1.buildingName", "classroom1.subjectId",
                                "subject1.subjectName", "classroom2.roomName", "classroom2.buildingId",
                                "building2.buildingName", "classroom2.subjectId", "subject2.subjectName", "distance.distance")
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom classroom1", "classroom1.id=distance.startRoomId"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building building1", "building1.id=classroom1.buildingId"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject subject1", "subject1.id=classroom1.subjectId"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom classroom2", "classroom2.id=distance.endRoomId"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building building2", "building2.id=classroom2.buildingId"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject subject2", "subject2.id=classroom2.subjectId"));
                selectAll = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectAll.executeQuery();
            while (set.next()) {
                Subject subject1 = new Subject(set.getInt(7), set.getString(8));
                Subject subject2 = new Subject(set.getInt(12), set.getString(13));
                Building building1 = new Building(set.getInt(5), set.getString(6));
                Building building2 = new Building(set.getInt(10), set.getString(11));
                Classroom classroom1 = new Classroom(set.getInt(2), set.getString(4), building1, subject1);
                Classroom classroom2 = new Classroom(set.getInt(3), set.getString(9), building2, subject2);
                distances.add(new Distance(set.getInt(1), classroom1, classroom2, set.getInt(14)));
            }
            set.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return distances;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Distance> getById(int id) throws DataAccessException {
        Distance distance = null;

        try {
            if (selectId == null || selectId.isClosed()) {
                SqlBuilder builder = new SqlBuilder("distance", StatementType.SELECT)
                        .addColumns("distance.startRoomId", "distance.endRoomId", "classroom1.roomName",
                                "classroom1.buildingId", "building1.buildingName", "classroom1.subjectId",
                                "subject1.subjectName", "classroom2.roomName", "classroom2.buildingId",
                                "building2.buildingName", "classroom2.subjectId", "subject2.subjectName", "distance.distance")
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom classroom1", "classroom1.id=distance.startRoomId"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building building1", "building1.id=classroom1.buildingId"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject subject1", "subject1.id=classroom1.subjectId"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom classroom2", "classroom2.id=distance.startRoomId"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building building2", "building2.id=classroom2.buildingId"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject subject2", "subject2.id=classroom2.subjectId"))
                        .addWhereClause("distance.id=?");
                selectId = connection.prepareStatement(builder.build());
            }

            ResultSet set = selectId.executeQuery();
            set.next();
            Subject subject1 = new Subject(set.getInt(7), set.getString(8));
            Subject subject2 = new Subject(set.getInt(12), set.getString(13));
            Building building1 = new Building(set.getInt(5), set.getString(6));
            Building building2 = new Building(set.getInt(10), set.getString(11));
            Classroom classroom1 = new Classroom(set.getInt(2), set.getString(4), building1, subject1);
            Classroom classroom2 = new Classroom(set.getInt(3), set.getString(9), building2, subject2);
            distance = new Distance(set.getInt(1), classroom1, classroom2, set.getInt(14));
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Distance> getAllDistancesFrom(Classroom classroom) throws DataAccessException {
        ArrayList<Distance> distances = new ArrayList<>();

        try {
            if (selectAllRoomStart == null || selectAllRoomStart.isClosed()) {
                SqlBuilder builder = new SqlBuilder("distance", StatementType.SELECT)
                        .addColumns("distance.id", "classroom.id", "classroom.roomName", "building.id", "buildingName",
                                "subject.id", "subject.subjectName", "distance.distance")
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom", "distance.endRoomId=classroom.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "classroom.subjectId=subject.id"))
                        .addWhereClause("distance.startRoomId=?");
                selectAllRoomStart = connection.prepareStatement(builder.build());
            }

            selectAllRoomStart.setInt(1, classroom.id);
            ResultSet startSet = selectAllRoomStart.executeQuery();
            while (startSet.next()) {
                Distance distance = new Distance(startSet.getInt(1), classroom,
                        new Classroom(startSet.getInt(2), startSet.getString(3),
                                new Building(startSet.getInt(4), startSet.getString(5)),
                                new Subject(startSet.getInt(6), startSet.getString(7))),
                        startSet.getInt(8));
                distances.add(distance);
            }
            startSet.close();

            if (selectAllRoomEnd == null || selectAllRoomEnd.isClosed()) {
                SqlBuilder builder = new SqlBuilder("distance", StatementType.SELECT)
                        .addColumns("distance.id", "classroom.id", "classroom.roomName", "building.id", "buildingName",
                                "subject.id", "subject.subjectName", "distance.distance")
                        .addJoinClause(new JoinClause(JoinType.INNER, "classroom", "distance.startRoomId=classroom.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "building", "classroom.buildingId=building.id"))
                        .addJoinClause(new JoinClause(JoinType.INNER, "subject", "classroom.subjectId=subject.id"))
                        .addWhereClause("distance.endRoomId=?");
                selectAllRoomEnd = connection.prepareStatement(builder.build());
            }

            selectAllRoomEnd.setInt(1, classroom.id);
            ResultSet endSet = selectAllRoomEnd.executeQuery();
            while (endSet.next()) {
                Distance distance = new Distance(endSet.getInt(1), classroom,
                        new Classroom(endSet.getInt(2), endSet.getString(3),
                                new Building(endSet.getInt(4), endSet.getString(5)),
                                new Subject(endSet.getInt(6), endSet.getString(7))),
                        endSet.getInt(8));
                distances.add(distance);
            }
            endSet.close();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataAccessException(e);
        }

        return distances;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int insertDistance(Distance distance) throws DataUpdateException, DataAccessException {
        int id = -1;

        try {
            if (insert == null || insert.isClosed()) {
                SqlBuilder builder = new SqlBuilder("distance", StatementType.INSERT)
                        .addColumns("startRoomId", "endRoomId", "distance")
                        .addValue("?,?,?");
                insert = connection.prepareStatement(builder.build(), Statement.RETURN_GENERATED_KEYS);
            }

            insert.setInt(1, distance.startRoom.id);
            insert.setInt(2, distance.endRoom.id);
            insert.setInt(3, distance.distance);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DataAccessException!");
            throw new DataUpdateException(e);
        }

        try {
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
        }

        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateDistance(Distance distance) throws DataUpdateException, DataAccessException {
        boolean success = false;

        try {
            if (update == null || update.isClosed()) {
                SqlBuilder builder = new SqlBuilder("distance", StatementType.UPDATE)
                        .addSetClauses("startRoomId=?", "endRoomId=?", "distance=?")
                        .addWhereClause("id=?");
                update = connection.prepareStatement(builder.build());
            }

            update.setInt(1, distance.startRoom.id);
            update.setInt(2, distance.endRoom.id);
            update.setInt(3, distance.distance);
            update.setInt(4, distance.id);
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
    public boolean deleteDistance(Distance distance) throws DataUpdateException, DataAccessException {
        boolean success = false;

        try {
            if (delete == null || delete.isClosed()) {
                SqlBuilder builder = new SqlBuilder("distance", StatementType.DELETE)
                        .addWhereClause("id=?");
            }

            delete.setInt(1, distance.id);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DataAccessException!");
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
}
