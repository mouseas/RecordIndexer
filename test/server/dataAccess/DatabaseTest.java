package server.dataAccess;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import server.ServerException;

import java.io.*;
import java.sql.*;

public class DatabaseTest {

	Database db;
	
	@Before
	public void setUp() throws Exception {
		// correct location
		db = new Database("database" + File.separator +  "indexer-app.sqlite");
		
		// incorrect location
		//db = new Database("database" + File.separator +  "fail.sqlite");
	}

	@After
	public void tearDown() throws Exception {
		db.close();
		db = null;
	}

	@Test
	public void testInitialize() {
		assertTrue(Database.isInitialized());
	}

	@Test
	public void testDatabase() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGetConnection() {
		assertNull(db.getConnection());
		try {
			db.startTransaction();
		} catch (ServerException e) {
			fail(e.getMessage());
		}
		assertNotNull(db.getConnection());
	}

	@Test
	public void testStartTransaction() {
		try {
			db.startTransaction();
		} catch (ServerException e) {
			fail(e.getMessage());
		}
		assertNotNull(db.getConnection());
	}

	@Test
	public void testPerformQuery() {
		ResultSet rs = null;
		try {
			db.startTransaction();
			rs = db.performQuery("SELECT * FROM fields");
			int numResults = 0;
			do {
				System.out.println("ID: " + rs.getString("id"));
				numResults++;
			} while (rs.next());
			assertTrue("Empty query results returned!", numResults > 0);
			db.endTransaction(false);
		} catch (SQLException e) {
			fail(e.getMessage());
		} catch (ServerException e) {
			fail(e.getMessage());
		}
		finally {
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) {}
			}
			db.close();
		}
	}

	@Test
	public void testEndTransaction() {
		try {
			db.startTransaction();
			db.endTransaction(false);
		} catch (ServerException e) {
			fail(e.getMessage());
		}
		// not fully implemented.
	}

}
