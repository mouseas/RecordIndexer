package client.serverCommunicator;

//import java.sql.*;
import java.util.*;

import client.dataAccess.*;

import shared.dataTransfer.*;

public class ServerCommunicator {
	
	/**
	 * Holds a User once a valid result has been returned by verifyUser().
	 * This user object is held as long as the user is logged in, and is used
	 * in subsequent SQL requests.
	 */
	private User currentUser;
	
	/**
	 * Verifies a username + password combo, and returns the user's info (or null
	 * if username/password was incorrect.
	 * @param username Username supplied by the user
	 * @param password Password supplied by the user
	 * @return A user object with the validated user's information, or null for an
	 * invalid username or password
	 */
	public User verifyUser(String username, String password) {
		return null;
	}
	
	/**
	 * Requests a list of all the Projects available for indexing
	 * @return All projects currently available to the user for indexing.
	 */
	public List<Project> requestProjectsList() {
		return null;
	}
	
	/**
	 * Requests a sample image for a given project, to help the user decide if
	 * this project is right for them.
	 * @param p The project to request a sample from
	 * @return An image from the project.
	 */
	public Image requestSampleImage(Project p) {
		return null;
	}
	
	/**
	 * Requests a batch for a given project. The batch data from the server 
	 * should include an image url and record information, if any.
	 * @param p The project to request a batch from
	 * @return The batch received, or null if no batch received.
	 */
	public Batch requestBatch(Project p) {
		return null;
	}

	/**
	 * Submits a batch to the server, along with associated data.
	 * @param b Batch to submit. Records are attached to this batch.
	 * @param completed Whether the batch is "complete", as defined by the user.
	 * An incomplete batch will be assigned to another user later. A complete batch
	 * will not be submitted to a user to index, but may be searched.
	 * @return Whether the submission was accepted by the server.
	 */
	public boolean submitBatch(Batch b, boolean completed) {
		return false;
	}
	
	/**
	 * Deactivates the currently logged-in user object held locally.
	 * @return Whether the user was logged out. If this returns false, it is usually
	 * because there was no user logged in.
	 */
	public boolean logout() {
		if (currentUser != null) {
			currentUser = null;
			return true;
		}
		return false;
	}
	
	/**
	 * Searches for a series of terms in a series of fields among all records, and
	 * returns all records with matches.
	 * @param fields One or more fields to check for all of the search terms in
	 * @param searchValues One or more search terms to check for in all of the fields
	 * @return All records with a field-value match.
	 */
	public List<Record> searchRecords(List<Field> fields, List<String> searchValues) {
		return null;
	}
	
	/**
	 * Gets a list of all the fields in the database, used for populating the fields
	 * menu in the search tool.
	 * @return A list containing one of every field in the database.
	 */
	public List<Field> requestFieldsList() {
		return null;
	}

	
}
