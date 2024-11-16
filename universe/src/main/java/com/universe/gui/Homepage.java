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
import javax.swing.ImageIcon;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.universe.FirebaseInitializer;
import com.universe.FirestoreHandler;
import com.universe.models.UserProfile;

public class Homepage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel friendsListPanel; // Left panel for searching users
    private JPanel friendsPanelRight; // Panel on the right for added friends
    private JPanel rightFriendsListPanel; // Scrollable panel for added friends
    private JTextField searchField;

    private List<UserProfile> allUsers; // Store all users fetched from the database
    private List<UserProfile> addedFriends; // Store added friends

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setResizable(false); // Prevent resizing
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        contentPane.setBackground(new Color(210, 236, 255));
        setContentPane(contentPane);

        // Initialize added friends list
        addedFriends = new ArrayList<>();

        // Welcome message panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBounds(100, 10, 770, 60);
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        welcomePanel.setLayout(null);
        JLabel welcomeLabel = new JLabel("Hi John Doe, Welcome to UniVerse!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Inria Sans", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(31, 162, 255));
        welcomeLabel.setBounds(0, 5, 770, 50);
        welcomePanel.add(welcomeLabel);
        contentPane.add(welcomePanel);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBounds(10, 10, 70, 540);
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(null);

        // Profile label above the profile picture
        JLabel profileLabel = new JLabel("Profile");
        profileLabel.setFont(new Font("Roboto", Font.BOLD, 14));
        profileLabel.setForeground(new Color(97, 97, 97)); // Gray color for subtle look
        profileLabel.setBounds(10, 5, 50, 20); // Positioned above the profile picture
        sidebar.add(profileLabel);

        // Profile picture
        JLabel profilePic = new JLabel(new ImageIcon("src/main/resources/icons/profile.png"));
        profilePic.setBounds(5, 25, 60, 60);
        sidebar.add(profilePic);

        // Sidebar icons with functionality
        addSidebarIcon(sidebar, "src/main/resources/icons/home.png", "Home", 100, e -> JOptionPane.showMessageDialog(null, "Home clicked!"));
        addSidebarIcon(sidebar, "src/main/resources/icons/messages.png", "Chat", 170, e -> JOptionPane.showMessageDialog(null, "Chat clicked!"));
        addSidebarIcon(sidebar, "src/main/resources/icons/notifications.png", "Notifications", 240, e -> JOptionPane.showMessageDialog(null, "Notifications clicked!"));
        addSidebarIcon(sidebar, "src/main/resources/icons/community.png", "Community", 310, e -> JOptionPane.showMessageDialog(null, "Community clicked!"));
        addSidebarIcon(sidebar, "src/main/resources/icons/settings.png", "Settings", 380, e -> JOptionPane.showMessageDialog(null, "Settings clicked!"));
        addSidebarIcon(sidebar, "src/main/resources/icons/leave.png", "Logout", 450, e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0); // Exit application
            }
        });

        contentPane.add(sidebar);

        // Friends List Panel
        JPanel friendsPanel = new JPanel();
        friendsPanel.setBounds(100, 80, 270, 480);
        friendsPanel.setBackground(Color.WHITE);
        friendsPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        friendsPanel.setLayout(null);

        JLabel searchLabel = new JLabel("Add or search for friends!");
        searchLabel.setBounds(15, 10, 200, 20);
        searchLabel.setFont(new Font("Roboto", Font.BOLD, 14));
        friendsPanel.add(searchLabel);

        // Search bar
        searchField = new JTextField();
        searchField.setBounds(15, 35, 180, 30);
        searchField.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        friendsPanel.add(searchField);

        // Search button
        JButton searchButton = new JButton("Go");
        searchButton.setBounds(200, 35, 60, 30);
        searchButton.setFont(new Font("Roboto", Font.BOLD, 12));
        searchButton.setBackground(new Color(46, 157, 251));
        searchButton.setForeground(Color.BLACK);
        searchButton.setFocusPainted(false);
        friendsPanel.add(searchButton);

        // Friends list panel with a scrollable view
        friendsListPanel = new JPanel();
        friendsListPanel.setLayout(new BoxLayout(friendsListPanel, BoxLayout.Y_AXIS));
        friendsListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(friendsListPanel);
        scrollPane.setBounds(15, 75, 240, 390);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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

        // Right Panel for Logo and Friends
        friendsPanelRight = new JPanel();
        friendsPanelRight.setBounds(380, 80, 490, 480);
        friendsPanelRight.setBackground(Color.WHITE);
        friendsPanelRight.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        friendsPanelRight.setLayout(null);

        // Logo Section
        JLabel logoLabel = new JLabel(new ImageIcon(new ImageIcon("src/main/resources/icons/logo5.png").getImage().getScaledInstance(200, 100, Image.SCALE_SMOOTH)));
        logoLabel.setBounds(145, 10, 200, 100);
        friendsPanelRight.add(logoLabel);

        // Friends Section
        JLabel friendsTitleLabel = new JLabel("Friends", JLabel.CENTER);
        friendsTitleLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        friendsTitleLabel.setBounds(0, 120, 490, 30);
        friendsTitleLabel.setOpaque(true);
        friendsTitleLabel.setBackground(new Color(46, 157, 251));
        friendsTitleLabel.setForeground(Color.WHITE);
        friendsPanelRight.add(friendsTitleLabel);

        // Scrollable Friends List
        rightFriendsListPanel = new JPanel();
        rightFriendsListPanel.setLayout(new BoxLayout(rightFriendsListPanel, BoxLayout.Y_AXIS)); // BoxLayout for dynamic height
        rightFriendsListPanel.setBackground(Color.WHITE);

        JScrollPane rightScrollPane = new JScrollPane(rightFriendsListPanel);
        rightScrollPane.setBounds(10, 160, 470, 310);
        rightScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        friendsPanelRight.add(rightScrollPane);

        // "No friends added yet" label
        noFriendsLabel = new JLabel("No friends added yet!", JLabel.CENTER);
        noFriendsLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        rightFriendsListPanel.add(noFriendsLabel);

        contentPane.add(friendsPanelRight);
        
     // Real-time listener for added friends
        FirestoreHandler.getFriends((snapshots, e) -> {
            if (e != null) {
                System.err.println("Error listening to real-time updates: " + e.getMessage());
                return;
            }

            addedFriends.clear(); // Clear the current list
            for (QueryDocumentSnapshot doc : snapshots.getDocuments()) {
                UserProfile friend = doc.toObject(UserProfile.class);
                addedFriends.add(friend); // Update the addedFriends list
            }
            updateRightFriendsList(); // Refresh the right panel
        });
        
    }

    private void addSidebarIcon(JPanel sidebar, String iconPath, String tooltip, int yPosition, java.awt.event.ActionListener action) {
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

    private void populateFriendsList(List<UserProfile> users) {
        friendsListPanel.removeAll();

        if (users != null && !users.isEmpty()) {
            for (UserProfile user : users) {
                addFriendEntry(user);
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

    private void addFriendEntry(UserProfile user) {
        JPanel friendEntry = new JPanel();
        friendEntry.setPreferredSize(new Dimension(230, 60));
        friendEntry.setBackground(new Color(230, 230, 230));
        friendEntry.setLayout(null);

        JLabel friendPic = new JLabel(new ImageIcon("src/main/resources/icons/sandra.png")); // Profile picture
        friendPic.setBounds(5, 5, 50, 50);
        friendEntry.add(friendPic);

        JLabel friendName = new JLabel(user.getUsername());
        friendName.setFont(new Font("Roboto", Font.BOLD, 14));
        friendName.setBounds(70, 5, 120, 20);
        friendEntry.add(friendName);

        JLabel friendUniversity = new JLabel(user.getUniversity());
        friendUniversity.setFont(new Font("Roboto", Font.PLAIN, 12));
        friendUniversity.setBounds(70, 30, 120, 20);
        friendEntry.add(friendUniversity);

        JButton addButton = new JButton(addedFriends.contains(user) ? "Remove" : "Add");
        addButton.setBounds(180, 10, 60, 30);
        addButton.setFont(new Font("Roboto", Font.BOLD, 10));
        addButton.setBackground(new Color(46, 157, 251));
        addButton.setForeground(Color.BLACK);
        addButton.addActionListener(e -> handleAddOrRemoveFriend(user, addButton));
        friendEntry.add(addButton);

        friendsListPanel.add(friendEntry);
    }

    private void handleAddOrRemoveFriend(UserProfile user, JButton button) {
        if (addedFriends.contains(user)) {
            // Remove friend
            addedFriends.remove(user);
            FirestoreHandler.removeFriend(user); // Add to Firestore
            //updateRightFriendsList();
            button.setText("Add");
        } else {
            // Add friend
            addedFriends.add(user);
            FirestoreHandler.addFriend(user); // Add to Firestore
           // updateRightFriendsList();
            button.setText("Remove");
        }
    }




    private void updateRightFriendsList() {
        rightFriendsListPanel.removeAll();

        if (addedFriends.isEmpty()) {
            rightFriendsListPanel.add(noFriendsLabel);
        } else {
            for (UserProfile friend : addedFriends) {
                addToRightFriendsPanel(friend);
            }
        }

        rightFriendsListPanel.revalidate();
        rightFriendsListPanel.repaint();
    }

    private void addToRightFriendsPanel(UserProfile user) {
        JPanel friendEntry = new JPanel();
        friendEntry.setPreferredSize(new Dimension(470, 60));
        friendEntry.setBackground(new Color(230, 230, 230));
        friendEntry.setLayout(null);

        JLabel friendName = new JLabel(user.getUsername());
        friendName.setFont(new Font("Roboto", Font.BOLD, 14));
        friendName.setBounds(10, 5, 300, 20);
        friendEntry.add(friendName);

        JLabel friendUniversity = new JLabel(user.getUniversity());
        friendUniversity.setFont(new Font("Roboto", Font.PLAIN, 12));
        friendUniversity.setBounds(10, 30, 300, 20);
        friendEntry.add(friendUniversity);

        JButton messageButton = new JButton("Message");
        messageButton.setBounds(370, 15, 90, 30);
        messageButton.setFont(new Font("Roboto", Font.BOLD, 10));
        messageButton.setBackground(new Color(46, 157, 251));
        messageButton.setForeground(Color.BLACK);
        messageButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Messaging " + user.getUsername() + "!"));
        friendEntry.add(messageButton);

        rightFriendsListPanel.add(friendEntry);
    }

    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            populateFriendsList(allUsers);
            return;
        }

        List<UserProfile> filteredUsers = new ArrayList<>();
        for (UserProfile user : allUsers) {
            if (user.getUsername().toLowerCase().contains(query) || user.getUniversity().toLowerCase().contains(query)) {
                filteredUsers.add(user);
            }
        }

        populateFriendsList(filteredUsers);
    }
    
    
}
