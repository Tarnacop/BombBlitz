package test.networking;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.networking.ClientServerPlayer;

public class ClientServerPlayerTest {
	private ClientServerPlayer player1;
	private ClientServerPlayer player2;

	@Before
	public void setUp() throws Exception {
		player1 = new ClientServerPlayer(4, "R", true);
		player2 = new ClientServerPlayer(8, "L", true, 16);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertEquals(4, player1.getID());
		assertEquals("R", player1.getName());
		assertTrue(player1.isInRoom());
		assertTrue(player1.isReadyToPlay());

		assertEquals(8, player2.getID());
		assertEquals("L", player2.getName());
		assertTrue(player2.isInRoom());
		assertEquals(16, player2.getRoomID());
	}

}
