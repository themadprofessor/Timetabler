package me.timetabler.sql;

import me.util.Log;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

/**
 * The interface between the system and the database. The interface does not return nulls and will catch all SQLExceptions.
 */
public class Database {
    private String addr, username, password;
    private DataSource source;

    /**
     * Initialises the database interface. Can throw a DatabaseConnectionException if it cannot connect to the database.
     * @param addr The url for the server, which must be in the form <b>jdbc:<i>subprotocol</i>:<i>subname</i></b>
     * @param username The username of the user on the server.
     * @param password The password of the user.
     */
    public Database(String addr, String username, String password) {
        this.addr = addr;
        this.username = username;
        this.password = password;

        Connection connection = null;
        try {
            MariaDbDataSource dataSource = new MariaDbDataSource(addr);
            dataSource.setUser(username);
            dataSource.setPassword(password);
            source = dataSource;

            connection = openConn();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing DatabaseConnectionException.");
            throw new DatabaseConnectionException(addr, e);
        } finally {
            if (connection != null) {
                try {
                    closeConn(connection);
                } catch (SQLException e) {
                    Log.error(e);
                }
            }
        }


    }

    /**
     * Sends the given SQL statement to the server specified at init. Returns the result within an optional as to avoid null pointers if the query does return values such as INSERT or UPDATE.
     * @param statement The SQL statement to be sent to the server. Must be a valid statement, as the statement is not check until it reaches the server.
     * @return Returns an optional which can contain the result of the request if appropriate.
     */
    public Optional<ResultSet> sendSQL(String statement) {
        Optional<ResultSet> resultSet = Optional.empty();
        Connection connection = null;
        try {
            connection = openConn();
            Statement sql = connection.createStatement();
            if (sql.execute(statement)) {
                resultSet = Optional.of(sql.getResultSet());
            }
        } catch (SQLException e) {
            Log.error(e);
        } finally {
            if (connection != null) {
                try {
                    closeConn(connection);
                } catch (SQLException e) {
                    Log.error(e);
                }
            }
        }
        return resultSet;
    }

    /**
     * Sends the given list of SQL statements to the server as a batch query which is more optimal than multiple single requests.
     * @param queries The queries to to be sent to the server.
     * @return Returns an array containing the number of row changes per request, as each element in the order sent to the server.
     */
    public int[] sendSqlBatch(List<String> queries) {
        int[] changes = new int[0];
        Connection connection = null;
        try {
            connection = openConn();
            if (!connection.getMetaData().supportsBatchUpdates()) {
                throw new SQLFeatureNotSupportedException("The server [" + addr + "] does not support batch updates!");
            }
            Statement sql = connection.createStatement();
            queries.forEach(statement -> {
                try {
                    sql.addBatch(statement);
                } catch (SQLException e) {
                    Log.error(e);
                }
            });
            changes = sql.executeBatch();
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so throwing a DatabaseUpdateException");
            throw new DatabaseUpdateException(e);
        } finally {
            if (connection != null) {
                try {
                    closeConn(connection);
                } catch (SQLException e) {
                    Log.error(e);
                }
            }
        }

        return changes;
    }

    /**
     * Opens a connection to the server using the variables addr, username and password.
     * @return The connection to the server. This connection should be closed after it is no longer needed and should not be kept open.
     * @throws DatabaseConnectionException Thrown if the connection cannot be established with the server.
     */
    private Connection openConn() {
        try {
            return source.getConnection(username, password);
        } catch (SQLException e) {
            Log.debug("Caught [" + e + "] so have thrown DatabaseConnectionException.");
            throw new DatabaseConnectionException(addr, e);
        }
    }

    /**
     * Closes the given connection to the server. Currently, just calls the connection's close method.
     * @param connection The connection to to be closed.
     * @throws SQLException Thrown if a database error occurs.
     */
    private void closeConn(Connection connection) throws SQLException {
        connection.close();
    }
}

