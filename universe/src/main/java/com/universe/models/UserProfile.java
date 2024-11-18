package com.universe.models;

import java.util.List;

public class UserProfile {
	private String userId;
	private String username;
	private String email;
	private String passwordHash;
	private String bio;
	private String dateOfBirth;
	private String province;
	private String university;
	private List<String> interests;

	public UserProfile() {
	}

	public UserProfile(String userId, String username, String email, String passwordHash) {
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
	}

	public UserProfile(String userId, String username, String email, String bio, String dateOfBirth, String province,
			String university, List<String> interests) {
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.bio = bio;
		this.dateOfBirth = dateOfBirth;
		this.province = province;
		this.university = university;
		this.interests = interests;
	}

	// Constructor for Full Profile
	public UserProfile(String userId, String username, String email, String bio, String dateOfBirth, String province,
			String university, List<String> interests, String passwordHash) {
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.bio = bio;
		this.dateOfBirth = dateOfBirth;
		this.province = province;
		this.university = university;
		this.interests = interests;
		this.passwordHash = passwordHash;
	}

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

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getUniversity() {
		return university;
	}

	public void setUniversity(String university) {
		this.university = university;
	}

	public List<String> getInterests() {
		return interests;
	}

	public void setInterests(List<String> interests) {
		this.interests = interests;
	}
	
	@Override
	public String toString() {
	    return "UserProfile{" +
	            "userId='" + userId + '\'' +
	            ", username='" + username + '\'' +
	            ", email='" + email + '\'' +
	            ", passwordHash='" + passwordHash + '\'' +
	            ", bio='" + bio + '\'' +
	            ", dateOfBirth='" + dateOfBirth + '\'' +
	            ", province='" + province + '\'' +
	            ", university='" + university + '\'' +
	            ", interests=" + interests +
	            '}';
	}

	