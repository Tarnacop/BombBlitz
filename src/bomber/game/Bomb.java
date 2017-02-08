package bomber.game;

import java.awt.Point;

public class Bomb {

	private String playerName;	
	private Point pos;			
	private int time;			
	private int radius;			
	private int scalar =64;
	private Point gridPos;
	public Bomb(String playerName, Point pos, int time, int radius){
		
		this.playerName = playerName;
		this.pos = pos;
		this.time = time;
		this.radius = radius;
		this.gridPos = new Point();
		updatePos();
	}

	public String getPlayerName(){
		
		return this.playerName;
	}
	
	public Point getPos(){
		
		return this.pos;
	}
	
	public Point getGridPos()
	{
		return this.gridPos;
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
