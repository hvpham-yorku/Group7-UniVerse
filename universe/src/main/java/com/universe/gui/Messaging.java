package com.universe.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Messaging extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Messaging frame = new Messaging();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
        contentPane.setLayout(new BorderLayout(10, 10));  // BorderLayout for main structure
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
        sidebarPanel.add(createSidebarButton("/Users/sarimahchindah/Downloads/icons8-home-50.png"));
        sidebarPanel.add(createSidebarButton("/Users/sarimahchindah/Downloads/icons8-chat-48.png"));
        sidebarPanel.add(createSidebarButton("/Users/sarimahchindah/Downloads/icons8-notification-50.png"));
        sidebarPanel.add(createSidebarButton("/Users/sarimahchindah/Downloads/icons8-community-48.png"));
        sidebarPanel.add(createSidebarButton("/Users/sarimahchindah/Downloads/icons8-settings-50.png"));
        sidebarPanel.add(createSidebarButton("/Users/sarimahchindah/Downloads/icons8-exit-48.png"));

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
        JPanel contactsList = new JPanel();
        contactsList.setLayout(new BoxLayout(contactsList, BoxLayout.Y_AXIS));
        JScrollPane contactsScrollPane = new JScrollPane(contactsList);
        contactsScrollPane.setPreferredSize(new Dimension(200, 100)); // Reduced height
        contactsPanel.add(contactsScrollPane, BorderLayout.CENTER);

        
     // Search Bar for Contacts
        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setToolTipText("Search Contacts...");
        contactsPanel.add(searchField, BorderLayout.NORTH);

        // Adding styled dummy contacts
        for (int i = 0; i < 19; i++) {
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
            JLabel contactName = new JLabel("Contact " + (i + 1));
            JLabel contactStatus = new JLabel("Online");
            contactStatus.setForeground(Color.BLUE);
            contactDetails.add(contactName);
            contactDetails.add(contactStatus);
            contactItem.add(contactDetails, BorderLayout.CENTER);

            contactsList.add(contactItem);
        }

        // Chat Panel
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(Color.WHITE);
        contentPane.add(chatPanel, BorderLayout.EAST);

        // Chat History Panel
        JPanel chatHistoryPanel = new JPanel();
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
                JLabel messageLabel = new JLabel(message);
                messageLabel.setOpaque(true);
                messageLabel.setBackground(new Color(173, 216, 230));
                messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                chatHistoryPanel.add(messageLabel);
                chatHistoryPanel.revalidate();
                chatHistoryPanel.repaint();
                messageField.setText("");
            }
        });
    }

    /**
     * Utility method to create sidebar buttons with icons.
     */
    private JButton createSidebarButton(String iconPath) {
        JButton button = new JButton(new ImageIcon(iconPath));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setFocusPainted(false);
        return button;
    }

    /**
     * Utility method to add a message bubble to the chat history.
     */
    private void addMessageToChat(JPanel chatHistoryPanel, String message, boolean isUserMessage) {
        JLabel messageLabel = new JLabel("<html><body style='width: 200px'>" + message + "</body></html>");
        messageLabel.setOpaque(true);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));  // Reduced padding
        messageLabel.setBackground(isUserMessage ? new Color(173, 216, 230) : new Color(245, 245, 245));
        messageLabel.setForeground(isUserMessage ? Color.BLACK : Color.DARK_GRAY);
        messageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        messageLabel.setAlignmentX(isUserMessage ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        // Add message bubble to panel
        JPanel bubble = new JPanel();
        bubble.setLayout(new BorderLayout());
        bubble.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));  // Adjusted spacing between bubbles
        bubble.add(messageLabel, BorderLayout.LINE_START);
        bubble.setBackground(Color.WHITE);

        chatHistoryPanel.add(bubble);
    }
}