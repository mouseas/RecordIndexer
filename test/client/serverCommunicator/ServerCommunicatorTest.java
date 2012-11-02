package client.serverCommunicator;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.*;

import shared.dataTransfer.*;
import server.dataAccess.*;

public class ServerCommunicatorTest {

	private ServerCommunicator sc;
	
	@BeforeClass
	public static void reset() throws Exception {
		String[] args = new String[1];
		args[0] = "Records.xml";
		DataImporter.main(args); // wipes and re-imports the database prior to 
		// running the tests. This ensures that batches are available and users
		// have the same number of records indexed, and only the desired
		// records have values saved.
	}
	
	@Before
	public void setUp() throws Exception {
		sc = new ServerCommunicator("localhost", 8080);
		sc.verifyUser("test1", "test1"); // verifies a user, and logs in.
	}

	@After
	public void tearDown() throws Exception {
		sc = null;
	}

//	@Test
//	public void testServerCommunicator() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testVerifyUser() {
		sc.logout();
		User expected = new User(2, "sheila", "Sheila", "Parker",
				"sheila.parker@gmail.com", 0, "parker");
		User actual = sc.verifyUser("sheila", "parker");
		assertNotNull("No user returned from verifyUser()", actual);
		assertEquals(expected.getFullName(), actual.getFullName());
		assertEquals(expected.getEmail(), actual.getEmail());
		
		actual = sc.verifyUser("fail", "fail2");
	}

	@Test
	public void testRequestProjectsList() {
		List<Project> actual = sc.requestProjectsList();
		assertNotNull("No results!", actual);
		assertTrue("Empty project list!", actual.size() > 0);
		for (Project p: actual) {
			System.out.println("PROJECT: " + p.getTitle() + " ID:" + p.getID());
		}
	}

//	@Test
//	public void testRequestSampleImage() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testRequestBatch() {
		Batch actual = sc.requestBatch(1);
		assertNotNull("No batch from project 1", actual);
		System.out.println("BATCH: " + actual.getID() + " PID:" + actual.getProjectID()
				+ " " + actual.getImage().getFilename());
		actual = sc.requestBatch(0);
		assertNotNull("No batch from project 0", actual);
		System.out.println("BATCH: " + actual.getID() + " PID:" + actual.getProjectID()
				+ " " + actual.getImage().getFilename());
		actual = sc.requestBatch(2);
		assertNull("Batch returned from project 2 " +
					"(all batches should be completed)", actual);
		System.out.println("BATCH: " + actual.getID() + " PID:" + actual.getProjectID()
				+ " " + actual.getImage().getFilename());
	}

//	@Test
//	public void testSubmitBatch() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testLogout() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSearchRecords() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testRequestFieldsList() {
		List<Field> actual = sc.requestFieldsList(new Project(1, 0, 0, 0, ""));
		assertNotNull("No result list of fields returned!", actual);
		assertTrue("No fields in list!", actual.size() > 0);
		for (Field f : actual) {
			System.out.println("FIELD: " + f.getID() + " " + f.getTitle());
		}
	}

}
