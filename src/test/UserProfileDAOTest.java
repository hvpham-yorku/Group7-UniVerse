package test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import DAO.UserProfileDAO;
import models.UserProfile;

public class UserProfileDAOTest {
    private UserProfileDAO userDAO;
    private UserProfile testUser;

    @Before
    public void setUp() {
        userDAO = new UserProfileDAO();
         testUser = new UserProfile(0, "timi", "timi@yorku.ca", "12345", "test bio");
        int generatedId = userDAO.createUser(testUser);
        testUser.setUserId(generatedId); }

    @After
    public void tearDown() {
        if (testUser.getUserId() > 0) {
            userDAO.deleteUser(testUser.getUserId());  
        }
    }

    @Test
    public void testCreateUser() {
        UserProfile retrievedUser = userDAO.getUserById(testUser.getUserId());
        assertNotNull("User should be created and retrieved successfully", retrievedUser);
        assertEquals("Usernames should match", testUser.getUsername(), retrievedUser.getUsername());
        assertEquals("Bios should match", testUser.getBio(), retrievedUser.getBio());
    }

    @Test
    public void testGetUserById() {
        UserProfile retrievedUser = userDAO.getUserById(testUser.getUserId());
        assertNotNull("User should be retrieved successfully", retrievedUser);
        assertEquals("Usernames should match", "timi", retrievedUser.getUsername());
        assertEquals("Emails should match", "timi@yorku.ca", retrievedUser.getEmail());
        assertEquals("Bios should match", "test bio", retrievedUser.getBio());
    }

    @Test
    public void testUpdateUser() {
        testUser.setBio("Updated bio for Timi.");
        userDAO.updateUser(testUser);
        UserProfile updatedUser = userDAO.getUserById(testUser.getUserId());
        assertEquals("Bio should be updated", "Updated bio for Timi.", updatedUser.getBio());
    }
}
