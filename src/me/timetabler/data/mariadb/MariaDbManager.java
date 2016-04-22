package me.timetabler.data.mariadb;

import javafx.scene.control.Alert;
import me.timetabler.ui.main.JavaFxBridge;
import me.util.CollectionBuilder;
import me.util.Log;
import me.util.LogLevel;
import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Manages the database server process. The database server will be ran in the current user, and expected to be
 * connected to via TCP.
 */
public class MariaDbManager implements AutoCloseable {
    /**
     * The server process.
     */
    private Process process;

    /**
     * Initialises the server with the given config. The config map must contain the key/value combinations 'exec' and
     * 'args', where exec is the path to mysqld and args is a space separated list of arguments for mysqld.
     * @param config The config map for mariadb.
     * @throws IOException Thrown if any IO exception occurs.
     */
    public MariaDbManager(Map<String, String> config) throws IOException {
        ArrayList<String> command = (ArrayList<String>) new CollectionBuilder<String>(new ArrayList<>())
                .add(config.get("exec"))
                .addAll(config.get("args").split(" "))
                .build();

        Log.debug("Starting MariaDB with [" + (command.size() - 1) + "] arguments");
        Log.verbose("Staring MariaDB with the following arguments [" + command.toString() + ']');

        if (Log.willLog(LogLevel.ERROR)) {
            process = new ProcessBuilder().command(command).redirectError(ProcessBuilder.Redirect.INHERIT).start();
        } else if (Log.willLog(LogLevel.INFO)) {
            process = new ProcessBuilder().command(command).inheritIO().start();
        }
        Runtime.getRuntime().addShutdownHook(new Thread (() -> process.destroyForcibly()));
        if (!process.isAlive()) {
            JavaFxBridge.createAlert(Alert.AlertType.ERROR, "Failed to start database!", null, "Failed to start database server. It close with the error code [" + process.exitValue() +']', true);
        }
    }

    /**
     * Returns a new data source which represents the database. The config map must contain the key/value combinations
     * 'database', 'addr ,'port', 'username' and 'password'. This method does not store the data source between calls,
     * and creates a new data source for each call.
     * @param config The config map for mariadb.
     * @return The data source.
     */
    public DataSource getDataSource(Map<String, String> config) {
        //Ensure the server is still running
        if (!process.isAlive()) {
            JavaFxBridge.createAlert(Alert.AlertType.ERROR, "Failed to connect to the database!", null, "Failed to connect to the database server. It close with the error code [" + process.exitValue() +']', true);
        }

        MariaDbDataSource mariaDbDataSource = new MariaDbDataSource();

        mariaDbDataSource.setPort(Integer.parseInt(config.get("port")));
        mariaDbDataSource.setServerName(config.get("addr"));
        mariaDbDataSource.setDatabaseName(config.get("database"));
        mariaDbDataSource.setUserName(config.get("username"));
        mariaDbDataSource.setPassword(config.get("password"));
        Log.debug("Created new data source");

        return mariaDbDataSource;
    }

    /**
     * Stops the database server process.
     */
    @Override
    public void close() {
        if (process != null && process.isAlive()) process.destroy();
    }
}
