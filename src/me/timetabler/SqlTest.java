package me.timetabler;

import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.data.mariadb.MariaDaoManager;
import me.timetabler.data.sql.JoinClause;
import me.timetabler.data.sql.JoinType;
import me.timetabler.data.sql.SqlBuilder;
import me.timetabler.data.sql.StatementType;
import me.util.Log;
import me.util.LogLevel;
import me.util.MapBuilder;

import java.util.HashMap;

/**
 * Created by stuart on 29/02/16.
 */
public class SqlTest {
    public static void main(String[] args) throws DataConnectionException, DataAccessException {
        Log.LEVEL = LogLevel.VERBOSE;

        MapBuilder<String, String> config = new MapBuilder<>(new HashMap<>());
        DaoManager manager = new MariaDaoManager(config.put("type", "MARIADB").put("addr", "127.0.0.1").put("port", "3306").put("database", "school").build());

        SqlBuilder builder = new SqlBuilder("distance", StatementType.SELECT)
                .addColumns("distance.id", "distance.startRoomId", "distance.endRoomId", "classroom1.roomName",
                        "classroom1.buildingId", "building1.buildingName", "classroom1.subjectId",
                        "subject1.subjectName", "classroom2.roomName", "classroom2.buildingId",
                        "building2.buildingName", "classroom2.subjectId", "subject2.subjectName")
                .addJoinClause(new JoinClause(JoinType.INNER, "classroom classroom1", "classroom1.id=distance.startRoomId"))
                .addJoinClause(new JoinClause(JoinType.INNER, "building building1", "building1.id=classroom1.buildingId"))
                .addJoinClause(new JoinClause(JoinType.INNER, "classroom classroom2", "classroom2.id=distance.startRoomId"))
                .addJoinClause(new JoinClause(JoinType.INNER, "building building2", "building2.id=classroom2.buildingId"));
        Log.info(builder.build());
    }
}
