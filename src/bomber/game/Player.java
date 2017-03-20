package bomber.game;

import java.awt.Point;
import static bomber.game.Constants.*;
import bomber.AI.AIDifficulty;

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
	
	public Player(String name, Point pos, int lives, double speed) {

		this.name = name;
		this.pos = pos;
		this.lives = lives;
		this.speed = speed;
		this.bombRange = 3; //setting the initial bomb range

		this.maxNrOfBombs = 2;

		this.isAlive = true;
		this.keyState = new KeyboardState();
		this.gridPos = new Point();
		this.invulnerability = 0;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public void setGridPos(Point gridPos) {
		this.gridPos = gridPos;
	}
	
	public void begin() {

	}

	public int getBombRange() {
		return bombRange;
	}

	public void setBombRange(int bombRange) {
		this.bombRange = bombRange;
	}

	public String getName() {

		return this.name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public Point getPos() {

		return this.pos;
	}

	public Point getGridPos() {

		int x = pos.x / MAP_BLOCK_TO_GRID_MULTIPLIER;
		int y = pos.y / MAP_BLOCK_TO_GRID_MULTIPLIER;
		gridPos.setLocation(x, y);
		return gridPos;
	}

	private void updatePos() {
		this.gridPos.setLocation((pos.x / MAP_BLOCK_TO_GRID_MULTIPLIER), (pos.y / MAP_BLOCK_TO_GRID_MULTIPLIER));
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public KeyboardState getKeyState() {
		return keyState;
	}

	public void setKeyState(KeyboardState keyState) {
		this.keyState = keyState;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public void setPos(Point pos) {
		this.pos = pos;
		updatePos();
	}

	public int getMaxNrOfBombs()
	{
		return maxNrOfBombs;
	}

	public void setMaxNrOfBombs(int maxNrOfBombs)
	{
		this.maxNrOfBombs = maxNrOfBombs;
	}

	public int getInvulnerability()
	{
		return invulnerability;
	}

	public void setInvulnerability(int invulnerability)
	{
		this.invulnerability = invulnerability;
	}

	public void setDifficulty(AIDifficulty diff)
	{
		
	}
	
	public void pause()
	{
		
	}
	
	public void resume()
	{
		
	}
	
	public void stop()
	{
		
	}
}
