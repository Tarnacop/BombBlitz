package bomber.game;

import static bomber.game.Constants.MAP_BLOCK_TO_GRID_MULTIPLIER;

import java.awt.Point;

/**
 *
 * @author Owen Jenkins
 * @version 1.4
 * @since 2017-03-23
 * 
 *        Bomb class for "Bomb Blitz" Game Application (2017 Year 2 Team
 *        Project, Team B1). Represents a bomb in the game.
 */
public class Bomb {

	private String playerName;
	private Point pos;
	private int time;
	private int playerID;
	private int radius;

	private Point gridPos;

	/**
	 * Create a new Bomb object.
	 * 
	 * @param playerName
	 *            the name of player who placed the bomb
	 * @param pos
	 *            the position of the bomb
	 * @param time
	 *            the time until detonation
	 * @param radius
	 *            the radius of the bomb
	 */
	public Bomb(String playerName, Point pos, int time, int radius) {

		this.playerName = playerName;
		this.pos = pos;
		this.time = time;
		this.radius = radius;
		this.gridPos = new Point();
		updatePos();
	}

	/**
	 * Get the playerID of the bomb.
	 * 
	 * @return the playerID
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
	 * Set the playerID of the bomb.
	 * 
	 * @param playerID
	 *            the new playerID
	 */
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	/**
	 * Get the player name of the bomb
	 * 
	 * @return the player name
	 */
	public String getPlayerName() {

		return this.playerName;
	}

	/**
	 * Set the player name of the bomb
	 * 
	 * @param playerName
	 *            the new player name
	 */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	/**
	 * Get the position of the bomb
	 * 
	 * @return the position
	 */
	public Point getPos() {

		return this.pos;
	}

	/**
	 * Set the position of the bomb
	 * 
	 * @param pos
	 *            the new position
	 */
	public void setPos(Point pos) {
		this.pos = pos;
	}

	/**
	 * Get the radius of the bomb
	 * 
	 * @return the radius
	 */
	public int getRadius() {

		return this.radius;
	}

	/**
	 * Set the radius of the bomb
	 * 
	 * @param radius
	 *            the new radius
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * Get the grid position of the bomb
	 * 
	 * @return the grid position
	 */
	public Point getGridPos() {
		int x = pos.x / MAP_BLOCK_TO_GRID_MULTIPLIER;
		int y = pos.y / MAP_BLOCK_TO_GRID_MULTIPLIER;
		return new Point(x, y);
	}

	/**
	 * Set the grid position of the bomb
	 * 
	 * @param gridPos
	 *            the new grid position
	 */
	public void setGridPos(Point gridPos) {
		this.gridPos = gridPos;
	}

	/**
	 * Update the grid position of the bomb.
	 */
	private void updatePos() {
		this.gridPos.setLocation((pos.x / MAP_BLOCK_TO_GRID_MULTIPLIER),
				(pos.y / MAP_BLOCK_TO_GRID_MULTIPLIER));
	}

	/**
	 * Get the detonation time of the bomb
	 * 
	 * @return the detonation time
	 */
	public int getTime() {

		return this.time;
	}

	/**
	 * Set the detonation time of the bomb
	 * 
	 * @param time
	 *            the new detonation time
	 */
	public void setTime(int time) {
		this.time = time;
	}
}
