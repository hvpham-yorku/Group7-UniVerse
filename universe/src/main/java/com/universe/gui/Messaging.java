package com.universe.gui;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONObject;

import com.ConfigLoader;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.ListenerRegistration;
import com.universe.FirebaseInitializer;
import com.universe.FirestoreHandler;
import com.universe.models.UserProfile;
import com.universe.utils.Constants;
import com.universe.utils.SessionManager;
import com.universe.utils.Sidebar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

public class Messaging extends JFrame {
	private static final long serialVersionUID = 1L;

	// Components
	private JPanel contentPane;
	private JPanel contactsList; // Contacts panel
	private JPanel chatHistoryPanel; // Chat history panel
	private JScrollPane chatScrollPane; // Scrollable chat history
	private List<UserProfile> friendsList; // List of friends for the current user
	private UserProfile currentUser; // Logged-in user's profile


	// Firestore real-time listener
	private ListenerRegistration chatListener;

	// User data
	private String currentUserId;
	private String currentChatContactId;

	private JLabel profilePic;
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
		friendsList = FirestoreHandler.getUserContacts(currentUserId); // Fetch friends from Firestore

		 // Add ChatGPT Bot at the top of the list
	    UserProfile UniVerseBot = new UserProfile("chat_bot", "UniVerse Bot", "chatbot@universe.com");
	    friendsList.add(0, UniVerseBot); // Pinned at the top
		// Frame setup
		setTitle("Messaging App");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);

		// Main content pane
		contentPane = new JPanel(new BorderLayout(10, 10));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		// Sidebar panel (left)
		JPanel sidebarPanel = Sidebar.createSidebar(currentUser,this);
		sidebarPanel.setPreferredSize(new Dimension(80, 0)); // Set fixed width for the sidebar
		contentPane.add(sidebarPanel, BorderLayout.WEST);
		
		//contentPane.add(sidebarPanel);
		//add(sidebarPanel, BorderLayout.WEST);
		

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

	
	

	

	
	private void navigateToHomepage(JFrame parentFrame) {
		EventQueue.invokeLater(() -> {
			Homepage homepage = new Homepage();
			homepage.setVisible(true);
			homepage.setLocationRelativeTo(null); // Center the new window
		});
		dispose(); // Close current page
	}
	
	
	private void handleLogout() {
		int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit Confirmation",
				JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			System.exit(0); // Exit the app
		}
	}

	private JPanel createContactsPanel() {
		JPanel contactsPanel = new JPanel(new BorderLayout());
		contactsPanel.setBackground(Color.WHITE);

		// Contacts Header
		JLabel contactsLabel = new JLabel("Contacts", SwingConstants.CENTER);
		contactsLabel.setBorder(BorderFactory.createEmptyBorder(7, 0, 7, 0));
		
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
		contactsList.removeAll();

		if (friendsList != null && !friendsList.isEmpty()) {
			for (UserProfile friend : friendsList) {
				JPanel contactPanel = createContactPanel(friend);
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

	private JPanel createContactPanel(UserProfile contact) {
		JPanel contactPanel = new JPanel(new BorderLayout());
		contactPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
		contactPanel.setBackground(Color.WHITE);

		JLabel nameLabel = new JLabel(contact.getUsername());
		nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
		contactPanel.add(nameLabel, BorderLayout.CENTER);

		contactPanel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				switchChat(contact.getUserId(), contact.getUsername());
			}
		});

		return contactPanel;
	}

//	private void sendMessage(String messageContent) {
//		if (currentChatContactId != null && !currentChatContactId.isEmpty()) {
//			FirestoreHandler.saveMessages(currentUserId, currentChatContactId, messageContent);
//		} else {
//			JOptionPane.showMessageDialog(this, "No contact selected.");
//		}
//	}
	private void sendMessage(String messageContent) {
	    System.out.println("sendMessage called. Message: " + messageContent); // DEBUG LOG

	    if (currentChatContactId != null && !currentChatContactId.isEmpty()) {
	        System.out.println("Chat Contact ID: " + currentChatContactId); // DEBUG LOG

	        if (currentChatContactId.equals("chat_bot")) {
	            System.out.println("Sending message to ChatGPT bot..."); // DEBUG LOG
	            displayMessage(messageContent, true);

	            new Thread(() -> {
	                System.out.println("Calling getGPTResponse..."); // DEBUG LOG
	                String botResponse = getGPTResponse(messageContent);
	                System.out.println("Bot Response: " + botResponse); // DEBUG LOG
	                SwingUtilities.invokeLater(() -> displayMessage(botResponse, false));
	            }).start();
	        } else {
	            FirestoreHandler.saveMessages(currentUserId, currentChatContactId, messageContent);
	        }
	    } else {
	        System.out.println("No contact selected."); // DEBUG LOG
	        JOptionPane.showMessageDialog(this, "No contact selected.");
	    }
	}

	private String getGPTResponse(String userMessage) {
		String apiKey = ConfigLoader.getApiKey();
		
		String apiUrl = "https://api.openai.com/v1/chat/completions";
	    

	    // Prepare the JSON payload
	    String jsonPayload = "{"
	        + "\"model\": \"gpt-3.5-turbo\","
	        + "\"messages\": [{\"role\": \"user\", \"content\": \"" + userMessage + "\"}],"
	        + "\"max_tokens\": 150"
	        + "}";

	    System.out.println("Payload Sent: " + jsonPayload);

	    // Use OkHttp to send the HTTP POST request
	    OkHttpClient client = new OkHttpClient();
	    RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json"));
	    Request request = new Request.Builder()
	        .url(apiUrl)
	        .post(body)
	        .addHeader("Authorization", "Bearer " + apiKey)
	        .addHeader("Content-Type", "application/json")
	        .build();

	    try (Response response = client.newCall(request).execute()) {
	        if (!response.isSuccessful()) {
	            return "Error: Unable to fetch response from ChatGPT.";
	        }

	        // Parse the response JSON
	        String responseBody = response.body().string();
	        JSONObject jsonObject = new JSONObject(responseBody);
	        return jsonObject.getJSONArray("choices")
	                         .getJSONObject(0)
	                         .getJSONObject("message")
	                         .getString("content")
	                         .trim();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Sorry, I couldn't process your request right now.";
	    }
	}
	private void handleAddContact(String searchQuery) {
		if (searchQuery.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Please enter a search query.", "Invalid Search",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Check if the search query matches any friend in the friends list
		UserProfile matchedFriend = friendsList.stream()
				.filter(friend -> friend.getUsername().equalsIgnoreCase(searchQuery)
						|| friend.getEmail().equalsIgnoreCase(searchQuery))
				.findFirst().orElse(null);

		if (matchedFriend != null) {
			JOptionPane.showMessageDialog(this, "This user is already in your contacts.", "Already Added",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "You can only search for users who are your friends.", "Access Denied",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public void switchChat(String contactId, String contactName) {
		currentChatContactId = contactId;

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

			// Clear the chat panel to avoid duplicates
			chatHistoryPanel.removeAll();

			// Populate the chat panel with updated messages
			if (snapshots != null && !snapshots.isEmpty()) {
				for (DocumentSnapshot document : snapshots.getDocuments()) {
					String content = document.getString("content");
					String senderId = document.getString("senderId");
					boolean isUserMessage = senderId.equals(currentUserId);

					displayMessage(content, isUserMessage);
				}
			}

			// Refresh the chat panel UI
			chatHistoryPanel.revalidate();
			chatHistoryPanel.repaint();
		});

		// Clear the chat panel for the new chat
		chatHistoryPanel.removeAll();

		// Update the UI title with the contact name
		setTitle("Chatting with " + contactName);
	}
	private void displayMessage(String message, boolean isUserMessage) {
	    JPanel messagePanel = new JPanel(new FlowLayout(isUserMessage ? FlowLayout.RIGHT : FlowLayout.LEFT));
	    messagePanel.setBackground(Color.WHITE); // Match chat background

	    // Create the message bubble
	    JTextArea messageLabel = new JTextArea(message);
	    messageLabel.setLineWrap(true);
	    messageLabel.setWrapStyleWord(true);
	    messageLabel.setOpaque(true);
	    messageLabel.setEditable(false);
	    messageLabel.setBackground(isUserMessage ? new Color(54, 125, 225) : new Color(240, 240, 240)); // Sent/Received colors
	    messageLabel.setForeground(isUserMessage ? Color.WHITE : Color.BLACK);
	    messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
	    messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding inside the bubble

	    // Dynamically calculate height and width
	    int bubbleWidth = 300; // Maximum width of the bubble
	    messageLabel.setSize(bubbleWidth, Short.MAX_VALUE); // Set a max width to calculate height
	    int bubbleHeight = messageLabel.getPreferredSize().height; // Calculate the dynamic height
	    messageLabel.setPreferredSize(new Dimension(bubbleWidth, bubbleHeight)); // Set preferred size dynamically

	    // Add the bubble to the message panel
	    messagePanel.add(messageLabel);

	    // Add some vertical spacing between messages
	    chatHistoryPanel.add(messagePanel);
	    chatHistoryPanel.add(Box.createVerticalStrut(10)); // Space between messages

	    // Refresh the chat panel
	    chatHistoryPanel.revalidate();
	    chatHistoryPanel.repaint();

	    // Scroll to the latest message
	    SwingUtilities.invokeLater(() -> chatScrollPane.getVerticalScrollBar()
	            .setValue(chatScrollPane.getVerticalScrollBar().getMaximum()));

	}
}