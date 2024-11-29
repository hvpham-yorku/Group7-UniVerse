package com.universe.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.universe.models.UserProfile;
import com.universe.utils.SessionManager;
import com.universe.FirestoreHandler;

public class Interestspage extends JFrame {

    private JPanel contentPane;
    private JPanel interestsListPanel;
    private JTextField searchField;
    private List<String> userInterests;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Interestspage frame = new Interestspage();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public Interestspage() {
        // Fetch the logged-in user's data
        String currentUserId = SessionManager.currentUserId;
        if (currentUserId == null || currentUserId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No user logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        UserProfile currentUser = FirestoreHandler.getUserData(currentUserId);
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "Error fetching user data.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        userInterests = currentUser.getInterests();
        if (userInterests == null) {
            userInterests = new ArrayList<>();
        }

        setTitle("Interests Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setResizable(false);

        // Content Pane
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        contentPane.setBackground(new Color(210, 236, 255));
        setContentPane(contentPane);

        // Sidebar
        JPanel sidebar = createSidebar(this);
        contentPane.add(sidebar);

        // Header Section: "Find your Community on UniVerse"
        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(100, 10, 770, 100);
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        headerPanel.setLayout(null);
        contentPane.add(headerPanel);

        JLabel headerLabel = new JLabel("Find your Community on UniVerse", JLabel.CENTER);
        headerLabel.setBounds(10, 10, 750, 80);
        headerLabel.setFont(new Font("Inria Sans", Font.BOLD, 28));
        headerLabel.setForeground(new Color(31, 162, 255));
        headerPanel.add(headerLabel);

        // Subtitle: "Based on your interests"
        JLabel subtitleLabel = new JLabel("Based on your interests");
        subtitleLabel.setBounds(100, 120, 770, 30);
        subtitleLabel.setFont(new Font("Roboto", Font.BOLD, 20));
        subtitleLabel.setForeground(new Color(46, 46, 46));
        contentPane.add(subtitleLabel);

        // Search bar
        JPanel searchPanel = new JPanel();
        searchPanel.setBounds(100, 160, 770, 50);
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        searchPanel.setLayout(null);
        contentPane.add(searchPanel);

        searchField = new JTextField("Search interests...");
        searchField.setBounds(15, 10, 740, 30);
        searchField.setFont(new Font("Roboto", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createEmptyBorder());
        searchPanel.add(searchField);

  
        
        // Add search functionality
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

      
        
        // Interests List Panel
        interestsListPanel = new JPanel();
        interestsListPanel.setBackground(Color.WHITE);
        interestsListPanel.setLayout(new GridLayout(0, 2, 20, 20)); // Grid layout for 2 columns

        JScrollPane scrollPane = new JScrollPane(interestsListPanel);
        scrollPane.setBounds(100, 220, 770, 320);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(scrollPane);

        populateInterestsList(userInterests);
    }

    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            populateInterestsList(userInterests);
        } else {
            List<String> filteredInterests = new ArrayList<>();
            for (String interest : userInterests) {
                if (interest.toLowerCase().contains(query)) {
                    filteredInterests.add(interest);
                }
            }
            populateInterestsList(filteredInterests);
        }
    } 
    
    private void populateInterestsList(List<String> interests) {
        interestsListPanel.removeAll();

        if (interests == null || interests.isEmpty()) {
            JLabel noInterestsLabel = new JLabel("<html><div style='text-align: center;'>You haven't selected any interests yet!<br>"
                    + "Update your profile to add interests and discover your community<br>"
                    + "or search for communities to join.</div></html>", JLabel.CENTER);
            noInterestsLabel.setFont(new Font("Roboto", Font.BOLD, 16));
            noInterestsLabel.setForeground(Color.GRAY);
            interestsListPanel.add(noInterestsLabel);
        } else {
            for (String interest : interests) {
                if (interest != null && !interest.trim().isEmpty() && !"none selected".equalsIgnoreCase(interest)) {
                    addInterestCard(interest);
                }
            }
        }

        interestsListPanel.revalidate();
        interestsListPanel.repaint();
    }

    private void addInterestCard(String interest) {
        JPanel interestCard = new JPanel();
        interestCard.setPreferredSize(new Dimension(350, 200));
        interestCard.setBackground(Color.WHITE);
        interestCard.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        interestCard.setLayout(new BorderLayout(10, 10));

        JLabel interestLabel = new JLabel(interest, JLabel.LEFT);
        interestLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        interestLabel.setBorder(new EmptyBorder(10, 10, 0, 10));
        interestCard.add(interestLabel, BorderLayout.NORTH);

        JLabel descriptionLabel = new JLabel("<html>A community for enthusiasts of " + interest.toLowerCase() + ".</html>");
        descriptionLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        descriptionLabel.setBorder(new EmptyBorder(0, 10, 10, 10));
        interestCard.add(descriptionLabel, BorderLayout.CENTER);

        JButton joinButton = new JButton("Join");
        joinButton.setFont(new Font("Roboto", Font.BOLD, 14));
        joinButton.setBackground(new Color(46, 157, 251));
        joinButton.setForeground(Color.BLACK);
        joinButton.addActionListener(e -> showJoinConfirmation(interest));
        interestCard.add(joinButton, BorderLayout.SOUTH);

        interestsListPanel.add(interestCard);
    }

    private void showJoinConfirmation(String interest) {
        int response = JOptionPane.showOptionDialog(this,
                "You're about to join the " + interest + " Community.\nDo you want to continue?",
                "Confirm Join",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Join Now", "Cancel"},
                "Cancel");

        if (response == JOptionPane.YES_OPTION) {
            openInterestGroupPage(interest);
        }
    }

    private void openInterestGroupPage(String interest) {
        JDialog groupDialog = new JDialog(this, "UniVerse " + interest + " Group", true);
        groupDialog.setSize(900, 600);
        groupDialog.setLayout(null);

        JLabel headerLabel = new JLabel("Welcome to UniVerse " + interest + " Group!", JLabel.CENTER);
        headerLabel.setBounds(20, 10, 850, 50);
        headerLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        headerLabel.setForeground(new Color(31, 162, 255));
        groupDialog.add(headerLabel);

        JPanel membersPanel = new JPanel(new BorderLayout());
        membersPanel.setBounds(20, 70, 300, 470);
        membersPanel.setBackground(Color.WHITE);

        JLabel membersTitle = new JLabel("Members of the Community", JLabel.CENTER);
        membersTitle.setFont(new Font("Roboto", Font.BOLD, 16));
        membersPanel.add(membersTitle, BorderLayout.NORTH);

        JList<String> membersList = new JList<>(getGroupMembers(interest));
        JScrollPane membersScrollPane = new JScrollPane(membersList);
        membersPanel.add(membersScrollPane, BorderLayout.CENTER);
        groupDialog.add(membersPanel);

        JPanel rightPanel = new JPanel(null);
        rightPanel.setBounds(340, 70, 530, 470);
        rightPanel.setBackground(Color.WHITE);

        JLabel descriptionLabel = new JLabel("<html>Welcome to the " + interest + " community. Connect, create, and share your passion!</html>");
        descriptionLabel.setBounds(20, 10, 490, 200);
        descriptionLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        rightPanel.add(descriptionLabel);

        JButton discordButton = new JButton("Join our Discord Channel now!");
        discordButton.setBounds(150, 250, 230, 40);
        discordButton.setFont(new Font("Roboto", Font.BOLD, 14));
        discordButton.setBackground(new Color(46, 157, 251));
        discordButton.setForeground(Color.BLACK);
        rightPanel.add(discordButton);

        groupDialog.add(rightPanel);
        groupDialog.setLocationRelativeTo(this);
        groupDialog.setVisible(true);
    }

    private String[] getGroupMembers(String interest) {
        return new String[]{"Sandra King", "Jane Doe", "Armin Singh", "James Anderson", "Asterios Vassilis"};
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
            Homepage homepage = new Homepage();
            homepage.setVisible(true);
            parentFrame.dispose();
        });

        addSidebarIcon(sidebar, "src/main/resources/icons/messages.png", "Messages", 170, e -> {
            Messaging messaging = new Messaging();
            messaging.setVisible(true);
            parentFrame.dispose();
        });

        addSidebarIcon(sidebar, "src/main/resources/icons/notifications.png", "Notifications", 240, e -> {
            JOptionPane.showMessageDialog(parentFrame, "Notifications clicked!");
        });

        addSidebarIcon(sidebar, "src/main/resources/icons/community.png", "Groups", 310, e -> {
            Interestspage interestspage = new Interestspage();
            interestspage.setVisible(true);
            parentFrame.dispose();
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

    private void addSidebarIcon(JPanel sidebar, String iconPath, String tooltip, int yPosition, java.awt.event.ActionListener action) {
        ImageIcon originalIcon = new ImageIcon(iconPath);
        Image resizedImage = originalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JButton iconButton = new JButton(new ImageIcon(resizedImage));
        iconButton.setBounds(5, yPosition, 60, 60);
        iconButton.setBackground(Color.WHITE);
        iconButton.setBorder(BorderFactory.createEmptyBorder());
        iconButton.setFocusPainted(false);
        iconButton.setToolTipText(tooltip);
        iconButton.addActionListener(action);
        sidebar.add(iconButton);
    }
}
