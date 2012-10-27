package server.dataAccess;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import shared.dataTransfer.*;

public class DataImporterTest {

	private DataImporter di;
	private static final String testLocation = "Records.xml";
	private DataAccess da;
	
	@Before
	public void setUp() throws Exception {
		di = new DataImporter(testLocation);
		da = di.getDataAccess();
	}

	@After
	public void tearDown() throws Exception {
		di = null;
		da = null;
	}

	@Test
	public void testDataImporter() {
		// exceptions thrown by construction will stop the test at setUp().
		assertNotNull(di);
		assertNotNull(da);
	}
	
	@Test
	public void testTheWholeThing() {
		di.wipeDatabase();
		
		testImportUsers();
//		testImportProjects();
	}

	private void testImportUsers() {
		di.importUsers();
		da.startTransaction();
		
		User actual = da.getUser("test1", "test1");
		assertNotNull("No username/password match! Import failed.", actual);
		assertEquals("test1", actual.getUsername());
		actual = da.getUser("sheila", "parker");
		assertNotNull("No username/password match! Import more than 1 user failed.", actual);
		assertEquals("Sheila Parker", actual.getFullName());
		assertEquals("sheila.parker@gmail.com", actual.getEmail());
		
		da.endTransaction(false);
	}

	private void testImportProjects() {
		

		
		testImportFields();
		testImportBatches();
		testImportRecords();
	}

	private void testImportBatches() {
		fail("Not yet implemented");
	}

	private void testImportFields() {
		fail("Not yet implemented");
	}

	private void testImportRecords() {
		fail("Not yet implemented");
	}

}
