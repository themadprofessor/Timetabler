package me.timetabler;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import me.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by stuart on 16/07/15.
 */
public class AddSubjectDialog extends Stage implements Initializable {
    @FXML TextField id;
    @FXML TextField name;
    @FXML Button save;

    public Subject subject;

    public AddSubjectDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addDialogue.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            setTitle("Add Subject");
            setScene(new Scene(root));
            show();
        } catch (IOException e) {
            Log.err(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        save.setOnAction(event -> {
            subject = new Subject(name.getText(), id.getText());
            close();
        });
    }
}
