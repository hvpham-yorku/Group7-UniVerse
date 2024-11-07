package com.universe;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import java.util.concurrent.ExecutionException;
import com.universe.models.UserProfile;

public class FirestoreHandler {

    private static final String COLLECTION_NAME = "users";

    // Add a new user to Firestore
    public static void addUserData(UserProfile user) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference users = db.collection(COLLECTION_NAME);
        ApiFuture<WriteResult> writeResult = users.document(user.getUserId()).set(user);
        try {
            System.out.println("Add user timestamp: " + writeResult.get().getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Get user by ID
    public static void getUserData(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(userId);
        ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = docRef.get();

        try {
            com.google.cloud.firestore.DocumentSnapshot document = future.get();
            if (document.exists()) {
                UserProfile user = document.toObject(UserProfile.class);
                System.out.println("User Data: " + user);
            } else {
                System.out.println("No such document!");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Delete user by ID
    public static void deleteUserData(String userId) {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(userId);
        ApiFuture<WriteResult> writeResult = docRef.delete();
        try {
            System.out.println("User deleted at: " + writeResult.get().getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
