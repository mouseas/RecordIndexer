package shared.dataTransfer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BatchTest {

	private Batch b;
	
	@Before
	public void setUp() throws Exception {
		b = new Batch(17, 3, "test.png");
	}

	@After
	public void tearDown() throws Exception {
		b = null;
	}

	@Test
	public void testBatch() {
		assertEquals(17, b.getID());
		assertEquals(3, b.getProjectID());
		assertEquals("test.png", b.getImage().getFilename());
	}

	@Test
	public void testGetImage() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFields() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRecords() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetProjectID() {
		assertEquals(3, b.getProjectID());
	}

	@Test
	public void testGetNumRecords() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFieldX() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFieldY() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRowHeight() {
		fail("Not yet implemented");
	}

}
