package server.dataAccess;

import server.ServerException;
import shared.dataTransfer.*;
import java.sql.*;
import java.util.*;
import java.io.*;

/**
 * Accesses a database object, drawing data from and inserting data into it.
 * If I were to re-write the program from scratch, I would merge it with
 * Database's functionality.
 * @author Martin
 *
 */
public class DataAccess {
	
	// Directly accesses the SQL database.
	// Returns dataTransfer objects to the server.
	
	private String dbName;
	private Database db;
	
	private Connection connection;
	
	public Connection getConnection() throws IOException {
		if (connection != null) {
			return connection;
		} else {
			throw new IOException("No open connection!");
		}
	}
	
	private static final String DATABASE_SCHEMA_LOCATION = "database-schema.txt";
	
	/**
	 * Constructor
	 * @param databaseName
	 * @throws ServerException
	 */
	public DataAccess(String databaseName) throws ServerException {
		if (!Database.isInitialized()) {
			Database.initialize();
		}
		this.dbName = databaseName;
		db = new Database(dbName); // may throw a ServerException
	}
	
	/**
	 * Starts a transaction with the database.
	 */
	public void startTransaction() {
		if (db != null) {
			if (db.getConnection() != null) {
				System.out.println("Connection already open!");
			}
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
			ps = getConnection().prepareStatement(statement);
			ps.setString(1, username);
			ps.setString(2, password);
			rs = ps.executeQuery();
			if (rs != null && rs.next()) {
				result = buildUser(rs, username, password);
			} else {
				String na = "invalid";
				result = new User(-1, na, na, na, na, 0, na); // invalid username/password
			}
		} catch (Exception e) {
			System.out.println("Exception while trying to get a user.");
			System.out.println(e.getMessage());
		} finally {
			closeQuery(rs, ps);
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
			ps = getConnection().prepareStatement(statement);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(buildProject(rs));
			}
		} catch (Exception e) {
			System.out.println("Exception while getting the project list.");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		closeQuery(rs, ps);
		
		return result;
	}
	
	/**
	 * Gets the url of a sample image from the given project.
	 * @param projectID
	 * @return URL string of the image of the first batch in the project
	 */
	public Image getSampleImage(int projectID) 
			throws SQLException, ServerException {
		Image result = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String statement = "SELECT * FROM batches WHERE project_id = ?";
		try {
			ps = getConnection().prepareStatement(statement);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage() + " - " + connection);
		}
		ps.setInt(1, projectID);
		rs = ps.executeQuery();
		
		if (rs != null && rs.next()) {
			result = new Image(projectID, rs.getString("filename"));
		} else {
			result = null; // no sample image available
			// Either no batches in project, or invalid project id
		}
		closeQuery(rs, ps);
			
		return result;
	}
	
	/**
	 * Gets the first unfinished, available batch for that project.
	 * @param projectID
	 * @return
	 */
	public Batch getNextBatch(int projectID, String username) throws SQLException, ServerException {
		Batch result = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String statement = "SELECT * FROM batches WHERE project_id = ? AND " +
				"completed = 0 AND in_use = 0";
		if (userHasBatchAlready(username)) { return null; }
		ps = connection.prepareStatement(statement);
		ps.setInt(1, projectID);
		rs = ps.executeQuery();
		if (rs.next()) {
			result = buildBatch(rs); // get first available batch
			rs.close();
			// then mark that batch as in_use.
			statement = "UPDATE batches SET in_use = ? WHERE id = ?";
			ps = db.getConnection().prepareStatement(statement);
			ps.setString(1, username);
			ps.setInt(2, result.getID());
			ps.executeUpdate();
		} else {
			result = null; // no batches available
		}
		closeQuery(rs, ps);
		return result;
	}
	
	/**
	 * Marks every batch in the database as in_use = 0 (false), used to
	 * reset batches that have been marked as in-use.
	 */
	public void markBatchesNotInUse() {
		if (connection != null) {
			endTransaction(false);
			System.out.println("WARNING: Had to close an open connection while marking" +
					"all batches as in-use = false.");
		}
		startTransaction();
		String statement = "UPDATE batches SET in_use = 0";
		try {
			getConnection().prepareStatement(statement).execute();
		} catch (Exception e) {
			System.out.println("Error while marking batchs as not in use:");
			System.out.println(e.getMessage());
		}
		endTransaction(true);
	}
	
	/**
	 * Gets a specific batch from the database, usually for use in a search.
	 * @param batchID
	 * @return
	 * @throws SQLException
	 * @throws ServerException
	 */
	public Batch getSpecificBatch(int batchID) throws SQLException, ServerException {
		Batch result = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String statement = "SELECT * FROM batches WHERE id = ?";

		ps = connection.prepareStatement(statement);
		ps.setInt(1, batchID);
		rs = ps.executeQuery();
		if (rs.next()) {
			result = buildBatch(rs); // get first available batch
		} else {
			result = null; // no batches available
		}
		closeQuery(rs, ps);
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
		try {
			ps = getConnection().prepareStatement(statement);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		if (completed) { ps.setInt(1, 1); } // completed
		else { ps.setInt(1, 0); } // not completed
		ps.setInt(2, input.getID());
		ps.execute();
		
		closeQuery(null, ps);
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
			if (!saveRecord(inputList.get(i))) {
				result = false; // if even one record failed to save, return false.
			}
		}
		
		return result;
	}
	
	/**
	 * Wipes the database's contents, usually in preparation to import data from a
	 * separate file. Loads the database-schema.txt file and uses it as a SQL
	 * command to drop and create the tables used by the server.
	 * @param commit Whether to automatically commit the wipe.
	 * @return Whether the wipe was successful. Returns false if confirm is false.
	 */
	public boolean wipeDatabase(boolean commit) {
		File schemaFile = new File(DATABASE_SCHEMA_LOCATION);
		PreparedStatement ps = null;
		Scanner in = null;
		try { // load in the schema file
			in = new Scanner(schemaFile);
			StringBuilder sb = new StringBuilder();
			while (in.hasNext()) { // convert it all into one string
				sb.append(in.nextLine());
				sb.append(" ");
				if (sb.toString().contains(";")) {
					// convert the string to a prepared SQL statement
					ps = getConnection().prepareStatement(sb.toString());
					ps.execute(); // execute it.
					ps.close();
					ps = null;
					sb = new StringBuilder(); // clear the string builder.
				}
			}
			if (commit) { getConnection().commit(); }
			
		} catch (Exception e) {
			System.out.println("Exception while wiping database.");
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			closeQuery(null, ps);
			in.close();
		}
		return true;
	}
	
	/**
	 * Adds a User to the database.
	 * @param user The User to add to the database.
	 * @return
	 */
	
	public boolean addUser(User user) {
		if (user == null) { return false; }
		String statement;
		boolean IDalreadyExists = IDexists(user.getID(), "users");
		if(userExists(user.getUsername(), user.getEmail())) {
			return false; // user already exists, or email already in use.
			// Cannot have two users with the same username and/or email address.
		}
		boolean result = true;
		if (IDalreadyExists) {
			statement = "INSERT INTO users (username, password, firstname," +
					" lastname, email, indexed_records) VALUES (?, ?, ?, ?, ?, ?)";
		} else {
			statement = "INSERT INTO users" +
				"(id, username, password, firstname, lastname, email, indexed_records) " +
				"VALUES (" + user.getID() + ", ?, ?, ?, ?, ?, ?)";
		}
		
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement(statement);
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ps.setString(3, user.getFirstName());
			ps.setString(4, user.getLastName());
			ps.setString(5, user.getEmail());
			ps.setInt(6, user.getNumIndexedRecords());
			ps.execute();
		} catch (Exception e) {
			System.out.println("Exception while adding a user.");
			System.out.println(e.getMessage());
		} finally {
			closeQuery(null, ps);
		}
		
		return result;
	}
	
	/**
	 * Adds a Batch to the database.
	 * @param batch The batch to add to the database.
	 * @param completed Has the batch been completed? Set to true if it
	 * has any records attached to it.
	 * @return
	 */
	public boolean addBatch(Batch batch) {
		String statement;
		boolean IDalreadyExists = IDexists(batch.getID(), "batches");
		boolean result = false;
		if (IDalreadyExists) {
			statement = "INSERT INTO batches (project_id, filename, " +
					"completed, in_use) VALUES (?, ?, ?, ?)";
		} else {
			statement = "INSERT INTO batches" +
				"(id, project_id, filename, completed, in_use) " +
				"VALUES (" + batch.getID() + ", ?, ?, ?, ?)";
		}
		
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement(statement);
			ps.setInt(1, batch.getProjectID());
			ps.setString(2, batch.getImage().getFilename());
			if (batch.isCompleted()) {
				ps.setInt(3, 1); // 1 means completed.
			} else {
				ps.setInt(3, 0); // 0 means not completed.
			}
			ps.setString(4, batch.getUsername());
			result = ps.execute();
		} catch (Exception e) {
			System.out.println("Exception while adding a batch.");
			System.out.println(e.getMessage());
		} finally {
			closeQuery(null, ps);
		}
		
		return result;
	}
	
	/**
	 * Adds a Project to the database. You will need to add the project
	 * before adding any fields or batches assigned to it.
	 * @param project
	 * @return
	 */
	public boolean addProject(Project project) {
		String statement;
		boolean IDalreadyExists = IDexists(project.getID(), "projects");
		boolean result = false;
		if (IDalreadyExists) {
			statement = "INSERT INTO projects (title, records_per_image, first_y_coord," +
					" field_height) VALUES (?, ?, ?, ?)";
		} else {
			statement = "INSERT INTO projects" +
				"(id, title, records_per_image, first_y_coord, field_height) " +
				"VALUES (" + project.getID() + ", ?, ?, ?, ?)";
		}
		
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement(statement);
			ps.setString(1, project.getTitle());
			ps.setInt(2, project.getRecordsPerImage());
			ps.setInt(3, project.getY(0));
			ps.setInt(4, project.getRowHeight());
			result = ps.execute();
		} catch (Exception e) {
			System.out.println("Exception while adding a project.");
			System.out.println(e.getMessage());
		} finally {
			closeQuery(null, ps);
		}
		
		return result;
	}
	
	/**
	 * Adds a Field to the database.
	 * @param field
	 * @return
	 */
	public boolean addField(Field field) {
		String statement;
		boolean IDalreadyExists = IDexists(field.getID(), "fields");
		boolean result = false;
		if (IDalreadyExists) {
			statement = "INSERT INTO fields (project_id, title, x_coord," +
					" width, help_html, known_data) VALUES (?, ?, ?, ?, ?, ?)";
		} else {
			statement = "INSERT INTO fields" +
				"(id, project_id, title, x_coord, width, help_html, known_data) " +
				"VALUES (" + field.getID() + ", ?, ?, ?, ?, ?, ?)";
		}
		
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement(statement);
			ps.setInt(1, field.getProjectID());
			ps.setString(2, field.getTitle());
			ps.setInt(3, field.getXCoord());
			ps.setInt(4, field.getWidth());
			ps.setString(5, field.getHelpHtmlLoc());
			ps.setString(6, field.getKnownDataLoc());
			result = ps.execute();
		} catch (Exception e) {
			System.out.println("Exception while adding a field.");
			System.out.println("\t" + statement);
			System.out.println("\t" + e.getMessage());
		} finally {
			closeQuery(null, ps);
		}
		
		return result;
	}
	
	/**
	 * Gets a Record if one was in the database, or creates a new, empty
	 * one if none was found in the database.
	 * @param batch
	 * @param rowNumber
	 * @param fieldID
	 * @return
	 */
	public Record getRecord(int batchID, int rowNumber, int fieldID) {
		String statement = "SELECT * FROM records WHERE " +
			"batch_id = ? AND field_id = ? AND row_number = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		Record result = null;
		
		try {
			ps = getConnection().prepareStatement(statement);
			ps.setInt(1, batchID);
			ps.setInt(2, fieldID);
			ps.setInt(3, rowNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = buildRecord(rs);
			} // else result is null; no matching record found.
		} catch (Exception e) {
			System.out.println("Error at getRecord(): " + e.getMessage());
		} finally {
			closeQuery(rs, ps);
		}
		
		return result;
	}
	
	/**
	 * Searches for a search value within a specific field.
	 * @param fieldID
	 * @param searchValue
	 * @return
	 */
	public List<Record> search(int fieldID, String searchValue) {
		String statement = "SELECT * FROM records WHERE field_id = ?";
		searchValue = searchValue.toLowerCase();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Record> output = new ArrayList<Record>();
		
		try {
			ps = getConnection().prepareStatement(statement);
			ps.setInt(1, fieldID);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				String value = rs.getString("value");
				value = value.toLowerCase();
				if (value.equalsIgnoreCase(searchValue)) { // value.contains(searchCalue)
					Record r = buildRecord(rs);
					output.add(r);
				}
			}
		} catch (Exception e) {
			System.out.println("Exception during Search: " + e.getMessage());
		} finally {
			closeQuery(rs, ps);
		}
		
		return output;
	}
	
	/**
	 * Gets all the fields associated with this project.
	 * @param projectID
	 * @return
	 */
	public List<Field> getFields(int projectID) {
		String statement = "SELECT * FROM fields WHERE project_id = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Field> output = new ArrayList<Field>();
		
		try {
			ps = getConnection().prepareStatement(statement);
			ps.setInt(1, projectID);
			rs = ps.executeQuery();
			while (rs != null && rs.next()) {
				Field f = buildField(rs);
				output.add(f);
			}
		} catch (Exception e) {
			System.out.println("Exception during getFields(): " + e.getMessage());
		} finally {
			closeQuery(rs, ps);
		}
		
		return output;
	}
	
	/**
	 * Indicates whether an open connection exists.
	 * @return
	 */
	public boolean isConnectionOpen() {
		return connection != null;
	}
	
	private boolean userHasBatchAlready(String username) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			String statement = "SELECT * FROM batches WHERE in_use = ?";
			ps = connection.prepareStatement(statement);
			ps.setString(1, username);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = true; // user already has a batch.
			} else {
				result = false; // user has no batch and may receive a new one.
			}
			
		} catch (Exception e) {
			System.out.println("Error while verifying that user does not have a batch already");
			e.printStackTrace();
		} finally {
			closeQuery(rs, ps);
		}
		return result;
	}
	
	/**
	 * Takes in a Record and saves it to the database. Called exclusively by 
	 * saveSeveralRecords().
	 * @param input Individual Record to be saved to the database
	 * @return
	 */
	private boolean saveRecord(Record input) throws SQLException {
		PreparedStatement ps = null;
		String statement = null;
		
		if (recordExists(input)) { // Update it
			statement = "UPDATE records SET value = ? " +
				"WHERE batch_id = ? AND field_id = ? AND row_number = ?";
		} else { // Create it
			statement = "INSERT INTO records (value, batch_id," +
				"field_id, row_number) VALUES (?, ?, ?, ?);";
		}
		try {
			ps = getConnection().prepareStatement(statement);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		ps.setString(1, input.getValue());
		ps.setInt(2, input.getBatchID());
		ps.setInt(3, input.getFieldID());
		ps.setInt(4, input.getRowNumber());
		
		ps.execute();
		
		closeQuery(null, ps);
		return true;
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
	
	/**
	 * Builds a Project object from a valid, successful ResultSet.
	 * @param rs
	 * @return
	 */
	private Project buildProject (ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String title = rs.getString("title");
		int numRows = rs.getInt("records_per_image");
		int firstYCoord = rs.getInt("first_y_coord");
		int fieldHeight = rs.getInt("field_height");
		return new Project(id, firstYCoord, fieldHeight, numRows, title);
	}
	
	/**
	 * Builds a Batch object from a valid, successful ResultSet. 
	 * @param rs
	 * @return
	 */
	private Batch buildBatch (ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String username = rs.getString("in_use");
		int projectID = rs.getInt("project_id");
		String imageFilename = rs.getString("filename");
		return new Batch(id, projectID, imageFilename, username);
	}
	
	/**
	 * Builds a Record object from a valid, successful ResultSet. 
	 * @param rs
	 * @return
	 */
	private Record buildRecord (ResultSet rs) throws SQLException {
		if (rs == null) { return null; }
		int id = rs.getInt("id");
		int batchID = rs.getInt("batch_id");
		int fieldID = rs.getInt("field_id");
		int rowNum = rs.getInt("row_number");
		String value = rs.getString("value");
		return new Record(id, batchID, fieldID, rowNum, value);
	}
	
	/**
	 * Builds a Field object from a valid, successful ResultSet. 
	 * @param rs
	 * @return
	 */
	private Field buildField(ResultSet rs) throws SQLException {
		if (rs == null) { return null; }
		int id = rs.getInt("id");
		int projectID = rs.getInt("project_id");
		String title = rs.getString("title");
		int xCoord = rs.getInt("x_coord");
		int width = rs.getInt("width");
		String helpHtmlLoc = rs.getString("help_html");
		String knownData = rs.getString("known_data");
		return new Field(id, projectID, title, xCoord, width, helpHtmlLoc, knownData);
	}
	
	/**
	 * Checks whether a Record exists in the database.
	 * @param input
	 * @return
	 * @throws SQLException
	 */
	private boolean recordExists(Record input) throws SQLException {
		String selectStatement = "SELECT * FROM records " +
				"WHERE batch_id = ? AND field_id = ? AND row_number = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try { 
			ps = getConnection().prepareStatement(selectStatement);
			ps.setInt(1, input.getBatchID());
			ps.setInt(2, input.getFieldID());
			ps.setInt(3, input.getRowNumber());
			
			rs = ps.executeQuery();
			result = rs.next();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		if (rs != null) { rs.close(); }
		if (ps != null) { ps.close(); }
		return result;
	}
	
	/**
	 * Checks whether a given ID in a given table is available for use.
	 * @param ID
	 * @param table
	 * @return
	 */
	private boolean IDexists(int ID, String table) {
		String selectStatement = "SELECT * FROM " + table + " WHERE id = ?;";
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			ps = getConnection().prepareStatement(selectStatement);
			ps.setInt(1, ID);
			
			rs = ps.executeQuery();
			result = (rs != null && rs.next());
		} catch (Exception e) {
			System.out.println("Exception while checking if an element exists with that ID");
			System.out.println("\t" + table + " - " + selectStatement);
			System.out.println("\t" + e.getMessage());
		} finally {
			closeQuery(rs, ps);
		}
		return result;
	}
	
	/**
	 * Checks whether either of a username and e-mail already exist in the database.
	 * If either exists, this will return false, and prevent creating a new user.
	 * @param username Username to check for
	 * @param email Email to check for
	 * @return
	 */
	private boolean userExists(String username, String email) {
		String statement = "SELECT username, email FROM users " +
				"WHERE username=? OR email=?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			ps = getConnection().prepareStatement(statement);
			ps.setString(1, username);
			ps.setString(2, email);
			
			rs = ps.executeQuery();
			result = rs.next();
		} catch (Exception e) {
			System.out.println("Exception while checking if a user exists with given username or email");
			System.out.println("\t" + e.getMessage());
		} finally {
			closeQuery(rs, ps);
		}
		return result;
	}
	
	/**
	 * Closes a ResultSet and a PreparedStatement, which most of the queries
	 * have at least one of.
	 * @param rs
	 * @param ps
	 */
	private void closeQuery(ResultSet rs, PreparedStatement ps) {
		try {
			if (rs != null) { rs.close(); }
			if (ps != null) { ps.close(); }
		} catch (SQLException e) {
			System.out.println("Failed to close. " + e.getMessage());
			e.printStackTrace();
		}
	}
	
}
