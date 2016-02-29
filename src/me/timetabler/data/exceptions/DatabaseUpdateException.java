package me.timetabler.data.exceptions;

/**
 * Created by stuart on 25/02/16.
 */
public class DatabaseUpdateException extends DatabaseException {
    public DatabaseUpdateException(Throwable cause) {
        super("Failed to update database!" ,cause);
    }
}
