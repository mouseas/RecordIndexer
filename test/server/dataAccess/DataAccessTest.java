package server.dataAccess;

import static org.junit.Assert.*;

import java.io.File;
import java.util.*;

import org.junit.*;

import shared.dataTransfer.*;

/**
 * #### Heads-Up ####
 * This test class is a bit outdated. If you run testWipeAndRepopulateDatabase() first,
 * then run the test, it should work. It is based off data that existed only before
 * DataImporter was written.
 * @author Martin
 *
 */
public class DataAccessTest {

	private DataAccess da;
	
	@Before
	public void setUp() throws Exception {
		da = new DataAccess("database" + File.separator +  "indexer-app.sqlite");
	}

	@After
	public void tearDown() throws Exception {
		da = null;
	}

	@Test
	public void testDataAccess() {
		// no real test here, as long as no exceptions occur.
	}
	
	// This test needs to happen first; then the others will work.
	@Test
	public void testWipeAndRepopulateDatabase() {
		System.out.println("wipe and repopulate ################");
		da.startTransaction();
		
		testWipeDatabase();

//		da.endTransaction(true);
//		da.startTransaction();
		
		testAddProject();

//		da.endTransaction(true);
//		da.startTransaction();
		
		testAddUser();

//		da.endTransaction(true);
//		da.startTransaction();
		
		testAddField();

//		da.endTransaction(true);
//		da.startTransaction();
		
		testAddBatch();

		da.endTransaction(true);
	}
	
	private void testAddUser() {
		da.addUser(new User(1, "mouseasw", "Martin", "Carney",
				"mouseasw@gmail.com", 0, "password"));
		da.addUser(new User(2, "test", "Mister", "Test",
				"mouse_asw@yahoo.com", 0, "password"));
		
		User expected = new User(1, "mouseasw", "Martin", "Carney",
				"mouseasw@gmail.com", 0, "password");
		User actual = da.getUser("mouseasw", "password");
		assertEquals(expected.getFullName(), actual.getFullName());
		assertEquals(expected.getUsername(), actual.getUsername());
		assertEquals(expected.getID(), actual.getID());
		assertEquals(expected.getEmail(), actual.getEmail());
		assertEquals(expected.getPassword(), actual.getPassword());
		
		expected = new User(2, "test", "Mister", "Test",
				"mouse_asw@yahoo.com", 0, "password");
		actual = da.getUser("test", "password");
		assertEquals(expected.getFullName(), actual.getFullName());
		assertEquals(expected.getUsername(), actual.getUsername());
		assertEquals(expected.getID(), actual.getID());
		assertEquals(expected.getEmail(), actual.getEmail());
		assertEquals(expected.getPassword(), actual.getPassword());
	}

	private void testAddBatch() {
		da.addBatch(new Batch(1, 1, "batch001.png", "0", true));
		da.addBatch(new Batch(2, 1, "batch002.png", "0"));
		Batch batch2 = null;
		try {
			batch2 = da.getNextBatch(1, "test1"); // marks batch 2 as in-use, and grabs it.
		} catch (Exception e) {
			fail(e.getMessage());
		}
		da.addBatch(new Batch(3, 1, "batch003.png", "0"));
		assertEquals("batch002.png", batch2.getImage().getFilename());
		assertEquals(2, batch2.getID());
	}

	private void testAddProject() {
		da.addProject(new Project(1, 327, 20, 30, "1987 Census"));
		List<Project> projects = da.getProjectList();
		assertNotNull(projects);
		assertTrue(projects.size() > 0);
		Project p = projects.get(0);
		assertEquals("1987 Census", p.getTitle());
		assertEquals(327, p.getY(0));
		assertEquals(20, p.getRowHeight());
		assertEquals(30, p.getRecordsPerImage());
	}

	private void testAddField() {
		da.addField(new Field(1, 1, "First Name", 217, 87, "test.html", "names.txt"));
		da.addField(new Field(2, 1, "Last Name", 304, 115, "test.html", "names.txt"));
		da.addField(new Field(3, 1, "Gender", 419, 27, "test.html", "names.txt"));
		da.addField(new Field(4, 1, "Date of Birth", 446, 65, "test.html", "names.txt"));
		
		List<Field> fields = da.getFields(1);
		assertNotNull(fields);
		assertTrue(fields.size() > 3);
		
		Field f = fields.get(0);
		assertEquals(1, f.getProjectID());
		assertEquals(217, f.getXCoord());
		assertEquals(87, f.getWidth());
	}

	@Test
	public void testGetFields() {
		System.out.println("get fields ################");
		da.startTransaction();
		
		List<Field> expected = new ArrayList<Field>();
		expected.add(new Field(1, 1, "First Name", 217, 87, "test.html", "names.txt"));
		expected.add(new Field(2, 1, "Last Name", 304, 115, "test.html", "names.txt"));
		expected.add(new Field(3, 1, "Gender", 419, 27, "test.html", "names.txt"));
		expected.add(new Field(4, 1, "Date of Birth", 446, 65, "test.html", "names.txt"));
		List<Field> actual = da.getFields(1);
		assertNotNull("No Fields returned!", actual);
		assertTrue("Less than 4 fields: " + actual.size(), actual.size() > 3);
		assertEquals(expected.get(0).getTitle(), actual.get(0).getTitle());
		assertEquals(expected.get(1).getTitle(), actual.get(1).getTitle());
		assertEquals(expected.get(2).getTitle(), actual.get(2).getTitle());
		assertEquals(expected.get(3).getTitle(), actual.get(3).getTitle());
		assertEquals(expected.get(0).getHelpHtmlLoc(), actual.get(0).getHelpHtmlLoc());
		assertEquals(expected.get(0).getWidth(), actual.get(0).getWidth());
		assertEquals(expected.get(0).getXCoord(), actual.get(0).getXCoord());
		assertEquals(expected.get(0).getKnownDataLoc(), actual.get(0).getKnownDataLoc());

		da.endTransaction(false);
	}
	
	private void testSearch() {
		Record expected = new Record(5, 2, 1, 1, "Ashley");
		List<Record> actual = da.search(1, "AshlEy");
		assertNotNull(actual);
		assertTrue(actual.size() > 0);
		assertEquals(expected.getRowNumber(), actual.get(0).getRowNumber());
		assertEquals("Ashley", actual.get(0).getValue());
	}

	@Test
	public void testGetUser() {
		System.out.println("get user ################");
		da.startTransaction();
		
		User expected = new User(1, "mouseasw", "Martin", "Carney",
				"mouseasw@gmail.com", 0, "password");
		User actual = da.getUser("mouseasw", "password");
		assertEquals(expected.getFullName(), actual.getFullName());
		assertEquals(expected.getUsername(), actual.getUsername());
		assertEquals(expected.getID(), actual.getID());
		assertEquals(expected.getEmail(), actual.getEmail());
		assertEquals(expected.getPassword(), actual.getPassword());
		
		actual = da.getUser("invalid", "hjsdbfgkefgn");
		assertNotNull(actual); // should return a user with ID -1 and username "invalid".
		assertEquals(-1, actual.getID()); // invalid user
		
		expected = new User(2, "test", "Mister", "Test",
				"mouse_asw@yahoo.com", 0, "password");
		actual = da.getUser("test", "password");
		assertEquals(expected.getFullName(), actual.getFullName());
		assertEquals(expected.getUsername(), actual.getUsername());
		assertEquals(expected.getID(), actual.getID());
		assertEquals(expected.getEmail(), actual.getEmail());
		assertEquals(expected.getPassword(), actual.getPassword());

		da.endTransaction(false);
	}

	@Test
	public void testGetProjectList() {
		System.out.println("get project list ################");
		da.startTransaction();
		
		Project expected = new Project(1, 327, 20, 30, "1987 Census");
		List<Project> actualList = da.getProjectList();
		Project actual = actualList.get(0);
		assertEquals(expected.getID(), actual.getID());
		assertEquals(expected.getTitle(), actual.getTitle());
		assertEquals(expected.getRecordsPerImage(), actual.getRecordsPerImage());
		assertEquals(expected.getRowHeight(), actual.getRowHeight());
		assertEquals(expected.getY(0), actual.getY(0));

		da.endTransaction(false);
	}

	@Test
	public void testGetSampleImageLocation() {
		System.out.println("get sample image ################");
		da.startTransaction();
		
		try {
			String expected = "batch001.png";
			String actual = da.getSampleImage(1).getFilename();
			assertEquals(actual, expected, actual);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}

		da.endTransaction(false);
	}

	@Test
	public void testBatches() {
		da.startTransaction();
		
		testGetNextBatch();
		
		da.endTransaction(true);
		da.startTransaction();
		
		testSaveBatch();
		
		da.endTransaction(true);
	}
	
	private void testGetNextBatch() {
		System.out.println("get next batch ################");
		
		try {
			Batch expected = new Batch(3, 1, "batch003.png", "0");
			Batch actual = da.getNextBatch(1, "test");
			assertNotNull("No batch returned!", actual);
			assertEquals(expected.getID(), actual.getID());
			assertEquals(expected.getImage().getFilename(), 
					actual.getImage().getFilename());
			assertEquals(expected.getProjectID(), actual.getProjectID());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	private void testSaveBatch() {
		System.out.println("save batch ################");
		
		try {
			Batch result = new Batch(3, 1, "batch003.png", "test1");
			assertTrue("Failed with completed.", da.saveBatch(result, true));
			assertTrue("Failed with not completed.", da.saveBatch(result, false));
		} catch (Exception e) {
			fail("Exception thrown: " + e.getMessage());
		}

	}

	@Test
	public void testSaveAndGetRecordsAndSearch() {
		System.out.println("save and get records and search ################");
		da.startTransaction();
		
		testSaveMultpleRecords();

		da.endTransaction(true);
		da.startTransaction();
		
		testGetRecord();
		testSearch();

		da.endTransaction(true);
	}
	
	private void testSaveMultpleRecords() {
		try {
			List<Record> records = new ArrayList<Record>();
			records.add(new Record(1, 2, 1, 0, "Martin"));
			records.add(new Record(2, 2, 2, 0, "Carney"));
			records.add(new Record(3, 2, 3, 0, "Male"));
			records.add(new Record(4, 2, 4, 0, "06/19/1987"));
			records.add(new Record(5, 2, 1, 1, "Ashley"));
			records.add(new Record(6, 2, 2, 1, "Carney"));
			records.add(new Record(7, 2, 3, 1, "Female"));
			records.add(new Record(8, 2, 4, 1, "10/08/1988"));
			
			assertTrue(da.saveSeveralRecords(records));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	private void testGetRecord() {
		Record expected = new Record(5, 2, 1, 1, "Ashley");
		Record actual = da.getRecord(2, 1, 1);
		assertNotNull("No Record returned!", actual);
		assertEquals(expected.getBatchID(), actual.getBatchID());
		assertEquals(expected.getFieldID(), actual.getFieldID());
		assertEquals(expected.getRowNumber(), actual.getRowNumber());
		assertEquals(expected.getValue(), actual.getValue());
	}
	
	private void testWipeDatabase() {
		try {
		da.wipeDatabase(true);
		da.endTransaction(true); // commit change
		da.startTransaction(); // start another transaction
		List<Field> actualFields = da.getFields(1);
		assertTrue("Fields were not empty!", actualFields.size() < 1);
		List<Project> actualProjects = da.getProjectList();
		assertTrue("Projects were not empty!", actualProjects.size() < 1);
		Batch actualBatch = da.getNextBatch(1, "test1");
		assertNull("Batches exist", actualBatch);
		User actualUser = da.getUser("mouseasw", "password");
		assertNotNull("Null user instead of invalid user.", actualUser);
		assertEquals("Returned a valid user.", -1, actualUser.getID());
		
		} catch (Exception e) {
			fail("Exception occured during database wipe.");
		}
		
	}

	
}
