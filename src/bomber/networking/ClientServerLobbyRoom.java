package bomber.networking;

/**
 * 
 * Client side representation of rooms on the server lobby
 *
 */
public class ClientServerLobbyRoom {
	private int id;
	private String name;
	private int playerNumber;
	private int maxPlayer;
	private boolean inGame;
	private int mapID;

	/**
	 * Construct a ClientServerLobbyRoom object
	 * 
	 * @param id
	 *            the id of the room
	 * @param name
	 *            the name of the room
	 * @param playerNumber
	 *            the number of players in the room
	 * @param maxPlayer
	 *            the max number of players allowed in the room
	 * @param inGame
	 *            true if there is a game in progress in the room
	 * @param mapID
	 *            the map id of the room
	 */
	public ClientServerLobbyRoom(int id, String name, int playerNumber, int maxPlayer, boolean inGame, int mapID) {
		this.id = id;
		this.name = name;
		this.playerNumber = playerNumber;
		this.setMaxPlayer(maxPlayer);
		this.inGame = inGame;
		this.mapID = mapID;
	}

	/**
	 * Get the id of the room
	 * 
	 * @return the id of the room
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
		this.name = name;
	}

	/**
	 * Get the number of players in the room
	 * 
	 * @return the number of players in the room
	 */
	public int getPlayerNumber() {
		return playerNumber;
	}

	/**
	 * Set the number of players in the room
	 * 
	 * @param playerNumber
	 *            the number of players in the room
	 */
	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}

	/**
	 * Determine whether there is a game in progress in the room
	 * 
	 * @return true if there is a game in progress in the room
	 */
	public boolean isInGame() {
		return inGame;
	}

	/**
	 * Set whether there is a game in progress in the room
	 * 
	 * @param inGame
	 *            true if there is a game in progress in the room
	 */
	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	/**
	 * Get the map id of the room
	 * 
	 * @return the map id of the room
	 */
	public int getMapID() {
		return mapID;
	}

	/**
	 * Set the map id of the room
	 * 
	 * @param mapID
	 *            the map id of the room
	 */
	public void setMapID(int mapID) {
		this.mapID = mapID;
	}

	/**
	 * Get the max number of players allowed in the room
	 * 
	 * @return the max number of players allowed in the room
	 */
	public int getMaxPlayer() {
		return maxPlayer;
	}

	/**
	 * Set the max number of players allowed in the room
	 * 
	 * @param maxPlayer
	 *            the max number of players allowed in the room
	 */
	public void setMaxPlayer(int maxPlayer) {
		this.maxPlayer = maxPlayer;
	}

}
