package com.universe.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.universe.FirebaseInitializer;
import com.universe.FirestoreHandler;
import com.universe.models.UserProfile;
import com.universe.utils.SessionManager;

public class Notifications extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                FirebaseInitializer.initializeFirebase();
                Notifications frame = new Notifications();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Notifications() {
        FirebaseInitializer.initializeFirebase();

        setTitle("Notifications");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        String userId = SessionManager.currentUserId;
        if (userId == null || userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No user is logged in. Please log in first.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Sidebar for navigation
        JPanel sidebar = createSidebar();
        contentPane.add(sidebar, BorderLayout.WEST);

        // Notifications content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BorderLayout());
        mainContent.setBackground(Color.WHITE);
        populateNotifications(mainContent, userId);
        contentPane.add(mainContent, BorderLayout.CENTER);

        // Listen for real-time notifications
        FirestoreHandler.listenToNotifications(userId, (snapshots, e) -> {
            if (e != null) {
                System.err.println("Error listening to notifications: " + e.getMessage());
                return;
            }
            populateNotifications(mainContent, userId);
        });
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(80, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(null);

        // Profile picture
        JLabel profilePic = new JLabel(new ImageIcon("src/main/resources/icons/profile.png"));
        profilePic.setBounds(10, 25, 60, 60);
        sidebar.add(profilePic);

        // Navigation buttons
        addSidebarIcon(sidebar, "src/main/resources/icons/home.png", "Home", 100, e -> navigateToHomepage());
        addSidebarIcon(sidebar, "src/main/resources/icons/messages.png", "Chat", 170, e -> navigateToMessaging());
        addSidebarIcon(sidebar, "src/main/resources/icons/notifications.png", "Notifications", 240, e -> {});
        addSidebarIcon(sidebar, "src/main/resources/icons/community.png", "Community", 310, e -> navigateToCommunity());
        addSidebarIcon(sidebar, "src/main/resources/icons/settings.png", "Settings", 380, e -> navigateToSettings());
        addSidebarIcon(sidebar, "src/main/resources/icons/leave.png", "Logout", 450, e -> handleLogout());

        return sidebar;
    }

    private void addSidebarIcon(JPanel sidebar, String iconPath, String tooltip, int yPosition, java.awt.event.ActionListener action) {
        ImageIcon originalIcon = new ImageIcon(iconPath);
        Image resizedImage = originalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        JButton iconButton = new JButton(resizedIcon);
        iconButton.setBounds(10, yPosition, 60, 60);
        iconButton.setBackground(Color.WHITE);
        iconButton.setBorder(BorderFactory.createEmptyBorder());
        iconButton.setFocusPainted(false);
        iconButton.setToolTipText(tooltip);
        iconButton.addActionListener(action);
        sidebar.add(iconButton);
    }

    private void navigateToHomepage() {
        EventQueue.invokeLater(() -> {
            Homepage homepage = new Homepage();
            homepage.setVisible(true);
            homepage.setLocationRelativeTo(null);
        });
        dispose();
    }

    private void navigateToMessaging() {
        EventQueue.invokeLater(() -> {
            Messaging messaging = new Messaging();
            messaging.setVisible(true);
            messaging.setLocationRelativeTo(null);
        });
        dispose();
    }

    private void navigateToCommunity() {
        JOptionPane.showMessageDialog(this, "Community page coming soon!");
    }

    private void navigateToSettings() {
        JOptionPane.showMessageDialog(this, "Settings page coming soon!");
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void populateNotifications(JPanel mainContent, String userId) {
        mainContent.removeAll();

        List<Map<String, Object>> notifications = FirestoreHandler.getUserNotifications(userId);
        JPanel notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));

        if (notifications.isEmpty()) {
            JLabel noNotificationsLabel = new JLabel("No notifications available.", JLabel.CENTER);
            notificationsPanel.add(noNotificationsLabel);
        } else {
            for (Map<String, Object> notification : notifications) {
                JPanel notificationEntry = new JPanel();
                notificationEntry.setLayout(null);
                notificationEntry.setPreferredSize(new Dimension(600, 80));
                notificationEntry.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));

                String content = (String) notification.get("content");
                JLabel contentLabel = new JLabel(content != null ? content : "No content");
                contentLabel.setBounds(10, 10, 400, 20);
                notificationEntry.add(contentLabel);

                String type = (String) notification.get("type");
                JButton actionButton = new JButton("View");
                actionButton.setBounds(450, 25, 100, 30);
                actionButton.addActionListener(e -> handleNotificationAction(type, notification));
                notificationEntry.add(actionButton);

                notificationsPanel.add(notificationEntry);
            }
        }

        JScrollPane scrollPane = new JScrollPane(notificationsPanel);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        mainContent.add(scrollPane, BorderLayout.CENTER);

        mainContent.revalidate();
        mainContent.repaint();
    }

    private void handleNotificationAction(String type, Map<String, Object> notification) {
        if (notification == null || type == null) {
            JOptionPane.showMessageDialog(this, "Invalid notification.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } 

        String notificationId = (String) notification.get("id"); // Get notification ID for deletion
        String userId = SessionManager.currentUserId;

        switch (type) {
        case "friend_request":
        	String friendRequestSenderId = (String) notification.get("senderId");
            if (friendRequestSenderId == null || friendRequestSenderId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Notification metadata is missing sender information.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fetch sender profile and display a friend request dialog
            UserProfile senderProfile = FirestoreHandler.getUserData(friendRequestSenderId);
            if (senderProfile != null) {
                showFriendRequestProfile(senderProfile, userId, notificationId);
            } else {
                JOptionPane.showMessageDialog(this, "Unable to fetch user information.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            break;
            case "new_message":
                Map<String, Object> metadata = (Map<String, Object>) notification.get("metadata");
                if (metadata != null) {
                    String chatId = (String) metadata.get("chatId");
                    String senderId = (String) metadata.get("senderId");
                    String senderName = (String) metadata.getOrDefault("senderName", "Unknown");

                    if (senderId == null || senderId.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Notification metadata is missing sender information.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Navigate to the Messaging page and open the correct chat
                    EventQueue.invokeLater(() -> {
                        Messaging messagingPage = new Messaging();
                        messagingPage.switchChat(senderId, senderName); // Pass senderId instead of chatId
                        messagingPage.setVisible(true);
                        messagingPage.setLocationRelativeTo(null);
                    });

                    // Delete notification from Firestore after opening the chat
                    FirestoreHandler.markNotificationAsRead(userId, notificationId);

                    // Dispose of the Notifications page
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Notification metadata is missing.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;


            case "scheduled_activity":
                JOptionPane.showMessageDialog(this, "Scheduled activity details displayed.");
                // Delete the notification after viewing
                FirestoreHandler.markNotificationAsRead(userId, notificationId);
                break;

            default:
                JOptionPane.showMessageDialog(this, "Unknown notification type: " + type, "Error", JOptionPane.ERROR_MESSAGE);
                break;
                
        }
        
    }
    private void showFriendRequestProfile(UserProfile senderProfile, String currentUserId, String notificationId) {
        JFrame profileFrame = new JFrame("Friend Request - " + senderProfile.getUsername());
        profileFrame.setSize(400, 300);
        profileFrame.setLayout(new BorderLayout());

        // Profile Info Panel
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        profilePanel.setBackground(Color.WHITE);

        JLabel usernameLabel = new JLabel("Username: " + senderProfile.getUsername());
        JLabel emailLabel = new JLabel("Email: " + senderProfile.getEmail());
        JLabel universityLabel = new JLabel("University: " + senderProfile.getUniversity());
        JLabel bioLabel = new JLabel("Bio: " + (senderProfile.getBio() != null ? senderProfile.getBio() : "N/A"));

        profilePanel.add(usernameLabel);
        profilePanel.add(emailLabel);
        profilePanel.add(universityLabel);
        profilePanel.add(bioLabel);

        // Add Friend Button
        JButton addFriendButton = new JButton("Add Friend");
        addFriendButton.addActionListener(e -> {
            // Add friend to Firestore and mark notification as read
            FirestoreHandler.addFriend(currentUserId, senderProfile.getUserId(), senderProfile.getUsername(), senderProfile.getUniversity());
            FirestoreHandler.markNotificationAsRead(currentUserId, notificationId);

            JOptionPane.showMessageDialog(profileFrame, "You are now friends with " + senderProfile.getUsername());
            profileFrame.dispose();
        });

        profileFrame.add(profilePanel, BorderLayout.CENTER);
        profileFrame.add(addFriendButton, BorderLayout.SOUTH);

        profileFrame.setLocationRelativeTo(null);
        profileFrame.setVisible(true);
    }

    

}