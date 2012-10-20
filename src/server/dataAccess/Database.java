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
			initialized = true;
		}
		catch (ClassNotFoundException e) {
			// ERROR! Could not load database driver
		}
	}

//	private Contacts contacts;
	private Connection connection;
	private String location;
	
	public Database(String location) throws ServerException{
		if (!initialized) { initialize();}
		
		this.connection = null;
		this.location = location;
		assert location != null;
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
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + location);
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			//System.out.println(e.getMessage());
			throw new ServerException("Failed to make a connection to server at: "
					+ location + "\n" + e.getMessage());
		}
	}

	/**
	 * Performs a query on the database.
	 * @param query The query to perform.
	 * @return ResultSet from the query. This must be closed once you're done using it.
	 */
	public ResultSet performQuery(String query) {
		// This function may be superfluous.
		assert connection != null;
		PreparedStatement statement = null;
		ResultSet result = null;
		try {
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
		} catch (SQLException e) {
			System.out.println("Exception while performing a query.");
			System.out.println(e.getMessage());
		} finally {
			if (statement != null) {
//				 try { statement.close(); } catch (SQLException e) {
//					 System.out.println("Well, crap. Exception while closing statement.");
//				 }
			}
		}
		return result;
	}
	
	/**
	 * Commit or rollback the transaction and close the connection
	 * @param commit Whether to commit or rollback the transaction.
	 */
	public void endTransaction(boolean commit) {
		try {
			if (commit) {
				connection.commit();
			} else {
				connection.rollback();
			}
		} catch (SQLException e) {
			System.out.println("Error while committing or rolling back. Commit=" + commit);
		} finally {
			close();
		}
	}
	
	/**
	 * Closes the database connection. Generally endTransaction() should be used publicly,
	 * but close() can be used as a last-ditch effort.
	 */
	public void close() {
		try {   
			if (connection != null) {
				connection.close();
				connection = null;
			}
         } catch (SQLException e) {  
        	 e.printStackTrace();  
         }
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public String getLocation() {
		return location;
	}

	/**
	 * Changes the database location, and closes any open connection
	 * @param location Location of the new database.
	 */
	public void setLocation(String location) {
		if (connection != null) {
			close();
		}
		this.location = location;
	}
	
}
