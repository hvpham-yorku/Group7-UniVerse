package com.universe.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.universe.FirebaseInitializer;
import com.universe.FirestoreHandler;
import com.universe.models.UserProfile;
import com.universe.utils.SessionManager;

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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				// Initialize Firebase
				FirebaseInitializer.initializeFirebase();

				// Launch the Homepage
				Homepage frame = new Homepage();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Homepage() {
		// Fetch the current user's details
		String currentUserId = SessionManager.currentUserId;
		System.out.println(currentUserId);
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
		JLabel welcomeLabel = new JLabel("Hi " + currentUser.getUsername() + ", Welcome to UniVerse!", JLabel.CENTER);
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
		JButton searchButton = new JButton("Search");
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

	private JPanel createSidebar(JFrame parentFrame) {
		JPanel sidebar = new JPanel();
		sidebar.setBounds(10, 10, 70, 540);
		sidebar.setBackground(Color.WHITE);
		sidebar.setLayout(null);

		JLabel profilePic = new JLabel(new ImageIcon("src/main/resources/icons/profile.png"));
		profilePic.setBounds(5, 25, 60, 60);
		sidebar.add(profilePic);

		addSidebarIcon(sidebar, "src/main/resources/icons/home.png", "Home", 100, e -> {
			// Homepage homepage = new Homepage();
			// homepage.setVisible(true);
			// parentFrame.dispose();
		});
		addSidebarIcon(sidebar, "src/main/resources/icons/messages.png", "Chat", 170, e -> {
			Messaging messaging = new Messaging();
			messaging.setVisible(true);
			messaging.setLocationRelativeTo(null);
			parentFrame.dispose();
		});
		addSidebarIcon(sidebar, "src/main/resources/icons/notifications.png", "Notifications", 240, e -> {
			JOptionPane.showMessageDialog(parentFrame, "Notifications clicked!");
		});
		addSidebarIcon(sidebar, "src/main/resources/icons/community.png", "Community", 310, e -> navigatetoInterestPage(parentFrame));
		

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
		private void navigatetoInterestPage(JFrame parentFrame) {
			Interestspage interest = new Interestspage();
			interest.setVisible(true);
			interest.setLocationRelativeTo(null);
			parentFrame.dispose();
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
