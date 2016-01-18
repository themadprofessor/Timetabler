package me.timetabler.ui;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import me.timetabler.data.SchoolClass;
import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
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
import java.util.Map;
import java.util.ResourceBundle;

/**
 * The JavaFX controller used to initialise the HTML
 */
public class Controller implements Initializable {
    @FXML WebView view;
    private WebEngine engine;
    private Map<String, Subject> subjects;
    private Map<String, Staff> staff;
    private Map<String, SchoolClass> classes;

    /**
     * Initialises the controller. Due to the parameters, the controller must be constructed then given to JavaFX.
     * @param subjects A map of the school subject's id and the subject object.
     * @param staff A map of the staff member's id and the staff object.
     * @param classes A map of the class's id and the class object.
     */
    public Controller(Map<String, Subject> subjects, Map<String, Staff> staff, Map<String, SchoolClass> classes) {
        this.subjects = subjects;
        this.staff = staff;
        this.classes = classes;
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
                JSObject bridge = (JSObject) engine.executeScript("window");
                bridge.setMember("java", new Bridge(subjects, staff, classes));
                subjects.forEach((id, subject) -> {
                    bridge.call("addToTableJava", "subjectTable", new String[]{id, subject.name}, id);
                    bridge.call("addToSelect", "classSubject", subject.name, subject.id);
                });
                staff.forEach((id, staff) -> bridge.call("addToTableJava", "staffTable", new String[]{id, staff.name}, id));
                classes.forEach((id, clazz) -> bridge.call("addToTableJava", "classTable", new String[]{id, clazz.name}, id));
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
}
