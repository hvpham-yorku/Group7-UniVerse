package com.universe;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.universe.models.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.ListenerRegistration;



import java.util.HashMap;
import java.util.Map;
import com.google.cloud.firestore.*;
import com.universe.models.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FirestoreHandler {

	private static final String COLLECTION_NAME = "UserProfile";
	 private static final String FRIENDS_COLLECTION = "friends"; 
	    private static final String CHATS_COLLECTION = "chats";
	    private static Firestore db = FirestoreClient.getFirestore(); //kennie modified

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
	

	public static void addFriend(String userId, String contactUserId, String contactUsername, String contactUniversity) {
	    CollectionReference contactsRef = db.collection("UserProfile").document(userId).collection("contacts");

	    Map<String, Object> contactData = new HashMap<>();
	    contactData.put("contactUserId", contactUserId);
	    contactData.put("username", contactUsername);
	    contactData.put("university", contactUniversity);

	    ApiFuture<WriteResult> writeResult = contactsRef.document(contactUserId).set(contactData);
	    try {
	        System.out.println("Contact added successfully at: " + writeResult.get().getUpdateTime());
	    } catch (InterruptedException | ExecutionException e) {
	        System.err.println("Error adding contact: " + e.getMessage());
	    }
	}



	public static void removeFriend(String userId, String contactUserId) {
	    DocumentReference docRef = db.collection("UserProfile")
	                                 .document(userId)
	                                 .collection("contacts")
	                                 .document(contactUserId);

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
        CollectionReference contactsRef = db.collection("UserProfile").document(userId).collection("contacts");

        List<UserProfile> contacts = new ArrayList<>();
        try {
            QuerySnapshot contactsSnapshot = contactsRef.get().get();
            for (QueryDocumentSnapshot document : contactsSnapshot.getDocuments()) {
                String contactUserId = document.getString("contactUserId");
                String username = document.getString("username");
                String university = document.getString("university");

                // Check if this friend is already in the list
                boolean alreadyExists = contacts.stream().anyMatch(contact -> contact.getUserId().equals(contactUserId));
                if (!alreadyExists) {
                    contacts.add(new UserProfile(contactUserId, username, "", "", university));
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error fetching contacts: " + e.getMessage());
        }
        return contacts;
    }



    
    public static void getFriends(EventListener<QuerySnapshot> listener, String userId) {
        CollectionReference contactsRef = db.collection("UserProfile")
                                            .document(userId)
                                            .collection("contacts");
        contactsRef.addSnapshotListener(listener);
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
	
	// Save Messages in Firestore for Both Directions
    public static void saveMessages(String userId, String contactId, String messageContent) {
        Firestore db = FirestoreClient.getFirestore();

        // Create message data
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("content", messageContent);
        messageData.put("senderId", userId);
        messageData.put("timestamp", FieldValue.serverTimestamp());

        // Save message in both userId_contactId and contactId_userId
        saveMessageInChat(userId + "_" + contactId, messageData);
        saveMessageInChat(contactId + "_" + userId, messageData);
    }

    // Helper Method to Save Message in a Specific Chat
    private static void saveMessageInChat(String chatId, Map<String, Object> messageData) {
        CollectionReference messagesRef = db.collection(CHATS_COLLECTION).document(chatId).collection("messages");
        ApiFuture<DocumentReference> writeResult = messagesRef.add(messageData);
        try {
            DocumentReference documentReference = writeResult.get();
            System.out.println("Message stored in chat " + chatId + " with document ID: " + documentReference.getId());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error saving message in chat " + chatId + ": " + e.getMessage());
        }
    }

    // Get Chat History from Firestore
    public static List<Map<String, Object>> getChatHistory(String userId, String contactId) {
        Firestore db = FirestoreClient.getFirestore();
        List<Map<String, Object>> messages = new ArrayList<>();

        // Combine chatId and reverseChatId for bidirectional history
        String chatId = userId + "_" + contactId;
        String reverseChatId = contactId + "_" + userId;

        // Fetch messages from both directions
        messages.addAll(fetchMessagesFromChat(chatId));
        messages.addAll(fetchMessagesFromChat(reverseChatId));

        // Sort messages by timestamp to maintain chronological order
        messages.sort((m1, m2) -> {
            Timestamp t1 = (Timestamp) m1.get("timestamp");
            Timestamp t2 = (Timestamp) m2.get("timestamp");

            // Handle null cases
            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return -1;
            if (t2 == null) return 1;

            return t1.compareTo(t2);
        });

        return messages;
    }



    // Helper Method to Fetch Messages from a Specific Chat
    private static List<Map<String, Object>> fetchMessagesFromChat(String chatId) {
        CollectionReference messagesRef = db.collection("chats").document(chatId).collection("messages");
        ApiFuture<QuerySnapshot> future = messagesRef.get();

        List<Map<String, Object>> messages = new ArrayList<>();
        try {
            QuerySnapshot messagesSnapshot = future.get();
            for (DocumentSnapshot document : messagesSnapshot.getDocuments()) {
                String content = document.getString("content");
                String senderId = document.getString("senderId");
                Timestamp timestamp = document.getTimestamp("timestamp"); // Fetch as Timestamp

                Map<String, Object> messageData = new HashMap<>();
                messageData.put("content", content);
                messageData.put("senderId", senderId);
                messageData.put("timestamp", timestamp); // Store as Timestamp in the map

                messages.add(messageData);
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error fetching messages from chat " + chatId + ": " + e.getMessage());
        }

        return messages;
    }

    public static List<UserProfile> getAllUsers() {
        Firestore db = FirestoreClient.getFirestore();
        List<UserProfile> users = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot document : documents) {
                UserProfile user = document.toObject(UserProfile.class);
                users.add(user);
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }
	
	/** Kennie
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

    public static String getUserIdByEmail(String email) {
        Firestore db = FirestoreClient.getFirestore(); // Get Firestore instance
        CollectionReference users = db.collection(COLLECTION_NAME); // Reference to the "UserProfile" collection

        try {
            // Query Firestore to find a document with the given email
            ApiFuture<QuerySnapshot> future = users.whereEqualTo("email", email).get();
            QuerySnapshot snapshot = future.get(); // Get the query results

            // If a user is found, return their userId
            if (!snapshot.isEmpty()) {
                DocumentSnapshot document = snapshot.getDocuments().get(0); // Get the first matching document
                return document.getId(); // Return the document ID (userId)
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error fetching user ID by email: " + e.getMessage());
        }

        return null; // Return null if no user is found or an error occurs
    }
    
    public static UserProfile findUserByEmail(String email) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference users = db.collection(COLLECTION_NAME);

        try {
            // Search by email
            ApiFuture<QuerySnapshot> future = users.whereEqualTo("email", email).get();
            QuerySnapshot snapshot = future.get();
            if (!snapshot.isEmpty()) {
                DocumentSnapshot document = snapshot.getDocuments().get(0);
                return document.toObject(UserProfile.class); // Return the full UserProfile object
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }

        return null; // Return null if user is not found
    }
    public static ListenerRegistration addChatListener(String chatId, EventListener<QuerySnapshot> listener) {
        // Get the reference to the messages collection for the chat
        CollectionReference messagesRef = db.collection("chats").document(chatId).collection("messages");

        // Attach a snapshot listener to the collection (ordered by timestamp)
        return messagesRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(listener);
    }



	
}
