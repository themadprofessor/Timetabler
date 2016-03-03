package me.timetabler.data.exceptions;

/**
 * Created by stuart on 26/02/16.
 */
public class DataAccessException extends DataException {
    public DataAccessException(Throwable cause) {
        super("Failed to access database!", cause);
    }
}
