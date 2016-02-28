package me.timetabler.sql;

/**
 * Created by stuart on 26/02/16.
 */
public class DatabaseAccessException extends DatabaseException {
    public DatabaseAccessException(Throwable cause) {
        super("Failed to access database!", cause);
    }
}
