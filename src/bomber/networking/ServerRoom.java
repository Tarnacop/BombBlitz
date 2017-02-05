package bomber.networking;

import java.util.ArrayList;

/**
 * 
 * Server side representation of a room
 *
 */
public class ServerRoom {
	private String name;
	private int id;
	private ArrayList<ServerClientInfo> playerList = new ArrayList<ServerClientInfo>(4);
	private ServerGame game;
	private boolean inGame = false;

	public ServerRoom(String name, int id, ServerClientInfo firstPlayer) {
		if (name == null) {
			this.name = "Room " + id;
		} else {
			this.name = name;
		}
		this.id = id;
		playerList.add(firstPlayer);
	}

	public ServerRoom(int id, ServerClientInfo firstPlayer) {
		this.name = "Room " + id;
		this.id = id;
		playerList.add(firstPlayer);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null) {
			this.name = name;
		}
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public ArrayList<ServerClientInfo> getPlayerList() {
		return playerList;
	}

	public ServerGame getGame() {
		return game;
	}

	public void setGame(ServerGame game) {
		this.game = game;
	}

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

}
