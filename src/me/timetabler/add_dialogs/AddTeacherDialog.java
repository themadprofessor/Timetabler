package me.timetabler.add_dialogs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import me.timetabler.Subject;
import me.timetabler.Teacher;
import me.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by stuart on 16/07/15.
 */
public class AddTeacherDialog extends Stage implements Initializable {
    @FXML TextField id;
    @FXML TextField name;
    @FXML Button save;

    public Teacher teacher;

    public AddTeacherDialog(ListView<Subject> subjects) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addTeacherDialogue.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            setTitle("Add Teacher");
            setScene(new Scene(root));
            show();
        } catch (IOException e) {
            Log.err(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        save.setOnAction(event -> {
            teacher = new Teacher(name.getText(), id.getText());
            close();
        });
    }
}
