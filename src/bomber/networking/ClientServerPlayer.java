package bomber.networking;

/**
 * 
 * Client side representation of players connected to the server
 *
 */
public class ClientServerPlayer {
	private int id;
	private String name;

	public ClientServerPlayer(int id, String name) {
		this.id = id;
		this.name = name;
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

}
