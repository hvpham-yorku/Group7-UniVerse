package com.universe;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class FirebaseInitializer {
    private static boolean initialized = false; 
    private static Firestore firestoreInstance; 

    public static void initializeFirebase() {
        if (!initialized) {
            try {
            	String serviceAccountPath = System.getenv("FIREBASE_KEY_PATH");
            	if (serviceAccountPath == null || serviceAccountPath.isEmpty()) {
            	    throw new RuntimeException("Environment variable FIREBASE_KEY_PATH is not set.");
            	}

                // Load the service account JSON file
                FileInputStream serviceAccount = new FileInputStream(serviceAccountPath);

                // Create FirebaseOptions with credentials and database URL
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://universeapp-g7.firebaseio.com")
                        .build();

                // Initialize Firebase with the options
                FirebaseApp.initializeApp(options);
                firestoreInstance = FirestoreClient.getFirestore();
                initialized = true; // Mark Firebase as initialized
                System.out.println("Firebase has been initialized successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to initialize Firebase");
            }
        }
    }

    public static Firestore getFirestoreInstance() {
        if (!initialized) {
            throw new IllegalStateException("Firebase is not initialized. Call initializeFirebase() first.");
        }
        return firestoreInstance;
    }
}