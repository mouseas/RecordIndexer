package shared.dataTransfer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserTest {

	public User u;
	
	@Before
	public void setUp() throws Exception {
		u = new User(2, "mouseasw", "Martin", "Carney", "mouseasw@gmail.com",
				78, "passwordTest");
		
	}

	@After
	public void tearDown() throws Exception {
		u = null;
	}

	@Test
	public void testUser() {
		assertEquals(2, u.getID());
		assertEquals("mouseasw", u.getUsername());
		assertEquals("Martin", u.getFirstName());
		assertEquals("Carney", u.getLastName());
		assertEquals("mouseasw@gmail.com", u.getEmail());
		assertEquals(78, u.getNumIndexedRecords());
		assertEquals("passwordTest", u.getPassword());
		
	}

	@Test
	public void testGetPassword() {
		assertEquals("passwordTest", u.getPassword());
	}

	@Test
	public void testSetPassword() {
		assertEquals("passwordTest", u.getPassword());
		u.setPassword("testSetting");
		assertEquals("testSetting", u.getPassword());
	}

	@Test
	public void testGetUsername() {
		assertEquals("mouseasw", u.getUsername());
	}

	@Test
	public void testGetFullName() {
		assertEquals("Martin Carney", u.getFullName());
	}

	@Test
	public void testGetFirstName() {
		assertEquals("Martin", u.getFirstName());
	}

	@Test
	public void testGetLastName() {
		assertEquals("Carney", u.getLastName());
	}

	@Test
	public void testGetEmail() {
		assertEquals("mouseasw@gmail.com", u.getEmail());
	}

	@Test
	public void testGetNumIndexedRecords() {
		assertEquals(78, u.getNumIndexedRecords());
	}

}
