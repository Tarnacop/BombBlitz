package bomber.networking;

import java.awt.Point;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import bomber.game.GameState;
import bomber.game.KeyboardState;
import bomber.game.Map;
import bomber.game.Player;
import bomber.physics.PhysicsEngine;

/**
 * 
 * Server side representation of a game session in a room
 *
 */
public class ServerGame implements Runnable {
	private GameState gameState;

	private boolean inGame;

	private int roomID;

	private int mapID;

	private List<ServerClientInfo> playerList;

	private int tickRate;

	private int interval;

	private ServerThread serverThread;

	private List<Map> mapList = TestMaps.getMaps();

	private Thread thread = new Thread(this);

	private boolean shouldRun;

	public ServerGame(int roomID, int mapID, List<ServerClientInfo> playerList, int tickRate,
			ServerThread serverThread) {
		/*
		 * TODO currently if the map with this id cannot be found, we use test
		 * map with id 0
		 */
		this.roomID = roomID;
		this.mapID = mapID;
		this.playerList = playerList;
		this.tickRate = tickRate;
		if (this.tickRate < 1) {
			this.tickRate = 1;
		} else if (this.tickRate > 1000) {
			this.tickRate = 1000;
		}
		this.interval = 1000 / this.tickRate;
		if (this.interval < 1) {
			this.interval = 1;
		}
		this.serverThread = serverThread;
	}

	public void setMapID(int mapID) {
		this.mapID = mapID;
	}

	public int getMapID() {
		return mapID;
	}

	/**
	 * Check whether the map ID is valid
	 * 
	 * @return true if the map ID is valid
	 */
	public boolean isMapIDValid() {
		if (isMapIDValidreal()) {
			return true;
		} else {
			this.mapID = 0;
			return isMapIDValidreal();
		}
	}

	/*
	 * TODO currently if the map with this id cannot be found, we use test map
	 * with id 0
	 */
	private boolean isMapIDValidreal() {
		if (mapList == null || mapList.size() - 1 < mapID) {
			return false;
		}
		return mapList.get(mapID) != null;
	}

	/**
	 * Check whether the game is in progress
	 * 
	 * @return true if the game is in progress
	 */
	public boolean isInGame() {
		if (gameState == null) {
			inGame = false;
			return false;
		}
		return inGame;
	}

	/**
	 * Set the KeyboardState of a player
	 * 
	 * @param playerID
	 *            the ID of the player
	 * @param keyState
	 *            the KeyboardState
	 */
	public void setPlayerKeyState(int playerID, KeyboardState keyState) {
		if (gameState != null) {
			List<Player> players = gameState.getPlayers();
			if (players != null) {
				for (Player p : players) {
					if (p != null) {
						if (p.getPlayerID() == playerID) {
							p.setKeyState(keyState);
						}
					}
				}
			}
		}
	}

	/**
	 * Initialise and start running the game thread
	 */
	public void start() {
		shouldRun = true;
		inGame = true;
		thread.start();
	}

	/**
	 * Terminate the game thread manually. Normally the game thread will
	 * terminate itself when the game is over
	 */
	public void terminate() {
		shouldRun = false;
	}

	@Override
	public void run() {
		inGame = true;

		System.out.printf("ServerGame: game thread for room %d started, tick rate: %d, interval: %d\n", roomID,
				tickRate, interval);

		if (!isMapIDValid()) {
			System.out.println("ServerGame: attemping to start game thread with invalid map ID");
			inGame = false;
			return;
		}

		// initialise gameState
		List<Player> players = new ArrayList<Player>();
		for (int i = 0; i < playerList.size(); i++) {
			/*
			 * TODO we can choose the initial position of the player based on
			 * the index
			 */
			// currently just use hard-coded initial position
			ServerClientInfo c = playerList.get(i);
			if (c == null) {
				System.out.println("ServerGame: playerList contains null");
			} else {
				Player p = new Player(c.getName(), new Point(64, 64), 100, 300, null);
				p.setPlayerID(c.getID());
				players.add(p);
			}
		}
		gameState = new GameState(mapList.get(mapID), players);

		// initialise send buffer
		byte[] sendBuffer = new byte[2000];
		ByteBuffer sendByteBuffer = ByteBuffer.wrap(sendBuffer);
		int packetLen = 0;
		DatagramPacket packet = new DatagramPacket(sendBuffer, 0);

		// initialise physics engine
		PhysicsEngine physics = new PhysicsEngine(gameState);

		// tell clients the game is started
		sendByteBuffer.position(3);
		sendByteBuffer.putInt(roomID);
		packet.setLength(1 + 2 + 4);

		for (ServerClientInfo c : playerList) {
			packet.setSocketAddress(c.getSocketAddress());
			try {
				serverThread.sendPacket(packet, ProtocolConstant.MSG_S_ROOM_GAMESTART, true);
			} catch (IOException e) {
				System.out.println("ServerGame: failed to send packet: " + e);
			}
		}

		long loopStartTime = System.currentTimeMillis();
		int busyTime = 0;
		int sleepTime = interval;

		// thread will end when it is interrupted or the game is over
		while (shouldRun && !gameState.gameOver()) {
			loopStartTime = System.currentTimeMillis();

			// game is over when only one player is left in the room
			if (playerList.size() < 2) {
				System.out.printf("ServerGame: ending game in room %d due to less than 2 players left\n", roomID);
				terminate();
			}

			// sync between players and playerList
			for (Player p : players) {
				/*
				 * Does each Player in players still have a corresponding
				 * ServerClientInfo in playerList ?
				 */
				boolean shouldRemove = true;
				for (ServerClientInfo c : playerList) {
					if (p != null && c != null && p.getPlayerID() == c.getID()) {
						shouldRemove = false;
					}
				}
				if (shouldRemove && p != null) {
					// If no, kill this player
					System.out.printf("ServerGame: killing player %d due to not in room\n", p.getPlayerID());
					p.setLives(0);
					p.setAlive(false);
				}
			}

			// update gameState
			physics.update(interval);

			// encode gameState
			try {
				packetLen = ServerPacketEncoder.encodeGameState(gameState, roomID, sendBuffer);
			} catch (IOException e) {
				System.out.println("ServerGame: failed to encode game state: " + e);
				continue;
			}
			packet.setLength(packetLen);

			// send new gameState to clients
			for (ServerClientInfo c : playerList) {

				packet.setSocketAddress(c.getSocketAddress());
				try {
					serverThread.sendPacket(packet, ProtocolConstant.MSG_S_ROOM_GAMESTATE, false);
				} catch (IOException e) {
					System.out.println("ServerGame: failed to send packet: " + e);
				}
			}

			// sleep according to tick rate
			busyTime = (int) (System.currentTimeMillis() - loopStartTime);
			sleepTime = interval - busyTime;
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					System.out.println("ServerGame: thread is interrupted");
				}
			}

			/*
			 * System.out.printf(
			 * "ServerGame: tickrate: %d, interval: %d busy time: %d sleep time: %d\n"
			 * , tickRate, interval, busyTime, sleepTime);
			 */
		}

		// tell clients the game is over
		sendByteBuffer.position(3);
		sendByteBuffer.putInt(roomID);
		packet.setLength(1 + 2 + 4);

		for (ServerClientInfo c : playerList) {
			packet.setSocketAddress(c.getSocketAddress());
			try {
				serverThread.sendPacket(packet, ProtocolConstant.MSG_S_ROOM_GAMEOVER, true);
			} catch (IOException e) {
				System.out.println("ServerGame: failed to send packet: " + e);
			}
		}

		// set all the players to not ready after the game
		for (ServerClientInfo c : playerList) {
			if (c != null) {
				c.setReadyToPlay(false);
			}
		}

		System.out.printf("ServerGame: game thread for room %d terminating\n", roomID);

		inGame = false;
	}

}
