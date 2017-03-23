package bomber.game;

/**
 *
 * @author Owen Jenkins
 * @version 1.4
 * @since 2017-03-23
 * 
 *        KeyboardState class for "Bomb Blitz" Game Application (2017 Year 2
 *        Team Project, Team B1). Represents the state of the player's keyboard.
 */
public class KeyboardState {

	private boolean bomb; // true if the bomb key is pressed
	private boolean pause;
	private Movement movement; // the current movement key pressed, default NONE
	private boolean muted;

	/**
	 * Create a new KeyboardState object.
	 */
	public KeyboardState() {

		this.movement = Movement.NONE;
		this.bomb = false;
		this.pause = false;
	}

	/**
	 * Check if the player has placed a bomb.
	 * 
	 * @return true if the player has placed a bomb
	 */
	public boolean isBomb() {
		return bomb;
	}

	/**
	 * Set the bomb status of this player.
	 * 
	 * @param bomb
	 *            true if the player has placed a bomb
	 */
	public void setBomb(boolean bomb) {
		this.bomb = bomb;
	}

	/**
	 * Set the movement status of this player.
	 * 
	 * @param movement
	 *            the new movement
	 */
	public void setMovement(Movement movement) {
		this.movement = movement;
	}

	/**
	 * Get the player's movement status.
	 * 
	 * @return the movement
	 */
	public Movement getMovement() {

		return this.movement;
	}

	/**
	 * Check if the player has paused the game.
	 * 
	 * @return true if the player has paused the game
	 */
	public boolean isPaused() {
		return this.pause;
	}

	/**
	 * Set if the player has paused the game.
	 * 
	 * @param paused
	 *            true if the player has paused the game
	 */
	public void setPaused(boolean paused) {
		this.pause = paused;
	}

	/**
	 * Check if the player has muted the game.
	 * 
	 * @return true if the player has muted the game
	 */
	public boolean isMuted() {
		return this.muted;
	}

	/**
	 * Set if the player has muted the game.
	 * 
	 * @param muted
	 *            true if the player has muted the game
	 */
	public void setMuted(boolean muted) {
		this.muted = muted;
	}
}
