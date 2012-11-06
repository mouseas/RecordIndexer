package shared.dataTransfer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * A User object. Used to hold and transfer data about a user and their login.
 * @author Martin
 *
 */
public class User extends DataTransferObject {

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
		this.firstName = firstname;
		this.lastName = lastname;
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
	
	public static String serialize(User user) {
		XStream xstream = new XStream(new DomDriver());
		return xstream.toXML(user);
	}
}
