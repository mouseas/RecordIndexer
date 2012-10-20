package shared.dataTransfer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RecordTest {

	private Record r1;
	private Record r2;
	
	@Before
	public void setUp() throws Exception {
		r1 = new Record(87, 13, 4, 7, "");
		r2 = new Record(88, 13, 5, 7, null);
	}

	@After
	public void tearDown() throws Exception {
		r1 = null;
		r2 = null;
	}

	@Test
	public void testRecord() {
		assertEquals(87, r1.getID());
		assertEquals(13, r1.getBatchID());
		assertEquals(4, r1.getFieldID());
		assertEquals(7, r1.getRowNumber());
		assertEquals("", r1.getValue());
		assertEquals("", r2.getValue());
	}

	@Test
	public void testGetBatchID() {
		assertEquals(13, r1.getBatchID());
	}

	@Test
	public void testGetRowNumber() {
		assertEquals(7, r1.getRowNumber());
	}

	@Test
	public void testGetValue() {
		assertEquals("", r1.getValue());
		assertEquals("", r2.getValue());
	}

	@Test
	public void testSetValue() {
		assertEquals("", r1.getValue());
		assertEquals("", r2.getValue());
		r2.setValue("test r2");
		assertEquals("test r2", r2.getValue());
		r2.setValue(null);
		assertEquals("", r2.getValue());
		r1.setValue("");
		assertEquals("", r1.getValue());
	}

	@Test
	public void testGetFieldID() {
		assertEquals(4, r1.getFieldID());
	}

}
