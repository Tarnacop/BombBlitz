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
	private int[] playerID;

	/**
	 * Construct a ClientServerLobbyRoom object
	 * 
	 * @param id
	 *            the id of the room
	 * @param name
	 *            the name of the room
	 * @param playerNumber
	 *            the number of players(human + AI) in the room
	 * @param maxPlayer
	 *            the max number of players allowed in the room
	 * @param inGame
	 *            true if there is a game in progress in the room
	 * @param mapID
	 *            the map id of the room
	 * @param playerID
	 *            the array of id of human players in the room
	 */
	public ClientServerLobbyRoom(int id, String name, int playerNumber, int maxPlayer, boolean inGame, int mapID,
			int[] playerID) {
		this.id = id;
		this.name = name;
		this.playerNumber = playerNumber;
		this.maxPlayer = maxPlayer;
		this.inGame = inGame;
		this.mapID = mapID;
		this.playerID = playerID;
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
	 * Get the name of the room
	 * 
	 * @return the name of the room
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the number of players(human + AI) in the room
	 * 
	 * @return the number of players in the room
	 */
	public int getPlayerNumber() {
		return playerNumber;
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
	 * Get the map id of the room
	 * 
	 * @return the map id of the room
	 */
	public int getMapID() {
		return mapID;
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
	 * Get an array of id of human players in the room
	 * 
	 * @return an array of id of human players in the room
	 */
	public int[] getPlayerID() {
		return playerID;
	}
}
