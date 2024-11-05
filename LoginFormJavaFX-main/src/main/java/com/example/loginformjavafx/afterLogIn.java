package com.example.loginformjavafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;

public class afterLogIn {
    @FXML
    private Button logout;

    @FXML
    public void userLogOut() {
        loadLoginScreen();
    }

    private void loadLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/loginformjavafx/hello-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logout.getScene().getWindow(); // Get current stage
            stage.setScene(new Scene(root));
            stage.setTitle("UniVerse Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
