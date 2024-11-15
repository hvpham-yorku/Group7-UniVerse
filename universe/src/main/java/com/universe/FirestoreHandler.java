package com.universe;

import com.google.api.core.ApiFuture;

import com.google.cloud.firestore.*;
import com.universe.models.UserProfile;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.universe.models.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirestoreHandler {

	private static final String COLLECTION_NAME = "UserProfile";
	 private static final String FRIENDS_COLLECTION = "friends";
	// Firestore instance
	    private static Firestore db = FirestoreClient.getFirestore();

	public static void addUserData(UserProfile user) {
		Firestore db = FirestoreClient.getFirestore();
		CollectionReference users = db.collection(COLLECTION_NAME);
		

		ApiFuture<WriteResult> writeResult = users.document(user.getUserId()).set(user);
		try {
			System.out.println("User added at: " + writeResult.get().getUpdateTime());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	public static void updateUserData(UserProfile user) {
		Firestore db = FirestoreClient.getFirestore();
		DocumentReference docRef = db.collection(COLLECTION_NAME).document(user.getUserId());
		ApiFuture<WriteResult> writeResult = docRef.set(user);
		try {
			System.out.println("User updated at: " + writeResult.get().getUpdateTime());
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("Error updating user: " + e.getMessage());
		}
	}

	// Authenticate user (login)
    public static boolean authenticateUser(String email, String hashedPassword) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference users = db.collection(COLLECTION_NAME);
        ApiFuture<QuerySnapshot> future = users.whereEqualTo("email", email).get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (!documents.isEmpty()) {
                String storedPasswordHash = documents.get(0).getString("passwordHash");
                return storedPasswordHash.equals(hashedPassword);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static UserProfile getUserData(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(userId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        try {
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                // Convert Firestore document into UserProfile object
                UserProfile user = document.toObject(UserProfile.class);
                System.out.println("User Data Retrieved: " + user);
                return user; // Return the UserProfile object
            } else {
                System.err.println("No document found for userId: " + userId);
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error fetching user data: " + e.getMessage());
        }
        return null; // Return null if user not found or error occurred
    }


	public static void deleteUserData(String userId) {
		Firestore db = FirestoreClient.getFirestore();
		DocumentReference docRef = db.collection(COLLECTION_NAME).document(userId);
		ApiFuture<WriteResult> writeResult = docRef.delete();
		try {
			System.out.println("User deleted at: " + writeResult.get().getUpdateTime());
		} catch (InterruptedException | ExecutionException e) {
			System.err.println("Error deleting user: " + e.getMessage());
		}
	}
	//messaging page
	public static void addContact(String userId, String contactUserId, String contactUsername) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference contactsRef = db.collection(COLLECTION_NAME).document(userId).collection("contacts");

        Map<String, Object> contactData = new HashMap<>();
        contactData.put("contactUserId", contactUserId);
        contactData.put("username", contactUsername);

        ApiFuture<WriteResult> writeResult = contactsRef.document(contactUserId).set(contactData);
        try {
            System.out.println("Contact added successfully at: " + writeResult.get().getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error adding contact: " + e.getMessage());
        }
    }
	
	/**
     * Add a friend to the Firestore database.
     */
	public static void addFriend(UserProfile user) {
	    DocumentReference docRef = db.collection(FRIENDS_COLLECTION).document(user.getUsername());
	    ApiFuture<WriteResult> future = docRef.set(user);

	    try {
	        WriteResult result = future.get(); // Wait for the operation to complete
	        System.out.println("Friend added at: " + result.getUpdateTime());
	    } catch (InterruptedException | ExecutionException e) {
	        System.err.println("Error adding friend: " + e.getMessage());
	    }
	}

    /**
     * Remove a friend from the Firestore database.
     */
	public static void removeFriend(UserProfile user) {
	    DocumentReference docRef = db.collection(FRIENDS_COLLECTION).document(user.getUsername());
	    ApiFuture<WriteResult> future = docRef.delete();

	    try {
	        WriteResult result = future.get(); // Wait for the operation to complete
	        System.out.println("Friend removed at: " + result.getUpdateTime());
	    } catch (InterruptedException | ExecutionException e) {
	        System.err.println("Error removing friend: " + e.getMessage());
	    }
	}

    
    /**
     * Get all friends in the Firestore database.
     */
    public static void getFriends(EventListener<QuerySnapshot> listener) {
        db.collection(FRIENDS_COLLECTION).addSnapshotListener(listener);
    }
    
	
	public static List<UserProfile> getUserContacts(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference contactsRef = db.collection(COLLECTION_NAME).document(userId).collection("contacts");
        ApiFuture<QuerySnapshot> future = contactsRef.get();

        List<UserProfile> contacts = new ArrayList<>();
        try {
            QuerySnapshot contactsSnapshot = future.get();
            for (QueryDocumentSnapshot document : contactsSnapshot.getDocuments()) {
                String contactUserId = document.getString("contactUserId");
                String username = document.getString("username");
                contacts.add(new UserProfile(contactUserId, username, "", "")); // Only basic info for contacts
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error fetching contacts: " + e.getMessage());
        }
        return contacts;
    }
	
	public static UserProfile findUserByEmailOrUsername(String query) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference users = db.collection(COLLECTION_NAME);

        try {
            // Search by email
            ApiFuture<QuerySnapshot> future = users.whereEqualTo("email", query).get();
            QuerySnapshot snapshot = future.get();
            if (!snapshot.isEmpty()) {
                DocumentSnapshot document = snapshot.getDocuments().get(0);
                return document.toObject(UserProfile.class);
            }

            // Search by username
            future = users.whereEqualTo("username", query).get();
            snapshot = future.get();
            if (!snapshot.isEmpty()) {
                DocumentSnapshot document = snapshot.getDocuments().get(0);
                return document.toObject(UserProfile.class);
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }

        return null; // Return null if user is not found
    }
	
	public static List<Map<String, String>> getChatHistory(String userId, String contactId) {
	    Firestore db = FirestoreClient.getFirestore();
	    CollectionReference messagesRef = db.collection("chats").document(userId + "_" + contactId).collection("messages");
	    ApiFuture<QuerySnapshot> future = messagesRef.get();

	    List<Map<String, String>> messages = new ArrayList<>();
	    try {
	        QuerySnapshot messagesSnapshot = future.get();
	        for (DocumentSnapshot document : messagesSnapshot.getDocuments()) {
	            String content = document.getString("content");
	            String senderId = document.getString("senderId");
	            Map<String, String> messageData = new HashMap<>();
	            messageData.put("content", content);
	            messageData.put("senderId", senderId);
	            messages.add(messageData);
	        }
	    } catch (InterruptedException | ExecutionException e) {
	        System.err.println("Error fetching chat history: " + e.getMessage());
	    }
	    return messages;
	}

	//Ensuring messages are being saved in database and adding a saved messaged message into the console.
	public static void saveMessages(String userId, String contactId, String messages)
	{
		Firestore db = FirestoreClient.getFirestore();
		CollectionReference messagesRef = db.collection("chats").document(userId + "_" + contactId).collection("messages");
		Map<String, Object> messageData = new HashMap<>();
		messageData.put("content", messages);
		messageData.put("senderId", userId);
		messageData.put("timestamp", FieldValue.serverTimestamp());
		
		ApiFuture<DocumentReference> writeResult = messagesRef.add(messageData);
		try
		{
			DocumentReference documentReference = writeResult.get();
			System.out.println("Message stored in document ID: " + documentReference.getId());
		}
		catch(InterruptedException | ExecutionException e)
		{
			System.err.println("Error saving message: " + e.getMessage());
		}
	}
	public static List<UserProfile> getAllUsers() {
	    Firestore db = FirestoreClient.getFirestore();
	    List<UserProfile> users = new ArrayList<>();
	    try {
	        ApiFuture<QuerySnapshot> future = db.collection("UserProfile").get();
	        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
	        for (DocumentSnapshot document : documents) {
	            users.add(document.toObject(UserProfile.class));
	        }
	    } catch (InterruptedException | ExecutionException e) {
	        e.printStackTrace();
	    }
	    return users;
	}
	
	/**
     * Fetch a one-time list of all friends from Firestore.
     */
    public static List<UserProfile> getFriendsSync() {
        List<UserProfile> friends = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(FRIENDS_COLLECTION).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot doc : documents) {
                UserProfile friend = doc.toObject(UserProfile.class);
                friends.add(friend);
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error fetching friends: " + e.getMessage());
        }
        return friends;
    }

	
}
