package com.universe.utils;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
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
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.universe.FirestoreHandler;
import com.universe.gui.Homepage;
import com.universe.gui.Messaging;
import com.universe.models.UserProfile;



public class Sidebar {
	private UserProfile currentUser; // Logged-in user's profile
	private static JLabel profilePic;
	private String currentUserId;


	 public static JPanel createSidebar(UserProfile currentUser, JFrame parentFrame) {
	        JPanel sidebar = new JPanel();
	        sidebar.setBounds(10, 10, 70, 540);
	        sidebar.setBackground(Color.WHITE);
	        sidebar.setLayout(null);

	        // Profile Picture
	        JLabel profilePic = new JLabel();
	        if (currentUser != null && currentUser.getProfilePicture() != null) {
	            byte[] imageBytes = Base64.getDecoder().decode(currentUser.getProfilePicture());
	            ImageIcon profileImageIcon = new ImageIcon(imageBytes);
	            Image scaledImage = profileImageIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
	            profilePic.setIcon(new ImageIcon(scaledImage));
	        } else {
	            profilePic.setIcon(new ImageIcon("src/main/resources/icons/profile.png")); // Default image
	        }
	        profilePic.setBounds(5, 25, 60, 60);
	        profilePic.setToolTipText("View Profile");
	        profilePic.addMouseListener(new java.awt.event.MouseAdapter() {
	            @Override
	            public void mouseClicked(java.awt.event.MouseEvent e) {
	                showProfile(currentUser, parentFrame);
	            }
	        });
	        sidebar.add(profilePic);

	        // Add Sidebar Icons
	        addSidebarIcon(sidebar, "src/main/resources/icons/home.png", "Home", 100, e -> navigateToHomepage(parentFrame));
	        addSidebarIcon(sidebar, "src/main/resources/icons/messages.png", "Chat", 170, e -> navigateToMessages(parentFrame));
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

	    private static void addSidebarIcon(JPanel sidebar, String iconPath, String tooltip, int yPosition,
	                                       java.awt.event.ActionListener action) {
	        ImageIcon originalIcon = new ImageIcon(iconPath);
	        Image resizedImage = originalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
	        ImageIcon resizedIcon = new ImageIcon(resizedImage);
	        JButton iconButton = new JButton(resizedIcon);
	        iconButton.setBounds(10, yPosition, 60, 60);
	        iconButton.setBackground(Color.WHITE);
	        iconButton.setBorder(null);
	        iconButton.setFocusPainted(false);
	        iconButton.setToolTipText(tooltip);
	        iconButton.addActionListener(action);
	        sidebar.add(iconButton);
	    }

	    private static void navigateToHomepage(JFrame parentFrame) {
	        parentFrame.dispose();
	        new Homepage().setVisible(true);
	    }

	    private static void navigateToMessages(JFrame parentFrame) {
	        parentFrame.dispose();
	        new Messaging().setVisible(true);
	    }
	    private void navigateToNotifications(JFrame parentFrame) {
			 // Dispose of the current frame to clean up resources
		    parentFrame.dispose(); 

		    //UNCOMMENT AFTER MERGE!!!!
		    // Safely open the Messaging frame on the Event Dispatch Thread
		    //EventQueue.invokeLater(() -> {
		       // Notifications notifications = new Notifications();
		        //notifications.setVisible(true);
		        //notifications.setLocationRelativeTo(null); // Center the new window
		  //  });
			
		}
	   
	    public static void showProfile(UserProfile user, JFrame parentFrame) {
		    JDialog dialog = new JDialog(parentFrame, "Edit My Profile", true);
		    dialog.setSize(500, 700);
		    dialog.getContentPane().setLayout(null);

		    int yPosition = 20; // Vertical position for each component
		    int labelWidth = 120;
		    int fieldWidth = 250;
		    int fieldHeight = 30;

		    // Full Name Field (Not Editable)
		    JLabel lblName = new JLabel("Full Name:");
		    lblName.setBounds(20, yPosition, labelWidth, fieldHeight);
		    dialog.getContentPane().add(lblName);

		    JLabel nameField = new JLabel(user.getUsername());
		    nameField.setBounds(150, yPosition, fieldWidth, fieldHeight);
		    dialog.getContentPane().add(nameField);

		    yPosition += 50;

		    // Email Field (Not Editable)
		    JLabel lblEmail = new JLabel("Email:");
		    lblEmail.setBounds(20, yPosition, labelWidth, fieldHeight);
		    dialog.getContentPane().add(lblEmail);

		    JLabel emailField = new JLabel(user.getEmail());
		    emailField.setBounds(150, yPosition, fieldWidth, fieldHeight);
		    dialog.getContentPane().add(emailField);

		    yPosition += 50;

		    // Date of Birth Field (Not Editable)
		    JLabel lblDob = new JLabel("Date of Birth:");
		    lblDob.setBounds(20, yPosition, labelWidth, fieldHeight);
		    dialog.getContentPane().add(lblDob);

		    JLabel dobField = new JLabel(user.getDateOfBirth());
		    dobField.setBounds(150, yPosition, fieldWidth, fieldHeight);
		    dialog.getContentPane().add(dobField);

		    yPosition += 50;

		    // Bio Field (Editable)
		    JLabel lblBio = new JLabel("Bio:");
		    lblBio.setBounds(20, yPosition, labelWidth, fieldHeight);
		    dialog.getContentPane().add(lblBio);

		    JTextField bioField = new JTextField(user.getBio() != null ? user.getBio() : "");
		    bioField.setBounds(150, yPosition, fieldWidth, fieldHeight);
		    dialog.getContentPane().add(bioField);

		    yPosition += 50;

		    // City Field (Editable)
		    JLabel lblCity = new JLabel("City:");
		    lblCity.setBounds(20, yPosition, labelWidth, fieldHeight);
		    dialog.getContentPane().add(lblCity);

		    Choice cityChoice = new Choice();
		    cityChoice.setBounds(150, yPosition, fieldWidth, fieldHeight);
		    for (String city : Constants.CITIES) {
		        cityChoice.add(city);
		    }
		    cityChoice.select(user.getProvince() != null ? user.getProvince() : "");
		    dialog.getContentPane().add(cityChoice);

		    yPosition += 50;

		    // University Field (Not Editable)
		    JLabel lblUniversity = new JLabel("University:");
		    lblUniversity.setBounds(20, yPosition, labelWidth, fieldHeight);
		    dialog.getContentPane().add(lblUniversity);

		    JLabel universityField = new JLabel(user.getUniversity());
		    universityField.setBounds(150, yPosition, fieldWidth, fieldHeight);
		    dialog.getContentPane().add(universityField);

		    yPosition += 50;

		    // Interests Field (Editable via Button)
		    JLabel lblInterests = new JLabel("Interests:");
		    lblInterests.setBounds(20, yPosition, labelWidth, fieldHeight);
		    dialog.getContentPane().add(lblInterests);

		    String interestsText = (user.getInterests() != null) ? String.join(", ", user.getInterests()) : "No interests specified";
		    JLabel interestsSummary = new JLabel(interestsText);
		    interestsSummary.setBounds(150, yPosition, fieldWidth, fieldHeight);
		    dialog.getContentPane().add(interestsSummary);

		    JButton btnEditInterests = new JButton("Edit Interests");
		    btnEditInterests.setBounds(150, yPosition + 40, 150, fieldHeight);
		    dialog.getContentPane().add(btnEditInterests);

		    btnEditInterests.addActionListener(e -> {
		        List<String> selectedInterests = showInterestSelectionDialog(parentFrame, user.getInterests() != null ? user.getInterests() : new ArrayList<>());
		        user.setInterests(selectedInterests); // Update user's interests immediately
		        interestsSummary.setText(String.join(", ", selectedInterests)); // Update displayed interests
		    });

		    yPosition += 100;

		    // Profile Picture Section
		    JLabel lblProfilePicture = new JLabel("Profile Picture:");
		    lblProfilePicture.setBounds(20, yPosition, labelWidth, fieldHeight);
		    dialog.getContentPane().add(lblProfilePicture);

		    JLabel profilePicturePreview = new JLabel();
		    profilePicturePreview.setBounds(150, yPosition, 100, 100);
		    if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
		        byte[] imageBytes = Base64.getDecoder().decode(user.getProfilePicture());
		        ImageIcon profileImageIcon = new ImageIcon(imageBytes);
		        Image scaledImage = profileImageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		        profilePicturePreview.setIcon(new ImageIcon(scaledImage));
		    }
		    dialog.getContentPane().add(profilePicturePreview);

		    JButton btnChangeProfilePicture = new JButton("Change Picture");
		    btnChangeProfilePicture.setBounds(260, yPosition + 35, 140, fieldHeight);
//		    btnChangeProfilePicture.addActionListener(e -> {
//		        String newProfilePicture = selectProfilePicture();
//		        if (newProfilePicture != null) {
//		            user.setProfilePicture(newProfilePicture);
//		            
//		            // Update the preview in the dialog
//		            byte[] imageBytes = Base64.getDecoder().decode(newProfilePicture);
//		            ImageIcon profileImageIcon = new ImageIcon(imageBytes);
//		            Image scaledImage = profileImageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
//		            profilePicturePreview.setIcon(new ImageIcon(scaledImage));
//		            
//		            // Save the updated profile picture to Firestore
//		            FirestoreHandler.updateUserData(user);
//		            
//		            // Refresh the sidebar profile picture
//		            updateSidebarProfilePicture(newProfilePicture);
//		        }
//		    });
		    btnChangeProfilePicture.addActionListener(e -> {
		        String newProfilePicture = selectAndResizeProfilePicture();
		        if (newProfilePicture != null) {
		            user.setProfilePicture(newProfilePicture);

		            // Update the preview in the dialog
		            byte[] imageBytes = Base64.getDecoder().decode(newProfilePicture);
		            ImageIcon profileImageIcon = new ImageIcon(imageBytes);
		            Image scaledImage = profileImageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
		            profilePicturePreview.setIcon(new ImageIcon(scaledImage));

		            // Save the updated profile picture to Firestore
		            FirestoreHandler.updateUserData(user);

		            // Refresh the sidebar profile picture
		            updateSidebarProfilePicture(newProfilePicture);
		        }
		    });
		    dialog.getContentPane().add(btnChangeProfilePicture);

		    yPosition += 120;

		    // Save Changes Button
		    JButton btnSave = new JButton("Save Changes");
		    btnSave.setBounds(180, yPosition, 150, fieldHeight);
		    dialog.getContentPane().add(btnSave);

		    btnSave.addActionListener(e -> {
		        // Update other fields
		        user.setBio(bioField.getText());
		        user.setProvince(cityChoice.getSelectedItem());

		        // Save updates to Firestore
		        FirestoreHandler.updateUserData(user);

		        
		        JOptionPane.showMessageDialog(dialog, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
		        dialog.dispose(); // Close the dialog
		    });

		    dialog.setLocationRelativeTo(null);
		    dialog.setVisible(true);
		}
		private static List<String> showInterestSelectionDialog(JFrame parentFrame, List<String> currentInterests) {
		    JDialog dialog = new JDialog(parentFrame, "Select Interests", true);
		    dialog.setSize(400, 300);
		    dialog.getContentPane().setLayout(new BorderLayout());

		    JPanel interestsPanel = new JPanel();
		    interestsPanel.setLayout(new BoxLayout(interestsPanel, BoxLayout.Y_AXIS));

		    // Use sorted interests from Constants
		    String[] interests = Constants.INTERESTS;

		    // Create checkboxes and preselect based on currentInterests
		    List<JCheckBox> checkBoxes = new ArrayList<>();
		    for (String interest : interests) {
		        JCheckBox checkBox = new JCheckBox(interest);
		        if (currentInterests.contains(interest)) { // Preselect if it matches current interests
		            checkBox.setSelected(true);
		        }
		        interestsPanel.add(checkBox);
		        checkBoxes.add(checkBox);
		    }

		    dialog.getContentPane().add(new JScrollPane(interestsPanel), BorderLayout.CENTER);

		    JButton btnOk = new JButton("OK");
		    btnOk.addActionListener(e -> {
		        dialog.dispose();
		    });

		    JPanel buttonPanel = new JPanel();
		    buttonPanel.add(btnOk);
		    dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		    dialog.setVisible(true);

		    // Collect selected interests after the dialog is closed
		    List<String> selectedInterests = new ArrayList<>();
		    for (JCheckBox checkBox : checkBoxes) {
		        if (checkBox.isSelected()) {
		            selectedInterests.add(checkBox.getText());
		        }
		    }

		    return selectedInterests;
		}
		private static String selectAndResizeProfilePicture() {
		    JFileChooser fileChooser = new JFileChooser();
		    fileChooser.setDialogTitle("Select Profile Picture");
		    fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));

		    int result = fileChooser.showOpenDialog(null);
		    if (result == JFileChooser.APPROVE_OPTION) {
		        try {
		            File selectedFile = fileChooser.getSelectedFile();
		            BufferedImage originalImage = ImageIO.read(selectedFile);

		            // Resize the image to a maximum size (e.g., 300x300)
		            int maxDimension = 300;
		            BufferedImage resizedImage = resizeImage(originalImage, maxDimension, maxDimension);

		            // Convert the resized image to Base64
		            ByteArrayOutputStream baos = new ByteArrayOutputStream();
		            ImageIO.write(resizedImage, "png", baos); // Use "png" or other format
		            byte[] imageBytes = baos.toByteArray();

		            return Base64.getEncoder().encodeToString(imageBytes);
		        } catch (IOException e) {
		            JOptionPane.showMessageDialog(null, "Error reading image file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		        }
		    }
		    return null;
		}
		private static void updateSidebarProfilePicture(String newProfilePicture) {
		    if (newProfilePicture != null && !newProfilePicture.isEmpty()) {
		        byte[] imageBytes = Base64.getDecoder().decode(newProfilePicture);
		        ImageIcon profileImageIcon = new ImageIcon(imageBytes);
		        Image scaledImage = profileImageIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
		        profilePic.setIcon(new ImageIcon(scaledImage)); // Update the sidebar picture
		    } else {
		        // Fallback to default icon if no profile picture is set
		        ImageIcon defaultIcon = new ImageIcon("src/main/resources/icons/profile.png");
		        Image scaledDefaultImage = defaultIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
		        profilePic.setIcon(new ImageIcon(scaledDefaultImage)); // Set default profile picture
		    }
		    profilePic.revalidate();
		    profilePic.repaint();
		}
		
		private static BufferedImage resizeImage(BufferedImage originalImage, int maxWidth, int maxHeight) {
		    int originalWidth = originalImage.getWidth();
		    int originalHeight = originalImage.getHeight();

		    double scale = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);

		    int newWidth = (int) (originalWidth * scale);
		    int newHeight = (int) (originalHeight * scale);

		    BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
		    Graphics g = resizedImage.getGraphics();
		    g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
		    g.dispose();

		    return resizedImage;
		}
		private void refreshProfilePicture() {
		    String profilePicBase64 = FirestoreHandler.getUserData(currentUserId).getProfilePicture(); // Fetch latest picture
		    if (profilePicBase64 != null && !profilePicBase64.isEmpty()) {
		        byte[] imageBytes = Base64.getDecoder().decode(profilePicBase64);
		        ImageIcon profileImageIcon = new ImageIcon(imageBytes);
		        Image scaledImage = profileImageIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
		        profilePic.setIcon(new ImageIcon(scaledImage));
		    } else {
		        profilePic.setIcon(new ImageIcon("src/main/resources/icons/profile.png")); // Default icon
		    }
		    profilePic.revalidate();
		    profilePic.repaint(); // Ensure immediate UI refresh
		}
		
		
	}


