package com.universe.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import com.universe.FirestoreHandler;
import com.universe.models.UserProfile;
import com.universe.utils.Constants;
import com.universe.utils.SessionManager;

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
	private String encodedProfilePicture;

	public static void main(String[] args) {

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
		frame.setBounds(100, 100, 900, 600);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		// Create a background panel
		JPanel backgroundPanel = new JPanel() {
			private Image backgroundImage;

			{
				try {
					backgroundImage = new ImageIcon("src/main/resources/wallpaper.png").getImage()
							.getScaledInstance(900, 600, Image.SCALE_SMOOTH);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Failed to load background image.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (backgroundImage != null) {
					g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
				}
			}
		};

		backgroundPanel.setLayout(new BorderLayout());
		frame.setContentPane(backgroundPanel);

		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);
		mainPanel.setOpaque(false);
		backgroundPanel.add(mainPanel, BorderLayout.CENTER);

		JPanel signUpPanel = new JPanel();
		signUpPanel.setLayout(null);
		signUpPanel.setOpaque(false);
		initializeSignUpPanel(signUpPanel);

		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(null);
		loginPanel.setOpaque(false);
		initializeLoginPanel(loginPanel);

		JPanel welcomePanel = new JPanel();
		welcomePanel.setLayout(null);
		welcomePanel.setOpaque(false);
		initializeWelcomePanel(welcomePanel);

		mainPanel.add(signUpPanel, "SignUp");
		mainPanel.add(loginPanel, "Login");
		mainPanel.add(welcomePanel, "Welcome");

		cardLayout.show(mainPanel, "SignUp");
	}

	private void initializeSignUpPanel(JPanel signUpPanel) {
		// Adjust vertical and horizontal placement
		int verticalOffset = 250;
		int horizontalOffset = 100;

		// Full Name Field
		textFieldName = new JTextField();
		textFieldName.setBounds(horizontalOffset + 250, verticalOffset, 212, 34); // Adjusted X position
		signUpPanel.add(textFieldName);

		JLabel lblName = new JLabel("Full Name:");
		lblName.setBounds(horizontalOffset + 150, verticalOffset + 5, 76, 16); // Adjusted X position
		signUpPanel.add(lblName);

		// Email Field
		textFieldEmail = new JTextField();
		textFieldEmail.setBounds(horizontalOffset + 250, verticalOffset + 60, 212, 34); // Adjusted X position
		signUpPanel.add(textFieldEmail);

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setBounds(horizontalOffset + 150, verticalOffset + 65, 38, 16); // Adjusted X position
		signUpPanel.add(lblEmail);

		// Password Field
		passwordField = new JPasswordField();
		passwordField.setBounds(horizontalOffset + 250, verticalOffset + 120, 212, 36); // Adjusted X position
		signUpPanel.add(passwordField);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(horizontalOffset + 150, verticalOffset + 125, 71, 16); // Adjusted X position
		signUpPanel.add(lblPassword);

		// Sign-Up Button
		JButton btnSignUp = new JButton("Sign Up");
		btnSignUp.setBounds(horizontalOffset + 290, verticalOffset + 180, 117, 29); // Adjusted X position
		signUpPanel.add(btnSignUp);

		btnSignUp.addActionListener(e -> {
			String username = textFieldName.getText();
			String email = textFieldEmail.getText();
			String password = new String(passwordField.getPassword());

			if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
				JOptionPane.showMessageDialog(frame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Generate user ID and save it in Firestore
			String userId = String.valueOf(System.currentTimeMillis());
			String passwordHash = Integer.toHexString(password.hashCode());
			UserProfile user = new UserProfile(userId, username, email, passwordHash);
			FirestoreHandler.addUserData(user);

			// Store user information in SessionManager
			SessionManager.currentUserId = userId;
			SessionManager.currentUser = username;

			// Update Welcome Panel with user details
			lblUserName.setText("Name: " + username);
			lblUserEmail.setText("Email: " + email);

			// Inform the user and navigate to the Update Profile (Welcome) panel
			JOptionPane.showMessageDialog(frame, "Sign up successful! Please update your profile.", "Success",
					JOptionPane.INFORMATION_MESSAGE);
			cardLayout.show(mainPanel, "Welcome"); // Switch to Welcome (Update Profile) panel
		});

		JLabel lblLoginLink = new JLabel("<html><u>Already have an account? Login</u></html>");
		lblLoginLink.setForeground(Color.BLUE);
		lblLoginLink.setBounds(horizontalOffset + 250, verticalOffset + 230, 200, 30); // Adjusted X position
		signUpPanel.add(lblLoginLink);

		lblLoginLink.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				cardLayout.show(mainPanel, "Login");
			}
		});
	}

	private void initializeLoginPanel(JPanel loginPanel) {
		// Adjust horizontal and vertical placement
		int horizontalOffset = 100;
		int verticalOffset = 075;

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setBounds(horizontalOffset + 150, 180 + verticalOffset, 100, 30); // Adjusted X and Y position
		loginPanel.add(lblEmail);

		JTextField emailField = new JTextField();
		emailField.setBounds(horizontalOffset + 250, 180 + verticalOffset, 212, 30); // Adjusted X and Y position
		loginPanel.add(emailField);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(horizontalOffset + 150, 240 + verticalOffset, 100, 30); // Adjusted X and Y position
		loginPanel.add(lblPassword);

		JPasswordField passwordField = new JPasswordField();
		passwordField.setBounds(horizontalOffset + 250, 240 + verticalOffset, 212, 30); // Adjusted X and Y position
		loginPanel.add(passwordField);

		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(horizontalOffset + 290, 300 + verticalOffset, 100, 30); // Adjusted X and Y position
		loginPanel.add(btnLogin);

		btnLogin.addActionListener(e -> {
			String email = emailField.getText();
			String password = new String(passwordField.getPassword());
			String passwordHash = Integer.toHexString(password.hashCode()); // Hash the password

			if (FirestoreHandler.authenticateUser(email, passwordHash)) {
				// Fetch the UserProfile directly using email
				UserProfile userProfile = FirestoreHandler.findUserByEmail(email);

				if (userProfile != null) {
					// Store the username and ID globally
					SessionManager.currentUserId = userProfile.getUserId();
					SessionManager.currentUser = userProfile.getUsername();

					// Navigate to the Homepage
					Homepage homepage = new Homepage();
					homepage.setVisible(true);
					frame.dispose(); // Close the login window
				} else {
					JOptionPane.showMessageDialog(frame, "Error retrieving user profile.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(frame, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		JLabel lblSignUpLink = new JLabel("<html><u>Don't have an account? Sign Up</u></html>");
		lblSignUpLink.setForeground(Color.BLUE);
		lblSignUpLink.setBounds(horizontalOffset + 250, 350 + verticalOffset, 200, 30); // Adjusted X and Y position
		loginPanel.add(lblSignUpLink);

		lblSignUpLink.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				cardLayout.show(mainPanel, "SignUp");
			}
		});
	}

	private void initializeWelcomePanel(JPanel welcomePanel) {
		// Offsets for positioning
		int verticalOffset = 250; // Slightly raise everything
		int leftColumnX = 50; // Left column starting X position
		int rightColumnX = 400; // Right column starting X position
		int centerX = 350; // Center X for Save Profile button
		int fieldHeight = 25; // Standard height for fields
		int labelWidth = 120; // Width for labels
		int fieldWidth = 200; // Width for fields

		// Profile Picture Section (Left)
		JLabel lblProfilePic = new JLabel("Profile Picture:");
		lblProfilePic.setBounds(leftColumnX, verticalOffset, labelWidth, fieldHeight);
		welcomePanel.add(lblProfilePic);

		profilePicLabel = new JLabel();
		profilePicLabel.setBounds(leftColumnX + labelWidth, verticalOffset, 100, 100);
		profilePicLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		welcomePanel.add(profilePicLabel);

		JButton btnAddPic = new JButton("Add Picture");
		btnAddPic.setBounds(leftColumnX + labelWidth, verticalOffset + 110, 100, fieldHeight); // Directly below the
																								// picture
		welcomePanel.add(btnAddPic);
		btnAddPic.addActionListener(e -> handleAddPicture());

		// Name and Email (Left)
		lblUserName = new JLabel("Name: ");
		lblUserName.setFont(new Font("Arial", Font.BOLD, 16));
		lblUserName.setBounds(leftColumnX, verticalOffset + 150, labelWidth + 200, fieldHeight);
		welcomePanel.add(lblUserName);

		lblUserEmail = new JLabel("Email: ");
		lblUserEmail.setFont(new Font("Arial", Font.BOLD, 16));
		lblUserEmail.setBounds(leftColumnX, verticalOffset + 180, labelWidth + 200, fieldHeight);
		welcomePanel.add(lblUserEmail);

		// Date of Birth (Right)
		JLabel lblDob = new JLabel("Date of Birth:");
		lblDob.setBounds(rightColumnX, verticalOffset, labelWidth, fieldHeight);
		welcomePanel.add(lblDob);

		try {
			MaskFormatter dateMask = new MaskFormatter("##/##/####");
			dateMask.setPlaceholderCharacter('_');
			dobField = new JFormattedTextField(dateMask);
			dobField.setBounds(rightColumnX + labelWidth, verticalOffset, fieldWidth, fieldHeight);
			welcomePanel.add(dobField);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Bio (Right)
		JLabel lblBio = new JLabel("Bio:");
		lblBio.setBounds(rightColumnX, verticalOffset + 40, labelWidth, fieldHeight);
		welcomePanel.add(lblBio);

		bioTextField = new JTextField();
		bioTextField.setBounds(rightColumnX + labelWidth, verticalOffset + 40, fieldWidth, fieldHeight);
		welcomePanel.add(bioTextField);

		// City Dropdown (Right)
		JLabel lblCity = new JLabel("City:");
		lblCity.setBounds(rightColumnX, verticalOffset + 80, labelWidth, fieldHeight);
		welcomePanel.add(lblCity);

		choiceCity = new Choice();
		choiceCity.setBounds(rightColumnX + labelWidth, verticalOffset + 80, fieldWidth, fieldHeight);
	
		for (String city : Constants.CITIES) {
			choiceCity.add(city);
		}
		welcomePanel.add(choiceCity);

		// University Dropdown (Right)
		JLabel lblUniversity = new JLabel("University:");
		lblUniversity.setBounds(rightColumnX, verticalOffset + 120, labelWidth, fieldHeight);
		welcomePanel.add(lblUniversity);

		choiceUniversity = new Choice();
		choiceUniversity.setBounds(rightColumnX + labelWidth, verticalOffset + 120, fieldWidth, fieldHeight);
		
		for (String university : Constants.UNIVERSITIES) {
			choiceUniversity.add(university);
		}
		welcomePanel.add(choiceUniversity);

		// Interests Section (Right)
		JLabel lblInterests = new JLabel("Interests:");
		lblInterests.setBounds(rightColumnX, verticalOffset + 160, labelWidth, fieldHeight);
		welcomePanel.add(lblInterests);

		lblInterestsSummary = new JLabel("None selected");
		lblInterestsSummary.setBounds(rightColumnX + labelWidth, verticalOffset + 160, fieldWidth, fieldHeight);
		welcomePanel.add(lblInterestsSummary);

		JButton btnSelectInterests = new JButton("Select Interests");
		btnSelectInterests.setBounds(rightColumnX + labelWidth, verticalOffset + 190, 150, fieldHeight);
		welcomePanel.add(btnSelectInterests);
		btnSelectInterests.addActionListener(e -> showInterestSelectionDialog());

		// Save Profile Button (Center-Bottom)
		JButton btnSave = new JButton("Save Profile");
		btnSave.setBounds(centerX, verticalOffset + 250, 150, fieldHeight); // Centered at the bottom
		welcomePanel.add(btnSave);
		btnSave.addActionListener(e -> handleSave(e));
	}

	private void showInterestSelectionDialog() {
		JDialog dialog = new JDialog(frame, "Select Interests", true);
		dialog.setSize(400, 300);
		dialog.setLayout(new BorderLayout());

		JPanel interestsPanel = new JPanel();
		interestsPanel.setLayout(new BoxLayout(interestsPanel, BoxLayout.Y_AXIS));

		String[] interests = Constants.INTERESTS;

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
	    dialog.setLocationRelativeTo(null);

		
		dialog.setVisible(true);
	}


	private void handleAddPicture() {
	    JFileChooser fileChooser = new JFileChooser();
	    int result = fileChooser.showOpenDialog(frame);
	    if (result == JFileChooser.APPROVE_OPTION) {
	        try {
	            File selectedFile = fileChooser.getSelectedFile();
	            BufferedImage image = ImageIO.read(selectedFile);

	            // Encode image to Base64
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            ImageIO.write(image, "png", baos); // Use "png" or other format
	            byte[] imageBytes = baos.toByteArray();
	            encodedProfilePicture = Base64.getEncoder().encodeToString(imageBytes);

	            // Display the image in the UI
	            ImageIcon profilePic = new ImageIcon(selectedFile.getAbsolutePath());
	            Image scaledImage = profilePic.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
	            profilePicLabel.setIcon(new ImageIcon(scaledImage));
	        } catch (Exception e) {
	            JOptionPane.showMessageDialog(frame, "Failed to load image.", "Error", JOptionPane.ERROR_MESSAGE);
	            e.printStackTrace();
	        }
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

		JOptionPane.showMessageDialog(frame, "Sign up successful! Please complete your profile.", "Success",
				JOptionPane.INFORMATION_MESSAGE);
		cardLayout.show(mainPanel, "Welcome");
	}


	private void handleSave(ActionEvent e) {
	    if (SessionManager.currentUserId == null) {
	        JOptionPane.showMessageDialog(frame, "No user logged in to save data for.", "Error",
	                JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    String username = lblUserName.getText().replace("Name: ", "");
	    String email = lblUserEmail.getText().replace("Email: ", "");
	    String dateOfBirth = dobField.getText();
	    String bio = bioTextField.getText();
	    String city = choiceCity.getSelectedItem();
	    String university = choiceUniversity.getSelectedItem();

	    // Get selected interests
	    String interestsSummary = lblInterestsSummary.getText().replace("Selected Interests: ", "");
	    List<String> interests = interestsSummary.isEmpty() ? new ArrayList<>() : List.of(interestsSummary.split(", "));

	    // Fetch existing user data to preserve passwordHash
	    UserProfile existingUser = FirestoreHandler.getUserData(SessionManager.currentUserId);
	    if (existingUser == null) {
	        JOptionPane.showMessageDialog(frame, "User data not found in Firestore.", "Error",
	                JOptionPane.ERROR_MESSAGE);
	        return;
	    }

	    // Create updated user profile while preserving the passwordHash
	    UserProfile updatedUser = new UserProfile(
	        SessionManager.currentUserId, 
	        username, 
	        email, 
	        bio, 
	        dateOfBirth, 
	        city,
	        university, 
	        interests, 
	        existingUser.getPasswordHash(), 
	        encodedProfilePicture // Include the profile picture
	    );

	    // Update Firestore
	    FirestoreHandler.updateUserData(updatedUser);

	    JOptionPane.showMessageDialog(frame, "Profile updated successfully!", "Success",
	            JOptionPane.INFORMATION_MESSAGE);

	    // Navigate to the Homepage
	    EventQueue.invokeLater(() -> {
	        Homepage homepage = new Homepage();
	        homepage.setVisible(true);
	        homepage.setLocationRelativeTo(null);
	        frame.dispose(); // Close the Update Profile window
	    });
	}
	

}