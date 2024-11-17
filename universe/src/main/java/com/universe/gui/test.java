package com.universe.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.universe.FirebaseInitializer;
import com.universe.FirestoreHandler;
import com.universe.models.UserProfile;

public class test extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel contactsList; // For the contacts list
    private JPanel chatHistoryPanel; // For the chat history
    private String currentUserId = "currentUserIdPlaceholder"; // Placeholder for the logged-in user's ID
    private String currentChatContactId; // For the selected chat contact

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                FirebaseInitializer.initializeFirebase();
                Messaging frame = new Messaging();
                frame.setVisible(true);
                frame.setResizable(false);
                frame.setLocationRelativeTo(null); // Center the window
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public test() {
        setTitle("Messaging App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setLocationRelativeTo(null); // Center the frame

        // Main content pane
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // Sidebar Panel
        JPanel sidebar = createSidebar(this);
        sidebar.setBounds(10, 10, 70, 540);
        contentPane.add(sidebar);

        // Contacts List Panel
        JPanel contactsPanel = new JPanel();
        contactsPanel.setBounds(100, 10, 250, 540);
        contactsPanel.setBackground(Color.WHITE);
        contactsPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        contactsPanel.setLayout(new BorderLayout());

        JLabel contactsLabel = new JLabel("Contacts", SwingConstants.CENTER);
        contactsLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        contactsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        contactsPanel.add(contactsLabel, BorderLayout.NORTH);

        contactsList = new JPanel();
        contactsList.setLayout(new BoxLayout(contactsList, BoxLayout.Y_AXIS));
        JScrollPane contactsScrollPane = new JScrollPane(contactsList);
        contactsPanel.add(contactsScrollPane, BorderLayout.CENTER);

        JButton addContactButton = new JButton("Add Contact");
        addContactButton.setBackground(new Color(46, 157, 251));
        addContactButton.setForeground(Color.WHITE);
        addContactButton.setFont(new Font("Roboto", Font.BOLD, 14));
        addContactButton.addActionListener(e -> handleAddContact(JOptionPane.showInputDialog(this, "Enter contact name or email:")));
        contactsPanel.add(addContactButton, BorderLayout.SOUTH);

        contentPane.add(contactsPanel);

        // Chat Panel
        JPanel chatPanel = new JPanel();
        chatPanel.setBounds(370, 10, 500, 540);
        chatPanel.setBackground(Color.WHITE);
        chatPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        chatPanel.setLayout(new BorderLayout());

        chatHistoryPanel = new JPanel();
        chatHistoryPanel.setLayout(new BoxLayout(chatHistoryPanel, BoxLayout.Y_AXIS));
        JScrollPane chatScrollPane = new JScrollPane(chatHistoryPanel);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Message Input Panel
        JPanel messageInputPanel = new JPanel();
        messageInputPanel.setLayout(new BorderLayout());
        messageInputPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));

        JTextField messageField = new JTextField();
        messageInputPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.setBackground(new Color(46, 157, 251));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Roboto", Font.BOLD, 14));
        sendButton.addActionListener(e -> {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageField.setText("");
            }
        });
        messageInputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(messageInputPanel, BorderLayout.SOUTH);
        contentPane.add(chatPanel);

        populateContacts(); // Load contacts into the contacts panel
    }

    /**
     * Create the sidebar matching Homepage.
     */
    private JPanel createSidebar(JFrame parentFrame) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(null);
        sidebar.setBackground(Color.WHITE);

        JLabel profilePic = new JLabel(new ImageIcon("src/main/resources/icons/profile.png"));
        profilePic.setBounds(5, 10, 60, 60);
        sidebar.add(profilePic);

        addSidebarIcon(sidebar, "src/main/resources/icons/home.png", "Home", 100, e -> {
            Homepage homepage = new Homepage();
            homepage.setVisible(true);
            homepage.setLocationRelativeTo(null);
            parentFrame.dispose();
        });
        addSidebarIcon(sidebar, "src/main/resources/icons/messages.png", "Chat", 170, e -> {
            Messaging messagingPage = new Messaging();
            messagingPage.setVisible(true);
            messagingPage.setLocationRelativeTo(null);
            parentFrame.dispose();
        });
        addSidebarIcon(sidebar, "src/main/resources/icons/notifications.png", "Notifications", 240,
                e -> JOptionPane.showMessageDialog(this, "Notifications clicked!"));
        addSidebarIcon(sidebar, "src/main/resources/icons/community.png", "Community", 310,
                e -> JOptionPane.showMessageDialog(this, "Community clicked!"));
        addSidebarIcon(sidebar, "src/main/resources/icons/settings.png", "Settings", 380,
                e -> JOptionPane.showMessageDialog(this, "Settings clicked!"));
        addSidebarIcon(sidebar, "src/main/resources/icons/leave.png", "Logout", 450, e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        return sidebar;
    }

    private void addSidebarIcon(JPanel sidebar, String iconPath, String tooltip, int yPosition, java.awt.event.ActionListener action) {
        ImageIcon icon = new ImageIcon(iconPath);
        Image resizedImage = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(resizedImage));
        button.setBounds(5, yPosition, 60, 60);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setToolTipText(tooltip);
        button.addActionListener(action);
        sidebar.add(button);
    }

    private void populateContacts() {
        contactsList.removeAll();
        List<UserProfile> contacts = FirestoreHandler.getUserContacts(currentUserId);
        if (contacts != null && !contacts.isEmpty()) {
            for (UserProfile contact : contacts) {
                JPanel contactItem = new JPanel(new BorderLayout());
                contactItem.setPreferredSize(new Dimension(200, 50));
                contactItem.setBackground(Color.LIGHT_GRAY);
                contactItem.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

                JLabel nameLabel = new JLabel(contact.getUsername());
                nameLabel.setFont(new Font("Roboto", Font.BOLD, 14));
                contactItem.add(nameLabel, BorderLayout.CENTER);

                contactsList.add(contactItem);
            }
        } else {
            JLabel noContactsLabel = new JLabel("No Contacts Found", SwingConstants.CENTER);
            noContactsLabel.setFont(new Font("Roboto", Font.ITALIC, 14));
            contactsList.add(noContactsLabel);
        }
        contactsList.revalidate();
        contactsList.repaint();
    }

    private void sendMessage(String messageContent) {
        if (currentChatContactId != null && !currentChatContactId.isEmpty()) {
            FirestoreHandler.saveMessages(currentUserId, currentChatContactId, messageContent);
            addMessageToChat(messageContent, true);
        } else {
            JOptionPane.showMessageDialog(this, "Select a contact to send a message.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMessageToChat(String message, boolean isUserMessage) {
        JLabel messageLabel = new JLabel(message);
        messageLabel.setOpaque(true);
        messageLabel.setBackground(isUserMessage ? Color.CYAN : Color.LIGHT_GRAY);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel messagePanel = new JPanel(new FlowLayout(isUserMessage ? FlowLayout.RIGHT : FlowLayout.LEFT));
        messagePanel.add(messageLabel);

        chatHistoryPanel.add(messagePanel);
        chatHistoryPanel.revalidate();
        chatHistoryPanel.repaint();
    }

    private void handleAddContact(String searchQuery) {
        if (searchQuery == null || searchQuery.isEmpty()) return;

        UserProfile foundUser = FirestoreHandler.findUserByEmailOrUsername(searchQuery);
        if (foundUser != null) {
            if (foundUser.getUserId().equals(currentUserId)) {
                JOptionPane.showMessageDialog(this, "You cannot add yourself.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            FirestoreHandler.addContact(currentUserId, foundUser.getUserId(), foundUser.getUsername());
            JOptionPane.showMessageDialog(this, "Contact added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            populateContacts();
        } else {
            JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
