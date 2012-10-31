package shared.dataTransfer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProjectTest {

	private Project p;
	
	@Before
	public void setUp() throws Exception {
		p = new Project(7, 347, 20, 30, "1842 Census");
	}

	@After
	public void tearDown() throws Exception {
		p = null;
	}

	@Test
	public void testProject() {
		assertEquals(7, p.getID());
		assertEquals(347, p.getY(0));
		assertEquals(367, p.getY(1));
		assertEquals(387, p.getY(2));
		assertEquals(20, p.getRowHeight());
		assertEquals(30, p.getRecordsPerImage());
		assertEquals("1842 Census", p.getTitle());
	}

	@Test
	public void testGetY() {
		assertEquals(347, p.getY(0));
		assertEquals(367, p.getY(1));
		assertEquals(387, p.getY(2));
	}

	@Test
	public void testGetRecordsPerImage() {
		assertEquals(30, p.getRecordsPerImage());
	}

	@Test
	public void testGetRowHeight() {
		assertEquals(20, p.getRowHeight());
	}

	@Test
	public void testGetTitle() {
		assertEquals("1842 Census", p.getTitle());
	}
	
	@Test
	public void testSerialize() {
		String result = Project.serialize(p);
		assertNotNull(result);
	}
	
	@Test
	public void testDeserialize() {
		Project result = Project.deserialize(Project.serialize(p));
		assertNotNull(result);
		assertEquals(p.getTitle(), result.getTitle());
		assertEquals(p.getRecordsPerImage(), result.getRecordsPerImage());
	}

}
