package server.dataAccess;

import shared.dataTransfer.*;
import java.sql.*;

public class DataAccess {
	
	// Directly accesses the SQL database.
	// Returns dataTransfer objects to the server.
	
	private String dbName;
	
	public DataAccess(String databaseName) {
		this.dbName = databaseName;
		
		try {
			final String driver = "org.sqlite.JDBC";
			Class.forName(driver);
		}
		catch (ClassNotFoundException e) {
			// ERROR! Could not load database driver
		}
	}
	
	public User validateUser(String username, String password) {
		return null;
	}
	
	
	
}
