package test.networking;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.networking.ClientServerLobbyRoom;

public class ClientServerLobbyRoomTest {
	private ClientServerLobbyRoom room;

	@Before
	public void setUp() throws Exception {
		room = new ClientServerLobbyRoom(5, "TestRoom", 3, 4, false, 2, new int[] { 5, 7, 9 });
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertEquals(5, room.getID());
		assertEquals("TestRoom", room.getName());
		assertEquals(3, room.getPlayerNumber());
		assertEquals(4, room.getMaxPlayer());
		assertFalse(room.isInGame());
		assertEquals(2, room.getMapID());
		assertArrayEquals(new int[] { 5, 7, 9 }, room.getPlayerID());
	}

}
