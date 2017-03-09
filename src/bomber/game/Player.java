package bomber.game;

import java.awt.Point;

import bomber.AI.AIDifficulty;
import bomber.renderer.shaders.ColourMesh;

public class Player {

	private int playerID;
	private String name;
	private Point pos;
	private Point gridPos;
	private int lives;
	private double speed;
	private int bombRange;
	private int maxNrOfBombs;
	private final int scalar = 64;
	private KeyboardState keyState;
	private boolean isAlive;
	private ColourMesh mesh;
	
	public Player(String name, Point pos, int lives, double speed, ColourMesh mesh) {

		this.name = name;
		this.pos = pos;
		this.lives = lives;
		this.speed = speed;
		this.bombRange = 3; //setting the initial bomb range

		this.maxNrOfBombs = 2;

		this.isAlive = true;
		this.keyState = new KeyboardState();
		this.gridPos = new Point();
		this.mesh = mesh;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public int getScalar() {
		return scalar;
	}

	public void setGridPos(Point gridPos) {
		this.gridPos = gridPos;
	}

	public void setMesh(ColourMesh mesh) {
		this.mesh = mesh;
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

		int x = pos.x / scalar;
		int y = pos.y / scalar;
		if (x % scalar > 32)
			x++;
		if (y % scalar > 32)
			y++;
		gridPos.setLocation(x, y);
		return gridPos;
	}

	public void makeMesh(int width, int height, float[] colours) {

		this.mesh = new ColourMesh(width, height, colours);
	}

	private void updatePos() {
		this.gridPos.setLocation((pos.x / scalar), (pos.y / scalar));
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

	public ColourMesh getMesh() {

		return this.mesh;
	}

	public int getMaxNrOfBombs()
	{
		return maxNrOfBombs;
	}

	public void setMaxNrOfBombs(int maxNrOfBombs)
	{
		this.maxNrOfBombs = maxNrOfBombs;
	}

	public void addMesh(ColourMesh mesh) {

		this.mesh = mesh;
	}
	
	public void setDifficulty(AIDifficulty diff)
	{
		
	}
	
	public void stop()
	{
		
	}
}
