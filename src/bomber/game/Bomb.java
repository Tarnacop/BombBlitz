package bomber.game;

import java.awt.Point;


public class Bomb {

	private int playerID;
	private String playerName;	
	private Point pos;			
	private int time;			

	private int radius;		
	private int scalar = 64;

	private Point gridPos;
	
	public Bomb(String playerName, Point pos, int time, int radius){
		
		this.playerName = playerName;
		this.pos = pos;
		this.time = time;
		this.radius = radius;
		this.gridPos = new Point();
		updatePos();
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

	public void setScalar(int scalar) {
		this.scalar = scalar;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public void setGridPos(Point gridPos) {
		this.gridPos = gridPos;
	}

	public String getPlayerName(){
		
		return this.playerName;
	}
	
	public Point getPos(){
		
		return this.pos;
	}
	
	public Point getGridPos()
	{
		int x = pos.x/scalar;
		if(pos.x%scalar > 32) x++;
		int y = pos.y/scalar;
		if(pos.x%scalar > 32) x++;
		return new Point(x,y);
	}
	
	private void updatePos()
	{
		this.gridPos.setLocation((pos.x/scalar),(pos.y/scalar) );
	}
	
	public int getTime(){
		
		return this.time;
	}

	public void setTime(int time) 
	{
		this.time = time;		
	}

	public int getRadius(){
		
		return this.radius;
	}
}
