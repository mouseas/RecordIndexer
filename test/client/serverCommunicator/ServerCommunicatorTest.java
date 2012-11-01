package client.serverCommunicator;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import shared.dataTransfer.*;

public class ServerCommunicatorTest {

	private ServerCommunicator sc;
	@Before
	public void setUp() throws Exception {
		sc = new ServerCommunicator("localhost", 8080);
	}

	@After
	public void tearDown() throws Exception {
		sc = null;
	}

//	@Test
//	public void testServerCommunicator() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testVerifyUser() {
		User expected = new User(2, "sheila", "Sheila", "Parker",
				"sheila.parker@gmail.com", 0, "parker");
		User actual = sc.verifyUser("sheila", "parker");
		assertNotNull("No user returned from verifyUser()", actual);
		assertEquals(expected.getFullName(), actual.getFullName());
		assertEquals(expected.getEmail(), actual.getEmail());
		
		actual = sc.verifyUser("fail", "fail2");
	}

//	@Test
//	public void testRequestProjectsList() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testRequestSampleImage() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testRequestBatch() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSubmitBatch() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testLogout() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSearchRecords() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testRequestFieldsList() {
//		fail("Not yet implemented");
//	}

}
