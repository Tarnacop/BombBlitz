package bomber.networking;

/**
 * 
 * Client side representation of human players connected to the server
 *
 */
public class ClientServerPlayer {
	private int id;
	private String name;
	private boolean readyToPlay;
	private boolean inRoom;
	private int roomID;

	/**
	 * Construct a ClientServerPlayer object for lobby player list
	 * 
	 * @param id
	 *            the id of the player
	 * @param name
	 *            the name of the player
	 * @param inRoom
	 *            true if the player is in room
	 * @param roomID
	 *            the id of the room which the player is in(only meaningful when
	 *            inRoom is true)
	 */
	public ClientServerPlayer(int id, String name, boolean inRoom, int roomID) {
		this.id = id;
		this.name = name;
		this.readyToPlay = false;
		this.inRoom = inRoom;
		if (inRoom) {
			this.roomID = roomID;
		} else {
			roomID = -1;
		}
	}

	/**
	 * Construct a ClientServerPlayer object for in-room info
	 * 
	 * @param id
	 *            the id of the player
	 * @param name
	 *            the name of the player
	 * @param readyToPlay
	 *            true if the player is ready to play (when in a room)
	 */
	public ClientServerPlayer(int id, String name, boolean readyToPlay) {
		this.id = id;
		this.name = name;
		this.readyToPlay = readyToPlay;
		this.inRoom = true;
	}

	/**
	 * Get the id of the player
	 * 
	 * @return the id of the player
	 */
	public int getID() {
		return id;
	}

	/**
	 * Get the name of the player
	 * 
	 * @return the name of the player
	 */
	public String getName() {
		return name;
	}

	/**
	 * Determine whether the player is ready to play when it is in a room (this
	 * method should only be used when the client is in a room and should NOT be
	 * used for lobby player list)
	 * 
	 * @return true if the player is ready to play
	 */
	public boolean isReadyToPlay() {
		return readyToPlay;
	}

	/**
	 * Determine whether the player is in a room (this method should only be
	 * used for lobby player list)
	 * 
	 * @return true if the player is in a room
	 */
	public boolean isInRoom() {
		return inRoom;
	}

	/**
	 * Get the id of the room which the player is in (only meaningful when
	 * isInRoom() returns true)
	 * 
	 * @return the id of the room which the player is in
	 */
	public int getRoomID() {
		return roomID;
	}

}
