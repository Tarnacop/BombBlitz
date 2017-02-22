package bomber.networking;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Client side representation of a room which the player is currently in
 *
 */
public class ClientServerRoom {
	private int id;
	private String name;
	private int humanPlayerNumber;
	private int aiPlayerNumber;
	private int maxPlayer;
	private boolean inGame;
	private int mapID;
	private List<ClientServerPlayer> humanPlayerList = new ArrayList<ClientServerPlayer>(4);
	private List<ClientServerAI> aiPlayerList = new ArrayList<ClientServerAI>(4);

	public ClientServerRoom(int id, String name, int humanPlayerNumber, int aiPlayerNumber, int maxPlayer,
			boolean inGame, int mapID) {
		this.id = id;
		this.name = name;
		this.humanPlayerNumber = humanPlayerNumber;
		this.aiPlayerNumber = aiPlayerNumber;
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

	public int getHumanPlayerNumber() {
		return humanPlayerNumber;
	}

	public void setHumanPlayerNumber(int humanPlayerNumber) {
		this.humanPlayerNumber = humanPlayerNumber;
	}

	public int getAIPlayerNumber() {
		return aiPlayerNumber;
	}

	public void setAIPlayerNumber(int aiPlayerNumber) {
		this.aiPlayerNumber = aiPlayerNumber;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}

	public void setMaxPlayer(int maxPlayer) {
		this.maxPlayer = maxPlayer;
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

	public List<ClientServerPlayer> getHumanPlayerList() {
		return humanPlayerList;
	}

	public void setHumanPlayerList(List<ClientServerPlayer> humanPlayerList) {
		this.humanPlayerList = humanPlayerList;
	}

	public List<ClientServerAI> getAIPlayerList() {
		return aiPlayerList;
	}

	public void setAIPlayerList(List<ClientServerAI> aiPlayerList) {
		this.aiPlayerList = aiPlayerList;
	}

}
