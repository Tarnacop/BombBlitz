package bomber.networking;

import bomber.AI.AIDifficulty;

/**
 * Client side representation of AI players on the server
 * 
 * @author Qiyang Li
 */
public class ClientServerAI {

	private byte id;
	private AIDifficulty difficulty;

	/**
	 * Construct a ClientServerAI object
	 * 
	 * @param id
	 *            the id of the AI
	 * @param difficulty
	 *            the difficulty of the AI
	 */
	public ClientServerAI(byte id, AIDifficulty difficulty) {
		this.id = id;
		if (difficulty != null) {
			this.difficulty = difficulty;
		} else {
			this.difficulty = AIDifficulty.MEDIUM;
		}
	}

	/**
	 * Get the id of the AI
	 * 
	 * @return the id of the AI
	 */
	public byte getID() {
		return id;
	}

	/**
	 * Get the difficulty of the AI
	 * 
	 * @return the difficulty of the AI
	 */
	public AIDifficulty getDifficulty() {
		return difficulty;
	}

}
