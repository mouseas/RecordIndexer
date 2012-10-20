package server.dataAccess;

import server.ServerException;
import shared.dataTransfer.*;
import java.sql.*;

public class DataAccess {
	
	// Directly accesses the SQL database.
	// Returns dataTransfer objects to the server.
	
	private String dbName;
	private Database db;
	
	private static final String SELECT = "SELECT ";
	private static final String FROM = " FROM ";
	private static final String WHERE = " WHERE ";
	private static final String INSERT = "INSERT INTO ";
	private static final String VALUES = " VALUES ";
	private static final String AND = " AND ";
	
	public DataAccess(String databaseName) throws ServerException {
		this.dbName = databaseName;
		db = new Database(dbName); // may throw a ServerException
	}
	
	public User getUser(String username, String password) {
		User result = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String statement = "SELECT * FROM users WHERE username = ? AND password = ?";
		try {
			db.startTransaction();
			ps = db.getConnection().prepareStatement(statement);
			ps.setString(1, username);
			ps.setString(2, password);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = buildUser(rs, username, password);
			} else {
				// invalid username/password
			}
			db.endTransaction(false);
		} catch (Exception e) {
			System.out.println("Server or SQL Exception while trying to get a user.");
			System.out.println(e.getMessage());
		} finally {
			try {
				if (ps != null) { ps.close(); }
				if (rs != null) { rs.close(); }
			} catch (SQLException e) { /**/ }
		}
		return result;
	}
	
	/**
	 * Builds a User object from a valid, successful ResultSet. 
	 * Called exclusively by getUser()
	 * @param rs
	 * @return
	 */
	private User buildUser(ResultSet rs, String username, 
			String password) throws SQLException{
		User result = null;
		String firstname = rs.getString("firstname");
		String lastname = rs.getString("lastname");
		String email = rs.getString("email");
		int indexedRecords = rs.getInt("indexed_records");
		int id = rs.getInt("id");
		result = new User(id, username, firstname, lastname, email,
				indexedRecords, password);
		return result;
	}
	
	
}
