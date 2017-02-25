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

	public ClientServerPlayer(int id, String name) {
		this.id = id;
		this.name = name;
		this.readyToPlay = false;
	}

	public ClientServerPlayer(int id, String name, boolean readyToPlay) {
		this.id = id;
		this.name = name;
		this.readyToPlay = readyToPlay;
	}

	public int getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isReadyToPlay() {
		return readyToPlay;
	}

}
