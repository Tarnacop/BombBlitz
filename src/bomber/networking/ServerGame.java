package bomber.networking;

import java.awt.Point;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import bomber.AI.GameAI;
import bomber.game.Block;
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

	private List<ServerAI> aiList;

	private int tickRate;

	private int interval;

	private ServerThread serverThread;

	private Map map;

	private Thread thread = new Thread(this);

	private boolean shouldRun;

	public ServerGame(int roomID, int mapID, Map map, List<ServerClientInfo> playerList, List<ServerAI> aiList,
			int tickRate, ServerThread serverThread) {
		this.roomID = roomID;
		this.mapID = mapID;
		this.map = map;
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

	private boolean isIDHuman(int playerID) {
		return playerID < 32;
	}

	private int aiIDtoPlayerID(int aiID) {
		return aiID + 32;
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

		// initialise human & AI players and gameState
		List<Player> players = new ArrayList<Player>();

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
				GameAI a = new GameAI("AI " + ai.getID(), initPos, 3, 300, gameState, null, ai.getDifficulty());
				a.setPlayerID(aiIDtoPlayerID(ai.getID()));
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

			// game is over when no human player is alive
			int humanPlayer = 0;
			int deadHumanPlayer = 0;
			for (Player p : players) {
				if (p != null && isIDHuman(p.getPlayerID())) {
					humanPlayer += 1;
					if (p.getLives() == 0 || !p.isAlive()) {
						deadHumanPlayer += 1;
					}
				}
			}
			if (humanPlayer == deadHumanPlayer) {
				System.out.printf("ServerGame: ending game in room %d due to no human players alive\n", roomID);
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
				if (shouldRemove && p != null && isIDHuman(p.getPlayerID()) && p.getLives() != 0 && p.isAlive()) {
					// If no, kill this player
					System.out.printf("ServerGame: killing player %d due to not in room\n", p.getPlayerID());
					p.setLives(0);
					p.setAlive(false);
				}
			}

			/*
			 * TODO physics still does not check if there are duplicate audio
			 * events in the audio event list, so we empty the audio event list
			 * each time game state is updated
			 */
			// System.out.println(gameState.getAudioEvents().size());
			gameState.getAudioEvents().clear();

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
