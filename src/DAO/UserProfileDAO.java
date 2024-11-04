package DAO;

import database.DatabaseConnection;
import models.UserProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserProfileDAO {

	public int createUser(UserProfile user) {
		String sql = "INSERT INTO users (username, email, password_hash, bio) VALUES (?, ?, ?, ?)";
		try (Connection connection = DatabaseConnection.connect();
				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getPasswordHash());
			statement.setString(4, user.getBio());
			statement.executeUpdate();

			ResultSet keys = statement.getGeneratedKeys();
			if (keys.next()) {
				int generatedId = keys.getInt(1);
				user.setUserId(generatedId);
				return generatedId;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public UserProfile getUserById(int userId) {
		String sql = "SELECT * FROM users WHERE user_id = ?";
		try (Connection connection = DatabaseConnection.connect();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userId);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return new UserProfile(rs.getInt("user_id"), rs.getString("username"), rs.getString("email"),
						rs.getString("password_hash"), rs.getString("bio"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void updateUser(UserProfile user) {
		String sql = "UPDATE users SET username = ?, email = ?, bio = ? WHERE user_id = ?";
		try (Connection connection = DatabaseConnection.connect();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, user.getUsername());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getBio());
			statement.setInt(4, user.getUserId());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteUser(int userId) {
		String sql = "DELETE FROM users WHERE user_id = ?";
		try (Connection connection = DatabaseConnection.connect();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, userId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
