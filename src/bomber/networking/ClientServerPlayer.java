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

	/**
	 * Construct a ClientServerPlayer object
	 * 
	 * @param id
	 *            the id of the player
	 * @param name
	 *            the name of the player
	 */
	public ClientServerPlayer(int id, String name) {
		this.id = id;
		this.name = name;
		this.readyToPlay = false;
	}

	/**
	 * Construct a ClientServerPlayer object
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
	 * Determine whether the player is ready to play (when in a room)
	 * 
	 * @return true if the player is ready to play (when in a room)
	 */
	public boolean isReadyToPlay() {
		return readyToPlay;
	}

}
