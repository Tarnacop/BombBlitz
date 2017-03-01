package test.networking;

import java.awt.Point;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import bomber.game.AudioEvent;
import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.Map;
import bomber.game.Movement;
import bomber.game.Player;
import bomber.networking.ClientPacketEncoder;
import bomber.networking.ClientServerAI;
import bomber.networking.ClientServerLobbyRoom;
import bomber.networking.ClientServerPlayer;
import bomber.networking.ClientServerRoom;
import bomber.networking.ServerClientInfo;
import bomber.networking.ServerClientTable;
import bomber.networking.ServerPacketEncoder;
import bomber.networking.ServerRoom;
import bomber.networking.ServerRoomTable;
import bomber.networking.ServerThread;

public class PacketEncodeDecodeTest2 {

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
			System.out.printf("ID: %d, Name: %s, inRoom: %b", p.getID(), p.getName(), p.isInRoom());
			if (p.isInRoom()) {
				System.out.printf(", Room ID: %d\n", p.getRoomID());
			} else {
				System.out.println();
			}
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
			for (int id : r.getPlayerID()) {
				System.out.printf("Player ID: %d\n", id);
			}
		}

		System.out.println();

		// room test
		SocketAddress sockAddr = new InetSocketAddress("12.12.12.12", 12);
		ServerClientInfo client = new ServerClientInfo(sockAddr, "client " + 12);
		ServerRoom room = new ServerRoom("Test Room", client, 3, 1);
		room.addAI();
		room.addAI();

		try {
			ret = ServerPacketEncoder.encodeRoom(room, arr);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("encodeRoom returns " + ret);
		System.out.println(ServerThread.toHex(arr, ret));

		ClientServerRoom roomDecoded = null;
		try {
			roomDecoded = ClientPacketEncoder.decodeRoom(arr, ret);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("decodeRoom:");
		System.out.printf(
				"room ID: %d, room name: %s, human player: %d, AI player: %d, max player: %d, inGame: %b, map ID: %d\n",
				room.getID(), room.getName(), room.getHumanPlayerNumber(), room.getAIPlayerNumber(),
				room.getMaxPlayer(), room.isInGame(), room.getMapID());
		System.out.println("Human players:");
		for (ClientServerPlayer p : roomDecoded.getHumanPlayerList()) {
			System.out.printf("player ID: %d, name: %s, ready: %b\n", p.getID(), p.getName(), p.isReadyToPlay());
		}
		System.out.println("AI players:");
		for (ClientServerAI a : roomDecoded.getAIPlayerList()) {
			System.out.printf("player ID: %d\n", a.getID());
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
		List<Bomb> testBombList = testGameState.getBombs();
		testBombList.add(new Bomb("test Bomb", new Point(5, 5), 5, 5));
		List<AudioEvent> testAudioList = testGameState.getAudioEvents();
		testAudioList.add(AudioEvent.EXPLOSION);
		testAudioList.add(AudioEvent.PLACE_BOMB);
		testAudioList.add(AudioEvent.PLAYER_DEATH);
		testAudioList.add(AudioEvent.MOVEMENT);
		testAudioList.add(AudioEvent.POWERUP);

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
			testGameState = ClientPacketEncoder.decodeGameState(testGameState, arr, ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("decodeGameState:");
		System.out.println(testGameState);
		System.out.println("numAudioEvents: " + testGameState.getAudioEvents().size());
		for (AudioEvent a : testGameState.getAudioEvents()) {
			System.out.println("Audio: " + a);
		}

	}

}
