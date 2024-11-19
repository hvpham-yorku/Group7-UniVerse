package com.universe;

import com.universe.gui.SignUporIn;

public class UniVerse {
	public static void main(String[] args) {
		// Initialize Firebase
		FirebaseInitializer.initializeFirebase();

		// Launch the SignUporIn GUI
		SignUporIn.main(args);
	}
}
