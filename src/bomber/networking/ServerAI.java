package bomber.networking;

import bomber.AI.AIDifficulty;
import bomber.AI.GameAI;

/**
 * Server side representation of AI
 */
public class ServerAI {

	private GameAI ai;
	private byte id;
	private AIDifficulty difficulty;

	/**
	 * Construct a ServerAI object
	 * 
	 * @param id
	 *            the id of the AI, which should be in the range [0,2]
	 */
	public ServerAI(byte id) {
		this.id = id;
		this.difficulty = AIDifficulty.MEDIUM;
	}

	/**
	 * Construct a ServerAI object
	 * 
	 * @param id
	 *            the id of the AI, which should be in the range [0,2]
	 * @param difficulty
	 *            the difficulty of the AI
	 */
	public ServerAI(byte id, AIDifficulty difficulty) {
		this.id = id;
		setDifficulty(difficulty);
	}

	/**
	 * Get the GameAI in this object
	 * 
	 * @return the GameAI object
	 */
	public GameAI getGameAI() {
		return ai;
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

	/**
	 * Set the difficulty of the AI
	 * 
	 * @param difficulty
	 *            the difficulty of the AI
	 */
	public void setDifficulty(AIDifficulty difficulty) {
		if (difficulty == null) {
			this.difficulty = AIDifficulty.MEDIUM;
		} else {
			this.difficulty = difficulty;
		}
	}

	/**
	 * Set the GameAI for this object
	 * 
	 * @param ai
	 *            the GameAI object
	 */
	public void setGameAI(GameAI ai) {
		this.ai = ai;
	}

}
