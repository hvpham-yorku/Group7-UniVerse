package com.universe.models;

public class UserProfile {
	private String userId;
	private String username;
	private String email;
	private String bio;

	// Constructor

	//hmm
	public UserProfile() {
	}

	public UserProfile(String userId, String username, String email, String bio) {
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.bio = bio;
	}

	// Getters and Setters
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	@Override
	public String toString() {
		return "UserProfile{" + "userId='" + userId + '\'' + ", username='" + username + '\'' + ", email='" + email
				+ '\'' + ", bio='" + bio + '\'' + '}';
	}
}
