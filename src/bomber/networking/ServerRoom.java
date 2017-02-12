package bomber.networking;

import java.util.ArrayList;

/**
 * 
 * Server side representation of a room
 *
 */
public class ServerRoom {
	// id of the room, uniqueness required
	private int id;
	// name of the room, uniqueness required
	private String name;
	// list of players in the room
	private ArrayList<ServerClientInfo> playerList = new ArrayList<ServerClientInfo>(4);
	// max number of players allowed in the room (in the range [2,4])
	private byte maxPlayer = 4;
	// flag indicating whether the game is in progress
	private boolean inGame = false;
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
	 */
	public ServerRoom(String name, ServerClientInfo firstPlayer) {
		if (name == null) {
			this.name = "Room " + id;
		} else {
			this.name = name;
		}
		playerList.add(firstPlayer);
		game = new ServerGame(0);
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
	 */
	public ServerRoom(String name, ServerClientInfo firstPlayer, byte maxPlayer) {
		if (name == null) {
			this.name = "Room " + id;
		} else {
			this.name = name;
		}
		playerList.add(firstPlayer);
		if (maxPlayer >= 2 && maxPlayer <= 4) {
			this.setMaxPlayer(maxPlayer);
		}
		game = new ServerGame(0);
	}

	/**
	 * Construct a room with a default name
	 * 
	 * @param firstPlayer
	 *            the player who created this room
	 */
	public ServerRoom(ServerClientInfo firstPlayer) {
		this.name = "Room " + id;
		playerList.add(firstPlayer);
		game = new ServerGame(0);
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
	 * Get the list of players in the room. It is not advisable to modify the
	 * list directly
	 * 
	 * @return the list of players in the room
	 */
	public ArrayList<ServerClientInfo> getPlayerList() {
		return playerList;
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
	 * Set the current game of the room
	 * 
	 * @param game
	 *            the current game
	 */
	public void setGame(ServerGame game) {
		this.game = game;
	}

	/**
	 * Returns true if there is a game in progress
	 * 
	 * @return true if there is a game in progress
	 */
	public boolean isInGame() {
		return inGame;
	}

	/**
	 * Set whether there is a game in progress
	 * 
	 * @param inGame
	 *            true if a game is in progress
	 */
	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	/**
	 * Get the number of players in the room
	 * 
	 * @return the number of players in the room
	 */
	public int getPlayerNumber() {
		return playerList.size();
	}

	/**
	 * Add a player to the room
	 * 
	 * @param player
	 *            the player to be added
	 */
	public void addPlayer(ServerClientInfo player) {
		if (playerList.size() >= 4) {
			System.err.println("Unexpected addPlayer(): Room full");
			return;
		}

		if (player == null) {
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
	 * Get the max number of players allowed in this room
	 * 
	 * @return the max number of players
	 */
	public byte getMaxPlayer() {
		return maxPlayer;
	}

	/**
	 * Set the max number of players allowed in this room (in the range [2,4])
	 * 
	 * @param maxPlayer
	 *            the max number of players
	 */
	public void setMaxPlayer(byte maxPlayer) {
		if (maxPlayer >= 2 && maxPlayer <= 4) {
			this.maxPlayer = maxPlayer;
		}
	}

	public int getMapID() {
		if (this.game == null) {
			return -1;
		}

		return this.game.getMapID();
	}

	public void setMapID(int mapID) {
		if (this.game != null) {
			this.game.setMapID(mapID);
		}
	}

}
