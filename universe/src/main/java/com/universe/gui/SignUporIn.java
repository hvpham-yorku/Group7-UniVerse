package com.universe.gui;

import com.universe.FirebaseInitializer;
import com.universe.models.UserProfile;
import com.universe.FirestoreHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUporIn {

    private JFrame frame;
    private JPasswordField passwordField;
    private JTextField textFieldEmail;
    private JTextField textFieldName;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        // Initialize Firebase
        FirebaseInitializer.initializeFirebase();

        // Launch the GUI
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SignUporIn window = new SignUporIn();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public SignUporIn() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        // Set up the main frame
        frame = new JFrame();
        frame.setBounds(500, 500, 700, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        // Name field
        textFieldName = new JTextField();
        textFieldName.setBounds(420, 174, 212, 34);
        frame.getContentPane().add(textFieldName);

        JLabel lblName = new JLabel("Full Name:");
        lblName.setBounds(318, 183, 76, 16);
        frame.getContentPane().add(lblName);

        // Email field
        textFieldEmail = new JTextField();
        textFieldEmail.setBounds(422, 241, 212, 34);
        frame.getContentPane().add(textFieldEmail);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(349, 250, 38, 16);
        frame.getContentPane().add(lblEmail);

        // Password field
        passwordField = new JPasswordField();
        passwordField.setBounds(422, 310, 217, 36);
        frame.getContentPane().add(passwordField);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(325, 323, 71, 16);
        frame.getContentPane().add(lblPassword);

        // Sign Up button
        JButton btnSignUp = new JButton("Sign Up");
        btnSignUp.setBackground(new Color(255, 91, 108));
        btnSignUp.setBounds(470, 374, 117, 29);
        frame.getContentPane().add(btnSignUp);

        // Action listener for sign up button
        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignUp();
            }
        });
    }

    /**
     * Handle the "Sign Up" button click.
     */
    private void handleSignUp() {
        String name = textFieldName.getText();
        String email = textFieldEmail.getText();
        String password = new String(passwordField.getPassword()); // Convert char[] to String

        // Validate input fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a hashed version of the password (basic hash for demo purposes)
        String passwordHash = Integer.toHexString(password.hashCode());

        // Create a user profile and add to Firestore
        String userId = String.valueOf(System.currentTimeMillis()); // Unique ID based on current time
        UserProfile user = new UserProfile(userId, name, email, passwordHash);
        FirestoreHandler.addUserData(user);

        // Show confirmation message
        JOptionPane.showMessageDialog(frame, "Sign up successful! User ID: " + userId, "Success", JOptionPane.INFORMATION_MESSAGE);
        
        // Close the current frame and open the Welcome Page
        frame.dispose(); 
        new WelcomePage(name); 
    }
}
