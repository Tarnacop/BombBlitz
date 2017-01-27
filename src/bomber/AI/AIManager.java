package bomber.AI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;

public class AIManager extends Thread {

	private GameAI gameAI;
	private GameState gameState;
	private AIState currentState;
	private AIState escapeState;
	private AIState findState;
	private AIState attackState;
	
	public AIManager(GameAI ai, GameState gameState) {
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
			if(inDanger())
			{
				System.out.print("not");
				break;
			}
			else
			{
				System.out.print("danger");
				break;
			}
		}
	}

	//return if the player is in range of the bomb explosion
	//and can be affeected by that
	private boolean inDanger() {
		Point playerPos = gameAI.getPos();
		ArrayList<Point> points = new ArrayList<>();
		List<Bomb> bombs = gameState.getBombs();
		for (Bomb b : bombs) {
			points.addAll(getBombCoverage(b));
		}
		return points.contains(playerPos);
	}

	// return tiles which are affected by a single bomb
	private ArrayList<Point> getBombCoverage(Bomb bomb) {
		ArrayList<Point> points = new ArrayList<>();
		points.add(bomb.getPos());
		Point temp = bomb.getPos();
		int bombX = temp.x;
		int bombY = temp.y;
		for (int i = 1; i <= bomb.getRadius(); i++) {
			points.add(new Point((bombX - i), bombY));
			points.add(new Point((bombX + i), bombY));
			points.add(new Point(bombX, (bombY - i)));
			points.add(new Point(bombX, (bombY + i)));

		}

		Block[][] map = gameState.getMap().getGridMap();
		int x,y;
		for (int i=0; i<points.size(); i++) {
			x = points.get(i).x;
			y = points.get(i).y;
			if ((x < 0) || (y < 0) || map[x][y] == Block.SOFT || map[x][y] == Block.SOLID)
			{
				points.remove(i);
				i--;
			}
				
		}

		return points;
	}

}
