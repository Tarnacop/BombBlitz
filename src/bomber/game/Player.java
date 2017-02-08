package bomber.game;

import java.awt.Point;

public class Player {

	private String name;			
	private Point pos;	
	private Point gridPos;
	private int lives;			
	private double speed;			
	private int bombRange;
	private final int scalar = 64;
	private KeyboardState keyState;		
	private boolean isAlive;
	
	public Player(String name, Point pos, int lives, double speed){
		
		this.name = name;
		this.pos = pos;
		this.lives = lives;
		this.speed = speed;
		this.bombRange = 3; //setting the initial bomb range
		
		this.isAlive = true;
		this.keyState = new KeyboardState();
		this.gridPos = new Point();
		updatePos();
	}

	public int getBombRange() {
		return bombRange;
	}

	public void setBombRange(int bombRange) {
		this.bombRange = bombRange;
	}

	public String getName(){
		
		return this.name;
	}
	
	public void setName(String name){
		
		this.name = name;
	}

	public Point getPos(){
		
		return this.pos;
	}
	
	public Point getGridPos()
	{
		return gridPos;
	}
	
	
	private void updatePos()
	{
		this.gridPos.setLocation((pos.x/scalar),(pos.y/scalar) );
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
	
	
}
