package client.dataAccess;

import shared.dataTransfer.*;

public class User extends DataAccessObject{

	/**
	 * Attempts to register a new User to the database
	 * @param username
	 * @param password
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @return Whether the registration attempt was successful
	 */
	public static boolean register (String username, String password, 
			String firstname, String lastname, String email) {
		return false;
	}
	
	/**
	 * Attempts to log in an existing user, and returns a User object if
	 * successful, or null if unsuccessful.
	 * @param username
	 * @param password
	 * @return Successfully logged-in user, or null for incorrect username 
	 * and/or password.
	 */
	public static User login(String username, String password) {
		return null;
	}
	
	public User(XferUser u) {
		
	}
	
	public String getUsername() {
		return null;
	}
	
	public String getFullName() {
		return null;
	}
	
	public String getFirstName() {
		return null;
	}
	
	public String getLastName() {
		return null;
	}
	
	public String getEmail() {
		return null;
	}
	
	public int getNumIndexedRecords() {
		return 0;
	}
	
	
}