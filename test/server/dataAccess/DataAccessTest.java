package server.dataAccess;

import static org.junit.Assert.*;

import java.io.File;
import java.util.*;

import org.junit.*;
import shared.dataTransfer.*;

public class DataAccessTest {

	private DataAccess da;
	
	@BeforeClass
	public static void initialize() throws Exception {
		Database.initialize();
	}
	
	@Before
	public void setUp() throws Exception {
		da = new DataAccess("database" + File.separator +  "indexer-app.sqlite");
		da.startTransaction();
	}

	@After
	public void tearDown() throws Exception {
		da.endTransaction(false);
		da = null;
	}

	@Test
	public void testDataAccess() {
		// no real test here, as long as no exceptions occur.
	}

	@Test
	public void testGetUser() {
		User expected = new User(1, "mouseasw", "Martin", "Carney",
				"mouseasw@gmail.com", 0, "password");
		User actual = da.getUser("mouseasw", "password");
		assertEquals(expected.getFullName(), actual.getFullName());
		assertEquals(expected.getUsername(), actual.getUsername());
		assertEquals(expected.getID(), actual.getID());
		assertEquals(expected.getEmail(), actual.getEmail());
		assertEquals(expected.getPassword(), actual.getPassword());
		
		actual = da.getUser("invalid", "hjsdbfgkefgn");
		assertNull(actual);
		
		expected = new User(2, "test", "Mister", "Test",
				"mouse_asw@yahoo.com", 0, "password");
		actual = da.getUser("test", "password");
		assertEquals(expected.getFullName(), actual.getFullName());
		assertEquals(expected.getUsername(), actual.getUsername());
		assertEquals(expected.getID(), actual.getID());
		assertEquals(expected.getEmail(), actual.getEmail());
		assertEquals(expected.getPassword(), actual.getPassword());
	}

	@Test
	public void testGetProjectList() {
		Project expected = new Project(1, 327, 20, 30, "1987 Census");
		List<Project> actualList = da.getProjectList();
		Project actual = actualList.get(0);
		assertEquals(expected.getID(), actual.getID());
		assertEquals(expected.getTitle(), actual.getTitle());
		assertEquals(expected.getRecordsPerImage(), actual.getRecordsPerImage());
		assertEquals(expected.getRowHeight(), actual.getRowHeight());
		assertEquals(expected.getY(0), actual.getY(0));
	}

	@Test
	public void testGetSampleImageLocation() {
		try {
			String expected = "batch001.png";
			String actual = da.getSampleImageLocation(1);
			assertEquals(expected, actual);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testGetNextBatch() {
		try {
			Batch expected = new Batch(3, 1, "batch003.png");
			Batch actual = da.getNextBatch(1);
			assertNotNull("No batch returned!", actual);
			assertEquals(expected.getID(), actual.getID());
			assertEquals(expected.getImage().getFilename(), 
					actual.getImage().getFilename());
			assertEquals(expected.getProjectID(), actual.getProjectID());
		} catch (Exception e) {
			fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testSaveBatch() {
		try {
			Batch result = new Batch(3, 1, "batch003.png");
			assertTrue("Failed with completed.", da.saveBatch(result, true));
			assertTrue("Failed with not completed.", da.saveBatch(result, false));
		} catch (Exception e) {
			fail("Exception thrown: " + e.getMessage());
		} finally {
			da.endTransaction(true);
		}
	}

	@Test
	public void testSaveMultpleRecords() {
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
		} finally {
			da.endTransaction(true);
		}
	}

	@Test
	public void testGetRecord() {
		Record expected = new Record(5, 2, 1, 1, "Ashley");
		Record actual = da.getRecord(2, 1, 1);
		assertNotNull("No Record returned!", actual);
		assertEquals(expected.getBatchID(), actual.getBatchID());
		assertEquals(expected.getFieldID(), actual.getFieldID());
		assertEquals(expected.getRowNumber(), actual.getRowNumber());
		assertEquals(expected.getValue(), actual.getValue());
	}

	@Test
	public void testSearch() {
		Record expected = new Record(5, 2, 1, 1, "Ashley");
		List<Record> actual = da.search(1, "AshlEy");
		assertNotNull(actual);
		assertTrue(actual.size() > 0);
		assertEquals(expected.getRowNumber(), actual.get(0).getRowNumber());
		assertEquals("Ashley", actual.get(0).getValue());
	}

	@Test
	public void testGetFields() {
		//fail("Not yet implemented");
	}

}
