
package com.universe.gui;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.universe.FirebaseInitializer;
import com.universe.FirestoreHandler;
import com.universe.models.UserProfile;
import com.universe.utils.Constants;
import com.universe.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;

public class Homepage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel friendsListPanel; // Left panel for searching users
	private JPanel rightFriendsListPanel; // Panel on the right for added friends
	private JTextField searchField;

	private List<UserProfile> allUsers; // Store all users fetched from the database
	private List<UserProfile> addedFriends; // Store added friends for the logged-in user
	private UserProfile currentUser; // Logged-in user's profile

	private JLabel noFriendsLabel; // Label for "No friends added yet"
	private JFormattedTextField dobField; 
	private JLabel welcomeLabel;
	private JLabel profilePic; // Class-level declaration
	/**
	 * Create the frame.
	 */
	public Homepage() {
		// Fetch the current user's details
		String currentUserId = SessionManager.currentUserId;
		if (currentUserId == null || currentUserId.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No user logged in.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		currentUser = FirestoreHandler.getUserData(currentUserId);
		if (currentUser == null) {
			JOptionPane.showMessageDialog(null, "Error fetching user data.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		setTitle("Welcome, " + currentUser.getUsername() + "!");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		contentPane.setBackground(new Color(210, 236, 255));
		setContentPane(contentPane);
		setLocationRelativeTo(null);

		// Initialize the added friends list
		addedFriends = FirestoreHandler.getUserContacts(currentUserId);
		if (addedFriends == null) {
			addedFriends = new ArrayList<>();
		}

		
		// Welcome panel
		JPanel welcomePanel = new JPanel();
		welcomePanel.setBounds(100, 10, 770, 60);
		welcomePanel.setBackground(Color.WHITE);
		welcomePanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
		welcomePanel.setLayout(null);
		
		welcomeLabel = new JLabel("Hi " + currentUser.getUsername() + ", Welcome to UniVerse!", JLabel.CENTER);
		welcomeLabel.setFont(new Font("Inria Sans", Font.BOLD, 20));
		welcomeLabel.setForeground(new Color(31, 162, 255));
		welcomeLabel.setBounds(0, 5, 770, 50);
		welcomePanel.add(welcomeLabel);
		contentPane.add(welcomePanel);

		// Sidebar
		JPanel sidebar = createSidebar(this);
		contentPane.add(sidebar);

		// Create panels in the correct order
		createRightFriendsPanel(); // Initialize rightFriendsListPanel
		createFriendsPanel(); // Initialize friends list panel

		// Populate added friends and update the right panel
		updateRightFriendsList();

		// Set up a real-time listener for friends updates
		FirestoreHandler.getFriends((snapshots, e) -> {
			if (e != null) {
				System.err.println("Error listening to real-time updates: " + e.getMessage());
				return;
			}

			synchronized (addedFriends) {
				addedFriends.clear(); // Clear the list before adding

				for (QueryDocumentSnapshot doc : snapshots.getDocuments()) {
					String contactUserId = doc.getString("contactUserId");
					String username = doc.getString("username");
					String university = doc.getString("university");

					// Avoid adding duplicates
					if (addedFriends.stream().noneMatch(friend -> friend.getUserId().equals(contactUserId))) {
						addedFriends.add(new UserProfile(contactUserId, username, "", "", university));
					}
				}
			}

			updateRightFriendsList(); // Refresh the UI
		}, SessionManager.currentUserId);

	}

	private void createFriendsPanel() {
		JPanel friendsPanel = new JPanel();
		friendsPanel.setBounds(100, 80, 270, 480);
		friendsPanel.setBackground(Color.WHITE);
		friendsPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
		friendsPanel.setLayout(null);

		JLabel searchLabel = new JLabel("Search for friends:");
		searchLabel.setBounds(15, 10, 200, 20);
		searchLabel.setFont(new Font("Roboto", Font.BOLD, 14));
		friendsPanel.add(searchLabel);

		// Search bar
		searchField = new JTextField();
		searchField.setBounds(15, 35, 180, 30);
		searchField.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
		friendsPanel.add(searchField);

		// Search button
		JButton searchButton = new JButton("");
		searchButton.setEnabled(false);
		searchButton.setBounds(200, 35, 60, 30);
		searchButton.setFont(new Font("Roboto", Font.BOLD, 12));
		searchButton.setBackground(new Color(46, 157, 251));
		searchButton.setForeground(Color.WHITE);
		searchButton.setFocusPainted(false);
		friendsPanel.add(searchButton);

		// Friends list panel with a scrollable view
		friendsListPanel = new JPanel();
		friendsListPanel.setLayout(new BoxLayout(friendsListPanel, BoxLayout.Y_AXIS));
		friendsListPanel.setBackground(Color.WHITE);

		JScrollPane scrollPane = new JScrollPane(friendsListPanel);
		scrollPane.setBounds(15, 75, 240, 390);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		friendsPanel.add(scrollPane);

		contentPane.add(friendsPanel);

		// Populate friends list from the database
		allUsers = FirestoreHandler.getAllUsers();
		populateFriendsList(allUsers);

		// Add search functionality
		searchButton.addActionListener(e -> handleSearch());
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				handleSearch();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				handleSearch();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				handleSearch();
			}
		});
	}

	private void createRightFriendsPanel() {
		JPanel friendsPanelRight = new JPanel();
		friendsPanelRight.setBounds(380, 80, 490, 480);
		friendsPanelRight.setBackground(Color.WHITE);
		friendsPanelRight.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
		friendsPanelRight.setLayout(null);

		JLabel friendsTitleLabel = new JLabel("Your Friends", JLabel.CENTER);
		friendsTitleLabel.setFont(new Font("Roboto", Font.BOLD, 18));
		friendsTitleLabel.setBounds(0, 10, 490, 30);
		friendsTitleLabel.setOpaque(true);
		friendsTitleLabel.setBackground(new Color(46, 157, 251));
		friendsTitleLabel.setForeground(Color.WHITE);
		friendsPanelRight.add(friendsTitleLabel);

		// Initialize rightFriendsListPanel here
		rightFriendsListPanel = new JPanel();
		rightFriendsListPanel.setLayout(new BoxLayout(rightFriendsListPanel, BoxLayout.Y_AXIS));
		rightFriendsListPanel.setBackground(Color.WHITE);

		JScrollPane rightScrollPane = new JScrollPane(rightFriendsListPanel);
		rightScrollPane.setBounds(10, 50, 470, 410);
		rightScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		friendsPanelRight.add(rightScrollPane);

		// Add to contentPane
		contentPane.add(friendsPanelRight);
	}

	private void populateFriendsList(List<UserProfile> users) {
		friendsListPanel.removeAll();

		if (users != null && !users.isEmpty()) {
			for (UserProfile user : users) {
				// Exclude the current user from the friends list
				if (!user.getUserId().equals(currentUser.getUserId())) {
					addFriendEntry(user);
				}
			}
		} else {
			JLabel noUsersLabel = new JLabel("No users found!", JLabel.CENTER);
			noUsersLabel.setFont(new Font("Roboto", Font.BOLD, 16));
			noUsersLabel.setForeground(Color.GRAY);
			friendsListPanel.add(noUsersLabel);
		}

		friendsListPanel.revalidate();
		friendsListPanel.repaint();
	}

	private void updateRightFriendsList() {
		rightFriendsListPanel.removeAll();

		synchronized (addedFriends) {
			List<UserProfile> uniqueFriends = addedFriends.stream().distinct().toList();

			if (uniqueFriends.isEmpty()) {
				if (noFriendsLabel == null) {
					noFriendsLabel = new JLabel("No friends added yet!", JLabel.CENTER);
					noFriendsLabel.setFont(new Font("Roboto", Font.BOLD, 16));
					noFriendsLabel.setForeground(Color.GRAY);
				}
				rightFriendsListPanel.add(noFriendsLabel);
			} else {
				for (UserProfile friend : uniqueFriends) {
					addToRightFriendsPanel(friend);
				}
			}
		}

		rightFriendsListPanel.revalidate();
		rightFriendsListPanel.repaint();
	}

	private void handleSearch() {
		String query = searchField.getText().trim().toLowerCase(); // Convert query to lowercase for case-insensitive
																	// search
		if (query.isEmpty()) {
			// If the search field is empty, display all users
			populateFriendsList(allUsers);
			return;
		}
		List<UserProfile> filteredUsers = new ArrayList<>();
		for (UserProfile user : allUsers) {
			// Check if the query matches any user field
			boolean matches = false;
			if (user.getUsername() != null && user.getUsername().toLowerCase().contains(query)) {
				matches = true;
			} else if (user.getUniversity() != null && user.getUniversity().toLowerCase().contains(query)) {
				matches = true;
			} else if (user.getProvince() != null && user.getProvince().toLowerCase().contains(query)) {
				matches = true;
			} else if (user.getInterests() != null) {
				// Check if the query matches any interest
				for (String interest : user.getInterests()) {
					if (interest != null && interest.toLowerCase().contains(query)) {
						matches = true;
						break;
					}
				}
			}
			if (matches) {
				filteredUsers.add(user);
			}
		}
		populateFriendsList(filteredUsers); // Update the friends list with filtered users
	}

	private void addToRightFriendsPanel(UserProfile user) {
		JPanel friendEntry = new JPanel();
		friendEntry.setPreferredSize(new Dimension(470, 60));
		friendEntry.setBackground(new Color(230, 230, 230));
		friendEntry.setLayout(null);

		JLabel friendName = new JLabel("Name: " + user.getUsername());
		friendName.setFont(new Font("Roboto", Font.BOLD, 14));
		friendName.setBounds(10, 5, 300, 20);
		friendEntry.add(friendName);

		JLabel friendUniversity = new JLabel("University: " + user.getUniversity());
		friendUniversity.setFont(new Font("Roboto", Font.PLAIN, 12));
		friendUniversity.setBounds(10, 30, 300, 20);
		friendEntry.add(friendUniversity);

		// "Message" button
		JButton messageButton = new JButton("Message");
		messageButton.setBounds(290, 15, 90, 30);
		messageButton.setFont(new Font("Roboto", Font.BOLD, 10));
		messageButton.setBackground(new Color(46, 157, 251)); // Blue color
		messageButton.setForeground(Color.BLACK);
		messageButton.addActionListener(e -> {
			// Open the Messaging page with the selected contact
			Messaging messagingPage = new Messaging();
			messagingPage.switchChat(user.getUserId(), user.getUsername()); // Pass userId and username
			messagingPage.setVisible(true);
			messagingPage.setLocationRelativeTo(null);
			dispose(); // Close the Homepage
		});
		friendEntry.add(messageButton);

		JButton removeButton = new JButton("Remove");
		removeButton.setBounds(390, 15, 100, 30);
		removeButton.setFont(new Font("Roboto", Font.BOLD, 10));
		removeButton.setBackground(new Color(231, 76, 60)); // Red color
		removeButton.setForeground(Color.BLACK);
		removeButton.addActionListener(e -> {
			synchronized (addedFriends) {
				addedFriends.remove(user);
			}
			FirestoreHandler.removeFriend(SessionManager.currentUserId, user.getUserId());
			refreshRightPanelAfterDelay();
		});
		friendEntry.add(removeButton);

		rightFriendsListPanel.add(friendEntry);
	}

	private void addFriendEntry(UserProfile user) {
		JPanel friendEntry = new JPanel();
		friendEntry.setPreferredSize(new Dimension(230, 60));
		friendEntry.setBackground(new Color(230, 230, 230));
		friendEntry.setLayout(null);

		JLabel friendName = new JLabel(user.getUsername());
		friendName.setFont(new Font("Roboto", Font.BOLD, 14));
		friendName.setBounds(10, 5, 120, 20);
		friendEntry.add(friendName);

		JLabel friendUniversity = new JLabel(user.getUniversity());
		friendUniversity.setFont(new Font("Roboto", Font.PLAIN, 12));
		friendUniversity.setBounds(10, 30, 120, 20);
		friendEntry.add(friendUniversity);

		// Profile button
		JButton profileButton = new JButton("Profile");
		profileButton.setBounds(150, 10, 70, 30);
		profileButton.setFont(new Font("Roboto", Font.BOLD, 10));
		profileButton.setBackground(new Color(46, 157, 251)); // Blue color
		profileButton.setForeground(Color.BLACK);
		profileButton.addActionListener(e -> showProfilePopup(user));
		friendEntry.add(profileButton);

		friendsListPanel.add(friendEntry);
	}

	private void refreshRightPanelAfterDelay() {
		// Use a Swing Timer for the delay (e.g., 300ms)
		new javax.swing.Timer(300, e -> {
			updateRightFriendsList();
		}).start();
	}

	private void showProfilePopup(UserProfile user) {
		JPanel profilePanel = new JPanel();
		profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
		profilePanel.add(new JLabel("Name: " + user.getUsername()));
		profilePanel.add(new JLabel("University: " + user.getUniversity()));
		profilePanel.add(new JLabel("City: " + user.getProvince()));
		profilePanel.add(new JLabel("Interest: " + user.getInterests()));
		profilePanel.add(new JLabel("Date of Birth: " + user.getDateOfBirth()));
		profilePanel.add(new JLabel("Email: " + user.getEmail()));

		int option = JOptionPane.showOptionDialog(this, profilePanel, "User Profile", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, new String[] { "Add Friend", "Close" }, "Close");

		if (option == JOptionPane.OK_OPTION) {
			// Check if the user is already added
			synchronized (addedFriends) {
				boolean isAlreadyAdded = addedFriends.stream()
						.anyMatch(friend -> friend.getUserId().equals(user.getUserId()));
				if (isAlreadyAdded) {
					// Notify the user that the friend is already added
					JOptionPane.showMessageDialog(this, user.getUsername() + " is already added as a friend!",
							"Friend Already Added", JOptionPane.WARNING_MESSAGE);
				} else {
					// Add the friend to the Firestore and update UI
					FirestoreHandler.addFriend(SessionManager.currentUserId, user.getUserId(), user.getUsername(),
							user.getUniversity());
					addedFriends.add(user);
					refreshRightPanelAfterDelay();
					JOptionPane.showMessageDialog(this, user.getUsername() + " has been added as a friend!", "Success",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}

	public void showProfile(UserProfile user, JFrame parentFrame) {
	    JDialog dialog = new JDialog(parentFrame, "Edit My Profile", true);
	    dialog.setSize(500, 700);
	    dialog.getContentPane().setLayout(null);

	    int yPosition = 20; // Vertical position for each component
	    int labelWidth = 120;
	    int fieldWidth = 250;
	    int fieldHeight = 30;

	    // Full Name Field (Not Editable)
	    JLabel lblName = new JLabel("Full Name:");
	    lblName.setBounds(20, yPosition, labelWidth, fieldHeight);
	    dialog.getContentPane().add(lblName);

	    JLabel nameField = new JLabel(user.getUsername());
	    nameField.setBounds(150, yPosition, fieldWidth, fieldHeight);
	    dialog.getContentPane().add(nameField);

	    yPosition += 50;

	    // Email Field (Not Editable)
	    JLabel lblEmail = new JLabel("Email:");
	    lblEmail.setBounds(20, yPosition, labelWidth, fieldHeight);
	    dialog.getContentPane().add(lblEmail);

	    JLabel emailField = new JLabel(user.getEmail());
	    emailField.setBounds(150, yPosition, fieldWidth, fieldHeight);
	    dialog.getContentPane().add(emailField);

	    yPosition += 50;

	    // Date of Birth Field (Not Editable)
	    JLabel lblDob = new JLabel("Date of Birth:");
	    lblDob.setBounds(20, yPosition, labelWidth, fieldHeight);
	    dialog.getContentPane().add(lblDob);

	    JLabel dobField = new JLabel(user.getDateOfBirth());
	    dobField.setBounds(150, yPosition, fieldWidth, fieldHeight);
	    dialog.getContentPane().add(dobField);

	    yPosition += 50;

	    // Bio Field (Editable)
	    JLabel lblBio = new JLabel("Bio:");
	    lblBio.setBounds(20, yPosition, labelWidth, fieldHeight);
	    dialog.getContentPane().add(lblBio);

	    JTextField bioField = new JTextField(user.getBio() != null ? user.getBio() : "");
	    bioField.setBounds(150, yPosition, fieldWidth, fieldHeight);
	    dialog.getContentPane().add(bioField);

	    yPosition += 50;

	    // City Field (Editable)
	    JLabel lblCity = new JLabel("City:");
	    lblCity.setBounds(20, yPosition, labelWidth, fieldHeight);
	    dialog.getContentPane().add(lblCity);

	    Choice cityChoice = new Choice();
	    cityChoice.setBounds(150, yPosition, fieldWidth, fieldHeight);
	    for (String city : Constants.CITIES) {
	        cityChoice.add(city);
	    }
	    cityChoice.select(user.getProvince() != null ? user.getProvince() : "");
	    dialog.getContentPane().add(cityChoice);

	    yPosition += 50;

	    // University Field (Not Editable)
	    JLabel lblUniversity = new JLabel("University:");
	    lblUniversity.setBounds(20, yPosition, labelWidth, fieldHeight);
	    dialog.getContentPane().add(lblUniversity);

	    JLabel universityField = new JLabel(user.getUniversity());
	    universityField.setBounds(150, yPosition, fieldWidth, fieldHeight);
	    dialog.getContentPane().add(universityField);

	    yPosition += 50;

	    // Interests Field (Editable via Button)
	    JLabel lblInterests = new JLabel("Interests:");
	    lblInterests.setBounds(20, yPosition, labelWidth, fieldHeight);
	    dialog.getContentPane().add(lblInterests);

	    String interestsText = (user.getInterests() != null) ? String.join(", ", user.getInterests()) : "No interests specified";
	    JLabel interestsSummary = new JLabel(interestsText);
	    interestsSummary.setBounds(150, yPosition, fieldWidth, fieldHeight);
	    dialog.getContentPane().add(interestsSummary);

	    JButton btnEditInterests = new JButton("Edit Interests");
	    btnEditInterests.setBounds(150, yPosition + 40, 150, fieldHeight);
	    dialog.getContentPane().add(btnEditInterests);

	    btnEditInterests.addActionListener(e -> {
	        List<String> selectedInterests = showInterestSelectionDialog(parentFrame, user.getInterests() != null ? user.getInterests() : new ArrayList<>());
	        user.setInterests(selectedInterests); // Update user's interests immediately
	        interestsSummary.setText(String.join(", ", selectedInterests)); // Update displayed interests
	    });

	    yPosition += 100;

	    // Profile Picture Section
	    JLabel lblProfilePicture = new JLabel("Profile Picture:");
	    lblProfilePicture.setBounds(20, yPosition, labelWidth, fieldHeight);
	    dialog.getContentPane().add(lblProfilePicture);

	    JLabel profilePicturePreview = new JLabel();
	    profilePicturePreview.setBounds(150, yPosition, 100, 100);
	    if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
	        byte[] imageBytes = Base64.getDecoder().decode(user.getProfilePicture());
	        ImageIcon profileImageIcon = new ImageIcon(imageBytes);
	        Image scaledImage = profileImageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
	        profilePicturePreview.setIcon(new ImageIcon(scaledImage));
	    }
	    dialog.getContentPane().add(profilePicturePreview);

	    JButton btnChangeProfilePicture = new JButton("Change Picture");
	    btnChangeProfilePicture.setBounds(260, yPosition + 35, 140, fieldHeight);
	    btnChangeProfilePicture.addActionListener(e -> {
	        String newProfilePicture = selectProfilePicture();
	        if (newProfilePicture != null) {
	            user.setProfilePicture(newProfilePicture);
	            
	            // Update the preview in the dialog
	            byte[] imageBytes = Base64.getDecoder().decode(newProfilePicture);
	            ImageIcon profileImageIcon = new ImageIcon(imageBytes);
	            Image scaledImage = profileImageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
	            profilePicturePreview.setIcon(new ImageIcon(scaledImage));
	            
	            // Save the updated profile picture to Firestore
	            FirestoreHandler.updateUserData(user);
	            
	            // Refresh the sidebar profile picture
	            updateSidebarProfilePicture(newProfilePicture);
	        }
	    });
	    dialog.getContentPane().add(btnChangeProfilePicture);

	    yPosition += 120;

	    // Save Changes Button
	    JButton btnSave = new JButton("Save Changes");
	    btnSave.setBounds(180, yPosition, 150, fieldHeight);
	    dialog.getContentPane().add(btnSave);

	    btnSave.addActionListener(e -> {
	        // Update other fields
	        user.setBio(bioField.getText());
	        user.setProvince(cityChoice.getSelectedItem());

	        // Save updates to Firestore
	        FirestoreHandler.updateUserData(user);

	        
	        JOptionPane.showMessageDialog(dialog, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
	        dialog.dispose(); // Close the dialog
	    });

	    dialog.setLocationRelativeTo(null);
	    dialog.setVisible(true);
	}

	private void updateSidebarProfilePicture(String newProfilePicture) {
	    if (newProfilePicture != null && !newProfilePicture.isEmpty()) {
	        byte[] imageBytes = Base64.getDecoder().decode(newProfilePicture);
	        ImageIcon profileImageIcon = new ImageIcon(imageBytes);
	        Image scaledImage = profileImageIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
	        profilePic.setIcon(new ImageIcon(scaledImage)); // Update the sidebar picture
	    } else {
	        profilePic.setIcon(new ImageIcon("src/main/resources/icons/profile.png")); // Default icon
	    }
	    profilePic.revalidate();
	    profilePic.repaint();
	}
	private String selectProfilePicture() {
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Select Profile Picture");
	    fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));

	    int result = fileChooser.showOpenDialog(null);
	    if (result == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        try {
	            byte[] imageBytes = Files.readAllBytes(selectedFile.toPath());
	            return Base64.getEncoder().encodeToString(imageBytes);
	        } catch (IOException e) {
	            JOptionPane.showMessageDialog(null, "Error reading image file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
	    return null;
	}
	private String getFormattedInterests(UserProfile user) {
	    List<String> interests = user.getInterests();
	    if (interests == null || interests.isEmpty()) {
	        return "No interests specified"; // Placeholder text
	    }
	    return String.join(", ", interests);
	}
	private List<String> showInterestSelectionDialog(JFrame parentFrame, List<String> currentInterests) {
	    JDialog dialog = new JDialog(parentFrame, "Select Interests", true);
	    dialog.setSize(400, 300);
	    dialog.getContentPane().setLayout(new BorderLayout());

	    JPanel interestsPanel = new JPanel();
	    interestsPanel.setLayout(new BoxLayout(interestsPanel, BoxLayout.Y_AXIS));

	    // Use sorted interests from Constants
	    String[] interests = Constants.INTERESTS;

	    // Create checkboxes and preselect based on currentInterests
	    List<JCheckBox> checkBoxes = new ArrayList<>();
	    for (String interest : interests) {
	        JCheckBox checkBox = new JCheckBox(interest);
	        if (currentInterests.contains(interest)) { // Preselect if it matches current interests
	            checkBox.setSelected(true);
	        }
	        interestsPanel.add(checkBox);
	        checkBoxes.add(checkBox);
	    }

	    dialog.getContentPane().add(new JScrollPane(interestsPanel), BorderLayout.CENTER);

	    JButton btnOk = new JButton("OK");
	    btnOk.addActionListener(e -> {
	        dialog.dispose();
	    });

	    JPanel buttonPanel = new JPanel();
	    buttonPanel.add(btnOk);
	    dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	    dialog.setVisible(true);

	    // Collect selected interests after the dialog is closed
	    List<String> selectedInterests = new ArrayList<>();
	    for (JCheckBox checkBox : checkBoxes) {
	        if (checkBox.isSelected()) {
	            selectedInterests.add(checkBox.getText());
	        }
	    }

	    return selectedInterests;
	}

	private void openEditDetailsDialog(UserProfile user) {
		JPanel editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));

		JTextField nameField = new JTextField(user.getUsername());
		JTextField universityField = new JTextField(user.getUniversity());
		JTextField cityField = new JTextField(user.getProvince());
		JTextField interestsField = new JTextField(String.join(", ", user.getInterests()));
		JTextField dobField = new JTextField(user.getDateOfBirth());
		JTextField emailField = new JTextField(user.getEmail());

		editPanel.add(new JLabel("Name:"));
		editPanel.add(nameField);
		editPanel.add(new JLabel("University:"));
		editPanel.add(universityField);
		editPanel.add(new JLabel("City:"));
		editPanel.add(cityField);
		editPanel.add(new JLabel("Interests (comma-separated):"));
		editPanel.add(interestsField);
		editPanel.add(new JLabel("Date of Birth:"));
		editPanel.add(dobField);
		editPanel.add(new JLabel("Email:"));
		editPanel.add(emailField);

		int result = JOptionPane.showConfirmDialog(this, editPanel, "Edit My Profile", JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION) {
			// Save the updated details to the user's profile
			user.setUsername(nameField.getText());
			user.setUniversity(universityField.getText());
			user.setProvince(cityField.getText());
			user.setInterests(List.of(interestsField.getText().split(",\\s*")));
			user.setDateOfBirth(dobField.getText());
			user.setEmail(emailField.getText());

			// Update the database with the new details
			FirestoreHandler.updateUserData(user);

			// Show a success message
			JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}


	
	private JPanel createSidebar(JFrame parentFrame) {
	    JPanel sidebar = new JPanel();
	    sidebar.setBounds(10, 10, 70, 540);
	    sidebar.setBackground(Color.WHITE);
	    sidebar.setLayout(null);

	    // Profile Picture
	    profilePic = new JLabel();
	    String profilePicBase64 = currentUser.getProfilePicture();
	    if (profilePicBase64 != null && !profilePicBase64.isEmpty()) {
	        // Decode Base64 and set as profile picture
	        byte[] imageBytes = Base64.getDecoder().decode(profilePicBase64);
	        ImageIcon profileImageIcon = new ImageIcon(imageBytes);
	        Image scaledImage = profileImageIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
	        profilePic.setIcon(new ImageIcon(scaledImage));
	    } else {
	        // Use placeholder if no profile picture is available
	        profilePic.setIcon(new ImageIcon("src/main/resources/icons/profile.png"));
	    }

	    profilePic.setBounds(5, 25, 60, 60);
	    profilePic.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)); // Change cursor to hand for click indication
	    profilePic.setToolTipText("View Profile"); // Tooltip for accessibility

	    profilePic.addMouseListener(new java.awt.event.MouseAdapter() {
	        @Override
	        public void mouseClicked(java.awt.event.MouseEvent e) {
	            // Show the current user's profile in a pop-up
	            showProfile(currentUser, Homepage.this);
	        }
	    });
	    sidebar.add(profilePic);

	    // Sidebar Icons
	    addSidebarIcon(sidebar, "src/main/resources/icons/home.png", "Home", 100, e -> {
	        // Homepage homepage = new Homepage();
	        // homepage.setVisible(true);
	        // parentFrame.dispose();
	    });
	    addSidebarIcon(sidebar, "src/main/resources/icons/messages.png", "Chat", 170, e -> {
	        navigateToMessages(parentFrame);
	    });
	    addSidebarIcon(sidebar, "src/main/resources/icons/notifications.png", "Notifications", 240, e -> {
	        JOptionPane.showMessageDialog(parentFrame, "Notifications clicked!");
	    });
	    addSidebarIcon(sidebar, "src/main/resources/icons/community.png", "Community", 310, e -> {
	        JOptionPane.showMessageDialog(parentFrame, "Community clicked!");
	    });
	    addSidebarIcon(sidebar, "src/main/resources/icons/settings.png", "Settings", 380, e -> {
	        JOptionPane.showMessageDialog(parentFrame, "Settings clicked!");
	    });
	    addSidebarIcon(sidebar, "src/main/resources/icons/leave.png", "Logout", 450, e -> {
	        int confirm = JOptionPane.showConfirmDialog(parentFrame, "Are you sure you want to exit?",
	                "Exit Confirmation", JOptionPane.YES_NO_OPTION);
	        if (confirm == JOptionPane.YES_OPTION) {
	            System.exit(0);
	        }
	    });

	    return sidebar;
	}

	private void navigateToMessages(JFrame parentFrame) {
	    // Dispose of the current frame to clean up resources
	    parentFrame.dispose(); 

	    // Safely open the Messaging frame on the Event Dispatch Thread
	    EventQueue.invokeLater(() -> {
	        Messaging messaging = new Messaging();
	        messaging.setVisible(true);
	        messaging.setLocationRelativeTo(null); // Center the new window
	    });
	    dispose();
	}
	private void addSidebarIcon(JPanel sidebar, String iconPath, String tooltip, int yPosition,
			java.awt.event.ActionListener action) {
		ImageIcon originalIcon = new ImageIcon(iconPath);
		Image resizedImage = originalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
		ImageIcon resizedIcon = new ImageIcon(resizedImage);
		JButton iconButton = new JButton(resizedIcon);
		iconButton.setBounds(5, yPosition, 60, 60);
		iconButton.setBackground(Color.WHITE);
		iconButton.setBorder(BorderFactory.createEmptyBorder());
		iconButton.setFocusPainted(false);
		iconButton.setToolTipText(tooltip);
		iconButton.addActionListener(action);
		sidebar.add(iconButton);
	}
}
