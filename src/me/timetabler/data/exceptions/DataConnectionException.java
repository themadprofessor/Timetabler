package me.timetabler.data.exceptions;

/**
 * Created by stuart on 25/02/16.
 */
public class DataConnectionException extends DataException {
    public DataConnectionException(String source) {
        super("Failed to connect to data source [" + source + "]");
    }

    public DataConnectionException(String source, Throwable cause) {
        super("Failed to connect to data source [" + source + "]", cause);
    }
}
