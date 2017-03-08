package test.networking;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.game.Map;
import bomber.game.Maps;
import bomber.networking.ServerClientInfo;
import bomber.networking.ServerRoom;

public class ServerRoomTest {
	private ServerRoom room;
	private ServerClientInfo client;
	private List<Map> maps;

	@Before
	public void setUp() throws Exception {
		maps = new Maps().getMaps();
		client = new ServerClientInfo(null, "Player 1");
		room = new ServerRoom("Test Room", client, maps, 0);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertFalse(room.isInGame());

		assertEquals("Test Room", room.getName());
		room.setName("N");
		assertEquals("N", room.getName());

		assertEquals(0, room.getMapID());
		room.setMapID(3);
		assertEquals(3, room.getMapID());

		assertFalse(room.allPlayersReady());
		client.setReadyToPlay(true);
		assertTrue(room.allPlayersReady());

		ServerClientInfo client2 = new ServerClientInfo(null, "Player 2");
		assertTrue(room.containsPlayer(client));
		assertFalse(room.containsPlayer(client2));
		assertFalse(room.containsPlayer(null));

		room.addPlayer(client2);
		assertTrue(room.containsPlayer(client2));

		assertFalse(room.allPlayersReady());
		client2.setReadyToPlay(true);
		assertTrue(room.allPlayersReady());

		assertEquals(3, room.getMapID());
		assertEquals(maps.size() - 1, room.getMaxMapID());

		assertEquals(4, room.getMaxPlayer());
		room.setMaxPlayer(3);
		assertEquals(3, room.getMaxPlayer());
		room.setMaxPlayer(4);
		assertEquals(4, room.getMaxPlayer());

		assertEquals(2, room.getHumanPlayerNumber());
		assertEquals(0, room.getAIPlayerNumber());
		assertEquals(2, room.getPlayerNumber());

		room.addAI();
		room.addAI();
		room.addAI();
		room.addAI();
		room.addAI();

		assertEquals(2, room.getHumanPlayerNumber());
		assertEquals(2, room.getAIPlayerNumber());
		assertEquals(4, room.getPlayerNumber());

		room.addCustomMap(null);
		assertEquals(maps.size() - 1, room.getMaxMapID());

		room.addCustomMap(new Maps().getMaps().get(1));
		assertEquals(maps.size(), room.getMaxMapID());

		room.addCustomMap(new Maps().getMaps().get(2));
		assertEquals(maps.size() + 1, room.getMaxMapID());

		room.removeAI();
		room.removeAI();
		room.removeAI();

		assertEquals(2, room.getHumanPlayerNumber());
		assertEquals(0, room.getAIPlayerNumber());
		assertEquals(2, room.getPlayerNumber());

		room.removePlayer(client2);
		assertEquals(1, room.getHumanPlayerNumber());
		assertEquals(0, room.getAIPlayerNumber());
		assertEquals(1, room.getPlayerNumber());

	}

}
