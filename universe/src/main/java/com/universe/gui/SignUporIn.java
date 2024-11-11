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
    private JLabel lblInterestsSummary;
    private JLabel lblUserName;
    private JLabel lblUserEmail;
    private Choice choiceCity;
    private Choice choiceUniversity;

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
        // Set up the main frame
        frame = new JFrame("Sign Up or Welcome Page");
        frame.setBounds(500, 500, 700, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        frame.getContentPane().add(mainPanel);

        // Create the sign-up/sign-in panel
        JPanel signUpPanel = new JPanel();
        signUpPanel.setLayout(null);
        initializeSignUpPanel(signUpPanel);

        // Create the welcome panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(null);
        initializeWelcomePanel(welcomePanel);

        // Add panels to main panel with CardLayout
        mainPanel.add(signUpPanel, "SignUp");
        mainPanel.add(welcomePanel, "Welcome");

        // Show the sign-up panel first
        cardLayout.show(mainPanel, "SignUp");
    }

    private void initializeSignUpPanel(JPanel signUpPanel) {
        // Name field
        textFieldName = new JTextField();
        textFieldName.setBounds(420, 174, 212, 34);
        signUpPanel.add(textFieldName);

        JLabel lblName = new JLabel("Full Name:");
        lblName.setBounds(318, 183, 76, 16);
        signUpPanel.add(lblName);

        // Email field
        textFieldEmail = new JTextField();
        textFieldEmail.setBounds(422, 241, 212, 34);
        signUpPanel.add(textFieldEmail);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(349, 250, 38, 16);
        signUpPanel.add(lblEmail);

        // Password field
        passwordField = new JPasswordField();
        passwordField.setBounds(422, 310, 217, 36);
        signUpPanel.add(passwordField);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(325, 323, 71, 16);
        signUpPanel.add(lblPassword);

        // Sign Up button
        JButton btnSignUp = new JButton("Sign Up");
        btnSignUp.setBackground(new Color(255, 91, 108));
        btnSignUp.setBounds(470, 374, 117, 29);
        signUpPanel.add(btnSignUp);

        // Action listener for sign up button
        btnSignUp.addActionListener(this::handleSignUp);
    }

    private void initializeWelcomePanel(JPanel welcomePanel) {
        // Profile picture section
        JLabel profilePicLabel = new JLabel();
        profilePicLabel.setBounds(292, 6, 117, 93);
        profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        welcomePanel.add(profilePicLabel);

        JButton btnAddPic = new JButton("Add Picture");
        btnAddPic.setBounds(410, 53, 102, 19);
        welcomePanel.add(btnAddPic);

        btnAddPic.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                ImageIcon profilePic = new ImageIcon(selectedFile.getAbsolutePath());
                Image scaledImage = profilePic.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                profilePicLabel.setIcon(new ImageIcon(scaledImage));
            }
        });

        // User name and email labels
        lblUserName = new JLabel();
        lblUserName.setFont(new Font("Arial", Font.BOLD, 16));
        lblUserName.setBounds(323, 99, 135, 19);
        welcomePanel.add(lblUserName);

        lblUserEmail = new JLabel();
        lblUserEmail.setFont(new Font("Arial", Font.BOLD, 16));
        lblUserEmail.setBounds(323, 130, 175, 19);
        welcomePanel.add(lblUserEmail);

        // Username field
        JTextField welcomeUsernameField = new JTextField();
        welcomeUsernameField.setBounds(237, 183, 229, 26);
        welcomePanel.add(welcomeUsernameField);

        // Date of birth field
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            dobField = new JFormattedTextField(dateMask);
            dobField.setBounds(237, 233, 229, 26);
            welcomePanel.add(dobField);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // About Me/Bio text field
        bioTextField = new JTextField();
        bioTextField.setBounds(237, 283, 229, 26);
        welcomePanel.add(bioTextField);

        // Button to open interest selection dialog
        JButton btnSelectInterests = new JButton("Select Interests");
        btnSelectInterests.setBounds(237, 330, 150, 25);
        welcomePanel.add(btnSelectInterests);

        lblInterestsSummary = new JLabel("Selected Interests: None");
        lblInterestsSummary.setBounds(237, 365, 400, 25);
        welcomePanel.add(lblInterestsSummary);

        btnSelectInterests.addActionListener(e -> showInterestSelectionDialog());

        // City dropdown
        choiceCity = new Choice();
        choiceCity.setBounds(208, 420, 150, 27);
        String[] citiesInOntario = {
                "Toronto", "Ottawa", "Mississauga", "Brampton", "Hamilton",
                "London", "Markham", "Vaughan", "Kitchener", "Windsor",
                "Richmond Hill", "Oakville", "Burlington", "Sudbury", "Oshawa",
                "St. Catharines", "Barrie", "Cambridge", "Kingston", "Guelph",
                "Thunder Bay", "Waterloo", "Pickering", "Niagara Falls", "Whitby"
            };
        for (String city : citiesInOntario) {
        	choiceCity.add(city);
        }
        welcomePanel.add(choiceCity);

        // University dropdown
        choiceUniversity = new Choice();
        choiceUniversity.setBounds(398, 420, 150, 27);
        String[] universitiesInOntario = {
                "University of Toronto", "York University", "McMaster University",
                "University of Waterloo", "Western University", "Queen's University",
                "University of Ottawa", "Carleton University", "University of Guelph",
                "Lakehead University", "Trent University", "Wilfrid Laurier University",
                "Brock University", "Ryerson University", "Laurentian University",
                "Nipissing University", "Ontario Tech University", "Algoma University"
            };
        for (String university : universitiesInOntario) {
            choiceUniversity.add(university);
        }
        welcomePanel.add(choiceUniversity);

        // Labels for text fields
        Label labelUsername = new Label("Create a Username *");
        labelUsername.setBounds(237, 161, 150, 16);
        welcomePanel.add(labelUsername);

        Label labelDob = new Label("Select Date of Birth *");
        labelDob.setBounds(237, 211, 150, 16);
        welcomePanel.add(labelDob);

        Label labelBio = new Label("About Me/Bio *");
        labelBio.setBounds(237, 261, 150, 16);
        welcomePanel.add(labelBio);

        Label labelProvince = new Label("Province");
        labelProvince.setBounds(208, 400, 100, 16);
        welcomePanel.add(labelProvince);

        Label labelUniversity = new Label("University");
        labelUniversity.setBounds(398, 400, 100, 16);
        welcomePanel.add(labelUniversity);

        // Save button
        JButton btnSave = new JButton("Save");
        btnSave.setBounds(292, 470, 117, 29);
        welcomePanel.add(btnSave);

        // Action listener for save button
        btnSave.addActionListener(this::handleSave);
    }

    private void handleSignUp(ActionEvent e) {
        String name = textFieldName.getText();
        String email = textFieldEmail.getText();
        String password = new String(passwordField.getPassword());

        // Validate input fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String passwordHash = Integer.toHexString(password.hashCode());

        // Create a user profile and add to Firestore
        String userId = String.valueOf(System.currentTimeMillis()); // Unique ID based on current time
        UserProfile user = new UserProfile(userId, name, email);
        FirestoreHandler.addUserData(user);

        // Show confirmation message
        JOptionPane.showMessageDialog(frame, "Sign up successful! User ID: " + userId, "Success", JOptionPane.INFORMATION_MESSAGE);

        // Set user data to the welcome panel and switch to it
        lblUserName.setText(name);
        lblUserEmail.setText(email);
        cardLayout.show(mainPanel, "Welcome");
    }

    private void handleSave(ActionEvent e) {
        String userId = String.valueOf(System.currentTimeMillis());
        String username = lblUserName.getText();
        String email = lblUserEmail.getText();
        String dateOfBirth = dobField.getText();
        String bio = bioTextField.getText();
        String province = choiceCity.getSelectedItem();
        String university = choiceUniversity.getSelectedItem();

        // Get selected interests
        String interestsSummary = lblInterestsSummary.getText().replace("Selected Interests: ", "");
        List<String> interests = interestsSummary.isEmpty() ? new ArrayList<>() : List.of(interestsSummary.split(", "));

        // Create user profile and save to Firestore
        UserProfile userProfile = new UserProfile(userId, username, email, bio, dateOfBirth, province, university, interests);
        FirestoreHandler.addUserData(userProfile);

        JOptionPane.showMessageDialog(frame, "Profile saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInterestSelectionDialog() {
        JDialog dialog = new JDialog(frame, "Select Your Interests", true);
        dialog.setSize(400, 300);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(frame);

        JPanel interestsPanel = new JPanel();
        interestsPanel.setLayout(new BoxLayout(interestsPanel, BoxLayout.Y_AXIS));

        String[] interests = {
                "Sports", "Music", "Reading", "Travel", "Art", "Technology", "Cooking", "Fitness", "Gaming", "Movies",
                "Photography", "Fashion", "Environment", "Social Media", "Entrepreneurship", "Volunteering", "Writing",
                "Public Speaking", "Languages", "Hiking", "Yoga", "Meditation", "Health & Wellness", "Debate",
                "Community Service", "Cultural Activities", "Programming", "Robotics", "Startups", "Investing",
                "Astronomy", "Biology", "Physics", "Chemistry", "Mathematics", "Economics", "History", "Philosophy",
                "Political Science", "Psychology", "Sociology", "Graphic Design", "Video Editing", "Content Creation",
                "Podcasts", "Camping", "Food Tasting", "Dance", "Theater", "Stand-Up Comedy", "Board Games",
                "Card Games", "Puzzles", "Gardening", "Pets & Animals", "Anime", "Comics", "Creative Writing",
                "Journalism", "3D Modeling", "Virtual Reality", "Augmented Reality", "Cryptocurrency", "Blockchain",
                "Marketing", "Advertising", "Digital Art", "Painting", "Sculpting", "Music Production", "DJing",
                "Cars", "Motorcycles", "DIY Projects", "Home Decor", "Cooking Experiments", "Baking", "Mixology",
                "Event Planning", "Networking", "Career Development", "Fitness Challenges", "Weightlifting",
                "CrossFit", "Rugby", "Soccer", "Basketball", "Swimming", "Martial Arts", "Self-Defense", "Esports",
                "Streaming", "Interior Design", "Mindfulness", "Climate Activism", "Pet Care", "Charity Work",
                "Startup Pitches", "Business Plan Writing", "Urban Exploration", "Bird Watching", "Science Fiction",
                "Fantasy", "Classical Music", "Hip Hop", "Rock Music", "Electronic Music", "Jazz", "Blues", "Country Music",
                "Reggae", "K-Pop", "J-Pop", "Latino Music", "Dancehall", "Piano", "Guitar", "Drums", "Violin",
                "Networking Events", "TED Talks", "Personal Finance", "Home Brewing", "Cheese Tasting", "Public Relations",
                "Social Activism", "Podcast Hosting", "Speech Competitions", "Debate Club", "Yoga Retreats", "Survival Skills",
                "Outdoor Adventures", "Mountain Biking", "Skateboarding", "Snowboarding", "Skiing", "Fishing", "Kayaking",
                "Sailing", "Scuba Diving", "Freediving", "Surfing", "Archery", "Horseback Riding", "Cycling",
                "Triathlons", "Running", "Marathon Training", "Hunting", "Skydiving", "Bungee Jumping", "Parkour",
                "Geocaching", "Street Art", "Mural Painting", "Tattoo Art", "Hairstyling", "Cosplay", "Conventions",
                "LARPing (Live Action Role Playing)", "Escape Rooms", "Trivia Nights", "Improv Comedy", "Sketching",
                "Woodworking", "Metalworking", "Leather Crafting", "Knitting", "Crocheting", "Sewing", "Quilting",
                "Storytelling", "Magic Tricks", "Collecting", "Antique Hunting", "Vinyl Collecting", "Record Stores",
                "Museum Hopping", "Concerts", "Theater Plays", "Opera", "Ballet", "Trivia Games", "Learning New Languages",
                "Coding Hackathons", "Mathematics Competitions", "Model United Nations", "Science Fairs", "History Reenactment",
                "Cultural Festivals", "Travel Blogging", "Food Blogging", "Vlogging", "Fitness Blogging", "Book Clubs",
                "Study Groups", "Research Projects", "Academic Writing", "Grant Writing", "Academic Conferences", "Eco-Friendly Lifestyle",
                "Upcycling", "Minimalism", "Zero Waste", "Urban Gardening", "Sustainable Development", "Wildlife Conservation"
            };

        List<JCheckBox> checkBoxes = new ArrayList<>();
        for (String interest : interests) {
            JCheckBox checkBox = new JCheckBox(interest);
            interestsPanel.add(checkBox);
            checkBoxes.add(checkBox);
        }

        JScrollPane scrollPane = new JScrollPane(interestsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(ev -> {
            List<String> selectedInterests = new ArrayList<>();
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
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
