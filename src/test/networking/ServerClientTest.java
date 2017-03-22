package test.networking;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.AI.AIDifficulty;
import bomber.game.KeyboardState;
import bomber.game.Movement;
import bomber.networking.ClientNetInterface;
import bomber.networking.ClientServerRoom;
import bomber.networking.ClientThread;
import bomber.networking.ServerConfiguration;
import bomber.networking.ServerThread;

public class ServerClientTest implements ClientNetInterface {
	private int port;
	private ServerThread server;
	private ClientThread client;
	private boolean disconnected;
	private boolean connectionAttemptTimeout;
	private boolean connectionAccepted;
	private boolean connectionRejected;
	private boolean alreadyConnected;
	private boolean notConnected;
	private boolean playerListReceived;
	private boolean roomListReceived;
	private boolean roomAccepted;
	private boolean roomRejected;
	private boolean notInRoom;
	private boolean alreadyInRoom;
	private boolean haveLeftRoom;
	private boolean roomReceived;
	private boolean gameStarted;
	private boolean gameStateReceived;
	private boolean gameEnded;

	@Before
	public void setUp() throws Exception {
		port = 1423;
		ServerConfiguration config = new ServerConfiguration();
		config.setTickRate(60);

		try {
			server = new ServerThread(port);
		} catch (SocketException e) {
			fail(e.toString());
		}
		server.exit();

		try {
			server = new ServerThread(port, System.out, config);
		} catch (SocketException e) {
			fail(e.toString());
		}
		server.exit();

		try {
			server = new ServerThread(port, config);
		} catch (SocketException e) {
			fail(e.toString());
		}

		try {
			client = new ClientThread("127.0.0.1", port, System.out);
		} catch (SocketException e) {
			fail(e.toString());
		}
		client.exit();

		try {
			client = new ClientThread("127.0.0.1", port);
		} catch (SocketException e) {
			fail(e.toString());
		}

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Thread serverThread = new Thread(server);
		serverThread.start();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}

		client.addNetListener(this);
		client.removeNetListener(this);
		client.addNetListener(this);
		client.removeAllNetListener();
		client.addNetListener(this);

		Thread clientThread = new Thread(client);
		clientThread.start();

		try {
			client.addAI();
			client.addRoomMap(server.defaultMap());
			client.createRoom("N", 4, 3);
			client.disconnect();
			client.getClientID();
			client.getConnectionRejectedReason();
			client.getGameState();
			client.getMapHeight();
			client.getMapID();
			client.getMapWidth();
			client.getName();
			client.getPlayerList();
			client.getRoom();
			client.getRoomID();
			client.getRoomList();
			client.getRoomRejectedReason();
			client.isConnected();
			client.isInGame();
			client.isInLobby();
			client.isInRoom();
			client.joinRoom(13);
			client.leaveRoom();
			client.readyToPlay(true);
			client.removeAI();
			client.removeNetListener(this);
			client.addNetListener(this);
			client.sendMove(new KeyboardState());
			client.sendRaw(
					new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x54, 0x53, 0x6F, 0x75, 0x72, 0x63,
							0x65, 0x20, 0x45, 0x6E, 0x67, 0x69, 0x6E, 0x65, 0x20, 0x51, 0x75, 0x65, 0x72, 0x79, 0x00 });
			client.sendRaw(
					new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x54, 0x53, 0x6F, 0x75, 0x72, 0x63,
							0x65, 0x20, 0x55, 0x6E, 0x67, 0x69, 0x6E, 0x65, 0x20, 0x51, 0x75, 0x65, 0x72, 0x79, 0x00 });
			client.setAIDifficulty(0, AIDifficulty.EXTREME);
			client.setRoomMapID(2);
			client.setRoomMaxPlayer(3);
			client.setRoomName("R");
			client.updatePlayerList();
			client.updateRoomList();
		} catch (IOException e) {
			fail(e.toString());
		}
		for (int i = 0; i < 2000; i++) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
			if (notConnected) {
				assertFalse(client.isConnected());
				break;
			}
		}
		if (!notConnected) {
			fail("Server failed to detect the client has not connected");
		}

		try {
			client.sendRaw(new byte[] { 1, 2, 3 });

			client.connect("P111111111111111111111111111111111");
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (connectionAttemptTimeout) {
					fail("Failed to connect to server due to timeout");
				} else if (connectionRejected) {
					break;
				} else if (connectionAccepted) {
					assertTrue(client.isConnected());
					assertTrue(client.isInLobby());
					assertFalse(client.isInGame());
					assertFalse(client.isInRoom());
					assertEquals("P1", client.getName());
					assertTrue(client.getClientID() >= 0);
					break;
				}
			}
			if (!connectionAttemptTimeout && !connectionRejected && !connectionAccepted) {
				fail("Connection not timeout, rejected or accepted");
			}
			connectionAttemptTimeout = false;
			connectionRejected = false;
			connectionAccepted = false;

			client.connect("P1");
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (connectionAttemptTimeout) {
					fail("Failed to connect to server due to timeout");
				} else if (connectionRejected) {
					fail("Connection rejected by server, reason: " + client.getConnectionRejectedReason());
				} else if (connectionAccepted) {
					assertTrue(client.isConnected());
					assertTrue(client.isInLobby());
					assertFalse(client.isInGame());
					assertFalse(client.isInRoom());
					assertEquals("P1", client.getName());
					assertTrue(client.getClientID() >= 0);
					break;
				}
			}
			if (!connectionAttemptTimeout && !connectionRejected && !connectionAccepted) {
				fail("Connection not timeout, rejected or accepted");
			}

			client.connect("XD");
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (alreadyConnected) {
					assertTrue(client.isConnected());
					assertTrue(client.isInLobby());
					assertFalse(client.isInGame());
					assertFalse(client.isInRoom());
					assertEquals("P1", client.getName());
					assertTrue(client.getClientID() >= 0);
					break;
				}
			}
			if (!alreadyConnected) {
				fail("Server did not detect that the client has already connected");
			}

			client.updatePlayerList();
			client.updateRoomList();
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (playerListReceived && roomListReceived) {
					assertEquals(1, client.getPlayerList().size());
					assertEquals(0, client.getRoomList().size());
					break;
				}
			}
			if (!playerListReceived || !roomListReceived) {
				fail("Failed to receive player list and/or room list");
			}

			client.leaveRoom();
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (notInRoom) {
					assertTrue(client.isInLobby());
					assertFalse(client.isInGame());
					assertFalse(client.isInRoom());
					assertTrue(client.getRoomRejectedReason() > -1);
					break;
				}
			}
			if (!notInRoom) {
				fail("Server did not detect the client is already not in room");
			}

			client.joinRoom(13);
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (roomAccepted) {
					assertFalse(client.isInLobby());
					assertFalse(client.isInGame());
					assertTrue(client.isInRoom());
					client.leaveRoom();
					for (int j = 0; j < 2000; j++) {
						Thread.sleep(1);
						if (haveLeftRoom) {
							assertTrue(client.isInLobby());
							assertFalse(client.isInGame());
							assertFalse(client.isInRoom());
							break;
						}
					}
					if (!haveLeftRoom) {
						fail("Failed to leave room");
					}
				} else if (roomRejected) {
					assertTrue(client.isInLobby());
					assertFalse(client.isInGame());
					assertFalse(client.isInRoom());
					assertTrue(client.getRoomRejectedReason() > -1);
					break;
				}
			}
			if (!roomAccepted && !roomRejected) {
				fail("Failed to join room");
			}
			roomRejected = false;

			client.createRoom("R00900000000000000000000000000000000000000000", 55, 1234);
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (roomAccepted) {
					fail("Server failed to check room name length");
				} else if (roomRejected) {
					assertTrue(client.isInLobby());
					assertFalse(client.isInGame());
					assertFalse(client.isInRoom());
					assertTrue(client.getRoomRejectedReason() > -1);
					break;
				}
			}
			if (!roomAccepted && !roomRejected) {
				fail("Failed to create room");
			}

			roomAccepted = false;
			roomRejected = false;

			client.createRoom("R0", 3, 5);
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (roomAccepted) {
					assertFalse(client.isInLobby());
					assertFalse(client.isInGame());
					assertTrue(client.isInRoom());
				} else if (roomRejected) {
					assertTrue(client.isInLobby());
					assertFalse(client.isInGame());
					assertFalse(client.isInRoom());
					assertTrue(client.getRoomRejectedReason() > -1);
					fail("Failed to create room, reason: " + client.getRoomRejectedReason());
				}
			}
			if (!roomAccepted && !roomRejected) {
				fail("Failed to create room");
			}

			roomAccepted = false;
			roomRejected = false;
			client.joinRoom(0);
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (roomAccepted) {
					fail("Client should not be able to join another room when it is already in a room");
				} else if (alreadyInRoom) {
					assertFalse(client.isInLobby());
					assertFalse(client.isInGame());
					assertTrue(client.isInRoom());
					assertTrue(client.getRoomRejectedReason() > -1);
					break;
				}
			}
			if (!alreadyInRoom) {
				fail("Server did not detect the client is already in a room");
			}

			roomAccepted = false;
			roomRejected = false;
			client.createRoom("R0", 3, 5);
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (roomAccepted) {
					fail("Client should not be able to create another room when it is already in a room");
				} else if (alreadyInRoom) {
					assertFalse(client.isInLobby());
					assertFalse(client.isInGame());
					assertTrue(client.isInRoom());
					assertTrue(client.getRoomRejectedReason() > -1);
					break;
				}
			}
			if (!alreadyInRoom) {
				fail("Server did not detect the client is already in a room");
			}

			client.addRoomMap(server.defaultMap());
			client.setRoomName("Roooooooooooooooooooooooooooo");
			client.setRoomMapID(-123);
			client.setRoomMapID(133);
			client.setRoomMaxPlayer(0);
			client.setRoomMaxPlayer(5);
			client.setRoomMapID(6);
			client.setRoomMaxPlayer(4);
			client.setRoomName("RR0");
			client.removeAI();
			client.removeAI();
			client.addAI();
			client.addAI();
			client.addAI();
			client.addAI();
			client.addAI();
			client.setAIDifficulty(0, AIDifficulty.EASY);
			client.setAIDifficulty(1, AIDifficulty.MEDIUM);
			client.setAIDifficulty(2, AIDifficulty.HARD);
			client.setAIDifficulty(13, AIDifficulty.EXTREME);
			Thread.sleep(500);
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (roomReceived) {
					assertTrue(client.getRoomID() > -1);
					assertTrue(client.getMapID() == 6);
					ClientServerRoom room = client.getRoom();
					assertEquals(3, room.getAIPlayerList().size());
					assertEquals(3, room.getAIPlayerNumber());
					assertEquals(1, room.getHumanPlayerList().size());
					assertEquals(1, room.getHumanPlayerNumber());
					assertEquals(client.getRoomID(), room.getID());
					assertEquals(client.getMapID(), room.getMapID());
					assertTrue(room.getMapID() <= room.getMaxMapID());
					assertEquals(4, room.getMaxPlayer());
					assertEquals("RR0", room.getName());
					assertFalse(room.isInGame());
					break;
				}
			}
			if (!roomReceived) {
				fail("Failed to receive room info");
			}

			playerListReceived = false;
			roomListReceived = false;
			client.updatePlayerList();
			client.updateRoomList();
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (playerListReceived && roomListReceived) {
					assertEquals(1, client.getPlayerList().size());
					assertEquals(1, client.getRoomList().size());
					break;
				}
			}
			if (!playerListReceived || !roomListReceived) {
				fail("Failed to receive player list and/or room list");
			}

			client.readyToPlay(false);
			client.readyToPlay(true);
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (gameStarted) {
					assertFalse(client.isInLobby());
					assertTrue(client.isInGame());
					assertTrue(client.isInRoom());
					assertTrue(client.getMapHeight() > 0);
					assertTrue(client.getMapWidth() > 0);
					break;
				}
			}
			if (!gameStarted) {
				fail("Failed to start game");
			}

			for (int i = 0; i < 200; i++) {
				Thread.sleep(1);
				KeyboardState k = new KeyboardState();
				k.setMovement(Movement.RIGHT);
				k.setBomb(false);
				client.sendMove(k);
			}

			for (int i = 0; i < 200; i++) {
				Thread.sleep(1);
				KeyboardState k = new KeyboardState();
				k.setMovement(Movement.DOWN);
				k.setBomb(true);
				client.sendMove(k);
			}

			for (int i = 0; i < 200; i++) {
				Thread.sleep(1);
				KeyboardState k = new KeyboardState();
				k.setMovement(Movement.LEFT);
				k.setBomb(false);
				client.sendMove(k);
			}

			for (int i = 0; i < 200; i++) {
				Thread.sleep(1);
				KeyboardState k = new KeyboardState();
				k.setMovement(Movement.UP);
				k.setBomb(true);
				client.sendMove(k);
			}

			assertTrue(gameStateReceived);
			assertNotNull(client.getGameState());
			assertEquals(4, client.getGameState().getPlayers().size());

			haveLeftRoom = false;
			client.leaveRoom();
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (haveLeftRoom) {
					assertTrue(client.isInLobby());
					assertFalse(client.isInGame());
					assertFalse(client.isInRoom());
					break;
				}
			}
			if (!haveLeftRoom) {
				fail("Failed to leave room");
			}

			client.disconnect();
			for (int i = 0; i < 2000; i++) {
				Thread.sleep(1);
				if (disconnected) {
					assertFalse(client.isConnected());
					break;
				}
			}
			if (!disconnected) {
				fail("Failed to disconnect");
			}

		} catch (IOException | InterruptedException e) {
			fail(e.toString());
		}

		server.exit();

	}

	@Override
	public void disconnected() {
		this.disconnected = true;
	}

	@Override
	public void connectionAttemptTimeout() {
		this.connectionAttemptTimeout = true;
	}

	@Override
	public void connectionAccepted() {
		this.connectionAccepted = true;
	}

	@Override
	public void connectionRejected() {
		this.connectionRejected = true;
	}

	@Override
	public void alreadyConnected() {
		this.alreadyConnected = true;
	}

	@Override
	public void notConnected() {
		this.notConnected = true;
	}

	@Override
	public void playerListReceived() {
		this.playerListReceived = true;
	}

	@Override
	public void roomListReceived() {
		this.roomListReceived = true;
	}

	@Override
	public void roomAccepted() {
		this.roomAccepted = true;
	}

	@Override
	public void roomRejected() {
		this.roomRejected = true;
	}

	@Override
	public void notInRoom() {
		this.notInRoom = true;
	}

	@Override
	public void alreadyInRoom() {
		this.alreadyInRoom = true;
	}

	@Override
	public void haveLeftRoom() {
		this.haveLeftRoom = true;
	}

	@Override
	public void roomReceived() {
		this.roomReceived = true;
	}

	@Override
	public void gameStarted() {
		this.gameStarted = true;
	}

	@Override
	public void gameStateReceived() {
		this.gameStateReceived = true;
	}

	@Override
	public void gameEnded() {
		this.gameEnded = true;
	}

}
