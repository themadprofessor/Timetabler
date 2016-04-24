package me.timetabler.ui.login;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import me.timetabler.auth.Authenticator;
import me.timetabler.data.dao.DaoManager;
import me.timetabler.data.exceptions.DataConnectionException;
import me.timetabler.data.exceptions.DataExceptionHandler;
import me.timetabler.ui.main.JavaFxBridge;
import me.timetabler.ui.main.MainController;
import me.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * The controller for the auth window. It sets the action for the auth button.
 */
public class LoginController implements Initializable {
    /**
     * The authenticator which will be used authenticate the password given by the user.
     */
    private Authenticator authenticator;

    /**
     * The auth window. This will be closed when the main window opens.
     */
    private Stage loginStage;

    /**
     * The configuration map for the 'data_source' section of the configuration.
     */
    private Map<String, String> config;

    /**
     * The daoManager to be used to ensure the data source is ready for authentication and to be passed to the main
     * window for use later.
     */
    private DaoManager daoManager;

    /**
     * The auth button in the user interface.
     */
    public Button login;

    /**
     * The password field in the user interface.
     */
    public PasswordField password;

    /**
     * Initialies the controller. The given map must be the 'data_source' map from the top configuration map.
     * @param loginStage The auth window.
     * @param config The 'data_source' configuration map.
     */
    public LoginController(Stage loginStage, Map<String, String> config) {
        this.loginStage = loginStage;
        this.config = config;
        authenticator = Authenticator.getAuthenticator(config);
    }

    /**
     * {@inheritDoc}
     * Sets the {@link #login} button's onAction handler to be the {@link #auth()} method.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        login.setOnAction((event) -> auth());
    }

    /**
     * Authenticates the password in {@link #password} and the 'username' entry in {@link #config} with the data source.
     * If it successfully authenticates, it creates the main window and closes the login window. If it failes to
     * authenticate, it creates a popup, alerting the user to the incorrect password.
     */
    private void auth() {
        //Ensure there is a password to authenticate.
        if (password.getText().isEmpty()) {
            Log.warning("Password was empty!");
            JavaFxBridge.createAlert(Alert.AlertType.ERROR, "No Password Given", null, "Please specify a password! It was required to specify one when the program was installed.", false);
        }

        //Lazily initialise daoManager, but MUST only be initialised once.
        if (daoManager == null) {
            try {
                daoManager = DaoManager.getManager(config);
            } catch (DataConnectionException e) {
                DataExceptionHandler.handleJavaFx(e, null, false);
            }
            Log.debug("Initialised daoManager");
        }

        if (authenticator.authenticate(config.get("username"), password.getText().toCharArray())) {
            Log.info("Successfully authenticated.");
            //Open main window anc close auth window.
            Scene scene = null;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../main/main.fxml"));
                MainController controller = new MainController(daoManager);
                loader.setController(controller);
                scene = new Scene(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
                JavaFxBridge.createAlert(Alert.AlertType.ERROR, "An IO Exception Has Occurred!", null, "The IO Exception [" + e.getMessage() + "] has occurred. Please contact the system administrator", true);
            }
            Stage mainStage = new Stage();
            mainStage.setTitle("Timetabler");
            mainStage.setScene(scene);
            loginStage.close();
            mainStage.show();
        } else {
            Log.info("Failed authentication.");
            JavaFxBridge.createAlert(Alert.AlertType.WARNING, "Incorrect Password!", null, "The given password was incorrect. Please use the one given when the program was installed.", false);
        }
    }
}
