package test.networking;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.game.KeyboardState;
import bomber.game.Maps;
import bomber.networking.ServerAI;
import bomber.networking.ServerClientInfo;
import bomber.networking.ServerGame;

public class ServerGameTest {
	private ServerGame game;

	@Before
	public void setUp() throws Exception {
		List<ServerClientInfo> playerList = new ArrayList<ServerClientInfo>();
		ServerClientInfo c1 = new ServerClientInfo(null, "Client 1");
		c1.setID(0);
		ServerClientInfo c2 = new ServerClientInfo(null, "Client 2");
		c2.setID(1);
		playerList.add(c1);
		playerList.add(c2);
		List<ServerAI> aiList = new ArrayList<>();
		aiList.add(new ServerAI((byte) 0));
		aiList.add(new ServerAI((byte) 1));
		game = new ServerGame(0, 3, new Maps().getMaps().get(0), playerList, aiList, 40, null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertFalse(game.isInGame());

		game.setPlayerKeyState(0, new KeyboardState());
		game.setPlayerKeyState(1, new KeyboardState());

		game.setPlayerKeyState(-123331, new KeyboardState());
		game.setPlayerKeyState(331, new KeyboardState());
	}

}
