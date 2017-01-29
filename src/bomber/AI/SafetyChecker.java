package bomber.AI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.Movement;
import bomber.game.Player;

public class SafetyChecker {

	private GameState state;
	private GameAI gameAI;

	public SafetyChecker(GameState state, GameAI gameAI) {
		this.state = state;
		this.gameAI = gameAI;
	}

	// getting the map from the game state
	private Block[][] getMap() {
		return state.getMap().getGridMap();
	}

	// return if the player is in range of the bomb explosion
	// and can be affeected by that
	public boolean inDanger() {
		Point playerPos = gameAI.getPos();
		return positionSafety(playerPos);
	}

	// checks if the move from a particular position is safe
	public boolean checkMoveSafety(Movement move) {
		Point playerPos = gameAI.getPos();
		Point newPosition = null;
		switch (move) {
		case UP:
			newPosition = new Point(playerPos.x, playerPos.y - 1);
			break;
		case DOWN:
			newPosition = new Point(playerPos.x, playerPos.y + 1);
			break;
		case LEFT:
			newPosition = new Point(playerPos.x - 1, playerPos.y);
			break;
		case RIGHT:
			newPosition = new Point(playerPos.x + 1, playerPos.y);
			break;
		case NONE:
			newPosition = playerPos;
			break;
		}
		return positionSafety(newPosition);
	}

	// checks if the position is safe (no bombs are coming)
	private boolean positionSafety(Point position) {
		
		return getTilesAffectedByBombs().contains(position);
	}

	// return the tiles affected by bombs
	public ArrayList<Point> getTilesAffectedByBombs()
	{
		ArrayList<Point> points = new ArrayList<>();
		List<Bomb> bombs = state.getBombs();
		for (Bomb b : bombs) {
			points.addAll(getBombCoverage(b));
		}
		return points;
	}
	
	
	// return tiles which are affected by a single bomb
	public ArrayList<Point> getBombCoverage(Bomb bomb) {
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

		Block[][] map = getMap();
		int x, y;
		for (int i = 0; i < points.size(); i++) {
			x = points.get(i).x;
			y = points.get(i).y;
			if ((x < 0) || (y < 0) || map[x][y] == Block.SOFT || map[x][y] == Block.SOLID) {
				points.remove(i);
				i--;
			}

		}

		return points;
	}




	// checks if there is an enemy in the bomb distance
	public boolean isEnemyInBombRange() {
		Point playerPos = gameAI.getPos();
		int bombRange = gameAI.getBombRange();
		for (Player p : state.getPlayers()) {
			if (p == gameAI)
				continue;
			if (isStraightDistance(playerPos, p.getPos(), bombRange))
				return true;

		}
		return false;
	}

	// helper function for checking if there exists enenmy in the bomb distance
	private boolean isStraightDistance(Point p1, Point p2, int range) {
		Block[][] map = getMap();
		if (p1.x == p2.x) {
			int sign = 1;
			if (p1.y < p2.y)
				sign = -1;
			for (int i = 1; i <= range; i++) {
				if (map[p2.x][p2.y + sign * i] == Block.SOFT || map[p2.x][p2.y + sign * i] == Block.SOLID)
					return false;
				else if ((p1.y == p2.y + sign * i))
					return true;
			}

		}

		else if (p1.y == p2.y) {
			int sign = 1;
			if (p1.x < p2.x)
				sign = -1;
			for (int i = 1; i <= range; i++) {
				if (map[p2.x + sign * i][p2.y] == Block.SOFT || map[p2.x + sign * i][p2.y] == Block.SOLID)
					return false;
				else if ((p1.x == p2.x + sign * i))
					return true;
			}
		}

		return false;
	}
	
}
