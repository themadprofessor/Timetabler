package me.timetabler.ui;

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
public class Controller implements Initializable {
    @FXML WebView view;
    private WebEngine engine;
    private DaoManager daoManager;


    /**
     * Initialises the controller. Due to the parameters, the controller must be constructed then given to JavaFX.
     * @param daoManager The DaoManager to be used to manipulate data.
     */
    public Controller(DaoManager daoManager) {
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
        engine.setOnAlert(event -> JavaFxBridge.createAlert(Alert.AlertType.INFORMATION, "Alert!", null, event.getData(), false));
        engine.load(String.valueOf(getClass().getResource("html/index.html")));
        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                List<Subject> subjects = null;
                List<Staff> staff = null;
                List<SchoolYear> years = null;
                List<LearningSet> learningSets = null;
                List<SubjectSet> subjectSets = null;

                try {
                    subjects = daoManager.getSubjectDao().getAllSubjects();
                } catch (DataAccessException e) {
                    DataExceptionHandler.handleJavaFx(e, "subject", false);
                } catch (DataConnectionException e) {
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                try {
                    staff = daoManager.getStaffDao().getAllStaff();
                } catch (DataAccessException e) {
                    Log.warning(e);
                    DataExceptionHandler.handleJavaFx(e, "staff", false);
                } catch (DataConnectionException e) {
                    Log.error(e);
                    DataExceptionHandler.handleJavaFx(e, null, true);
                }

                try {
                    years = daoManager.getSchoolYearDao().getAllSchoolYears();
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

                Log.debug("Adding [" + (subjects != null ? subjects.size() : 0) + "] rows to the subject table");
                Log.debug("Adding [" + (staff != null ? staff.size() : 0) + "] rows to the staff table");
                Log.debug("Adding [" + (years != null ? years.size() : 0) + "] rows to the year table");
                Log.debug("Adding [" + (learningSets != null ? learningSets.size() : 0) + "] rows to the learningSet table");

                JSObject bridge = (JSObject) engine.executeScript("window");
                bridge.setMember("java", new Bridge(daoManager, bridge));

                if (subjects != null) {
                    subjects.forEach(subject1 -> {
                        bridge.call("addToTable", "subjectTable",
                                new String[]{String.valueOf(subject1.id), subject1.name});
                        bridge.call("addToSelect", "classSubject", subject1.name, subject1.id);
                        bridge.call("addToSelect", "staffSubject", subject1.name, subject1.id);
                    });
                }

                if (staff != null) {
                    staff.forEach(staff1 -> bridge.call("addToTable", "staffTable",
                            new String[]{String.valueOf(staff1.id), staff1.name, String.valueOf(staff1.subject.id),
                                    String.valueOf(staff1.hoursPerWeek)}));
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
                    subjectSets.forEach(subjectSet -> bridge.call("addToTable", "classTable",
                            new String[]{String.valueOf(subjectSet.id), String.valueOf(subjectSet.subject.id),
                                    String.valueOf(subjectSet.learningSet.id), String.valueOf(subjectSet.schoolYear.id)}));
                }

                engine.executeScript("console.log = function(msg) {java.out(msg);}");
                engine.executeScript("console.error = function(msg) {java.err(msg);}");

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

    private void initData() {

    }
}
