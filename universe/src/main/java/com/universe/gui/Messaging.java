package com.universe.gui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.ListenerRegistration;
import com.universe.FirebaseInitializer;
import com.universe.FirestoreHandler;
import com.universe.models.UserProfile;
import com.universe.utils.SessionManager;

public class Messaging extends JFrame {
    private static final long serialVersionUID = 1L;

    // Components
    private JPanel contentPane;
    private JPanel contactsList; // Contacts panel
    private JPanel chatHistoryPanel; // Chat history panel
    private JScrollPane chatScrollPane; // Scrollable chat history

    // Firestore real-time listener
    private ListenerRegistration chatListener;

    // User data
    private String currentUserId;
    private String currentChatContactId;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FirebaseInitializer.initializeFirebase();
                Messaging frame = new Messaging();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Messaging() {
        // Initialize user session
        currentUserId = SessionManager.currentUserId;

        if (currentUserId == null || currentUserId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No user logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Frame setup
        setTitle("Messaging App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);

        // Main content pane
        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        // Sidebar panel (left)
        JPanel sidebarPanel = createSidebar();
        contentPane.add(sidebarPanel, BorderLayout.WEST);

        // Contacts and Chat Panels
        JPanel contactsPanel = createContactsPanel();
        contentPane.add(contactsPanel, BorderLayout.CENTER);

        JPanel chatPanel = createChatPanel();
        contentPane.add(chatPanel, BorderLayout.EAST);

        // Add listener to cleanup Firestore listener on exit
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (chatListener != null) {
                    chatListener.remove();
                }
                System.exit(0);
            }
        });
    }

    private JPanel createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(80, 0));
        sidebarPanel.setBackground(new Color(240, 240, 240));

        // Profile Picture at the top
        JLabel profilePicture = new JLabel(new ImageIcon("path/to/profilePicture.png"));
        profilePicture.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePicture.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebarPanel.add(profilePicture);

        // Sidebar buttons
        sidebarPanel.add(createSidebarButton("/icons/icons8-home-50.png", "home"));
        sidebarPanel.add(createSidebarButton("/icons/icons8-chat-48.png", "chat"));
        sidebarPanel.add(createSidebarButton("/icons/icons8-notification-50.png", "notifications"));
        sidebarPanel.add(createSidebarButton("/icons/icons8-community-48.png", "community"));
        sidebarPanel.add(createSidebarButton("/icons/icons8-settings-50.png", "settings"));
        sidebarPanel.add(createSidebarButton("/icons/icons8-exit-48.png", "exit"));

        return sidebarPanel;
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
                JOptionPane.showMessageDialog(this, "Home clicked.");
                break;
            case "chat":
                JOptionPane.showMessageDialog(this, "Chat clicked.");
                break;
            case "notifications":
                JOptionPane.showMessageDialog(this, "Notifications clicked.");
                break;
            case "settings":
                JOptionPane.showMessageDialog(this, "Settings clicked.");
                break;
            case "exit":
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown action.");
        }
    }

    private JPanel createContactsPanel() {
        JPanel contactsPanel = new JPanel(new BorderLayout());
        contactsPanel.setBackground(Color.WHITE);

        // Contacts Header
        JLabel contactsLabel = new JLabel("Contacts", SwingConstants.CENTER);
        contactsLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        contactsPanel.add(contactsLabel, BorderLayout.NORTH);

        // Search and Add Contact
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        searchField.setToolTipText("Search contacts...");
        JButton addContactButton = new JButton("Add");
        addContactButton.addActionListener(e -> handleAddContact(searchField.getText().trim()));

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(addContactButton, BorderLayout.EAST);
        contactsPanel.add(searchPanel, BorderLayout.NORTH);

        // Contacts List
        contactsList = new JPanel();
        contactsList.setLayout(new BoxLayout(contactsList, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(contactsList);
        contactsPanel.add(scrollPane, BorderLayout.CENTER);

        // Populate Contacts
        populateContacts();

        return contactsPanel;
    }

    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(Color.WHITE);

        // Chat History Panel
        chatHistoryPanel = new JPanel();
        chatHistoryPanel.setLayout(new BoxLayout(chatHistoryPanel, BoxLayout.Y_AXIS));
        chatScrollPane = new JScrollPane(chatHistoryPanel);
        chatScrollPane.setPreferredSize(new Dimension(400, 0));
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Message Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageField.setText("");
            }
        });

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        return chatPanel;
    }

    private void populateContacts() {
        contactsList.removeAll(); // Clear existing contacts before loading new ones

        List<UserProfile> contacts = FirestoreHandler.getUserContacts(currentUserId);
        if (contacts != null) {
            for (UserProfile contact : contacts) {
                String contactId = contact.getUserId();
                String contactName = contact.getUsername();

                // Create a panel for each contact
                JPanel contactPanel = new JPanel(new BorderLayout());
                contactPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230))); // Add a divider
                contactPanel.setPreferredSize(new Dimension(200, 60)); // Adjust height for a messaging app feel
                contactPanel.setBackground(Color.WHITE);

                // Profile Picture/Icon
                JLabel profilePicture = new JLabel(new ImageIcon("path/to/defaultProfileIcon.png")); // Placeholder image
                profilePicture.setPreferredSize(new Dimension(50, 50)); // Adjust icon size
                contactPanel.add(profilePicture, BorderLayout.WEST);

                // Name and Status
                JPanel nameStatusPanel = new JPanel();
                nameStatusPanel.setLayout(new BoxLayout(nameStatusPanel, BoxLayout.Y_AXIS));
                nameStatusPanel.setBackground(Color.WHITE);

                JLabel nameLabel = new JLabel(contactName);
                nameLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Bold for the name
                nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel statusLabel = new JLabel("Last seen recently"); // Placeholder for status/last message
                statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                statusLabel.setForeground(Color.GRAY);
                statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                nameStatusPanel.add(nameLabel);
                nameStatusPanel.add(statusLabel);
                contactPanel.add(nameStatusPanel, BorderLayout.CENTER);

                // Add click listener to open a chat with the selected contact
                contactPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        switchChat(contactId, contactName);
                    }
                });

                contactsList.add(contactPanel);
            }
        } else {
            JLabel noContactsLabel = new JLabel("No contacts found.");
            noContactsLabel.setForeground(Color.GRAY);
            contactsList.add(noContactsLabel);
        }

        contactsList.revalidate();
        contactsList.repaint();
    }


    private void handleAddContact(String searchQuery) {
        UserProfile user = FirestoreHandler.findUserByEmailOrUsername(searchQuery);
        if (user != null) {
            if (!user.getUserId().equals(currentUserId)) {
                FirestoreHandler.addContact(currentUserId, user.getUserId(), user.getUsername());
                JOptionPane.showMessageDialog(this, "Contact added.");
                populateContacts();
            } else {
                JOptionPane.showMessageDialog(this, "Cannot add yourself.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "User not found.");
        }
    }

    private void switchChat(String contactId, String contactName) {
        currentChatContactId = contactId;

        // Clear the chat panel for the new chat
        chatHistoryPanel.removeAll();

        // Remove the previous listener if one exists
        if (chatListener != null) {
            chatListener.remove();
        }

        // Set up a new real-time listener for the selected chat
        String chatId = currentUserId + "_" + contactId; // Chat ID format
        chatListener = FirestoreHandler.addChatListener(chatId, (snapshots, e) -> {
            if (e != null) {
                System.err.println("Error listening for chat updates: " + e.getMessage());
                return;
            }

            // Clear the chat history panel before adding updated messages
            chatHistoryPanel.removeAll();

            // Populate the chat panel with updated messages
            if (snapshots != null && !snapshots.isEmpty()) {
                for (DocumentSnapshot document : snapshots.getDocuments()) {
                    String content = document.getString("content");
                    String senderId = document.getString("senderId");
                    boolean isUserMessage = senderId.equals(currentUserId);

                    addMessageToChat(content, isUserMessage);
                }
            }

            // Refresh the chat panel UI
            chatHistoryPanel.revalidate();
            chatHistoryPanel.repaint();
        });

        // Update the UI title with the contact name
        setTitle("Chatting with " + contactName);
    }

    private void sendMessage(String messageContent) {
        if (currentChatContactId != null && !currentChatContactId.isEmpty()) {
            // Save the message to Firestore
            FirestoreHandler.saveMessages(currentUserId, currentChatContactId, messageContent);

            // The listener will automatically update the chat UI
        } else {
            JOptionPane.showMessageDialog(this, "No contact selected.");
        }
    }

    private void addMessageToChat(String message, boolean isUserMessage) {
        // Create a panel to hold the message bubble
        JPanel messagePanel = new JPanel(new FlowLayout(isUserMessage ? FlowLayout.RIGHT : FlowLayout.LEFT));
        messagePanel.setBackground(Color.WHITE); // Match chat background

        // Create the message bubble
        JLabel messageLabel = new JLabel("<html><div style='padding: 8px; max-width: 200px;'>" + message + "</div></html>");
        messageLabel.setOpaque(true);
        messageLabel.setBackground(isUserMessage ? new Color(54, 125, 225) : new Color(240, 240, 240)); // Different colors for sender and receiver
        messageLabel.setForeground(isUserMessage ? Color.WHITE : Color.BLACK);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding inside the bubble

        // Set size constraints for the bubble
        messageLabel.setPreferredSize(new Dimension(200, messageLabel.getPreferredSize().height));
        messageLabel.setMaximumSize(new Dimension(200, messageLabel.getPreferredSize().height));

        // Add the bubble to the message panel
        messagePanel.add(messageLabel);

        // Add some vertical spacing between messages
        chatHistoryPanel.add(messagePanel);
        chatHistoryPanel.add(Box.createVerticalStrut(5)); // Space between messages

        // Refresh the chat panel
        chatHistoryPanel.revalidate();
        chatHistoryPanel.repaint();
    }

}
