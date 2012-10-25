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
	public void testImportUsers() {
		di.importUsers();
		User actual = da.getUser("test1", "test1");
	}

	@Test
	public void testImportProjects() {
		fail("Not yet implemented");
	}

	@Test
	public void testImportBatches() {
		fail("Not yet implemented");
	}

	@Test
	public void testImportFields() {
		fail("Not yet implemented");
	}

	@Test
	public void testImportRecords() {
		fail("Not yet implemented");
	}

}
