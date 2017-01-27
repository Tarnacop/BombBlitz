package bomber.AI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;

public class AIManager extends Thread{

	private GameAI gameAI;
	private GameState gameState;
	private AIState currentState;
	private AIState escapeState;
	private AIState findState;
	private AIState attackState;
	
	public AIManager(GameAI ai, GameState gameState)
	{
		this.gameAI = ai;
		this.gameState = gameState;
		this.escapeState = new EscapeState();
		this.findState = new FindState();
		this.attackState = new AttackState();
		this.currentState = findState;
	}
	
	public void run(){
		while(gameAI.isAlive())
		{
			if()
		}
	}
	
	private boolean inDanger()
	{
		Point playerPos = gameAI.getPos();
		List<Bomb> bombs = gameState.getBombs();
		for(Bomb b : bombs)
		{
			b.getPos();
		}
			
	}
	
	
	//return tiles which are affected by a single bomb
	private ArrayList<Point> getBombCoverage(Bomb bomb)
	{
		ArrayList<Point> points = new ArrayList<>();
		points.add(bomb.getPos());
		Point temp = bomb.getPos();
		int bombX = temp.x;
		int bombY = temp.y;
		for(int i=1; i<=bomb.getRadius(); i++)
		{
			points.add(new Point((bombX-i),bombY));
			points.add(new Point((bombX+i),bombY));
			points.add(new Point(bombX,(bombY-i)));
			points.add(new Point(bombX,(bombY+i)));
			
		}
		
		Block[][] map = gameState.getMap().getGridMap();
		
		for(Point p : points)
		{
			if((p.x<0)||(p.y<0) || map[p.x][p.y]==Block.SOFT || map[p.x][p.y]==Block.SOLID) points.remove(p);
		}
		
		return points;
	}
	
	
}
