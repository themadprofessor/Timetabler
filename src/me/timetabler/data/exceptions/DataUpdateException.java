package me.timetabler.data.exceptions;

/**
 * Created by stuart on 25/02/16.
 */
public class DataUpdateException extends DataException {
    public DataUpdateException(Throwable cause) {
        super("Failed to update database!" ,cause);
    }
}
