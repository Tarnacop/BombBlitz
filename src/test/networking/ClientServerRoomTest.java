package test.networking;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.AI.AIDifficulty;
import bomber.networking.ClientServerAI;
import bomber.networking.ClientServerPlayer;
import bomber.networking.ClientServerRoom;

public class ClientServerRoomTest {
	private ClientServerRoom room;
	private List<ClientServerPlayer> humanPlayerList = new ArrayList<ClientServerPlayer>(2);
	private List<ClientServerAI> aiPlayerList = new ArrayList<ClientServerAI>(1);

	@Before
	public void setUp() throws Exception {
		room = new ClientServerRoom(133, "Room 133", 2, 1, 4, false, 31, 35);

		humanPlayerList.add(new ClientServerPlayer(5, "P5", true));
		humanPlayerList.add(new ClientServerPlayer(10, "P10", false));
		aiPlayerList.add(new ClientServerAI((byte) 0, AIDifficulty.EXTREME));

		room.setHumanPlayerList(humanPlayerList);
		room.setAIPlayerList(aiPlayerList);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		room.setAIPlayerNumber(1);
		room.setHumanPlayerNumber(2);
		room.setID(133);
		room.setInGame(false);
		room.setMapID(31);
		room.setMaxMapID(35);
		room.setName("Room 133");

		assertEquals(133, room.getID());
		assertEquals("Room 133", room.getName());
		assertEquals(2, room.getHumanPlayerNumber());
		assertEquals(1, room.getAIPlayerNumber());
		assertEquals(4, room.getMaxPlayer());
		assertFalse(room.isInGame());
		assertEquals(31, room.getMapID());
		assertEquals(35, room.getMaxMapID());

		assertEquals(2, room.getHumanPlayerList().size());

		ClientServerPlayer p = room.getHumanPlayerList().get(0);
		assertEquals(5, p.getID());
		assertEquals("P5", p.getName());
		assertTrue(p.isReadyToPlay());

		p = room.getHumanPlayerList().get(1);
		assertEquals(10, p.getID());
		assertEquals("P10", p.getName());
		assertFalse(p.isReadyToPlay());

		assertEquals(1, room.getAIPlayerList().size());

		ClientServerAI ai = room.getAIPlayerList().get(0);
		assertEquals((byte) 0, ai.getID());
		assertEquals(AIDifficulty.EXTREME, ai.getDifficulty());
	}

}
