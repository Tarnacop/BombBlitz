
package bomber.networking;

import java.util.ArrayList;
import java.util.List;

import bomber.AI.AIDifficulty;

/**
 * 
 * Server side representation of a room
 *
 */
public class ServerRoom {
	// id of the room, uniqueness required
	private int id;
	// name of the room, uniqueness required
	private String name = null;
	// list of players in the room
	private List<ServerClientInfo> playerList = new ArrayList<ServerClientInfo>();
	// list of AI in the room
	private List<ServerAI> aiList = new ArrayList<ServerAI>();
	// max number of players allowed in the room (in the range [2,4])
	private int maxPlayer = 4;
	// flag indicating whether the game is in progress
	// private boolean inGame = false;
	// the map ID
	private int mapID = 0;
	// the game session
	private ServerGame game;
	// TODO consistency between constructor, getter, setter and game's mapID

	/**
	 * Construct a room
	 * 
	 * @param name
	 *            the name of the room
	 * @param firstPlayer
	 *            the player who created this room
	 * @param mapID
	 *            the initial map ID of the room
	 */
	public ServerRoom(String name, ServerClientInfo firstPlayer, int mapID) {
		if (name == null) {
			this.name = "Room " + id;
		} else {
			this.name = name;
		}

		if (firstPlayer != null) {
			playerList.add(firstPlayer);
		}

		setMapID(mapID);
	}

	/**
	 * Construct a room
	 * 
	 * @param name
	 *            the name of the room
	 * @param firstPlayer
	 *            the player who created this room
	 * @param maxPlayer
	 *            the max number of players allowed in this room(in the range
	 *            [2,4])
	 * @param mapID
	 *            the initial map ID of the room
	 */
	public ServerRoom(String name, ServerClientInfo firstPlayer, int maxPlayer, int mapID) {
		if (name == null) {
			this.name = "Room " + id;
		} else {
			this.name = name;
		}

		if (firstPlayer != null) {
			playerList.add(firstPlayer);
		}

		setMaxPlayer(maxPlayer);

		setMapID(mapID);
	}

	/**
	 * Construct a room with a default name
	 * 
	 * @param firstPlayer
	 *            the player who created this room
	 */
	public ServerRoom(ServerClientInfo firstPlayer) {
		this.name = "Room " + id;

		if (firstPlayer != null) {
			playerList.add(firstPlayer);
		}
	}

	/**
	 * Get the name of the room
	 * 
	 * @return the name of the room
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the room
	 * 
	 * @param name
	 *            the name of the room
	 */
	public void setName(String name) {
		if (name != null) {
			this.name = name;
		}
	}

	/**
	 * Get the ID of the room
	 * 
	 * @return the ID of the room
	 */
	public int getID() {
		return id;
	}

	/**
	 * Set the id of the room
	 * 
	 * @param id
	 *            the id of the room
	 */
	public void setID(int id) {
		this.id = id;
	}

	/**
	 * Returns true if all players in the room are ready to play the game
	 * 
	 * @return true if all players in the room are ready to play the game
	 */
	public boolean allPlayersReady() {
		synchronized (playerList) {
			boolean allPlayersReady = true;
			for (ServerClientInfo c : playerList) {
				if (c == null) {
					System.err.println("Bug: playerList should not contain null");
				} else {
					allPlayersReady = c.isReadyToPlay() && allPlayersReady;
				}
			}
			return allPlayersReady;
		}

	}

	/**
	 * Get the current game of the room
	 * 
	 * @return the current game
	 */
	public ServerGame getGame() {
		return game;
	}

	/**
	 * Create a new game for the room with the current mapID
	 * 
	 * @param tickRate
	 *            the tick rate at which the game will run
	 * @param serverThread
	 *            the server thread used for sending packets
	 * @return
	 */
	public boolean createGame(int tickRate, ServerThread serverThread) {
		game = new ServerGame(id, mapID, playerList, aiList, tickRate, serverThread);
		return game.isMapIDValid();
	}

	/**
	 * Returns true if there is a game in progress
	 * 
	 * @return true if there is a game in progress
	 */
	public boolean isInGame() {
		if (game == null) {
			return false;
		}
		return game.isInGame();
	}

	/**
	 * Get the number of players in the room (human + AI players)
	 * 
	 * @return the number of players in the room
	 */
	public int getPlayerNumber() {
		return playerList.size() + aiList.size();
	}

	/**
	 * Get the number of human players in the room
	 * 
	 * @return the number of human players in the room
	 */
	public int getHumanPlayerNumber() {
		return playerList.size();
	}

	/**
	 * Get the number of AI players in the room
	 * 
	 * @return the number of AI players in the room
	 */
	public int getAIPlayerNumber() {
		return aiList.size();
	}

	/**
	 * Add a player to the room
	 * 
	 * @param player
	 *            the player to be added
	 */
	public void addPlayer(ServerClientInfo player) {
		if (getPlayerNumber() >= getMaxPlayer()) {
			System.err.println("Unexpected addPlayer(): Room is full");
			return;
		}

		if (player == null) {
			System.err.println("Unexpected addPlayer(): Player is null");
			return;
		}

		if (playerList.contains(player)) {
			System.err.println("Unexpected addPlayer(): Player is already in the list");
			return;
		}

		playerList.add(player);

	}

	/**
	 * Remove a player from the room
	 * 
	 * @param player
	 *            the player to be removed
	 */
	public void removePlayer(ServerClientInfo player) {
		playerList.remove(player);
	}

	/**
	 * Add an AI player to the room
	 */
	public void addAI() {
		if (getPlayerNumber() >= getMaxPlayer()) {
			// System.err.println("Unexpected addAI(): Room is full");
			return;
		}

		aiList.add(new ServerAI((byte) aiList.size()));
	}

	/**
	 * Remove an AI player from the room
	 */
	public void removeAI() {
		if (aiList.size() > 0) {
			// remove the AI which is created most recently
			aiList.remove(aiList.size() - 1);
		}
	}

	/**
	 * Set the difficulty of an AI player
	 * 
	 * @param id
	 *            the id of the AI player, which should be in the range [0,2]
	 * @param difficulty
	 *            the difficulty of the AI player
	 */
	public void setAIDifficulty(int id, AIDifficulty difficulty) {
		if (aiList.size() > 0 && id >= 0 && id <= aiList.size() - 1) {
			ServerAI ai = aiList.get(id);
			if (ai != null) {
				ai.setDifficulty(difficulty);
			}
		}
	}

	/**
	 * Returns true if the room contains the specified player
	 * 
	 * @param player
	 *            the player whose presence in the room is to be tested
	 * @return true if the room contains the specified player
	 */
	public boolean containsPlayer(ServerClientInfo player) {
		return playerList.contains(player);
	}

	/**
	 * Get an array of human players in this room
	 * 
	 * @return an array of human players in this room
	 */
	public ServerClientInfo[] getHumanPlayers() {
		ServerClientInfo[] players = new ServerClientInfo[playerList.size()];
		for (int i = 0; i < playerList.size(); i++) {
			players[i] = playerList.get(i);
		}
		return players;
	}

	/**
	 * Get an array of AI players in this room
	 * 
	 * @return an array of AI players in this room
	 */
	public ServerAI[] getAIPlayers() {
		ServerAI[] players = new ServerAI[aiList.size()];
		for (int i = 0; i < aiList.size(); i++) {
			players[i] = aiList.get(i);
		}
		return players;
	}

	/**
	 * Get the max number of players allowed in this room
	 * 
	 * @return the max number of players
	 */
	public int getMaxPlayer() {
		return maxPlayer;
	}

	/**
	 * Set the max number of players allowed in this room (in the range [2,4]).
	 * Note that the max number of players cannot be changed when it is smaller
	 * than the number of players currently in the room
	 * 
	 * @param maxPlayer
	 *            the max number of players
	 */
	public void setMaxPlayer(int maxPlayer) {
		if (maxPlayer >= 2 && maxPlayer <= 4 && maxPlayer >= getPlayerNumber()) {
			this.maxPlayer = maxPlayer;
		}
	}

	/**
	 * Get the map ID of the room
	 * 
	 * @return the map ID
	 */
	public int getMapID() {
		return mapID;
	}

	/**
	 * Set the map ID of the room
	 * 
	 * @param mapID
	 *            the map ID
	 */
	public void setMapID(int mapID) {
		if (mapID >= 0) {
			this.mapID = mapID;
		}
	}

}
