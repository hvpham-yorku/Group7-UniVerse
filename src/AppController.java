package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import models.UserProfile;

public class AppController {

    // FXML elements (linked to RegisterScreen.fxml)
    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField bioField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button registerButton;

    // UserProfile instance to store user information
    private UserProfile userProfile;

    // Initialize method called after FXML elements are injected
    @FXML
    public void initialize() {
        userProfile = new UserProfile();
        
        // Set up the initial state of the form or load user data if needed
        loadUserData();
        
        // Add an action listener to the register button
        registerButton.setOnAction(event -> handleRegisterAction());
    }

    // Method to load user data into the fields (if user data is already available)
    private void loadUserData() {
        if (userProfile != null) {
            usernameField.setText(userProfile.getUsername());
            emailField.setText(userProfile.getEmail());
            bioField.setText(userProfile.getBio());
        }
    }

    // Method called when the register button is pressed
    private void handleRegisterAction() {
        // Get values from input fields
        String username = usernameField.getText();
        String email = emailField.getText();
        String bio = bioField.getText();

        // Update the UserProfile instance
        userProfile.setUsername(username);
        userProfile.setEmail(email);
        userProfile.setBio(bio);

        // Here, you could add additional logic, like validation or saving to a database

        // Update the status label
        statusLabel.setText("User registered successfully!");
    }
}
