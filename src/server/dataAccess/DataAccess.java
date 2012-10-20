package server.dataAccess;

import server.ServerException;
import shared.dataTransfer.*;
import java.sql.*;

public class DataAccess {
	
	// Directly accesses the SQL database.
	// Returns dataTransfer objects to the server.
	
	private String dbName;
	private Database db;
	
	public DataAccess(String databaseName) {
		this.dbName = databaseName;
		try {
			db = new Database(dbName);
		} catch (ServerException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public User validateUser(String username, String password) {
		return null;
	}
	
	/**
	 * Performs a query on the database.
	 * @param query The query to perform.
	 * @return ResultSet from the query. This must be closed once you're done using it.
	 */
	private ResultSet performQuery(String query) {
		assert db != null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			statement = db.getConnection().prepareStatement(query);
			result = statement.executeQuery();
		} catch (SQLException e) {
			System.out.println("Exception while performing a query.");
			System.out.println(e.getMessage());
		} finally {
			try {
				if (statement != null) { statement.close(); }
			} catch (SQLException e) {
				System.out.println("Well, crap. Exception while closing statement.");
			}
		}
		return result;
	}
	
}
