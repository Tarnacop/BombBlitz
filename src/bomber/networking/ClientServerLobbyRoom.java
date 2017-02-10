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

	public ClientServerLobbyRoom(int id, String name, int playerNumber, int maxPlayer, boolean inGame, int mapID) {
		this.id = id;
		this.name = name;
		this.playerNumber = playerNumber;
		this.setMaxPlayer(maxPlayer);
		this.inGame = inGame;
		this.mapID = mapID;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

	public void setPlayerNumber(int playerNumber) {
		this.playerNumber = playerNumber;
	}

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	public int getMapID() {
		return mapID;
	}

	public void setMapID(int mapID) {
		this.mapID = mapID;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}

	public void setMaxPlayer(int maxPlayer) {
		this.maxPlayer = maxPlayer;
	}

}
