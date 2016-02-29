package me.timetabler.data.exceptions;

/**
 * Created by stuart on 25/02/16.
 */
public class DatabaseConnectionException extends DatabaseException {
    public DatabaseConnectionException(String addr) {
        super("Failed to connect to server [" + addr + "]");
    }

    public DatabaseConnectionException(String addr, Throwable cause) {
        super("Failed to connect to server [" + addr + "]", cause);
    }
}
