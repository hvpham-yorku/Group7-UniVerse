import java.sql.*;

public class test {
	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/uni_verse_db";
		String user = "root";
		String password = "Oromidayo@01";
		String showTablesQuery = "SHOW TABLES";

		try {
			// Create connection
			Connection con = DriverManager.getConnection(url, user, password);
			// Create statement for showing tables
			Statement showTablesStatement = con.createStatement();
			ResultSet tablesResult = showTablesStatement.executeQuery(showTablesQuery);

			while (tablesResult.next()) {
				String tableName = tablesResult.getString(1);
				System.out.println("Table: " + tableName);

				// Query each table's contents
				String tableQuery = "SELECT * FROM " + tableName;
				Statement tableStatement = con.createStatement();
				ResultSet tableResult = tableStatement.executeQuery(tableQuery);

				ResultSetMetaData metaData = tableResult.getMetaData();
				int columnCount = metaData.getColumnCount();

				// Print column headers
				for (int i = 1; i <= columnCount; i++) {
					System.out.print(metaData.getColumnName(i) + "\t");
				}
				System.out.println();

				// Print rows
				while (tableResult.next()) {
					for (int i = 1; i <= columnCount; i++) {
						System.out.print(tableResult.getString(i) + "\t");
					}
					System.out.println();
				}
				System.out.println();
				tableStatement.close();
			}
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}