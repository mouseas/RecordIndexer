package shared.dataTransfer;

public class XferUser {
	
	public XferUser(int id, String username, String firstname, String lastname,
			String email, int indexedRecords, String password) {
		
	}
	
	public int id;
	public String username;
	public String firstName;
	public String lastName;
	public String email;
	public int indexedRecords;
	private String password;
	
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
}
