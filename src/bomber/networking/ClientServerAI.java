package bomber.networking;

/**
 * 
 * Client side representation of AI players on the server
 *
 */
public class ClientServerAI {

	private byte id;

	public ClientServerAI(byte id) {
		this.id = id;
	}

	public byte getID() {
		return id;
	}

}
