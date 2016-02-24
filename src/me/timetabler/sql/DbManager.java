package me.timetabler.sql;

import me.util.Log;

import java.sql.*;
import java.util.List;
import java.util.Optional;

/**
 * Created by stuart on 24/02/16.
 */
public class DbManager {
    private Connection connection;

    public DbManager(String addr, String username, String password, String driverName) throws SQLException, ClassNotFoundException {
        connection = DriverManager.getConnection(addr, username, password);
    }

    public Optional<ResultSet> sendSQL(String statement) throws SQLException {
        Statement sql = connection.createStatement();
        if (sql.execute(statement)) {
            return Optional.of(sql.getResultSet());
        } else {
            return Optional.empty();
        }
    }

    public int[] sendSqlBatch(List<String> queries) throws SQLException {
        Statement sql = connection.createStatement();
        queries.forEach(statement -> {
            try {
                sql.addBatch(statement);
            } catch (SQLException e) {
                Log.error(e);
            }
        });
        return sql.executeBatch();
    }

    public void close() throws SQLException {
        connection.close();
    }
}
