package bomber.networking;

import java.awt.Point;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import bomber.AI.AIDifficulty;
import bomber.AI.GameAI;
import bomber.game.Block;
import bomber.game.GameState;
import bomber.game.KeyboardState;
import bomber.game.Map;
import bomber.game.Maps;
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

	private List<ServerAI> aiList;

	private int tickRate;

	private int interval;

	private ServerThread serverThread;

	private List<Map> mapList = new Maps().getMaps();

	private Thread thread = new Thread(this);

	private boolean shouldRun;

	public ServerGame(int roomID, int mapID, List<ServerClientInfo> playerList, List<ServerAI> aiList, int tickRate,
			ServerThread serverThread) {
		/*
		 * TODO currently if the map with this id cannot be found, we use test
		 * map with id 0
		 */
		this.roomID = roomID;
		this.mapID = mapID;
		this.playerList = playerList;
		this.aiList = aiList;
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
		if (mapID < 0 || mapList == null || mapList.size() - 1 < mapID) {
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

		// initialise human & AI players and gameState
		List<Player> players = new ArrayList<Player>();
		Map map = mapList.get(mapID);

		gameState = new GameState(map, players);

		List<Point> spawnPoints = map.getSpawnPoints();
		int posIndex = 0;
		synchronized (playerList) {
			for (int i = 0; i < playerList.size(); i++) {
				ServerClientInfo c = playerList.get(i);
				if (c == null) {
					System.out.println("ServerGame: playerList contains null");
				} else {
					Point initPos = null;
					if (spawnPoints == null || spawnPoints.size() < posIndex + 1) {
						initPos = new Point(64, 64);
					} else {
						initPos = spawnPoints.get(posIndex);
						if (initPos == null) {
							initPos = new Point(64, 64);
						}
					}
					Player p = new Player(c.getName(), initPos, 3, 300, null);
					p.setPlayerID(c.getID());
					players.add(p);
					posIndex += 1;
				}
			}
		}

		for (int i = 0; i < aiList.size(); i++) {
			ServerAI ai = aiList.get(i);
			if (ai == null) {
				System.out.println("ServerGame: aiList contains null");
			} else {
				Point initPos = null;
				if (spawnPoints == null || spawnPoints.size() < posIndex + 1) {
					initPos = new Point(64, 64);
				} else {
					initPos = spawnPoints.get(posIndex);
					if (initPos == null) {
						initPos = new Point(64, 64);
					}
				}
				GameAI a = new GameAI("AI " + ai.getID(), initPos, 3, 300, gameState, null, AIDifficulty.EXTREME);
				a.setPlayerID(ai.getID() + 32);
				players.add(a);
				ai.setGameAI(a);
				posIndex += 1;
			}
		}

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
		sendByteBuffer.putInt(mapID);
		{
			// get the width and height of the map
			byte width = 16;
			byte height = 16;
			Block[][] gridMap = map.getGridMap();
			if (gridMap != null) {
				byte twidth = (byte) gridMap.length;
				if (twidth > 0 && twidth <= 16) {
					width = twidth;
					Block[] column = gridMap[0];
					if (column != null) {
						byte theight = (byte) column.length;
						if (theight > 0 && theight <= 16) {
							height = theight;
						}
					}
				}
			}
			sendByteBuffer.put(width);
			sendByteBuffer.put(height);
		}
		packet.setLength(1 + 2 + 4 + 4 + 1 + 1);

		synchronized (playerList) {
			for (ServerClientInfo c : playerList) {
				if (c == null) {
					continue;
				}
				packet.setSocketAddress(c.getSocketAddress());
				try {
					serverThread.sendPacket(packet, ProtocolConstant.MSG_S_ROOM_GAMESTART, true);
				} catch (IOException e) {
					System.out.println("ServerGame: failed to send packet: " + e);
				}
			}
		}

		// start AI threads
		for (ServerAI a : aiList) {
			GameAI ai = a.getGameAI();
			if (ai == null) {
				continue;
			}
			ai.begin();
		}

		long loopStartTime = System.currentTimeMillis();
		int busyTime = 0;
		int sleepTime = interval;

		// thread will end when terminate() is called or the game is over
		while (shouldRun && !gameState.gameOver()) {
			loopStartTime = System.currentTimeMillis();

			// game is over when no human player is left in the room
			if (playerList.size() < 1) {
				System.out.printf("ServerGame: ending game in room %d due to no human players in room\n", roomID);
				terminate();
			}

			// game is over when only one player is left in the room
			if (playerList.size() + aiList.size() < 2) {
				System.out.printf("ServerGame: ending game in room %d due to fewer than 2 players in room\n", roomID);
				terminate();
			}

			// sync between players and playerList
			for (Player p : players) {
				/*
				 * Does each Player in players still have a corresponding
				 * ServerClientInfo in playerList ?
				 */
				boolean shouldRemove = true;
				synchronized (playerList) {
					for (ServerClientInfo c : playerList) {
						if (p != null && c != null && p.getPlayerID() == c.getID()) {
							shouldRemove = false;
						}
					}
				}
				if (shouldRemove && p != null && p.getPlayerID() < 32 && p.getLives() != 0 && p.isAlive()) {
					// If no, kill this player
					System.out.printf("ServerGame: killing player %d due to not in room\n", p.getPlayerID());
					p.setLives(0);
					p.setAlive(false);
				}
			}

			// update gameState
			physics.update(interval);
			/*
			 * TODO physics is very likely to have
			 * ArrayIndexOutOfBoundsException when tick rate is low
			 */

			// encode gameState
			try {
				packetLen = ServerPacketEncoder.encodeGameState(gameState, roomID, sendBuffer);
			} catch (IOException e) {
				System.out.println("ServerGame: failed to encode game state: " + e);
				continue;
			}
			packet.setLength(packetLen);

			synchronized (playerList) {
				// send new gameState to clients
				for (ServerClientInfo c : playerList) {

					packet.setSocketAddress(c.getSocketAddress());
					try {
						serverThread.sendPacket(packet, ProtocolConstant.MSG_S_ROOM_GAMESTATE, false);
					} catch (IOException e) {
						System.out.println("ServerGame: failed to send packet: " + e);
					}
				}
			}

			/*
			 * TODO physics still does not check if there are duplicate audio
			 * events in the audio event list, and since movement is very
			 * frequent, the list is filled with movement sound which is quite
			 * annoying
			 */
			// TODO so empty the audio event list
			gameState.getAudioEvents().clear();

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

		}

		// terminate AI threads if there is any
		for (ServerAI a : aiList) {
			GameAI ai = a.getGameAI();
			if (ai == null) {
				continue;
			}
			ai.setLives(0);
			ai.setAlive(false);
		}
		/*
		 * TODO one AI thread seems to be unable to be stopped when there are 1
		 * human player and 3 AI players in a game
		 */
		/*
		 * TODO sometimes AI keeps attacking players who have 0 lives(maybe due
		 * to isAlive still being true ?)
		 */

		// tell clients the game is over
		sendByteBuffer.position(3);
		sendByteBuffer.putInt(roomID);
		packet.setLength(1 + 2 + 4);

		synchronized (playerList) {
			for (ServerClientInfo c : playerList) {
				packet.setSocketAddress(c.getSocketAddress());
				try {
					serverThread.sendPacket(packet, ProtocolConstant.MSG_S_ROOM_GAMEOVER, true);
				} catch (IOException e) {
					System.out.println("ServerGame: failed to send packet: " + e);
				}
				// set all the players to not ready after the game
				c.setReadyToPlay(false);
			}
		}

		System.out.printf("ServerGame: game thread for room %d terminating\n", roomID);

		inGame = false;
	}

}
