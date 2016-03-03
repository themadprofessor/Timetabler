package me.timetabler.data.exceptions;

/**
 * Created by stuart on 25/02/16.
 */
public class DataConnectionException extends DataException {
    public DataConnectionException(String addr) {
        super("Failed to connect to server [" + addr + "]");
    }

    public DataConnectionException(String addr, Throwable cause) {
        super("Failed to connect to server [" + addr + "]", cause);
    }
}
