package bomber.networking;

/**
 * 
 * Server side representation of a game session in a room
 *
 */
public class ServerGame {
	// private GameState gameState;
	private int mapID = -1;

	public ServerGame(int mapID) {
		this.mapID = mapID;
	}

	public void setMapID(int mapID) {
		this.mapID = mapID;
	}

	public int getMapID() {
		return mapID;
	}
}
