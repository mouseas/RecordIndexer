package shared.dataTransfer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FieldTest {

	private Field f;
	
	@Before
	public void setUp() throws Exception {
		f = new Field(17, 3, "First Name", 27, 127, "help.html", "firstNameKnown.txt");
	}

	@After
	public void tearDown() throws Exception {
		f = null;
	}

	@Test
	public void testField() {
		assertEquals(17, f.getID());
		assertEquals(3, f.getProjectID());
		assertEquals("First Name", f.getTitle());
		assertEquals(27, f.getXCoord());
		assertEquals(127, f.getWidth());
	}

	@Test
	public void testGetProjectID() {
		assertEquals(3, f.getProjectID());
	}

	@Test
	public void testGetXCoord() {
		assertEquals(27, f.getXCoord());
	}

	@Test
	public void testGetTitle() {
		assertEquals("First Name", f.getTitle());
	}

	@Test
	public void testGetWidth() {
		assertEquals(127, f.getWidth());
	}

//	@Test
//	public void testGetKnownData() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetHelpText() {
//		fail("Not yet implemented");
//	}

}
