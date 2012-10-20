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
	

	private ResultSet performQuery(String query) {
		return null;
	}
	
}
