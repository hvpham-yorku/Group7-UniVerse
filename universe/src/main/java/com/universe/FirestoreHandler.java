package com.universe;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.universe.models.UserProfile;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class FirestoreHandler {

	private static final String COLLECTION_NAME = "UserProfile";

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
}
