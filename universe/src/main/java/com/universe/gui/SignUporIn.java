package com.universe.gui;

import com.universe.FirebaseInitializer;
import com.universe.models.UserProfile;
import com.universe.FirestoreHandler;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SignUporIn {

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTextField textFieldName;
    private JTextField textFieldEmail;
    private JPasswordField passwordField;
    private JFormattedTextField dobField;
    private JTextField bioTextField;
    private JLabel lblUserName;
    private JLabel lblUserEmail;
    private JLabel lblInterestsSummary;
    private Choice choiceCity;
    private Choice choiceUniversity;
    private List<String> selectedInterests;
    private JLabel profilePicLabel;
    private String currentUserId;

    public static void main(String[] args) {
        // Initialize Firebase
        FirebaseInitializer.initializeFirebase();

        // Launch the GUI
        EventQueue.invokeLater(() -> {
            try {
                SignUporIn window = new SignUporIn();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public SignUporIn() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Sign Up or Login");
        frame.setBounds(500, 500, 800, 800);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        frame.getContentPane().add(mainPanel);

        JPanel signUpPanel = new JPanel();
        signUpPanel.setLayout(null);
        initializeSignUpPanel(signUpPanel);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        initializeLoginPanel(loginPanel);

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(null);
        initializeWelcomePanel(welcomePanel);

        mainPanel.add(signUpPanel, "SignUp");
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(welcomePanel, "Welcome");

        cardLayout.show(mainPanel, "SignUp");
    }

    private void initializeSignUpPanel(JPanel signUpPanel) {
        // Full Name Field
        textFieldName = new JTextField();
        textFieldName.setBounds(250, 120, 212, 34);
        signUpPanel.add(textFieldName);

        JLabel lblName = new JLabel("Full Name:");
        lblName.setBounds(150, 125, 76, 16);
        signUpPanel.add(lblName);

        // Email Field
        textFieldEmail = new JTextField();
        textFieldEmail.setBounds(250, 180, 212, 34);
        signUpPanel.add(textFieldEmail);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(150, 185, 38, 16);
        signUpPanel.add(lblEmail);

        // Password Field
        passwordField = new JPasswordField();
        passwordField.setBounds(250, 240, 212, 36);
        signUpPanel.add(passwordField);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(150, 245, 71, 16);
        signUpPanel.add(lblPassword);

        // Sign-Up Button
        JButton btnSignUp = new JButton("Sign Up");
        btnSignUp.setBounds(290, 300, 117, 29);
        signUpPanel.add(btnSignUp);

        btnSignUp.addActionListener(this::handleSignUp);

        JLabel lblLoginLink = new JLabel("<html><u>Already have an account? Login</u></html>");
        lblLoginLink.setForeground(Color.BLUE);
        lblLoginLink.setBounds(250, 350, 200, 30);
        signUpPanel.add(lblLoginLink);

        lblLoginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cardLayout.show(mainPanel, "Login");
            }
        });
    }

    private void initializeLoginPanel(JPanel loginPanel) {
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(150, 180, 100, 30);
        loginPanel.add(lblEmail);

        JTextField emailField = new JTextField();
        emailField.setBounds(250, 180, 212, 30);
        loginPanel.add(emailField);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(150, 240, 100, 30);
        loginPanel.add(lblPassword);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(250, 240, 212, 30);
        loginPanel.add(passwordField);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(290, 300, 100, 30);
        loginPanel.add(btnLogin);

        btnLogin.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String passwordHash = Integer.toHexString(password.hashCode()); // Hash the entered password

            boolean isAuthenticated = FirestoreHandler.authenticateUser(email, passwordHash);
            if (isAuthenticated) {
                JOptionPane.showMessageDialog(frame, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "Welcome"); // Redirect to Welcome/Profile page
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JLabel lblSignUpLink = new JLabel("<html><u>Don't have an account? Sign Up</u></html>");
        lblSignUpLink.setForeground(Color.BLUE);
        lblSignUpLink.setBounds(250, 350, 200, 30);
        loginPanel.add(lblSignUpLink);

        lblSignUpLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                cardLayout.show(mainPanel, "SignUp");
            }
        });
    }


    private void initializeWelcomePanel(JPanel welcomePanel) {
        // Profile Picture Section
        JLabel lblProfilePic = new JLabel("Profile Picture:");
        lblProfilePic.setBounds(150, 20, 100, 20);
        welcomePanel.add(lblProfilePic);

        profilePicLabel = new JLabel();
        profilePicLabel.setBounds(250, 20, 100, 100);
        profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        welcomePanel.add(profilePicLabel);

        JButton btnAddPic = new JButton("Add Picture");
        btnAddPic.setBounds(370, 60, 120, 30);
        welcomePanel.add(btnAddPic);

        btnAddPic.addActionListener(e -> handleAddPicture());

        // User Name Label
        lblUserName = new JLabel("Name: ");
        lblUserName.setFont(new Font("Arial", Font.BOLD, 16));
        lblUserName.setBounds(200, 140, 300, 20);
        welcomePanel.add(lblUserName);

        lblUserEmail = new JLabel("Email: ");
        lblUserEmail.setFont(new Font("Arial", Font.BOLD, 16));
        lblUserEmail.setBounds(200, 170, 300, 20);
        welcomePanel.add(lblUserEmail);

        // Date of Birth
        JLabel lblDob = new JLabel("Date of Birth:");
        lblDob.setBounds(150, 210, 100, 20);
        welcomePanel.add(lblDob);

        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            dobField = new JFormattedTextField(dateMask);
            dobField.setBounds(250, 210, 150, 25);
            welcomePanel.add(dobField);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Bio
        JLabel lblBio = new JLabel("About Me:");
        lblBio.setBounds(150, 250, 100, 20);
        welcomePanel.add(lblBio);

        bioTextField = new JTextField();
        bioTextField.setBounds(250, 250, 300, 25);
        welcomePanel.add(bioTextField);

        // City
        JLabel lblCity = new JLabel("City:");
        lblCity.setBounds(150, 290, 50, 20);
        welcomePanel.add(lblCity);

        choiceCity = new Choice();
        choiceCity.setBounds(250, 290, 200, 25);
        String[] cities = {"Toronto", "Ottawa", "Mississauga"};
        for (String city : cities) {
            choiceCity.add(city);
        }
        welcomePanel.add(choiceCity);

        // University
        JLabel lblUniversity = new JLabel("University:");
        lblUniversity.setBounds(150, 330, 100, 20);
        welcomePanel.add(lblUniversity);

        choiceUniversity = new Choice();
        choiceUniversity.setBounds(250, 330, 200, 25);
        String[] universities = {"University of Toronto", "York University", "McMaster University"};
        for (String university : universities) {
            choiceUniversity.add(university);
        }
        welcomePanel.add(choiceUniversity);

        // Interests
        JLabel lblInterests = new JLabel("Interests:");
        lblInterests.setBounds(150, 370, 100, 20);
        welcomePanel.add(lblInterests);

        lblInterestsSummary = new JLabel("None selected");
        lblInterestsSummary.setBounds(250, 370, 300, 20);
        welcomePanel.add(lblInterestsSummary);

        JButton btnSelectInterests = new JButton("Select Interests");
        btnSelectInterests.setBounds(250, 400, 150, 30);
        welcomePanel.add(btnSelectInterests);

        btnSelectInterests.addActionListener(e -> showInterestSelectionDialog());

        // Save Button
        JButton btnSave = new JButton("Save Profile");
        btnSave.setBounds(290, 450, 150, 30);
        welcomePanel.add(btnSave);

        btnSave.addActionListener(e -> handleSave(e));
    }


    private void showInterestSelectionDialog() {
        JDialog dialog = new JDialog(frame, "Select Interests", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        JPanel interestsPanel = new JPanel();
        interestsPanel.setLayout(new BoxLayout(interestsPanel, BoxLayout.Y_AXIS));

        String[] interests = {"Sports", "Music", "Reading", "Art", "Technology"};
        List<JCheckBox> checkBoxes = new ArrayList<>();
        for (String interest : interests) {
            JCheckBox checkBox = new JCheckBox(interest);
            interestsPanel.add(checkBox);
            checkBoxes.add(checkBox);
        }

        dialog.add(new JScrollPane(interestsPanel), BorderLayout.CENTER);

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(e -> {
            selectedInterests = new ArrayList<>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    selectedInterests.add(checkBox.getText());
                }
            }
            lblInterestsSummary.setText("Selected Interests: " + String.join(", ", selectedInterests));
            dialog.dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnOk);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void handleAddPicture() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            ImageIcon profilePic = new ImageIcon(selectedFile.getAbsolutePath());
            Image scaledImage = profilePic.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            profilePicLabel.setIcon(new ImageIcon(scaledImage));
        }
    }

    private void handleSignUp(ActionEvent e) {
        String username = textFieldName.getText();
        String email = textFieldEmail.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentUserId = String.valueOf(System.currentTimeMillis());
        String passwordHash = Integer.toHexString(password.hashCode());

        UserProfile user = new UserProfile(currentUserId, username, email, passwordHash);
        FirestoreHandler.addUserData(user);

        lblUserName.setText("Name: " + username);
        lblUserEmail.setText("Email: " + email);

        JOptionPane.showMessageDialog(frame, "Sign up successful! Please complete your profile.", "Success", JOptionPane.INFORMATION_MESSAGE);
        cardLayout.show(mainPanel, "Welcome");
    }

    private void handleSave(ActionEvent e) {
        if (currentUserId == null) {
            JOptionPane.showMessageDialog(frame, "No user logged in to save data for.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = lblUserName.getText().replace("Name: ", "");
        String email = lblUserEmail.getText().replace("Email: ", "");
        String dateOfBirth = dobField.getText();
        String bio = bioTextField.getText();
        String province = choiceCity.getSelectedItem();
        String university = choiceUniversity.getSelectedItem();

        // Get selected interests
        String interestsSummary = lblInterestsSummary.getText().replace("Selected Interests: ", "");
        List<String> interests = interestsSummary.isEmpty() ? new ArrayList<>() : List.of(interestsSummary.split(", "));

        // Fetch existing user data to preserve passwordHash
        UserProfile existingUser = FirestoreHandler.getUserData(currentUserId);
        if (existingUser == null) {
            JOptionPane.showMessageDialog(frame, "User data not found in Firestore.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create updated user profile while preserving the passwordHash
        UserProfile updatedUser = new UserProfile(
            currentUserId,
            username,
            email,
            bio,
            dateOfBirth,
            province,
            university,
            interests,
            existingUser.getPasswordHash() // Preserve passwordHash
        );

        // Update Firestore
        FirestoreHandler.updateUserData(updatedUser);

        JOptionPane.showMessageDialog(frame, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

}
