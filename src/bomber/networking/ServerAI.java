package bomber.networking;

import bomber.AI.GameAI;

public class ServerAI {

	private GameAI ai;
	private byte id;

	public ServerAI(byte id) {
		this.id = id;
	}

	public GameAI getGameAI() {
		return ai;
	}

	public byte getID() {
		return id;
	}

	public void setGameAI(GameAI ai) {
		this.ai = ai;
	}

}
