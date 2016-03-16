package me.timetabler.ui;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
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

    private List<Subject> subjects = null;
    private List<Staff> staff = null;

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
        engine.load(String.valueOf(getClass().getResource("html/index.html")));
        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                initData();


                JSObject bridge = (JSObject) engine.executeScript("window");
                bridge.setMember("java", new Bridge(daoManager, bridge));

                subjects.forEach(subject1 -> {
                    bridge.call("addToTableJava", "subjectTable",
                            new String[]{String.valueOf(subject1.id), subject1.name}, subject1.id);
                    bridge.call("addToSelect", "classSubject", subject1.name, subject1.id);
                });

                staff.forEach(staff1 -> bridge.call("addToTableJava", "staffTable",
                        new String[]{String.valueOf(staff1.id), staff1.name, String.valueOf(staff1.subject.id)}, staff1.id));

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
            DataExceptionHandler.handleJavaFx(e, "staff", false);
        } catch (DataConnectionException e) {
            DataExceptionHandler.handleJavaFx(e, null, true);
        }
    }
}
