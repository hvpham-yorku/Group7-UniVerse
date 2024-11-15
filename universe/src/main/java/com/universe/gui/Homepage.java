package com.universe.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;

public class Homepage extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Homepage frame = new Homepage();
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
    public Homepage() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setResizable(false); // Prevents resizing of the window
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        contentPane.setBackground(new Color(210, 236, 255));
        setContentPane(contentPane);

        // Welcome message panel aligned with logoPanel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBounds(100, 10, 770, 60); // Adjusted width to align with right edge of logoPanel
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        welcomePanel.setLayout(null);
        JLabel welcomeLabel = new JLabel("Hi John Doe, Welcome to UniVerse!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Inria Sans", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(31, 162, 255));
        welcomeLabel.setBounds(0, 5, 770, 50); // Adjusted width to match welcomePanel
        welcomePanel.add(welcomeLabel);
        contentPane.add(welcomePanel);

        // Sidebar panel
        JPanel sidebar = new JPanel();
        sidebar.setBounds(10, 10, 70, 540);
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(null);

        // Profile label above the profile picture
        JLabel profileLabel = new JLabel("Profile");
        profileLabel.setFont(new Font("Roboto", Font.BOLD, 14));
        profileLabel.setForeground(new Color(97, 97, 97)); // Gray color to match a subtle look
        profileLabel.setBounds(10, 5, 50, 20); // Positioned above the profile picture
        sidebar.add(profileLabel);

        // Profile picture
        JLabel profilePic = new JLabel(new ImageIcon("/icons/profile.png")); // Profile icon
        profilePic.setBounds(5, 25, 60, 60);
        sidebar.add(profilePic);

        // Sidebar icons with adjusted positions and even spacing
        int iconStartY = 100;
        int iconSpacing = 70;

        JLabel homeIcon = new JLabel(new ImageIcon("/Users/kosy/Downloads/home.png")); // Home icon
        homeIcon.setBounds(5, iconStartY, 60, 60);
        sidebar.add(homeIcon);

        JLabel chatIcon = new JLabel(new ImageIcon("/Users/kosy/Downloads/messages.png")); // Messages icon
        chatIcon.setBounds(5, iconStartY + iconSpacing, 60, 60);
        sidebar.add(chatIcon);

        JLabel bellIcon = new JLabel(new ImageIcon("/Users/kosy/Downloads/notifications.png")); // Notifications icon
        bellIcon.setBounds(5, iconStartY + 2 * iconSpacing, 60, 60);
        sidebar.add(bellIcon);

        JLabel friendsIcon = new JLabel(new ImageIcon("/Users/kosy/Downloads/community.png")); // Community icon
        friendsIcon.setBounds(5, iconStartY + 3 * iconSpacing, 60, 60);
        sidebar.add(friendsIcon);

        JLabel settingsIcon = new JLabel(new ImageIcon("/Users/kosy/Downloads/settings.png")); // Settings icon
        settingsIcon.setBounds(5, iconStartY + 4 * iconSpacing, 60, 60);
        sidebar.add(settingsIcon);

        JLabel logoutIcon = new JLabel(new ImageIcon("/Users/kosy/Downloads/leave.png")); // Leave icon
        logoutIcon.setBounds(5, iconStartY + 5 * iconSpacing, 60, 60);
        sidebar.add(logoutIcon);

        contentPane.add(sidebar);

        // Friends list panel
        JPanel friendsPanel = new JPanel();
        friendsPanel.setBounds(100, 80, 270, 480);
        friendsPanel.setBackground(Color.WHITE);
        friendsPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        friendsPanel.setLayout(null);

        JLabel searchLabel = new JLabel("Add or search for friends!");
        searchLabel.setBounds(15, 10, 220, 20);
        searchLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        friendsPanel.add(searchLabel);

        // Search field with blue border
        JTextField searchField = new JTextField();
        searchField.setBounds(15, 35, 240, 30);
        searchField.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        friendsPanel.add(searchField);

        // Friend entries with adjusted yPosition for spacing
        addFriendEntry(friendsPanel, "Sandra King", "York University", "/Users/kosy/Downloads/sandra.png", 80);
        addFriendEntry(friendsPanel, "Jane Freeman Leonardino", "University of Toronto", "/Users/kosy/Downloads/jane.png", 160);
        addFriendEntry(friendsPanel, "Armin Singh", "Toronto Metropolitan University", "/Users/kosy/Downloads/armin.png", 240);
        addFriendEntry(friendsPanel, "James Anderson", "University of Waterloo", "/Users/kosy/Downloads/james.png", 320);
        addFriendEntry(friendsPanel, "Asterios Vassilis", "Seneca College", "/Users/kosy/Downloads/asterios.png", 400);

        contentPane.add(friendsPanel);

        // Logo panel with resized logo
        JPanel logoPanel = new JPanel();
        logoPanel.setBounds(380, 80, 490, 480);
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        logoPanel.setLayout(null);

        // Load and resize the logo image
        ImageIcon logoIcon = new ImageIcon("/Users/kosy/Downloads/logo.png");
        Image scaledLogo = logoIcon.getImage().getScaledInstance(490, 480, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setBounds(0, 0, 490, 480);
        logoPanel.add(logoLabel);

        contentPane.add(logoPanel);
    }

    private void addFriendEntry(JPanel parentPanel, String name, String university, String imagePath, int yPosition) {
        JPanel friendEntry = new JPanel();
        friendEntry.setBounds(15, yPosition, 240, 60);
        friendEntry.setBackground(new Color(230, 230, 230));
        friendEntry.setLayout(null);

        JLabel friendPic = new JLabel(new ImageIcon(imagePath)); // Friend's profile picture
        friendPic.setBounds(5, 5, 50, 50);
        friendEntry.add(friendPic);

        JLabel friendName = new JLabel(name);
        friendName.setFont(new Font("Roboto", Font.BOLD, 14));
        friendName.setBounds(70, 10, 120, 20);
        friendEntry.add(friendName);

        JLabel friendUniversity = new JLabel(university);
        friendUniversity.setFont(new Font("Roboto", Font.PLAIN, 12));
        friendUniversity.setBounds(70, 30, 120, 20);
        friendEntry.add(friendUniversity);

        // Plain clickable button with white background and blue border
        JButton addButton = new JButton();
        addButton.setBounds(210, 15, 20, 20); // Set the size of the button
        addButton.setBackground(Color.WHITE); // White background inside the button
        addButton.setContentAreaFilled(true); // Ensures the content area is filled with white
        addButton.setOpaque(true); // Ensures background color is visible
        addButton.setFocusPainted(false); // Removes focus outline on click
        addButton.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251))); // Blue border

        // Interactive effect: change border color on hover
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                addButton.setBorder(BorderFactory.createLineBorder(new Color(31, 162, 255), 2)); // Darker blue border on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                addButton.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251))); // Original border color
            }
        });

        // ActionListener to handle button clicks
        addButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Friend request sent to " + name);
        });

        friendEntry.add(addButton);
        parentPanel.add(friendEntry);
    }
}
