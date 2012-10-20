package server.dataAccess;

import java.sql.*;
import server.*;

public class Database {
	
	private static boolean initialized = false;
	
	/**
	 * Load the SQLite database driver
	 * @throws ServerException
	 */
	public static void initialize() throws ServerException {
		try {
			final String driver = "org.sqlite.JDBC";
			Class.forName(driver);
		}
		catch (ClassNotFoundException e) {
			// ERROR! Could not load database driver
		}
	}

//	private Contacts contacts;
	private Connection connection;
	private String location;
	
	public Database(String location) throws ServerException{
		if (!initialized) { initialize(); }
//		contacts = new Contacts(this);
		this.connection = null;
		this.location = location;
	}
	
	/**
	 * Gets the current database connection for external use.
	 * Is this a good idea?
	 * @return
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Open a connection to the database and start a transaction
	 * @throws ServerException
	 */
	public void startTransaction() throws ServerException {
		initConnection();
		
	}
	
	/**
	 * Commit or rollback the transaction and close the connection
	 * @param commit Whether to commit or rollback the transaction.
	 */
	public void endTransaction(boolean commit) {
		
		close();
	}

	/**
	 * Starts a database connection.
	 */
	private void initConnection() throws ServerException {
		try {
			connection = DriverManager.getConnection(location);
		} catch (SQLException e) {
			//System.out.println(e.getMessage());
			throw new ServerException("Failed to make a connection to server at: " + location);
		}
	}
	
	/**
	 * Closes a database connection.
	 */
	private void close() {
		try {   
			connection.close(); 
			connection = null;
         } catch (Exception e) {  
        	 e.printStackTrace();  
         }
	}
	
}
