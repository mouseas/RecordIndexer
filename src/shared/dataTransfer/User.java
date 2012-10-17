package shared.dataTransfer;

public class User extends ObjectWithID {

	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private int indexedRecords;
	private String password;
	
	public User(int id, String username, String firstname, String lastname,
			String email, int indexedRecords, String password) {
		setID(id);
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.indexedRecords = indexedRecords;
		this.password = password;
	}
	
	/**
	 * Gets the user's password
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Attempts to change the user's password. Requires the current password
	 * @param pass Password to assign
	 */
	public void setPassword(String pass) {
		password = pass;
	}
	
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
	
	public String getUsername() {
		return username;
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public int getNumIndexedRecords() {
		return indexedRecords;
	}
	
}
