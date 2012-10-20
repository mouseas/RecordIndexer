package shared.dataTransfer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ImageTest {

	private Image image;
	
	@Before
	public void setUp() throws Exception {
		image = new Image(7, "test.png");
	}

	@After
	public void tearDown() throws Exception {
		image = null;
	}

	@Test
	public void testImage() {
		assertEquals(7, image.getProjectID());
		assertEquals("test.png", image.getFilename());
	}

	@Test
	public void testGetProjectID() {
		assertEquals(7, image.getProjectID());
	}

//	@Test
//	public void testGetImage() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testGetFilename() {
		assertEquals("test.png", image.getFilename());
	}

}
