package com.example.loginformjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;

public class LogIn {

    @FXML
    private ImageView logoImageView;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label wrongLogin;

    @FXML
    public void initialize() {
        // Load the logo image
        Image image = new Image(getClass().getResource("/com/example/loginformjavafx/universelogo.png").toExternalForm());
        logoImageView.setImage(image);
    }

    @FXML
    public void userLogIn(ActionEvent event) throws IOException {
        if (validateLogin()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/loginformjavafx/afterLogin.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } else {
            wrongLogin.setText("Incorrect username or password.");
        }
    }

    private boolean validateLogin() {
        // Replace with actual validation logic
        return "user".equals(usernameField.getText()) && "password".equals(passwordField.getText());
    }
}
