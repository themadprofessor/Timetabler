package me.timetabler.ui.main;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import me.timetabler.data.*;
import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.exceptions.DataAccessException;
import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.data.exceptions.DataExceptionHandler;
import me.timetabler.ui.main.Bridge;
import me.util.Log;
import me.util.LogLevel;
import netscape.javascript.JSObject;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The JavaFX controller used to initialise the HTML
 */
public class MainController implements Initializable {
    /**
     * The WebView JavaFX component which displays the HTML.
     */
    @FXML WebView view;

    /**
     * The WebEngine of the WebView, which parses the HTML, Javascript and CSS.
     */
    private WebEngine engine;

    /**
     * The DaoManager to access the data from.
     */
    private DaoManager daoManager;


    /**
     * Initialises the controller. Due to the parameters, the controller must be constructed then given to JavaFX.
     * @param daoManager The DaoManager to be used to manipulate data.
     */
    public MainController(DaoManager daoManager) {
        this.daoManager = daoManager;
    }

    /**
     * Loads the HTML into the JavaFX window and sends the school data to the Javascript. If Log.LEVEL is VERBOSE, a copy of the generated HTML
     * after the Javascript has ran will be created in the current working directory, called indexLoaded.html.
     * @param location {@inheritDoc}
     * @param resources {@inheritDoc}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine = view.getEngine();
        engine.setOnAlert(event -> me.timetabler.ui.main.JavaFxBridge.createAlert(Alert.AlertType.INFORMATION, "Alert!", null, event.getData(), false));  //Upon calling 'alert' in JavaScript, create an Alert dialog.
        engine.load(String.valueOf(getClass().getResource("html/index.html")));     //Load the HTML.
        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                List<Subject> subjects = null;
                List<Staff> staff = null;
                List<SchoolYear> years = null;
                List<LearningSet> learningSets = null;
                List<SubjectSet> subjectSets = null;
                List<Building> buildings = null;
                List<Classroom> classrooms = null;
                List<Period> periods = null;
                List<LessonPlan> lessonPlans = null;

                //Get lists of all the data from the database.
                try {
                    subjects = daoManager.getSubjectDao().getAll();
                } catch (DataAccessException e) {
                    DataExceptionHandler.handleJavaFx(e, "subject", false);
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                try {
                    staff = daoManager.getStaffDao().getAll();
                } catch (DataAccessException e) {
                    Log.warning(e);
                    DataExceptionHandler.handleJavaFx(e, "staff", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                try {
                    years = daoManager.getSchoolYearDao().getAll();
                } catch (DataAccessException e) {
                    Log.warning(e);
                    DataExceptionHandler.handleJavaFx(e, "staff", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                try {
                    learningSets = daoManager.getLearningSetDao().getAll();
                } catch (DataAccessException e) {
                    Log.warning(e);
                    DataExceptionHandler.handleJavaFx(e, "learningSets", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                try {
                    subjectSets = daoManager.getSubjectSetDao().getAll();
                } catch (DataAccessException e) {
                    Log.warning(e);
                    DataExceptionHandler.handleJavaFx(e, "subjectSets", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                try {
                    buildings = daoManager.getBuildingDao().getAll();
                } catch (DataAccessException e) {
                    Log.warning(e);
                    DataExceptionHandler.handleJavaFx(e, "buildings", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                try {
                    classrooms = daoManager.getClassroomDao().getAll();
                } catch (DataAccessException e) {
                    Log.warning(e);
                    DataExceptionHandler.handleJavaFx(e, "classrooms", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                try {
                    periods = daoManager.getPeriodDao().getAll();
                } catch (DataAccessException e) {
                    Log.warning(e);
                    DataExceptionHandler.handleJavaFx(e, "period", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                try {
                    lessonPlans = daoManager.getLessonPlanDao().getAll();
                } catch (DataAccessException e) {
                    Log.warning(e);
                    DataExceptionHandler.handleJavaFx(e, "lesson plan", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                //Print debug info about the lists.
                Log.debug("Adding [" + (subjects != null ? subjects.size() : 0) + "] rows to the subject table");
                Log.debug("Adding [" + (staff != null ? staff.size() : 0) + "] rows to the staff table");
                Log.debug("Adding [" + (years != null ? years.size() : 0) + "] rows to the year table");
                Log.debug("Adding [" + (learningSets != null ? learningSets.size() : 0) + "] rows to the learningSet table");
                Log.debug("Adding [" + (subjectSets != null ? subjectSets.size() : 0) + "] rows to the subjectSet table");
                Log.debug("Adding [" + (buildings != null ? buildings.size() : 0) + "] rows to the building table");
                Log.debug("Adding [" + (classrooms != null ? classrooms.size() : 0) + "] rows to the classroom table");
                Log.debug("Adding [" + (lessonPlans != null ? lessonPlans.size() : 0) + "] rows the less table");

                //Get the Javascript handle to run Javascript on the HTML.
                JSObject bridge = (JSObject) engine.executeScript("window");
                bridge.setMember("java", new Bridge(daoManager, bridge));

                //Run Javascript to add all the data in the lists to the HTML tables.
                if (subjects != null) {
                    subjects.forEach(subject1 -> {
                        bridge.call("addToTable", "subjectTable",
                                new String[]{String.valueOf(subject1.id), subject1.name});
                        bridge.call("addToSelect", "classSubject", subject1.name, subject1.id);
                        bridge.call("addToSelect", "staffSubject", subject1.name, subject1.id);
                    });
                }

                if (staff != null) {
                    staff.forEach(staff1 -> {
                        bridge.call("addToTable", "staffTable",
                                new String[]{String.valueOf(staff1.id), staff1.name, String.valueOf(staff1.subject.id),
                                        String.valueOf(staff1.hoursPerWeek)});
                        bridge.call("addToSelect", "lessonStaff", staff1.name, String.valueOf(staff1.id));
                    });
                }

                if (years != null) {
                    years.forEach(year -> {
                        bridge.call("addToTable", "yearTable", new String[]{String.valueOf(year.id), year.schoolYearName});
                        bridge.call("addToSelect", "classYear", year.schoolYearName, year.id);
                    });
                }

                if (learningSets != null) {
                    learningSets.forEach(learningSet -> {
                        bridge.call("addToTable", "setTable", new String[]{String.valueOf(learningSet.id), learningSet.name});
                        bridge.call("addToSelect", "classSet", learningSet.name, learningSet.id);
                    });
                }

                if (subjectSets != null) {
                    subjectSets.forEach(subjectSet -> {
                        bridge.call("addToTable", "classTable",
                            new String[]{String.valueOf(subjectSet.id), String.valueOf(subjectSet.subject.id),
                                    String.valueOf(subjectSet.learningSet.id), String.valueOf(subjectSet.schoolYear.id)});
                        bridge.call("addToSelect", "lessonClass",
                                subjectSet.schoolYear.schoolYearName + " " + subjectSet.learningSet.name + " " + subjectSet.subject.name,
                                String.valueOf(subjectSet.id));
                    });
                }

                if (buildings != null) {
                    buildings.forEach(building -> bridge.call("addToTableHideRmBut", "buildingTable",
                            new String[]{String.valueOf(building.id), building.buildingName}));
                }

                if (classrooms != null) {
                    classrooms.forEach(classroom -> {
                        bridge.call("addToTableHideRmBut", "classroomTable",
                                new String[]{
                                        String.valueOf(classroom.id),
                                        classroom.name,
                                        String.valueOf(classroom.building.id),
                                        String.valueOf(classroom.subject.id)
                                });
                        bridge.call("addToSelect", "lessonClassroom", classroom.name, String.valueOf(classroom.id));
                    });
                }

                if (periods != null) {
                    periods.forEach(period -> bridge.call("addToSelect", "lessonPeriod",
                            period.day.name + " " + period.startTime.toString(), String.valueOf(period.id)));
                }

                if (lessonPlans != null) {
                    lessonPlans.forEach(lessonPlan -> bridge.call("addToTable", "lessonTable",
                            new String[]{String.valueOf(lessonPlan.id),
                                    String.valueOf(lessonPlan.period.id),
                                    String.valueOf(lessonPlan.subjectSet.id),
                                    String.valueOf(lessonPlan.staff.id),
                                    String.valueOf(lessonPlan.classroom.id)}));
                }

                engine.executeScript("console.log = function(msg) {java.out(msg);}");
                engine.executeScript("console.error = function(msg) {java.err(msg);}");

                //If verbose logging is enabled, write the loaded HTML document after the Javascript has run to a file.
                if (Log.LEVEL == LogLevel.VERBOSE) {
                    try {
                        Transformer transformer = TransformerFactory.newInstance().newTransformer();
                        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                        FileWriter writer = new FileWriter("indexLoaded.html");
                        transformer.transform(new DOMSource(engine.getDocument()), new StreamResult(writer));
                        writer.flush();
                        writer.close();
                    } catch (TransformerException | IOException e) {
                        Log.error(e);
                    }
                }
            }
        });
    }
}
