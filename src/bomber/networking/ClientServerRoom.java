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
	private int maxMapID;
	private List<ClientServerPlayer> humanPlayerList = new ArrayList<ClientServerPlayer>(4);
	private List<ClientServerAI> aiPlayerList = new ArrayList<ClientServerAI>(4);

	/**
	 * Construct a ClientServerRoom object
	 * 
	 * @param id
	 *            the id of the room
	 * @param name
	 *            the name of the room
	 * @param humanPlayerNumber
	 *            the number of human players in the room
	 * @param aiPlayerNumber
	 *            the number of AI players in the room
	 * @param maxPlayer
	 *            the max number of players allowed in the room
	 * @param inGame
	 *            true if there is a game in progress in the room
	 * @param mapID
	 *            the map id of the room
	 * @param maxMapID
	 *            the max map id of the room
	 */
	public ClientServerRoom(int id, String name, int humanPlayerNumber, int aiPlayerNumber, int maxPlayer,
			boolean inGame, int mapID, int maxMapID) {
		this.id = id;
		this.name = name;
		this.humanPlayerNumber = humanPlayerNumber;
		this.aiPlayerNumber = aiPlayerNumber;
		this.setMaxPlayer(maxPlayer);
		this.inGame = inGame;
		this.mapID = mapID;
		this.maxMapID = maxMapID;
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
	 * Set the name of the room
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
	 * Get the number of human players in the room
	 * 
	 * @return the number of human players in the room
	 */
	public int getHumanPlayerNumber() {
		return humanPlayerNumber;
	}

	/**
	 * Set the number of human players in the room
	 * 
	 * @param humanPlayerNumber
	 *            the number of human players in the room
	 */
	public void setHumanPlayerNumber(int humanPlayerNumber) {
		this.humanPlayerNumber = humanPlayerNumber;
	}

	/**
	 * Get the number of AI players in the room
	 * 
	 * @return the number of AI players in the room
	 */
	public int getAIPlayerNumber() {
		return aiPlayerNumber;
	}

	/**
	 * Set the number of AI players in the room
	 * 
	 * @param aiPlayerNumber
	 *            the number of AI players in the room
	 */
	public void setAIPlayerNumber(int aiPlayerNumber) {
		this.aiPlayerNumber = aiPlayerNumber;
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
	 * Get the max map id of the room. For example, if the max map id is 5, then
	 * maps with id in the range [0,5] are available in the room
	 * 
	 * @return the max map id of the room
	 */
	public int getMaxMapID() {
		return maxMapID;
	}

	/**
	 * Set the max map id of the room
	 * 
	 * @param maxMapID
	 *            the max map id of the room
	 */
	public void setMaxMapID(int maxMapID) {
		this.maxMapID = maxMapID;
	}

	/**
	 * Get the list of human players in the room
	 * 
	 * @return the list of human players in the room
	 */
	public List<ClientServerPlayer> getHumanPlayerList() {
		return humanPlayerList;
	}

	/**
	 * Set the list of human players in the room
	 * 
	 * @param humanPlayerList
	 *            the list of human players in the room
	 */
	public void setHumanPlayerList(List<ClientServerPlayer> humanPlayerList) {
		this.humanPlayerList = humanPlayerList;
	}

	/**
	 * Get the list of AI players in the room
	 * 
	 * @return the list of AI players in the room
	 */
	public List<ClientServerAI> getAIPlayerList() {
		return aiPlayerList;
	}

	/**
	 * Set the list of AI players in the room
	 * 
	 * @param aiPlayerList
	 *            the list of AI players in the room
	 */
	public void setAIPlayerList(List<ClientServerAI> aiPlayerList) {
		this.aiPlayerList = aiPlayerList;
	}

}
