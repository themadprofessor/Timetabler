package me.timetabler.ui.main;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import me.timetabler.data.*;
import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.data.exceptions.DataExceptionHandler;
import me.timetabler.data.exceptions.DataUpdateException;
import me.timetabler.map.MapLoader;
import me.util.Log;
import me.util.MapBuilder;
import netscape.javascript.JSObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * The bridge between the Javascript and the Java objects.
 */
public class Bridge {
    private DaoManager daoManager;
    private JSObject bridge;

    /**
     * Initialises the Javascript-Java bridge.
     * @param daoManager The DaoManger to be used by this bridge to request and store data.
     * @param bridge The Java-Javascript bridge to be used by this bridge.
     */
    public Bridge(DaoManager daoManager, JSObject bridge) {
        this.daoManager = daoManager;
        this.bridge = bridge;
    }

    /**
     * Prints '[JAVASCRIPT] $msg' to Log.info.
     * @param msg The message to print.
     */
    public void out(String msg) {
        Log.info("[JAVASCRIPT] " + msg);
    }

    /**
     * Prints '[JAVASCRIPT] $msg' to Log.debug.
     * @param msg The message to print.
     */
    public void debug(String msg) {
        Log.debug("[JAVASCRIPT] " + msg);
    }

    /**
     * Prints '[JAVASCRIPT] $msg' to Log.err.
     * @param msg The message to print.
     */
    public void err(String msg) {
        Log.error("[JAVASCRIPT] " + msg);
    }

    /**
     * Prints '[JAVASCRIPT] $msg' to Log.verbose.
     * @param msg The message to print.
     */
    public void verbose(String msg) {
        Log.verbose("[JAVASCRIPT] " + msg);
    }

    /**
     * Prints '[JAVASCRIPT] $msg' to Log.warn.
     * @param msg The message to print.
     */
    public void warn(String msg) {
        Log.warning("[JAVASCRIPT] " + msg);
    }

    /**
     * Stores the given data of the given type with the DaoManager.
     * @param type The type of data. E.G. BuildingCell, Staff.
     * @param data The data to be stored.
     * @return The id of the data after it is stored by the DaoManager.
     */
    public int add(String type, String data) {
        switch (type) {
            case "Subject":
            case "subject":
                Subject subject = new Subject();
                subject.name = data;

                try {
                    subject.id = daoManager.getSubjectDao().insert(subject);
                    if (subject.id == -1) {
                        bridge.call("setSuccess", false);
                    } else {
                        bridge.call("setSuccess", true);
                    }
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "subject", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                return subject.id;
            case "Staff":
            case "staff":
                Staff st = new Staff();
                try {
                    String[] split = data.replace("[", "").replace("]", "").split(",");
                    st.name = split[0];
                    Subject sub = new Subject();
                    sub.id = Integer.parseInt(split[1]);

                    st.subject = sub;
                    st.hoursPerWeek = Integer.parseInt(split[2]);
                    st.id = daoManager.getStaffDao().insert(st);
                    Log.debug("Added Staff Member [" + st.name + ']');
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "staff", false);
                    bridge.call("window.success = false");
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }
                return st.id;
            case "Year":
            case "year":
                SchoolYear year = new SchoolYear();
                year.schoolYearName = data;

                try {
                    year.id = daoManager.getSchoolYearDao().insert(year);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "year", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                return year.id;
            case "Set":
            case "set":
                LearningSet learningSet = new LearningSet();
                learningSet.name = data;

                try {
                    learningSet.id = daoManager.getLearningSetDao().insert(learningSet);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "learningSet", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                return learningSet.id;
            case "Class":
            case "class":
                SubjectSet subjectSet = new SubjectSet();
                String[] split = data.replace("[", "").replace("]", "").split(",");

                Subject sub = new Subject();
                sub.id = Integer.parseInt(split[0]);
                subjectSet.subject = sub;

                LearningSet set = new LearningSet();
                set.id = Integer.parseInt(split[1]);
                subjectSet.learningSet = set;

                SchoolYear schoolYear = new SchoolYear();
                schoolYear.id = Integer.parseInt(split[2]);
                subjectSet.schoolYear = schoolYear;

                try {
                    subjectSet.id = daoManager.getSubjectSetDao().insert(subjectSet);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "subjectSet", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                return subjectSet.id;
            default:
                return -1;
        }
    }

    /**
     * Removes the given data of the given type with the DaoManager.
     * @param type The type of data. E.G. BuildingCell, Staff.
     * @param data The data to be removed.
     */
    public void remove(String type, String data) {
        switch (type) {
            case "Subject":
            case "subject":
                try {
                    Log.debug("Removing Subject.");
                    Log.verbose("Subject [" + data + ']');
                    Subject subject = new Subject();
                    subject.id = Integer.parseInt(data.split(",")[0].replace("]", ""));
                    daoManager.getSubjectDao().delete(subject);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "subject", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
            case "Staff":
            case "staff":
                try {
                    Log.debug("Removing Staff.");
                    Log.verbose("Staff [" + data + ']');
                    Staff staff = new Staff();
                    staff.id = Integer.parseInt(data.split(",")[0].replace("]", ""));
                    daoManager.getStaffDao().delete(staff);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "staff", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
            case "Year":
            case "year":
                try {
                    Log.debug("Removing Year.");
                    Log.verbose("SchoolYear [" + data + ']');
                    SchoolYear year = new SchoolYear();
                    year.id = Integer.parseInt(data.split(",")[0].replace("]", ""));
                    daoManager.getSchoolYearDao().delete(year);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "year", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
            case "Set":
            case "set":
                try {
                    Log.debug("Removing LearningSet.");
                    Log.verbose("SchoolLearningSet [" + data + ']');
                    LearningSet learningSet = new LearningSet();
                    learningSet.id = Integer.parseInt(data.split(",")[0].replace("]", ""));
                    daoManager.getLearningSetDao().delete(learningSet);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "learningSet", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
            case "Class":
            case "class":
                try {
                    Log.debug("Removing SubjectSet.");
                    Log.verbose("SubjectSet [" + data + ']');
                    SubjectSet subjectSet = new SubjectSet();
                    subjectSet.id = Integer.parseInt(data.split(",")[0].replace("]", ""));
                    daoManager.getSubjectSetDao().delete(subjectSet);
                    bridge.call("setSuccess", true);
                } catch (DataAccessException | DataUpdateException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, "subjectSet", false);
                    bridge.call("setSuccess", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                break;
        }
    }

    /**
     * Asks the user for the map folder and the root folder. Then gives this information to a MapLoader instance to run
     * in the background.
     */
    public void loadMap() {
        bridge.call("clearTable", "buildingTable");
        bridge.call("clearTable", "classroomTable");
        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Map Files", "*.csv"));
            fileChooser.setTitle("Select top map file.");
            File topMap = fileChooser.showOpenDialog(new Stage());
            Log.debug("Chosen top map file [" + topMap + "]");

            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select map folder.");
            File mapFolder = directoryChooser.showDialog(new Stage());
            Log.debug("Chosen map folder [" + mapFolder + "]");

            MapLoader loader = new MapLoader(new MapBuilder<>(new HashMap<String, String>())
                    .put("top_map", topMap.getAbsolutePath())
                    .put("other_maps", mapFolder.getAbsolutePath()).build(), daoManager);

            Stage stage = new Stage();
            Label label = new Label();
            VBox vBox = new VBox();
            ProgressBar progressBar = new ProgressBar();
            label.textProperty().bind(loader.messageProperty());
            progressBar.progressProperty().bind(loader.progressProperty());
            progressBar.prefWidthProperty().bind(vBox.widthProperty());

            vBox.getChildren().add(label);
            vBox.getChildren().add(progressBar);
            vBox.setFillWidth(true);

            stage.setScene(new Scene(vBox, 300, 100));
            stage.show();
            Log.debug("Opened dialog");

            loader.setOnSucceeded((event) -> Platform.runLater(() -> {

                try {
                    daoManager.getBuildingDao().getAll().forEach(building -> bridge.call("addToTableHideRmBut", "buildingTable",
                            new String[]{String.valueOf(building.id), building.buildingName}));
                } catch (DataAccessException e) {
                    DataExceptionHandler.handleJavaFx(e, "building", false);
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, "building", true);
                }

                try {
                    daoManager.getClassroomDao().getAll().forEach(classroom -> bridge.call("addToTableHideRmBut", "classroomTable",
                            new String[]{String.valueOf(classroom.id), classroom.name, String.valueOf(classroom.building.id)}));
                } catch (DataAccessException e) {
                    DataExceptionHandler.handleJavaFx(e, "classroom", false);
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, "classroom", true);
                }
            }));
            Log.debug("Started map loader thread");
        });
    }

    public void loadFromFile(String dataType, String tableName) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        chooser.setTitle("Select CSV Data File.");
        File file = chooser.showOpenDialog(null);
        if (file == null) {
            bridge.call("setSuccess", false);
            Log.warning("No file selected for bulk loading!");
            return;
        }

        switch (dataType) {
            case "Subject":
            case "subject":
                try {
                    bridge.call("clearTable", tableName);
                    boolean success = daoManager.getSubjectDao().loadFile(file);
                    Log.debug("Successfully Loaded [" + success + ']');

                    List<Subject> newSubjects = daoManager.getSubjectDao().getAll();

                    Log.debug("Adding [" + newSubjects.size() + ']');
                    newSubjects.forEach(subject -> bridge.call("addToTable", tableName, new String[]{
                            String.valueOf(subject.id),
                            subject.name
                    }));
                } catch (DataAccessException | DataUpdateException e) {
                    DataExceptionHandler.handleJavaFx(e, dataType, false);
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, dataType, true);
                } catch (IllegalArgumentException e) {
                    JavaFxBridge.createAlert(Alert.AlertType.WARNING, "Cannot Read File!", null, "The data file needs to have read permissions for [" + System.getProperty("user.name") + ']', false);
                }

                break;
            case "Staff":
            case "staff":
                try {
                    bridge.call("clearTable", tableName);
                    boolean success = daoManager.getStaffDao().loadFile(file);
                    Log.debug("Successfully Loaded [" + success + ']');

                    List<Staff> newStaff = daoManager.getStaffDao().getAll();

                    Log.debug("Adding [" + newStaff.size() + ']');
                    newStaff.forEach(staff -> bridge.call("addToTable", tableName, new String[]{
                            String.valueOf(staff.id),
                            staff.name,
                            String.valueOf(staff.subject.id),
                            String.valueOf(staff.hoursPerWeek)
                    }));
                } catch (DataAccessException | DataUpdateException e) {
                    DataExceptionHandler.handleJavaFx(e, dataType, false);
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, dataType, true);
                } catch (IllegalArgumentException e) {
                    JavaFxBridge.createAlert(Alert.AlertType.WARNING, "Cannot Read File!", null, "The data file needs to have read permissions for [" + System.getProperty("user.name") + ']', false);
                }
                break;
            case "SchoolYear":
            case "schoolYear":
                try {
                    bridge.call("clearTable", tableName);
                    boolean success = daoManager.getSchoolYearDao().loadFile(file);
                    Log.debug("Successfully Loaded [" + success + ']');

                    List<SchoolYear> newSchoolYears = daoManager.getSchoolYearDao().getAll();

                    Log.debug("Adding [" + newSchoolYears.size() + ']');
                    newSchoolYears.forEach(schoolYear -> bridge.call("addToTable", tableName, new String[]{
                            String.valueOf(schoolYear.id),
                            schoolYear.schoolYearName
                    }));
                } catch (DataAccessException | DataUpdateException e) {
                    DataExceptionHandler.handleJavaFx(e, dataType, false);
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, dataType, true);
                } catch (IllegalArgumentException e) {
                    JavaFxBridge.createAlert(Alert.AlertType.WARNING, "Cannot Read File!", null, "The data file needs to have read permissions for [" + System.getProperty("user.name") + ']', false);
                }

                break;
            case "LearningSet":
            case "learningSet":
                try {
                    bridge.call("clearTable", tableName);
                    boolean success = daoManager.getLearningSetDao().loadFile(file);
                    Log.debug("Successfully Loaded [" + success + ']');

                    List<LearningSet> newLearningSets = daoManager.getLearningSetDao().getAll();

                    Log.debug("Adding [" + newLearningSets.size() + ']');
                    newLearningSets.forEach(learningSet -> bridge.call("addToTable", tableName, new String[]{
                            String.valueOf(learningSet.id),
                            learningSet.name}
                    ));
                } catch (DataAccessException | DataUpdateException e) {
                    DataExceptionHandler.handleJavaFx(e, dataType, false);
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, dataType, true);
                } catch (IllegalArgumentException e) {
                    JavaFxBridge.createAlert(Alert.AlertType.WARNING, "Cannot Read File!", null, "The data file needs to have read permissions for [" + System.getProperty("user.name") + ']', false);
                }

                break;
            case "SubjectSet":
            case "subjectSet":
                try {
                    bridge.call("clearTable", tableName);
                    boolean success = daoManager.getSubjectSetDao().loadFile(file);
                    Log.debug("Successfully Loaded [" + success + ']');

                    List<SubjectSet> newSubjectSets = daoManager.getSubjectSetDao().getAll();

                    Log.debug("Adding [" + newSubjectSets.size() + ']');
                    newSubjectSets.forEach(subjectSet -> bridge.call("addToTable", tableName, new String[]{
                            String.valueOf(subjectSet.id),
                            String.valueOf(subjectSet.learningSet.id),
                            String.valueOf(subjectSet.subject.id),
                            String.valueOf(subjectSet.schoolYear.id)
                    }));
                } catch (DataAccessException | DataUpdateException e) {
                    DataExceptionHandler.handleJavaFx(e, dataType, false);
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, dataType, true);
                } catch (IllegalArgumentException e) {
                    JavaFxBridge.createAlert(Alert.AlertType.WARNING, "Cannot Read File!", null, "The data file needs to have read permissions for [" + System.getProperty("user.name") + ']', false);
                }

                break;
        }
    }
}

