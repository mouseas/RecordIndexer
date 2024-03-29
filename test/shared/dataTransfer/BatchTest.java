package shared.dataTransfer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BatchTest {

	private BatchImage b;
	
	@Before
	public void setUp() throws Exception {
		b = new BatchImage(17, 3, "test.png", "0");
	}

	@After
	public void tearDown() throws Exception {
		b = null;
	}

	@Test
	public void testBatch() {
		assertEquals(17, b.getID());
		assertEquals(3, b.getProjectID());
		assertEquals("test.png", b.getImageLoc());
	}

	@Test
	public void testGetImage() {
		assertEquals("test.png", b.getImageLoc());
	}

	@Test
	public void testGetProjectID() {
		assertEquals(3, b.getProjectID());
	}

}
