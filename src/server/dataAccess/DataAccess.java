package server.dataAccess;

import server.ServerException;
import shared.dataTransfer.*;
import java.sql.*;
import java.util.*;

public class DataAccess {
	
	// Directly accesses the SQL database.
	// Returns dataTransfer objects to the server.
	
	private String dbName;
	private Database db;
	
	private Connection connection;
	
	/**
	 * Constructor
	 * @param databaseName
	 * @throws ServerException
	 */
	public DataAccess(String databaseName) throws ServerException {
		this.dbName = databaseName;
		db = new Database(dbName); // may throw a ServerException
	}
	
	/**
	 * Starts a transaction with the database.
	 */
	public void startTransaction() {
		if (db != null) {
			try {
				db.startTransaction();
				connection = db.getConnection();
			} catch (Exception e) {
				System.out.println("Start transaction error:");
				System.out.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Ends the current transaction with the database.
	 * @param commit Whether to commit or not.
	 */
	public void endTransaction(boolean commit) {
		try {
			db.endTransaction(commit);
			connection = null;
		} catch (Exception e) {
			System.out.println("End transaction error:");
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Gets a user from the database by username and password.
	 * @param username
	 * @param password
	 * @return
	 */
	public User getUser(String username, String password) {
		User result = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String statement = "SELECT * FROM users WHERE username = ? AND password = ?";
		try {
			ps = connection.prepareStatement(statement);
			ps.setString(1, username);
			ps.setString(2, password);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = buildUser(rs, username, password);
			} else {
				result = null; // invalid username/password
			}
		} catch (Exception e) {
			System.out.println("Exception while trying to get a user.");
			System.out.println(e.getMessage());
		} finally {
			try {
				if (ps != null) { ps.close(); }
				if (rs != null) { rs.close(); }
			} catch (SQLException e) { System.out.println("Failed to close"); }
		}
		return result;
	}
	
	/**
	 * Gets a list of all the available projects.
	 * @return
	 */
	public List<Project> getProjectList() {
		List<Project> result = new ArrayList<Project>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String statement = "SELECT * FROM projects";
		try {
			ps = connection.prepareStatement(statement);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(buildProject(rs));
			}
		} catch (Exception e) {
			System.out.println("Exception while getting the project list.");
		}
		
		return result;
	}
	
	/**
	 * Gets the url of a sample image from the given project.
	 * @param projectID
	 * @return URL string of the image of the first batch in the project
	 */
	public String getSampleImageLocation(int projectID) 
			throws SQLException, ServerException {
		String result = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String statement = "SELECT * FROM batches WHERE project_id = ?";
		
		ps = connection.prepareStatement(statement);
		ps.setInt(1, projectID);
		rs = ps.executeQuery();
		if (rs.next()) {
			result = rs.getString("filename");
		} else {
			result = null; // no sample image available
			// Either no batches in project, or invalid project id
		}
	
		if (ps != null) { ps.close(); }
		if (rs != null) { rs.close(); }
			
		return result;
	}
	
	/**
	 * Gets the first unfinished, available batch for that project.
	 * @param projectID
	 * @return
	 */
	public Batch getNextBatch(int projectID) throws SQLException, ServerException {
		Batch result = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String statement = "SELECT * FROM batches WHERE project_id = ? AND " +
				"completed = 0 AND in_use = 0";

		ps = connection.prepareStatement(statement);
		ps.setInt(1, projectID);
		rs = ps.executeQuery();
		if (rs.next()) {
			result = buildBatch(rs); // get first available batch
			rs.close();
			// then mark that batch as in_use.
			statement = "UPDATE batches SET in_use = 1 WHERE id = ?";
			ps = db.getConnection().prepareStatement(statement);
			ps.setInt(1, result.getID());
			ps.executeUpdate();
		} else {
			result = null; // no batches available
		}
		if (ps != null) { ps.close(); }
		if (rs != null) { rs.close(); }
		return result;
	}
	
	/**
	 * Takes in a Batch and saves it to the database.
	 * @param input
	 * @param completed Whether to mark the batch as completed.
	 * @return
	 */
	public boolean saveBatch(Batch input, boolean completed) 
			throws SQLException, ServerException {
		PreparedStatement ps = null;
		String statement = "UPDATE batches SET completed = ?, in_use = 0" +
				" WHERE id = ?";
		
		ps = connection.prepareStatement(statement);
		if (completed) { ps.setInt(1, 1); } // completed
		else { ps.setInt(1, 0); } // not completed
		ps.setInt(2, input.getID());
		ps.execute();
	
		if (ps != null) { ps.close(); }
		return true;
	}
	
	/**
	 * Saves a list of Records to the database.
	 * @param inputList List of Record objects to insert or replace.
	 * @return Whether the records were saved successfully.
	 * @throws SQLException
	 * @throws ServerException
	 */
	public boolean saveSeveralRecords(List<Record> inputList) 
			throws SQLException, ServerException {
		if (inputList == null) {
			throw new ServerException("Attempting to save a null list of Records");
		}
		boolean result = true;
		
		for (int i = 0; i < inputList.size(); i++) {
			if (!saveRecord(inputList.get(i), db.getConnection())) {
				result = false; // if even one record failed to save, return false.
			}
		}
		
		return result;
	}
	
	/**
	 * Takes in a Record and saves it to the database. Called exclusively by 
	 * saveSeveralRecords().
	 * @param input Individual Record to be saved to the database
	 * @param connection Connection created during an open transaction.
	 * @return
	 */
	private boolean saveRecord(Record input, Connection connection) 
			throws SQLException, ServerException {
		PreparedStatement ps = null;
		String statement = "INSERT OR REPLACE INTO records (id, batch_id," +
				"field_id, row_number, value) VALUES (?, ?, ?, ?, ?);";
		
		ps = connection.prepareStatement(statement);
		
		ps.setInt(1, input.getID());
		ps.setInt(2, input.getBatchID());
		ps.setInt(3, input.getFieldID());
		ps.setInt(4, input.getRowNumber());
		ps.setString(5, input.getValue());
		
		ps.execute();
		
		if (ps != null) { ps.close(); }
		return true;
	}
	
	/**
	 * Gets a Record if one was in the database, or creates a new, empty
	 * one if none was found in the database.
	 * @param batch
	 * @param rowNumber
	 * @param fieldID
	 * @return
	 */
	public Record getRecord(Batch batch, int rowNumber, int fieldID) {
		return null;
	}
	
	/**
	 * Searches for a search value within a specific field.
	 * @param fieldID
	 * @param searchValue
	 * @return
	 */
	public List<Record> search(int fieldID, String searchValue) {
		return null;
	}
	
	/**
	 * Gets all the fields associated with this project.
	 * @param projectID
	 * @return
	 */
	public List<Field> getFields(int projectID) {
		return null;
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
	
	private Project buildProject (ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String title = rs.getString("title");
		int numRows = rs.getInt("records_per_image");
		int firstYCoord = rs.getInt("first_y_coord");
		int fieldHeight = rs.getInt("field_height");
		return new Project(id, firstYCoord, fieldHeight, numRows, title);
	}
	
	private Batch buildBatch (ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		int projectID = rs.getInt("project_id");
		String imageFilename = rs.getString("filename");
		return new Batch(id, projectID, imageFilename);
	}
	
}
