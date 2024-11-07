package com.universe;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseInitializer {

	public static void initializeFirebase() {
		try {
			// Load the service account JSON file
			FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");

			// Create FirebaseOptions with credentials and database URL
			FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setDatabaseUrl("https://universeapp-g7.firebaseio.com")

					.build();

			// Initialize Firebase with the options
			FirebaseApp.initializeApp(options);
			System.out.println("Firebase has been initialized successfully.");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
