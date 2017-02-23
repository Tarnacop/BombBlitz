package bomber.networking.tests;

import java.awt.Point;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import bomber.game.Block;
import bomber.game.GameState;
import bomber.game.Map;
import bomber.game.Movement;
import bomber.game.Player;
import bomber.networking.ClientPacketEncoder;
import bomber.networking.ClientServerLobbyRoom;
import bomber.networking.ClientServerPlayer;
import bomber.networking.ServerClientInfo;
import bomber.networking.ServerClientTable;
import bomber.networking.ServerPacketEncoder;
import bomber.networking.ServerRoom;
import bomber.networking.ServerRoomTable;
import bomber.networking.ServerThread;

public class PacketEncodeDecodeTest {

	public static void main(String[] args) {

		// player list test
		ServerClientTable clientTable = new ServerClientTable(32);

		for (int i = 0; i < 32; i++) {
			SocketAddress sockAddr = new InetSocketAddress("12.12.12." + i, 1221);
			ServerClientInfo client = new ServerClientInfo(sockAddr, "client " + i);
			clientTable.put(client);
		}

		byte[] arr = new byte[2000];
		int ret = 0;
		try {
			ret = ServerPacketEncoder.encodePlayerList(clientTable, arr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("encodePlayerList returns " + ret);
		System.out.println(ServerThread.toHex(arr, ret));

		List<ClientServerPlayer> playerList = null;
		try {
			playerList = ClientPacketEncoder.decodePlayerList(arr, ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("decodePlayerList:");
		for (ClientServerPlayer p : playerList) {
			System.out.printf("ID: %d, Name: %s\n", p.getID(), p.getName());
		}

		System.out.println();

		// room list test
		ServerRoomTable roomTable = new ServerRoomTable(32);

		for (int i = 0; i < 32; i++) {
			ServerRoom room = new ServerRoom("test_room " + i, clientTable.get(i), i);
			roomTable.put(room);
		}

		try {
			ret = ServerPacketEncoder.encodeRoomList(roomTable, arr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("encodeRoomList returns " + ret);
		System.out.println(ServerThread.toHex(arr, ret));

		List<ClientServerLobbyRoom> roomList = null;
		try {
			roomList = ClientPacketEncoder.decodeRoomList(arr, ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("decodeRoomList:");
		for (ClientServerLobbyRoom r : roomList) {
			System.out.printf("ID: %d, Name: %s, Number of players: %d, Max players: %d, inGame: %b, Map ID: %d\n",
					r.getID(), r.getName(), r.getPlayerNumber(), r.getMaxPlayer(), r.isInGame(), r.getMapID());
		}

		System.out.println();

		// game state test
		Player testPlayer = new Player("testPlayer", new Point(64, 64), 100, 300, null);
		testPlayer.getKeyState().setBomb(true);
		testPlayer.getKeyState().setMovement(Movement.RIGHT);
		ArrayList<Player> testPlayerList = new ArrayList<Player>();
		testPlayerList.add(testPlayer);

		Block[][] testGridMap = new Block[][] { { Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID },
				{ Block.SOLID, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOLID },

				{ Block.SOLID, Block.SOLID, Block.SOFT, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.SOFT, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID, Block.SOLID },

				{ Block.SOLID, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.SOFT, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.SOFT, Block.SOLID },
				{ Block.SOLID, Block.SOFT, Block.BLANK, Block.SOFT, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID } };
		Map testMap = new Map("test map", testGridMap, new ArrayList<>());

		GameState testGameState = new GameState(testMap, testPlayerList);

		System.out.println("GameState to test:");
		System.out.println(testGameState);

		try {
			ret = ServerPacketEncoder.encodeGameState(testGameState, 123, arr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("encodeGameState returns " + ret);
		System.out.println(ServerThread.toHex(arr, ret));

		try {
			testGameState = ClientPacketEncoder.decodeGameState(arr, ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("decodeGameState:");
		System.out.println(testGameState);

	}

}
