package com.universe.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Base64;
import java.util.HashSet;

import com.universe.models.UserProfile;
import com.universe.utils.SessionManager;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.universe.FirestoreHandler;

public class Interestspage extends JFrame {

    private JPanel contentPane;
    private JPanel interestsListPanel;
    private JPanel userGroupsListPanel;
    private JTextField searchField;
    private List<String> userInterests;
    private List<String> userGroups; // Groups user has joined
    private JLabel profilePic; // Sidebar profile picture
    private UserProfile currentUser;

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

        currentUser = FirestoreHandler.getUserData(currentUserId);
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "Error fetching user data.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        userInterests = currentUser.getInterests();
        if (userInterests == null) userInterests = new ArrayList<>();
        userGroups = new ArrayList<>();

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

        createLeftPanel();
        createRightPanel();

        // Attach real-time listener to the user's groups
        FirestoreHandler.getUserGroupsRealtime(currentUserId, (snapshot, e) -> {
            if (e != null) {
                System.err.println("Error listening to user groups: " + e.getMessage());
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                List<String> groups = (List<String>) snapshot.get("groups");
                if (groups != null) {
                    userGroups.clear();
                    userGroups.addAll(groups);
                    populateUserGroups(userGroups); // Update left panel
                }
            }
        });

        populateAllGroups(); // Populate the right panel with all groups
    }




    private void createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setBounds(100, 80, 430, 480);
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        leftPanel.setLayout(null);

        JLabel leftLabel = new JLabel("Your Groups:", JLabel.CENTER);
        leftLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        leftLabel.setBounds(15, 10, 390, 20);
        leftLabel.setOpaque(true);
        leftLabel.setBackground(new Color(46, 157, 251));
        leftLabel.setForeground(Color.WHITE);
        leftPanel.add(leftLabel);

        userGroupsListPanel = new JPanel();
        userGroupsListPanel.setLayout(new BoxLayout(userGroupsListPanel, BoxLayout.Y_AXIS));
        userGroupsListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(userGroupsListPanel);
        scrollPane.setBounds(15, 40, 390, 420);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftPanel.add(scrollPane);

        contentPane.add(leftPanel);
    }

    private void createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setBounds(540, 80, 330, 480);
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        rightPanel.setLayout(null);

        JLabel rightLabel = new JLabel("Popular Groups");
        rightLabel.setBounds(10, 10, 310, 20);
        rightLabel.setFont(new Font("Roboto", Font.BOLD, 12));
        rightPanel.add(rightLabel);

        JButton createGroupButton = new JButton("Create Group");
        createGroupButton.setBounds(210, 10, 110, 25);
        createGroupButton.setFont(new Font("Roboto", Font.BOLD, 10));
        createGroupButton.setBackground(new Color(46, 157, 251));
        createGroupButton.setForeground(Color.BLACK);
        createGroupButton.addActionListener(e -> showCreateGroupDialog());
        rightPanel.add(createGroupButton);

        searchField = new JTextField("Search groups...");
        searchField.setBounds(10, 45, 310, 30);
        searchField.setBorder(BorderFactory.createLineBorder(new Color(46, 157, 251), 2));
        searchField.setFont(new Font("Roboto", Font.PLAIN, 12));
        rightPanel.add(searchField);

        interestsListPanel = new JPanel();
        interestsListPanel.setLayout(new BoxLayout(interestsListPanel, BoxLayout.Y_AXIS));
        interestsListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(interestsListPanel);
        scrollPane.setBounds(10, 85, 310, 380);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        rightPanel.add(scrollPane);

        contentPane.add(rightPanel);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleGroupSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleGroupSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleGroupSearch();
            }
        });
    }

    private void populateAllGroups() {
        interestsListPanel.removeAll();

        List<Map<String, Object>> allGroups = FirestoreHandler.getAllGroups();

        if (allGroups == null || allGroups.isEmpty()) {
            JLabel noGroupsLabel = new JLabel("No groups have been created yet!", JLabel.CENTER);
            noGroupsLabel.setFont(new Font("Roboto", Font.BOLD, 14));
            noGroupsLabel.setForeground(Color.GRAY);
            interestsListPanel.add(noGroupsLabel);
        } else {
            for (Map<String, Object> group : allGroups) {
                addGroupCard(group);
            }
        }

        interestsListPanel.revalidate();
        interestsListPanel.repaint();
    }

    

    private void handleGroupSearch() {
        String query = searchField.getText().trim().toLowerCase();
        List<Map<String, Object>> allGroups = FirestoreHandler.getAllGroups();
        List<Map<String, Object>> filteredGroups = new ArrayList<>();

        for (Map<String, Object> group : allGroups) {
            String groupName = (String) group.get("groupName");
            if (groupName.toLowerCase().contains(query)) {
                filteredGroups.add(group);
            }
        }

        interestsListPanel.removeAll();

        if (filteredGroups.isEmpty()) {
            JLabel noGroupsLabel = new JLabel("No groups found for \"" + query + "\"", JLabel.CENTER);
            noGroupsLabel.setFont(new Font("Roboto", Font.BOLD, 14));
            noGroupsLabel.setForeground(Color.GRAY);
            interestsListPanel.add(noGroupsLabel);
        } else {
            for (Map<String, Object> group : filteredGroups) {
                addGroupCard(group);
            }
        }

        interestsListPanel.revalidate();
        interestsListPanel.repaint();
    }
    

    private void addGroupCard(Map<String, Object> groupData) {
        String groupName = (String) groupData.get("groupName");
        String groupDescription = (String) groupData.get("groupDescription");
        String relatedInterest = (String) groupData.get("relatedInterest");
        String rules = (String) groupData.get("rules");

        JPanel groupCard = new JPanel();
        groupCard.setPreferredSize(new Dimension(310, 60));
        groupCard.setBackground(new Color(230, 230, 230));
        groupCard.setLayout(null);

        JLabel groupLabel = new JLabel(groupName);
        groupLabel.setFont(new Font("Roboto", Font.BOLD, 13));
        groupLabel.setBounds(10, 5, 200, 20);
        groupCard.add(groupLabel);

        JButton joinButton = new JButton(userGroups.contains(groupName) ? "Joined" : "Join");
        joinButton.setBounds(220, 5, 60, 30);
        joinButton.setFont(new Font("Roboto", Font.BOLD, 10));
        joinButton.setBackground(new Color(46, 157, 251));
        joinButton.setForeground(Color.BLACK);
        joinButton.setEnabled(!userGroups.contains(groupName));

        joinButton.addActionListener(e -> {
            if (!userGroups.contains(groupName)) {
                // Show group details and confirmation dialog
                String groupInfo = "<html><b>Group Name:</b> " + groupName + "<br>" +
                                   "<b>Description:</b> " + groupDescription + "<br>" +
                                   "<b>Related Interest:</b> " + relatedInterest + "<br>" +
                                   "<b>Rules:</b> " + rules + "</html>";
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    groupInfo,
                    "Join Group",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    FirestoreHandler.addUserToGroup(SessionManager.currentUserId, groupName);
                    
                 // Avoid duplicate addition to the list
                    if (!userGroups.contains(groupName)) {
                        userGroups.add(groupName); // Update user's groups list
                    }
                    
                   // userGroups.add(groupName); // Update user's groups
                    //populateUserGroups(userGroups); // Refresh left panel
                    joinButton.setText("Joined");
                    joinButton.setEnabled(false);
                }
            }
        });
        groupCard.add(joinButton);

        interestsListPanel.add(groupCard);
    }




    private JPanel createSidebar(JFrame parentFrame) {
        JPanel sidebar = new JPanel();
        sidebar.setBounds(10, 10, 70, 540);
        sidebar.setBackground(Color.WHITE);
        sidebar.setLayout(null);

        // Profile Picture
        profilePic = new JLabel();
        String profilePicBase64 = currentUser.getProfilePicture();
        if (profilePicBase64 != null && !profilePicBase64.isEmpty()) {
            byte[] imageBytes = Base64.getDecoder().decode(profilePicBase64);
            ImageIcon profileImageIcon = new ImageIcon(imageBytes);
            Image scaledImage = profileImageIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            profilePic.setIcon(new ImageIcon(scaledImage));
        } else {
            profilePic.setIcon(new ImageIcon("src/main/resources/icons/profile.png"));
        }
        profilePic.setBounds(5, 25, 60, 60);
        profilePic.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profilePic.setToolTipText("View Profile");
        profilePic.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showProfile(currentUser, parentFrame);
            }
        });
        sidebar.add(profilePic);

        // Sidebar Icons
        addSidebarIcon(sidebar, "src/main/resources/icons/home.png", "Home", 100, e -> {
            Homepage homepage = new Homepage();
            homepage.setVisible(true);
            parentFrame.dispose();
        });
        addSidebarIcon(sidebar, "src/main/resources/icons/messages.png", "Messages", 170, e -> {
        	 Messaging messaging = new Messaging();
             messaging.setVisible(true);
             parentFrame.dispose();;
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

    private void showProfile(UserProfile user, JFrame parentFrame) {
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.add(new JLabel("Name: " + user.getUsername()));
        profilePanel.add(new JLabel("University: " + user.getUniversity()));
        profilePanel.add(new JLabel("City: " + user.getProvince()));
        profilePanel.add(new JLabel("Interests: " + user.getInterests()));
        profilePanel.add(new JLabel("Date of Birth: " + user.getDateOfBirth()));
        profilePanel.add(new JLabel("Email: " + user.getEmail()));

        JOptionPane.showMessageDialog(parentFrame, profilePanel, "Profile Details", JOptionPane.INFORMATION_MESSAGE);
    }


    private void populateUserGroups(List<String> groups) {
        userGroupsListPanel.removeAll(); // Clear existing components

        // Ensure unique groups are displayed
        List<String> uniqueGroups = new ArrayList<>(new HashSet<>(groups));

        if (uniqueGroups.isEmpty()) {
            JLabel noGroupsLabel = new JLabel("No groups added yet!", JLabel.CENTER);
            noGroupsLabel.setFont(new Font("Roboto", Font.BOLD, 14));
            noGroupsLabel.setForeground(Color.GRAY);
            userGroupsListPanel.add(noGroupsLabel);
        } else {
            for (String group : uniqueGroups) {
                addUserGroupCard(group);
            }
        }

        userGroupsListPanel.revalidate();
        userGroupsListPanel.repaint();
    }




    private void addUserGroupCard(String group) {
       //new
    	// Check if the group is already present on the panel to avoid duplication
        for (Component component : userGroupsListPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel existingGroupCard = (JPanel) component;
                for (Component label : existingGroupCard.getComponents()) {
                    if (label instanceof JLabel && ((JLabel) label).getText().equals(group)) {
                        return; // Group already exists, skip adding
                    }
                }
            }
        }
    	
    	
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
        
        
        leaveButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to leave the group '" + group + "'?",
                "Confirm Leave",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                FirestoreHandler.removeUserFromGroup(SessionManager.currentUserId, group); // Remove from Firestore
                userGroups.remove(group); // Remove from user's groups
                populateUserGroups(userGroups); // Refresh left panel
                populateAllGroups(); // Refresh right panel
                JOptionPane.showMessageDialog(this, "You have successfully left the group: " + group,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });     
        
        groupCard.add(leaveButton);
        
        //new for delete group
        
     // Add "Delete" button if the current user is the creator of the group
        if (FirestoreHandler.isGroupCreator(SessionManager.currentUserId, group)) {
            JButton deleteButton = new JButton("Delete");
            deleteButton.setBounds(330, 5, 60, 30);
            deleteButton.setFont(new Font("Roboto", Font.BOLD, 10));
            deleteButton.setBackground(new Color(231, 76, 60));
            deleteButton.setForeground(Color.BLACK);

            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete the group '" + group + "'? This action cannot be undone.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    FirestoreHandler.deleteGroup(group);
                    userGroups.remove(group);
                    populateUserGroups(userGroups);
                    populateAllGroups();
                    JOptionPane.showMessageDialog(this, "Group '" + group + "' has been deleted successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            groupCard.add(deleteButton);
        }

        
        userGroupsListPanel.add(groupCard);
    }
    


    private void openGroupPage(String group) {
        JDialog groupDialog = new JDialog(this, "Welcome to " + group + " Universe Group", true);
        groupDialog.setSize(800, 600);
        groupDialog.setLayout(new BorderLayout());
        groupDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        groupDialog.add(mainPanel, BorderLayout.CENTER);

        // Left: Members Panel
        JPanel membersPanel = new JPanel();
        membersPanel.setLayout(new BorderLayout(10, 10));
        membersPanel.setBorder(BorderFactory.createTitledBorder("# Members"));

        List<String> members = FirestoreHandler.getGroupMembers(group);
        if (members == null) {
            members = new ArrayList<>();
        }
        if (!members.contains(SessionManager.currentUserId)) {
            members.add(SessionManager.currentUserId);
            FirestoreHandler.addGroupMember(group, SessionManager.currentUserId);
        }

        JPanel memberListPanel = new JPanel();
        memberListPanel.setLayout(new BoxLayout(memberListPanel, BoxLayout.Y_AXIS));
        for (String member : members) {
            UserProfile user = FirestoreHandler.getUserData(member);
            if (user != null) {
                JPanel memberPanel = new JPanel(new BorderLayout());
                JLabel nameLabel = new JLabel(user.getUsername());
                nameLabel.setFont(new Font("Roboto", Font.BOLD, 14));

                // Disable "Add Friend" button for yourself
                if (!member.equals(SessionManager.currentUserId)) {
                    JButton addFriendButton = new JButton(
                        FirestoreHandler.isFriend(SessionManager.currentUserId, member) ? "Added" : "Add Friend"
                    );
                    addFriendButton.setEnabled(!FirestoreHandler.isFriend(SessionManager.currentUserId, member));
                    addFriendButton.addActionListener(e -> {
                        FirestoreHandler.addFriend(SessionManager.currentUserId, member, user.getUsername(), user.getUniversity());
                        addFriendButton.setText("Added");
                        addFriendButton.setEnabled(false);
                    });
                    memberPanel.add(addFriendButton, BorderLayout.EAST);
                }

                memberPanel.add(nameLabel, BorderLayout.WEST);
                memberListPanel.add(memberPanel);
            }
        }

        JScrollPane memberScrollPane = new JScrollPane(memberListPanel);
        membersPanel.add(memberScrollPane, BorderLayout.CENTER);

        ((javax.swing.border.TitledBorder) membersPanel.getBorder()).setTitle(members.size() + " Members");

        mainPanel.add(membersPanel);

        // Right: About Panel
        JPanel aboutPanel = new JPanel();
        aboutPanel.setLayout(new BorderLayout(10, 10));
        aboutPanel.setBorder(BorderFactory.createTitledBorder("About this Group"));

        // Fetch group details
        Map<String, Object> groupDetails = FirestoreHandler.getAllGroups().stream()
            .filter(groupData -> group.equals(groupData.get("groupName")))
            .findFirst()
            .orElse(null);

        if (groupDetails != null) {
            String groupDescription = (String) groupDetails.get("groupDescription");
            String relatedInterest = (String) groupDetails.get("relatedInterest");
            String rules = (String) groupDetails.get("rules");

            JLabel descriptionLabel = new JLabel("<html>" +
                "<b>Description:</b> " + groupDescription + "<br>" +
                "<b>Related Interest:</b> " + relatedInterest + "<br>" +
                "<b>Rules:</b> " + rules +
                "</html>");
            descriptionLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
            aboutPanel.add(descriptionLabel, BorderLayout.CENTER);
        } else {
            JLabel descriptionLabel = new JLabel("<html><b>No additional group information available.</b></html>");
            descriptionLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
            aboutPanel.add(descriptionLabel, BorderLayout.CENTER);
        }

        JButton discordButton = new JButton("Join Discord Chat!");
        discordButton.setFont(new Font("Roboto", Font.BOLD, 14));
        discordButton.setBackground(new Color(46, 157, 251));
        discordButton.setForeground(Color.BLACK);
        discordButton.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new java.net.URI("https://discord.gg/jSFmGcCv")); // Our Discord link
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(groupDialog, "Unable to open Discord link.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        aboutPanel.add(discordButton, BorderLayout.SOUTH);

        mainPanel.add(aboutPanel);

        groupDialog.setVisible(true);
    }

    
    
    private void showCreateGroupDialog() {
        JDialog createGroupDialog = new JDialog(this, "Create New Group", true);
        createGroupDialog.setSize(400, 400);
        createGroupDialog.setLayout(null);
        createGroupDialog.setLocationRelativeTo(this);

        JLabel nameLabel = new JLabel("Group Name:");
        nameLabel.setBounds(20, 30, 100, 25);
        createGroupDialog.add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(130, 30, 230, 25);
        createGroupDialog.add(nameField);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setBounds(20, 70, 100, 25);
        createGroupDialog.add(descriptionLabel);

        JTextArea descriptionField = new JTextArea();
        descriptionField.setBounds(130, 70, 230, 60);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        createGroupDialog.add(descriptionField);

        JLabel interestLabel = new JLabel("Related Interest:");
        interestLabel.setBounds(20, 150, 110, 25);
        createGroupDialog.add(interestLabel);

        JComboBox<String> interestComboBox = new JComboBox<>(userInterests.toArray(new String[0]));
        interestComboBox.setBounds(130, 150, 230, 25);
        createGroupDialog.add(interestComboBox);

        JLabel rulesLabel = new JLabel("Rules:");
        rulesLabel.setBounds(20, 190, 100, 25);
        createGroupDialog.add(rulesLabel);

        JTextArea rulesField = new JTextArea();
        rulesField.setBounds(130, 190, 230, 60);
        rulesField.setLineWrap(true);
        rulesField.setWrapStyleWord(true);
        createGroupDialog.add(rulesField);

        JButton createButton = new JButton("Create");
        createButton.setBounds(150, 270, 100, 30);
        createButton.setBackground(new Color(46, 157, 251));
        createButton.setForeground(Color.BLACK);
        createButton.addActionListener(e -> {
            String groupName = nameField.getText().trim();
            String groupDescription = descriptionField.getText().trim();
            String relatedInterest = (String) interestComboBox.getSelectedItem();
            String rules = rulesField.getText().trim();

            if (groupName.isEmpty()) {
                JOptionPane.showMessageDialog(createGroupDialog, "Group name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userGroups.contains(groupName)) {
                JOptionPane.showMessageDialog(createGroupDialog, "Group already exists in your list!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Save group data to Firestore
            FirestoreHandler.createGroup(SessionManager.currentUserId, groupName, groupDescription, relatedInterest, rules);

            // Ensure the group is added only once
            if (!userGroups.contains(groupName)) {
                userGroups.add(groupName);
            }

            // Refresh the left and right panels once
           // populateUserGroups(userGroups);
            populateAllGroups();

            JOptionPane.showMessageDialog(createGroupDialog, "Group created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            createGroupDialog.dispose();
        });
        createGroupDialog.add(createButton);

        createGroupDialog.setVisible(true);
    }






}
