package me.timetabler.data.exceptions;

import javafx.scene.control.Alert;
import me.timetabler.ui.JavaFxBridge;
import me.util.Log;

/**
 * Created by stuart on 14/03/16.
 */
public class DataExceptionHandler {
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
