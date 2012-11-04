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
		assertNotNull("Expected a user with invalid info", actual);
		assertEquals("Returned a valid user when invalid credentials were provided.",
				-1, actual.getID());
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

	@Test
	public void testRequestSampleImage() {
		String actual = sc.requestSampleImage(new Project(0, 0, 0, 0, "n/a")).getFilename();
		assertEquals("Project 0 sample mismatch.", "images/1890_image0.png", actual);
		actual = sc.requestSampleImage(new Project(1, 0, 0, 0, "n/a")).getFilename();
		assertEquals("Project 1 sample mismatch.", "images/1900_image0.png", actual);
		actual = sc.requestSampleImage(new Project(2, 0, 0, 0, "n/a")).getFilename();
		assertEquals("Project 2 sample mismatch.", "images/draft_image0.png", actual);
	}

	@Test
	public void testRequestAndSubmitBatch() {
		// request a couple batches.
		Batch actualBatch = sc.requestNextBatch(0);
		assertNotNull("No batch from project 0", actualBatch);
		System.out.println("BATCH: " + actualBatch.getID() + " PID:" + actualBatch.getProjectID()
				+ " " + actualBatch.getImage().getFilename());
		Batch actualBatch2 = sc.requestNextBatch(2);
		assertNull("Batch returned from project 2 " +
					"(all batches should be completed)", actualBatch2);
		
		// submit that batch, now finished.
		FinishedBatch finishedBatch = new FinishedBatch(actualBatch);
		Field f = new Field(0, 0, "Last Name", 60, 300, 
				"fieldhelp/last_name.html", "knowndata/1890_last_names.txt");
		finishedBatch.add(f);
		f = new Field(0, 0, "First Name", 360, 280, 
				"fieldhelp/last_name.html", "knowndata/1890_last_names.txt");
		finishedBatch.add(f);
		f = new Field(0, 0, "Gender", 640, 205, 
				"fieldhelp/last_name.html", "knowndata/1890_last_names.txt");
		finishedBatch.add(f);
		f = new Field(0, 0, "Age", 845, 120, "fieldhelp/last_name.html", "");
		finishedBatch.add(f);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < finishedBatch.getFields().size(); j++) {
				Record r = new Record(i * finishedBatch.getFields().size() + j,
						0, j, i, "testValue new " + i + " " + j);
				finishedBatch.add(r);
			}
		}
		
		boolean actual = sc.submitBatch(finishedBatch);
		assertTrue("Submission failed. Somehow.", actual);
	}

	@Test
	public void testLogout() {
		sc.logout();
		assertNull("Failed to log out!", sc.getCurrentUser());
	}

	@Test
	public void testSearchRecords() {
		List<Field> fields = new ArrayList<Field>();
		Field f = new Field(9, 2, "", 0, 0, "", "");
		fields.add(f);
		f = new Field(10, 2, "", 0, 0, "", "");
		fields.add(f);
		List<String> searchValues = new ArrayList<String>();
		searchValues.add("russ");
		searchValues.add("fox");
		
		List<Record> actual = sc.searchRecords(fields, searchValues);
		assertNotNull("Null search result!", actual);
		assertTrue("Empty search results!", actual.size() > 0);
		
		Record r = actual.get(0);
		assertEquals("FOX", r.getValue());
		assertEquals(1, r.getID());
		
		for (Record record : actual) {
			System.out.println("RECORD: " + record.getID() + " " + record.getValue());
		}
	}

	@Test
	public void testRequestFieldsList() {
		List<Field> actual = sc.requestFieldsList(new Project(1, 0, 0, 0, ""));
		assertNotNull("No result list of fields returned!", actual);
		assertTrue("No fields in list!", actual.size() > 0);
		for (Field f : actual) {
			System.out.println("FIELD: " + f.getID() + " " + f.getTitle());
		}
	}
	
	@Test
	public void testRequestSpecificBatch() {
		Batch expected = new Batch(40, 2, "images/draft_image0.png", "0");
		Batch actual = sc.requestSpecificBatch(40);
		assertNotNull("Null result, expected a batch.", actual);
		assertEquals(expected.getID(), actual.getID());
		assertEquals(expected.getImage().getFilename(), actual.getImage().getFilename());
		assertEquals(expected.getProjectID(), actual.getProjectID());
	}

}
