package com.universe.gui;

import javax.swing.JFrame;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.ListenerRegistration;
import com.universe.FirebaseInitializer;
import com.universe.FirestoreHandler;
import com.universe.models.UserProfile;
import com.universe.utils.SessionManager;
import java.awt.Label;
import java.util.List;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;
import javax.swing.JDesktopPane;
import javax.swing.JMenuBar;
import javax.swing.JScrollBar;

public class Notifications extends JFrame{

	//extra variables for now till test
	private UserProfile currentUser; // Logged-in user's profile
	private JPanel sidebar_1;
	private JLabel profileLabel;
	private JLabel profilePic;
	private JButton iconButton;
	private JPanel notificationsListPanel;

	public Notifications() {
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

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		contentPane.setBackground(new Color(210, 236, 255));
		setContentPane(contentPane);
		setBounds(100, 100, 900, 600);
	
		JPanel welcomePanel = new JPanel();
		welcomePanel.setLayout(null);
		welcomePanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
		welcomePanel.setBackground(Color.WHITE);
		welcomePanel.setBounds(135, 6, 579, 60);
		contentPane.add(welcomePanel);
		
		JLabel lblsNotificationCenter = new JLabel( currentUser.getUsername()+"'s Notification Center!", SwingConstants.CENTER);
		lblsNotificationCenter.setForeground(new Color(31, 162, 255));
		lblsNotificationCenter.setFont(new Font("Dialog", Font.BOLD, 20));
		lblsNotificationCenter.setBounds(-84, 6, 770, 50);
		welcomePanel.add(lblsNotificationCenter);
		
		// Sidebar
		JPanel sidebar = createSidebar(this);
		contentPane.add(sidebar);
		
		
		JPanel notificationsPanel = new JPanel();
		notificationsPanel.setBackground(Color.WHITE);
		notificationsPanel.setBounds(135, 78, 579, 474);
		contentPane.add(notificationsPanel);

		// Notifications List (Scrollable)
		notificationsListPanel = new JPanel();
		notificationsListPanel.setLayout(new GridBagLayout()); // Use GridBagLayout
		notificationsListPanel.setBackground(Color.WHITE);

		// Add notificationsListPanel to a JScrollPane
		JScrollPane scrollPane = new JScrollPane(notificationsListPanel);
		scrollPane.setBounds(10, 10, 559, 454); // Adjust bounds to fit your design
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		notificationsPanel.add(scrollPane);

        // Load notifications dynamically
        loadNotifications();
	
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
	private void loadNotifications() {
	    notificationsListPanel.removeAll(); // Clear any existing content

	    // GridBagConstraints for dynamic row addition
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.gridx = 0; // First column
	    gbc.gridy = 0; // Start at the first row
	    gbc.weightx = 1.0; // Stretch horizontally
	    gbc.fill = GridBagConstraints.HORIZONTAL; // Make components fill horizontally
	    gbc.anchor = GridBagConstraints.NORTHWEST; // Align to the top-left

	    // Fetch pending contacts (logic in FirestoreHandler)
	    List<UserProfile> pendingContacts = FirestoreHandler.getPendingContacts(SessionManager.currentUserId);

	    if (pendingContacts == null || pendingContacts.isEmpty()) {
	        JLabel emptyLabel = new JLabel("No new notifications.", SwingConstants.CENTER);
	        emptyLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
	        notificationsListPanel.add(emptyLabel, gbc);
	    } else {
	        for (UserProfile contact : pendingContacts) {
	            JPanel contactPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	            contactPanel.setBackground(new Color(240, 240, 240));
	            contactPanel.setPreferredSize(new Dimension(540, 50)); // Fixed width, flexible height

	            JLabel contactLabel = new JLabel(contact.getUsername());
	            contactLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
	            contactPanel.add(contactLabel);

	            JButton addButton = new JButton("Add");
	            addButton.addActionListener(e -> {
	                FirestoreHandler.addContact(
	                    SessionManager.currentUserId,
	                    contact.getUserId(),
	                    contact.getUsername()
	                );
	                JOptionPane.showMessageDialog(this, "Contact added: " + contact.getUsername());
	                loadNotifications(); // Refresh list
	            });
	            contactPanel.add(addButton);

	            notificationsListPanel.add(contactPanel, gbc); // Add contact panel to GridBagLayout
	            gbc.gridy++; // Move to the next row
	        }
	    }

	    notificationsListPanel.revalidate();
	    notificationsListPanel.repaint();
	}
}
