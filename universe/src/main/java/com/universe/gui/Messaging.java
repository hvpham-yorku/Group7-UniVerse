package com.universe.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.universe.FirebaseInitializer;
import com.universe.FirestoreHandler;
import com.universe.models.UserProfile;

public class Messaging extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel contactsList; // Declare at the class level to avoid scope issues
    private JPanel chatHistoryPanel; // Declare at the class level to avoid scope issues
    private String currentUserId = "currentUserIdPlaceholder"; // Placeholder. In practice, set this to the logged-in user's ID.

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
    	EventQueue.invokeLater(() -> {
            try {
                // Initialize Firebase first
                FirebaseInitializer.initializeFirebase();

                // Now create and show the Messaging GUI
                Messaging frame = new Messaging();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });;
    }

    /**
     * Create the frame.
     */
    public Messaging() {
        setTitle("Messaging App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);

        // Main content pane
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(10, 10));
        setContentPane(contentPane);

        // Sidebar Panel
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(240, 240, 240));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(80, 0));
        contentPane.add(sidebarPanel, BorderLayout.WEST);

        // Profile Picture at Top of Sidebar
        JLabel profilePicture = new JLabel(new ImageIcon("path/to/profilePicture.png"));
        profilePicture.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePicture.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebarPanel.add(profilePicture);

        // Sidebar buttons with icons
        sidebarPanel.add(createSidebarButton("/icons/icons8-home-50.png", "home"));
        sidebarPanel.add(createSidebarButton("/icons/icons8-chat-48.png", "chat"));
        sidebarPanel.add(createSidebarButton("/icons/icons8-notification-50.png", "notifications"));
        sidebarPanel.add(createSidebarButton("/icons/icons8-community-48.png", "community"));
        sidebarPanel.add(createSidebarButton("/icons/icons8-settings-50.png", "settings"));
        sidebarPanel.add(createSidebarButton("/icons/icons8-exit-48.png", "exit"));

        // Contacts List Panel
        JPanel contactsPanel = new JPanel();
        contactsPanel.setLayout(new BorderLayout());
        contactsPanel.setPreferredSize(new Dimension(150, 0));
        contactsPanel.setBackground(Color.WHITE);
        contentPane.add(contactsPanel, BorderLayout.CENTER);

        // Contacts Label
        JLabel contactsLabel = new JLabel("Contacts");
        contactsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contactsLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        contactsPanel.add(contactsLabel, BorderLayout.NORTH);

        // Contacts List (Scrollable)
        contactsList = new JPanel(); // Initialize the contact list at class level
        contactsList.setLayout(new BoxLayout(contactsList, BoxLayout.Y_AXIS));
        JScrollPane contactsScrollPane = new JScrollPane(contactsList);
        contactsScrollPane.setPreferredSize(new Dimension(200, 100));
        contactsPanel.add(contactsScrollPane, BorderLayout.CENTER);

        // Search Bar for Contacts
        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setToolTipText("Search Contacts...");
        contactsPanel.add(searchField, BorderLayout.NORTH);

        JButton addContactButton = new JButton("Add Contact");
        addContactButton.setPreferredSize(new Dimension(150, 30));
        contactsPanel.add(addContactButton, BorderLayout.SOUTH);
        addContactButton.addActionListener(e -> {
            String searchQuery = searchField.getText().trim();
            if (!searchQuery.isEmpty()) {
                handleAddContact(searchQuery);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a username or email to search.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Load existing contacts from Firestore
        populateContacts();

        // Chat Panel
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(Color.WHITE);
        contentPane.add(chatPanel, BorderLayout.EAST);

        // Chat History Panel
        chatHistoryPanel = new JPanel(); // Initialize the chat history at class level
        chatHistoryPanel.setLayout(new BoxLayout(chatHistoryPanel, BoxLayout.Y_AXIS));
        chatHistoryPanel.setBackground(Color.WHITE);
        JScrollPane chatScrollPane = new JScrollPane(chatHistoryPanel);
        chatScrollPane.setPreferredSize(new Dimension(400, 0));
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Message Input Panel
        JPanel messageInputPanel = new JPanel();
        messageInputPanel.setLayout(new BorderLayout());
        messageInputPanel.setBackground(Color.WHITE);
        messageInputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JTextField messageField = new JTextField();
        messageField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1, true));
        JButton sendButton = new JButton("Send");
        sendButton.setBackground(new Color(59, 89, 152));
        sendButton.setForeground(Color.BLACK);

        messageInputPanel.add(messageField, BorderLayout.CENTER);
        messageInputPanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(messageInputPanel, BorderLayout.SOUTH);

        // Send button action (adds message to chat history)
        sendButton.addActionListener(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                addMessageToChat(chatHistoryPanel, message, true);
                messageField.setText("");
                chatScrollPane.getVerticalScrollBar().setValue(chatScrollPane.getVerticalScrollBar().getMaximum());
            }
        });
    }

    private JButton createSidebarButton(String iconPath, String actionCommand) {
        JButton button = new JButton(new ImageIcon(getClass().getResource(iconPath)));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setFocusPainted(false);
        button.setActionCommand(actionCommand);
        button.addActionListener(e -> handleSidebarAction(e.getActionCommand()));
        return button;
    }

    private void handleSidebarAction(String actionCommand) {
        switch (actionCommand) {
            case "home":
                System.out.println("Home button clicked");
                break;
            case "chat":
                System.out.println("Chat button clicked");
                break;
            case "notifications":
                System.out.println("Notifications button clicked");
                break;
            case "community":
                System.out.println("Community button clicked");
                break;
            case "settings":
                System.out.println("Settings button clicked");
                break;
            case "exit":
                System.out.println("Exit button clicked");
                System.exit(0);
                break;
            default:
                System.out.println("Unknown action");
        }
    }

    private void populateContacts() {
        contactsList.removeAll(); // Clear existing contacts before loading new ones

        List<UserProfile> contacts = FirestoreHandler.getUserContacts(currentUserId);
        for (UserProfile contact : contacts) {
            String contactId = contact.getUserId();
            String contactName = contact.getUsername();

            // Create contact item panel
            JPanel contactItem = new JPanel();
            contactItem.setLayout(new BorderLayout());
            contactItem.setBackground(Color.WHITE);
            contactItem.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

            // Profile image
            JLabel contactImage = new JLabel(new ImageIcon("path/to/contactImage.png"));
            contactImage.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            contactItem.add(contactImage, BorderLayout.WEST);

            // Contact name and status
            JPanel contactDetails = new JPanel();
            contactDetails.setLayout(new BoxLayout(contactDetails, BoxLayout.Y_AXIS));
            contactDetails.setBackground(Color.WHITE);
            JLabel contactNameLabel = new JLabel(contactName);
            JLabel contactStatus = new JLabel("Online");
            contactStatus.setForeground(Color.BLUE);
            contactDetails.add(contactNameLabel);
            contactDetails.add(contactStatus);
            contactItem.add(contactDetails, BorderLayout.CENTER);

            // Add click listener to open a chat with the selected contact
            contactItem.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    switchChat(contactId, contactName);
                }
            });

            contactsList.add(contactItem);
        }

        contactsList.revalidate();
        contactsList.repaint();
    }

    private void handleAddContact(String searchQuery) {
        UserProfile foundUser = FirestoreHandler.findUserByEmailOrUsername(searchQuery);
        if (foundUser != null) {
            // Check if the user is trying to add themselves
            if (foundUser.getUserId().equals(currentUserId)) {
                JOptionPane.showMessageDialog(this, "You cannot add yourself as a contact.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            FirestoreHandler.addContact(currentUserId, foundUser.getUserId(), foundUser.getUsername());
            JOptionPane.showMessageDialog(this, "Contact added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            populateContacts(); // Refresh contact list after adding
        } else {
            JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void switchChat(String contactId, String contactName) {
        // Clear current chat panel
        chatHistoryPanel.removeAll();
        chatHistoryPanel.revalidate();
        chatHistoryPanel.repaint();

        // Load chat history from Firestore for this contact
        List<Map<String, String>> messages = FirestoreHandler.getChatHistory(currentUserId, contactId);
        for (Map<String, String> message : messages) {
            String messageContent = message.get("content");
            boolean isUserMessage = message.get("senderId").equals(currentUserId);
            addMessageToChat(chatHistoryPanel, messageContent, isUserMessage);
        }

        // Update chat panel title or something similar to indicate the active chat
        setTitle("Chatting with " + contactName);
    }

    private void addMessageToChat(JPanel chatHistoryPanel, String message, boolean isUserMessage) {
        JLabel messageLabel = new JLabel("<html><body style='width: 200px'>" + message + "</body></html>");
        messageLabel.setOpaque(true);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        messageLabel.setBackground(isUserMessage ? new Color(173, 216, 230) : new Color(245, 245, 245));
        messageLabel.setForeground(isUserMessage ? Color.BLACK : Color.DARK_GRAY);
        messageLabel.setHorizontalAlignment(isUserMessage ? SwingConstants.RIGHT : SwingConstants.LEFT);

        JPanel bubble = new JPanel();
        bubble.setLayout(new BorderLayout());
        bubble.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        bubble.add(messageLabel, BorderLayout.LINE_START);
        bubble.setBackground(Color.WHITE);

        chatHistoryPanel.add(bubble);
        chatHistoryPanel.revalidate();
        chatHistoryPanel.repaint();
    }
}
