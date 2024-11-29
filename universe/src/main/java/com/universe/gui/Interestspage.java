package com.universe.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.universe.models.UserProfile;
import com.universe.utils.SessionManager;
import com.universe.FirestoreHandler;

public class Interestspage extends JFrame {

    private JPanel contentPane;
    private JPanel interestsListPanel;
    private JPanel userGroupsListPanel;
    private JTextField searchField;
    private List<String> userInterests;
    private List<String> userGroups; // Groups user has joined

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

    public Interestspage() {
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
        if (userInterests == null) userInterests = new ArrayList<>();
        userGroups = FirestoreHandler.getUserGroups(currentUserId);
        if (userGroups == null) userGroups = new ArrayList<>();

        setTitle("Join a Community Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setResizable(false);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        contentPane.setBackground(new Color(210, 236, 255));
        setContentPane(contentPane);
        setLocationRelativeTo(null);

        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(100, 10, 770, 60);
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        headerPanel.setLayout(null);
        JLabel headerLabel = new JLabel("Find your Community on UniVerse!", JLabel.CENTER);
        headerLabel.setFont(new Font("Inria Sans", Font.BOLD, 20));
        headerLabel.setForeground(new Color(31, 162, 255));
        headerLabel.setBounds(0, 5, 770, 50);
        headerPanel.add(headerLabel);
        contentPane.add(headerPanel);

        JPanel sidebar = createSidebar(this);
        contentPane.add(sidebar);

        createLeftPanel(); // Groups user has joined
        createRightPanel(); // Groups user might be interested in

        populateUserGroups(userGroups);
        populateInterestsList(userInterests);
    }

    private void createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setBounds(100, 80, 430, 480); // Left panel width remains at 420px
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        leftPanel.setLayout(null);

//        JLabel leftLabel = new JLabel("Your Groups:");
//        leftLabel.setBounds(15, 10, 390, 20); // Adjusted width for the label
//        leftLabel.setFont(new Font("Roboto", Font.BOLD, 12));
//        leftPanel.add(leftLabel);
        JLabel leftLabel = new JLabel("Your Groups:", JLabel.CENTER);
        leftLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        leftLabel.setBounds(15, 10, 390, 20); // Adjusted width for the label
        leftLabel.setOpaque(true);
        leftLabel.setBackground(new Color(46, 157, 251));
        leftLabel.setForeground(Color.WHITE);
        leftPanel.add(leftLabel);
        

        userGroupsListPanel = new JPanel();
        userGroupsListPanel.setLayout(new BoxLayout(userGroupsListPanel, BoxLayout.Y_AXIS));
        userGroupsListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(userGroupsListPanel);
        scrollPane.setBounds(15, 40, 390, 420); // Adjusted width for the scrollable list
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftPanel.add(scrollPane);

        contentPane.add(leftPanel);
    }

    private void createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setBounds(540, 80, 330, 480); // Right panel width set to 330px and shifted to the right
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        rightPanel.setLayout(null);

        JLabel rightLabel = new JLabel("Based on your interests");
        rightLabel.setBounds(10, 10, 310, 20); // Adjusted width for the label
        rightLabel.setFont(new Font("Roboto", Font.BOLD, 12));
        rightPanel.add(rightLabel);

        searchField = new JTextField("Search interests...");
        searchField.setBounds(10, 35, 310, 30); // Adjusted width for the search bar
        searchField.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        searchField.setFont(new Font("Roboto", Font.PLAIN, 12));
        rightPanel.add(searchField);

        interestsListPanel = new JPanel();
        interestsListPanel.setLayout(new BoxLayout(interestsListPanel, BoxLayout.Y_AXIS));
        interestsListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(interestsListPanel);
        scrollPane.setBounds(10, 75, 310, 390); // Adjusted width for the scrollable list
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(scrollPane);

        contentPane.add(rightPanel);

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
    }








    private void populateInterestsList(List<String> interests) {
        interestsListPanel.removeAll();
        if (interests.isEmpty()) {
            JLabel noInterestsLabel = new JLabel("No interests found!", JLabel.CENTER);
            noInterestsLabel.setFont(new Font("Roboto", Font.BOLD, 14));
            noInterestsLabel.setForeground(Color.GRAY);
            interestsListPanel.add(noInterestsLabel);
        } else {
            for (String interest : interests) {
                addInterestCard(interest);
            }
        }
        interestsListPanel.revalidate();
        interestsListPanel.repaint();
    }

    private void populateUserGroups(List<String> groups) {
        userGroupsListPanel.removeAll();
        if (groups.isEmpty()) {
            JLabel noGroupsLabel = new JLabel("No groups added yet!", JLabel.CENTER);
            noGroupsLabel.setFont(new Font("Roboto", Font.BOLD, 14));
            noGroupsLabel.setForeground(Color.GRAY);
            userGroupsListPanel.add(noGroupsLabel);
        } else {
            for (String group : groups) {
                addUserGroupCard(group);
            }
        }
        userGroupsListPanel.revalidate();
        userGroupsListPanel.repaint();
    }

    private void addInterestCard(String interest) {
        JPanel interestCard = new JPanel();
        interestCard.setPreferredSize(new Dimension(350, 60));
        interestCard.setBackground(new Color(230, 230, 230));
        interestCard.setLayout(null);

        JLabel interestLabel = new JLabel(interest);
        interestLabel.setFont(new Font("Roboto", Font.BOLD, 13));
        interestLabel.setBounds(10, 5, 250, 20);
        interestCard.add(interestLabel);

        JButton joinButton = new JButton("Join");
        joinButton.setBounds(240, 5, 60, 30);
        joinButton.setFont(new Font("Roboto", Font.BOLD, 10));
        joinButton.setBackground(new Color(46, 157, 251));
        joinButton.setForeground(Color.BLACK);
        joinButton.addActionListener(e -> showJoinConfirmation(interest));
        interestCard.add(joinButton);

        interestsListPanel.add(interestCard);
    }

    private void addUserGroupCard(String group) {
        JPanel groupCard = new JPanel();
        groupCard.setPreferredSize(new Dimension(330, 60));
        groupCard.setBackground(new Color(230, 230, 230));
        groupCard.setLayout(null);

        JLabel groupLabel = new JLabel(group);
        groupLabel.setFont(new Font("Roboto", Font.BOLD, 14));
        groupLabel.setBounds(10, 5, 200, 20);
        groupCard.add(groupLabel);

        JButton viewButton = new JButton("View");
        viewButton.setBounds(220, 5, 50, 30);
        viewButton.setFont(new Font("Roboto", Font.BOLD, 10));
        viewButton.setBackground(new Color(46, 157, 251));
        viewButton.setForeground(Color.BLACK);
        viewButton.addActionListener(e -> openGroupPage(group));
        groupCard.add(viewButton);

        JButton leaveButton = new JButton("Leave");
        leaveButton.setBounds(280, 5, 60, 30);
        leaveButton.setFont(new Font("Roboto", Font.BOLD, 10));
        leaveButton.setBackground(new Color(231, 76, 60));
        leaveButton.setForeground(Color.BLACK);
        leaveButton.addActionListener(e -> showLeaveConfirmation(group));
        groupCard.add(leaveButton);

        userGroupsListPanel.add(groupCard);
    }

    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        List<String> filteredInterests = new ArrayList<>();
        for (String interest : userInterests) {
            if (interest.toLowerCase().contains(query)) {
                filteredInterests.add(interest);
            }
        }
        populateInterestsList(filteredInterests);
    }

    private void showJoinConfirmation(String interest) {
        String description = "<html><b>Description:</b><br>" +
                             "This group provides a platform to connect with like-minded individuals who share a passion for " + interest + ".<br>" +
                             "The purpose of this community is to foster collaboration and exchange ideas, resources, and strategies to grow together.<br>" +
                             "By joining, you will have the opportunity to:<br>" +
                             "- Share insights and experiences.<br>" +
                             "- Build valuable connections.<br>" +
                             "- Work collectively to achieve personal and group objectives.<br>" +
                             "</html>";

        JPanel confirmationPanel = new JPanel();
        confirmationPanel.setLayout(new BoxLayout(confirmationPanel, BoxLayout.Y_AXIS));
        confirmationPanel.add(new JLabel("You're about to join the " + interest + " Community."));
        confirmationPanel.add(new JLabel(description));

        int response = JOptionPane.showOptionDialog(this,
                confirmationPanel,
                "Confirm Join",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Join Now", "Cancel"},
                "Cancel");
        if (response == JOptionPane.YES_OPTION) {
            userGroups.add(interest);
            populateUserGroups(userGroups);
        }
    }


    private void showLeaveConfirmation(String group) {
        int response = JOptionPane.showOptionDialog(this,
                "Are you sure you want to leave the " + group + " group?",
                "Confirm Leave",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Leave", "Cancel"},
                "Cancel");
        if (response == JOptionPane.YES_OPTION) {
            userGroups.remove(group);
            populateUserGroups(userGroups);
        }
    }

    private void openGroupPage(String group) {
        JDialog groupDialog = new JDialog(this, "Welcome to " + group + " Universe Group", true);
        groupDialog.setSize(800, 600);
        groupDialog.setLayout(new BorderLayout());
        groupDialog.setLocationRelativeTo(this);

        // Main container panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        groupDialog.add(mainPanel, BorderLayout.CENTER);

        // Left panel: Members
        JPanel membersPanel = new JPanel();
        membersPanel.setLayout(new BorderLayout(10, 10));
        membersPanel.setBorder(BorderFactory.createTitledBorder("# Members"));

        List<String> members = FirestoreHandler.getGroupMembers(group); // Fetch members from the database
        if (members == null) {
            members = new ArrayList<>();
        }
        if (!members.contains(SessionManager.currentUserId)) {
            members.add(SessionManager.currentUserId); // Add current user to the group
            FirestoreHandler.addGroupMember(group, SessionManager.currentUserId);
        }

        // Member list
        JPanel memberListPanel = new JPanel();
        memberListPanel.setLayout(new BoxLayout(memberListPanel, BoxLayout.Y_AXIS));
        for (String member : members) {
            UserProfile user = FirestoreHandler.getUserData(member);
            if (user != null) {
                JPanel memberPanel = new JPanel(new BorderLayout());
                JLabel nameLabel = new JLabel(user.getUsername());
                nameLabel.setFont(new Font("Roboto", Font.BOLD, 14));

                JButton addFriendButton = new JButton(FirestoreHandler.isFriend(SessionManager.currentUserId, member) ? "Added" : "Add Friend");
                addFriendButton.setEnabled(!FirestoreHandler.isFriend(SessionManager.currentUserId, member));
                addFriendButton.addActionListener(e -> {
                    FirestoreHandler.addFriend(SessionManager.currentUserId, member, user.getUsername(), user.getUniversity());
                    addFriendButton.setText("Added");
                    addFriendButton.setEnabled(false);
                });

                memberPanel.add(nameLabel, BorderLayout.WEST);
                memberPanel.add(addFriendButton, BorderLayout.EAST);
                memberListPanel.add(memberPanel);
            }
        }

        JScrollPane memberScrollPane = new JScrollPane(memberListPanel);
        membersPanel.add(memberScrollPane, BorderLayout.CENTER);

        // Update members heading
        ((javax.swing.border.TitledBorder) membersPanel.getBorder()).setTitle(members.size() + " Members");

        mainPanel.add(membersPanel);

        // Right panel: About group
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BorderLayout(10, 10));
        aboutPanel.setBorder(BorderFactory.createTitledBorder("About this Group"));

        JLabel descriptionLabel = new JLabel("<html>" +
                "This group provides a platform to connect with like-minded individuals who share a passion for " + group + ".<br>" +
                "The purpose of this community is to foster collaboration, exchange ideas, resources, and strategies to grow together.<br>" +
                "By joining, you will have the opportunity to:<br>" +
                "- Share insights and experiences.<br>" +
                "- Build valuable connections.<br>" +
                "- Work collectively to achieve personal and group objectives." +
                "</html>");
        descriptionLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        aboutPanel.add(descriptionLabel, BorderLayout.CENTER);

        JButton discordButton = new JButton("Join Discord Chat!");
        discordButton.setFont(new Font("Roboto", Font.BOLD, 14));
        discordButton.setBackground(new Color(46, 157, 251));
        discordButton.setForeground(Color.WHITE);
        discordButton.addActionListener(e -> JOptionPane.showMessageDialog(groupDialog, "Redirecting to Discord..."));
        aboutPanel.add(discordButton, BorderLayout.SOUTH);

        mainPanel.add(aboutPanel);

        groupDialog.setVisible(true);
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
            JOptionPane.showMessageDialog(parentFrame, "Messages clicked!");
        });

        addSidebarIcon(sidebar, "src/main/resources/icons/notifications.png", "Notifications", 240, e -> {
            JOptionPane.showMessageDialog(parentFrame, "Notifications clicked!");
        });

        addSidebarIcon(sidebar, "src/main/resources/icons/community.png", "Community", 310, e -> {
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
