package me.timetabler.data.exceptions;

import javafx.beans.binding.StringBinding;
import javafx.scene.control.Alert;
import me.timetabler.ui.main.JavaFxBridge;
import me.util.Log;

/**
 * A utility method which handles a DataException.
 */
public class DataExceptionHandler {
    /**
     * Creates a JavaFX error dialog from the parameters given.
     * @param e The exception to be handled.
     * @param dataType The type of data being processed when the exception occurred.
     * @param exit True of the system should exit when the dialog is closed.
     */
    public static void handleJavaFx(DataException e, String dataType, boolean exit) {
        Log.error(e);
        if (e instanceof DataAccessException) {
            JavaFxBridge.createAlert(Alert.AlertType.ERROR,
                    "Failed to access data!", null, "The system failed to access the " + dataType + " data!\n" + formMessage(e), exit);
        } else if (e instanceof DataConnectionException){
            JavaFxBridge.createAlert(Alert.AlertType.ERROR, "Failed to connect to data source", null, "The system failed to connect to the data source!\n" + formMessage(e), exit);
        } else if (e instanceof DataUpdateException) {
            JavaFxBridge.createAlert(Alert.AlertType.ERROR, "Failed to update data!", null, "The system failed to update the " + dataType + " data!\n" + formMessage(e), exit);
        }
    }

    /**
     * Forms a multiline message from the given exception and all its causes.
     * @param e The exception to form the message from.
     * @return The message.
     */
    private static String formMessage(Exception e) {
        StringBuilder builder = new StringBuilder();
        builder.append(e.toString());

        //Add all causes to the message
        Throwable cause;
        Throwable result = e;
        while ((cause = result.getCause()) != null && result != cause) {
            builder.append("\n\nCaused by ").append(cause.toString());
            result = cause;
        }

        return builder.toString();
    }
}
