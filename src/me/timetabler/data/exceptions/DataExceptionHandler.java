package me.timetabler.data.exceptions;

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
            JavaFxBridge.createAlert(Alert.AlertType.ERROR, "Failed to access data!", null, "The system failed to access the " + dataType + " data!\n" + e.getLocalizedMessage(), exit);
        } else if (e instanceof DataConnectionException){
            JavaFxBridge.createAlert(Alert.AlertType.ERROR, "Failed to connect to data source", null, "The system failed to connect to the data source!\n" + e.getLocalizedMessage(), exit);
        } else if (e instanceof DataUpdateException) {
            JavaFxBridge.createAlert(Alert.AlertType.ERROR, "Failed to update data!", null, "The system failed to update the " + dataType + " data!\n" + e.getLocalizedMessage(), exit);
        }
    }
}
