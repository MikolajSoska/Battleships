package graphics.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginScreen {
    public Button submitButton;
    public TextField usernameField;
    public Label errorLabel;

    public void logIn() throws IOException {
        String username = usernameField.getText();
        if(!username.isEmpty()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/graphics/fxml/sample.fxml"));
            Parent root = loader.load();
            Controller controller = loader.getController();
            controller.setPlayerName(username);

            Stage oldStage = (Stage) submitButton.getScene().getWindow();
            oldStage.close();

            Stage newStage = new Stage();
            newStage.setResizable(false);
            newStage.setTitle("Battleship");
            newStage.setScene(new Scene(root));
            newStage.show();
        }
        else
            errorLabel.setVisible(true);

    }
}
