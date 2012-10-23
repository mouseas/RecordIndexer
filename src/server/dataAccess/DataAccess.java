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
			db.startTransaction();
			ps = db.getConnection().prepareStatement(statement);
			ps.setString(1, username);
			ps.setString(2, password);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = buildUser(rs, username, password);
			} else {
				result = null; // invalid username/password
			}
			db.endTransaction(true);
		} catch (Exception e) {
			System.out.println("Exception while trying to get a user.");
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
	 * Gets a list of all the available projects.
	 * @return
	 */
	public List<Project> getProjectList() {
		List<Project> result = new ArrayList<Project>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String statement = "SELECT * FROM projects";
		try {
			db.startTransaction();
			ps = db.getConnection().prepareStatement(statement);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(buildProject(rs));
			}
		} catch (Exception e) {
			System.out.println("Exception while getting the project list.");
		} finally {
			db.endTransaction(false);
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
		
		db.startTransaction();
		ps = db.getConnection().prepareStatement(statement);
		ps.setInt(1, projectID);
		rs = ps.executeQuery();
		if (rs.next()) {
			Blob b = rs.getBlob("filename");
			byte[] bytes = b.getBytes(1, (int)b.length());
			result = Arrays.toString(bytes);
		} else {
			result = null; // no sample image available
			// Either no batches in project, or invalid project id
		}
		db.endTransaction(false);
	
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
		db.startTransaction();
		ps = db.getConnection().prepareStatement(statement);
		ps.setInt(1, projectID);
		rs = ps.executeQuery();
		if (rs.next()) {
			result = buildBatch(rs); // get first available batch
			// then mark that batch as in_use.
			statement = "UPDATE batches SET in_use = 1 WHERE id = ?";
			ps = db.getConnection().prepareStatement(statement);
			ps.setInt(1, result.getID());
			ps.executeQuery();
		} else {
			result = null; // no batches available
		}
		db.endTransaction(true);
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
		
		db.startTransaction();
		ps = db.getConnection().prepareStatement(statement);
		if (completed) { ps.setInt(1, 1); } // completed
		else { ps.setInt(1, 0); } // not completed
		ps.setInt(2, input.getID());
		ps.execute();
		db.endTransaction(true);
	
		if (ps != null) { ps.close(); }
		return false;
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
		db.startTransaction();
		boolean result = true;
		for (int i = 0; i < inputList.size(); i++) {
			if (!saveRecord(inputList.get(i), db.getConnection())) {
				result = false; // if even one record failed to save, return false.
			}
		}
		db.endTransaction(result); // rollback if any record save failed.
		
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
		String imageFilename = rs.getBlob("filename").toString();
		return new Batch(id, projectID, imageFilename);
	}
	
}
