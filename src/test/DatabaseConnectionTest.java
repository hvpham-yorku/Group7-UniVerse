package test;

import java.sql.Connection;
import java.sql.SQLException;
import database.DatabaseConnection;

public class DatabaseConnectionTest {
	public static void main(String[] args) {
		try (Connection connection = DatabaseConnection.connect()) {
			System.out.println("Successful!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
