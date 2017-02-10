package bomber.game;

import java.awt.Point;
import bomber.renderer.shaders.Mesh;


public class Bomb {

	private int playerID;
	private String playerName;	
	private Point pos;			
	private int time;			

	private int radius;		
	private int scalar = 64;

	private Point gridPos;
	private final Mesh mesh;
	
	public Bomb(String playerName, Point pos, int time, int radius){
		
		this.playerName = playerName;
		this.pos = pos;
		this.time = time;
		this.radius = radius;
		this.gridPos = new Point();
		float[] colours = new float[] { 0.7f, 0.4f, 0.3f, 0.0f,  0.7f, 0.4f, 0.3f, 0.0f, 0.7f, 0.4f, 0.3f, 0.0f };
		
		this.mesh = new Mesh(0, 0, 50, 50, colours);
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
	
	public Mesh getMesh() {
		
		return this.mesh;
	}
}
