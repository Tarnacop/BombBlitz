package bomber.game;

import static bomber.game.Constants.MAP_BLOCK_TO_GRID_MULTIPLIER;

import java.awt.Point;

import bomber.AI.AIDifficulty;

/**
 *
 * @author Owen Jenkins
 * @version 1.4
 * @since 2017-03-23
 * 
 *        Map class for "Bomb Blitz" Game Application (2017 Year 2 Team Project,
 *        Team B1). Represents a map in the game.
 */
public class Player {

	private int playerID;
	private String name;
	private Point pos;
	private Point gridPos;
	private int lives;
	private double speed;
	private int bombRange;
	private int maxNrOfBombs;
	private KeyboardState keyState;
	private boolean isAlive;
	private int invulnerability;

	/**
	 * Create a new Player object.
	 * 
	 * @param name
	 *            the player name
	 * @param pos
	 *            the spawn position of the player
	 * @param lives
	 *            the number of lives the player has
	 * @param speed
	 *            the speed of the player
	 */
	public Player(String name, Point pos, int lives, double speed) {

		this.name = name;
		this.pos = pos;
		this.lives = lives;
		this.speed = speed;
		this.bombRange = 3; // setting the initial bomb range

		this.maxNrOfBombs = 2;

		this.isAlive = true;
		this.keyState = new KeyboardState();
		this.gridPos = new Point();
		this.invulnerability = 0;
	}

	/**
	 * Get the playerID.
	 * 
	 * @return the playerID
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * Set the playerID.
	 * 
	 * @param playerID
	 *            the new playerID
	 */
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	/**
	 * Only used for GameAI.
	 */
	public void begin() {

	}

	/**
	 * Get the bomb range for this player.
	 * 
	 * @return the bomb range
	 */
	public int getBombRange() {
		return bombRange;
	}

	/**
	 * Set the bomb range for this player.
	 * 
	 * @param bombRange
	 *            the new bomb range
	 */
	public void setBombRange(int bombRange) {
		this.bombRange = bombRange;
	}

	/**
	 * Get the name of this player.
	 * 
	 * @return the player name
	 */
	public String getName() {

		return this.name;
	}

	/**
	 * Set the name of this player.
	 * 
	 * @param name
	 *            the new player name
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * Get the position of this player
	 * 
	 * @return the position
	 */
	public Point getPos() {

		return this.pos;
	}

	/**
	 * Set the position of this player
	 * 
	 * @param pos
	 *            the new position
	 */
	public void setPos(Point pos) {
		this.pos = pos;
		updatePos();
	}

	/**
	 * Get the grid position of this player
	 * 
	 * @return the grid position
	 */
	public Point getGridPos() {

		int x = pos.x / MAP_BLOCK_TO_GRID_MULTIPLIER;
		int y = pos.y / MAP_BLOCK_TO_GRID_MULTIPLIER;
		gridPos.setLocation(x, y);
		return gridPos;
	}

	/**
	 * Set the grid position of this player
	 * 
	 * @param gridPos
	 *            the new grid position
	 */
	public void setGridPos(Point gridPos) {
		this.gridPos = gridPos;
	}

	/**
	 * Update the grid position of this player.
	 */
	private void updatePos() {
		this.gridPos.setLocation((pos.x / MAP_BLOCK_TO_GRID_MULTIPLIER),
				(pos.y / MAP_BLOCK_TO_GRID_MULTIPLIER));
	}

	/**
	 * Get the lives of this player.
	 * 
	 * @return the lives
	 */
	public int getLives() {
		return lives;
	}

	/**
	 * Set the lives of this player.
	 * 
	 * @param lives
	 *            the new lives
	 */
	public void setLives(int lives) {
		this.lives = lives;
	}

	/**
	 * Get the speed of this player.
	 * 
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Set the speed of this player.
	 * 
	 * @param speed
	 *            the new speed
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Get the keyboard state of this player.
	 * 
	 * @return the keyboard state
	 */
	public KeyboardState getKeyState() {
		return keyState;
	}

	/**
	 * Set the keyboard state of this player.
	 * 
	 * @param keyState
	 *            the new keyboard state
	 */
	public void setKeyState(KeyboardState keyState) {
		this.keyState = keyState;
	}

	/**
	 * Check if this player is alive.
	 * 
	 * @return true if the player is alive
	 */
	public boolean isAlive() {
		return isAlive;
	}

	/**
	 * Set this player alive value.
	 * 
	 * @param isAlive
	 *            true if the player is alive
	 */
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	/**
	 * Get the max number of bombs.
	 * 
	 * @return the max number of bombs
	 */
	public int getMaxNrOfBombs() {
		return maxNrOfBombs;
	}

	/**
	 * Set the max number of bombs.
	 * 
	 * @param maxNrOfBombs
	 *            the new max number of bombs
	 */
	public void setMaxNrOfBombs(int maxNrOfBombs) {
		this.maxNrOfBombs = maxNrOfBombs;
	}

	/**
	 * Get the invulnerability time of this player.
	 * 
	 * @return the invulnerability time
	 */
	public int getInvulnerability() {
		return invulnerability;
	}

	/**
	 * Set the invulnerability time of this player.
	 * 
	 * @param invulnerability
	 *            the new invulnerability time
	 */
	public void setInvulnerability(int invulnerability) {
		this.invulnerability = invulnerability;
	}

	/**
	 * Only used for GameAI.
	 */
	public void setDifficulty(AIDifficulty diff) {

	}

	/**
	 * Only used for GameAI.
	 */
	public void pause() {

	}

	/**
	 * Only used for GameAI.
	 */
	public void resume() {

	}

	/**
	 * Only used for GameAI.
	 */
	public void stop() {

	}
}
