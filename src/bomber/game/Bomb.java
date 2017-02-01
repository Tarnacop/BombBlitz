package bomber.game;

import java.awt.Point;

public class Bomb {

	private String playerName;	
	private Point pos;			
	private int time;			
	private int radius;			

	public Bomb(String playerName, Point pos, int time, int radius){
		
		this.playerName = playerName;
		this.pos = pos;
		this.time = time;
		this.radius = radius;
	}

	public String getPlayerName(){
		
		return this.playerName;
	}
	
	public Point getPos(){
		
		return this.pos;
	}
	
	public int getTime(){
		
		return this.time;
	}

	public int getRadius(){
		
		return this.radius;
	}
}
