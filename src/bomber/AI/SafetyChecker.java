package bomber.AI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.Player;

/**
 * Checking safety of the moves and AI.
 * 
 * @author Jokubas Liutkus
 */
public class SafetyChecker {

	/** The game state. */
	private GameState state;

	/** The game AI. */
	private GameAI gameAI;

	/**
	 * Instantiates a new safety checker.
	 *
	 * @param state
	 *            the game state
	 * @param gameAI
	 *            the game AI
	 */
	public SafetyChecker(GameState state, GameAI gameAI) {
		this.state = state;
		this.gameAI = gameAI;
	}

	/**
	 * Getting the map from the game state
	 *
	 * @return the map
	 */
	private Block[][] getMap() {
		return state.getMap().getGridMap();
	}

	/**
	 * In danger. Checks if the player is in range of the bomb explosion
	 * 
	 * @return true, if AI is in danger
	 */
	public boolean inDanger() {
		Point playerPos = gameAI.getGridPos();
		return positionSafety(playerPos);
	}

	/**
	 * Check move safety. Checks if the move from a particular position is safe
	 *
	 * @param move
	 *            the move
	 * @return true, if move is safe
	 */
	public boolean checkMoveSafety(AIActions move) {
		Point playerPos = gameAI.getGridPos();
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
		default: 
			newPosition = playerPos;
			break;
		} 
		return newPosition.x >= getMap().length || newPosition.y >= getMap()[0].length || newPosition.x < 0
				|| newPosition.y < 0 ? false
						: !positionSafety(newPosition) && getMap()[newPosition.x][newPosition.y] != Block.BLAST;
	}

	/**
	 * Position safety. Checks if the position is safe (no bombs are in range)
	 *
	 * @param position
	 *            the position
	 * @return true, if successful
	 */
	private boolean positionSafety(Point position) {

		return getTilesAffectedByBombs().contains(position);
	}

	/**
	 * Gets the tiles affected by bombs.
	 *
	 * @return the tiles affected by bombs
	 */
	public ArrayList<Point> getTilesAffectedByBombs() {
		ArrayList<Point> points = new ArrayList<>();
		List<Bomb> bombs = new ArrayList<>(state.getBombs());

		bombs.forEach(b -> points.addAll(getBombCoverage(b, getMap())));

		return points;
	}

	/**
	 * Gets the single bomb coverage.
	 *
	 * @param bomb
	 *            the bomb
	 * @param map
	 *            the map
	 * @return the tiles which are affected by a single bomb
	 */
	public ArrayList<Point> getBombCoverage(Bomb bomb, Block[][] map) {
		// TODO need to solve the issue
		ArrayList<Point> points = new ArrayList<>();
		if (bomb == null)
			return points;
		points.add(bomb.getGridPos());
		Point temp = bomb.getGridPos();
		int bombX = temp.x;
		int bombY = temp.y;
		for (int i = 1; i < bomb.getRadius(); i++) {
			points.add(new Point((bombX - i), bombY));
			points.add(new Point((bombX + i), bombY));
			points.add(new Point(bombX, (bombY - i)));
			points.add(new Point(bombX, (bombY + i)));

		}

		int x, y;
		for (int i = 0; i < points.size(); i++) {
			x = points.get(i).x;
			y = points.get(i).y;
			if ((x < 0) || (y < 0) || map.length <= x || map[0].length <= y || map[x][y] == Block.SOFT
					|| map[x][y] == Block.SOLID) {
				points.remove(i);
				i--;
			}

		}
		return points;
	}

	/**
	 * Checks if enemy is in bomb range.
	 *
	 * @return true, if enemy is in bomb range
	 */
	public boolean isEnemyInBombRange() {
		Point playerPos = gameAI.getGridPos();
		int bombRange = gameAI.getBombRange();
		for (Player p : state.getPlayers()) {
			if (p.equals(gameAI) || !p.isAlive())
				continue;

			if (isStraightDistance(playerPos, p.getGridPos(), bombRange) || p.getGridPos().equals(gameAI.getGridPos()))
				return true;

		}
		return false;
	}

	/**
	 * Checks if is straight distance to enemy.
	 *
	 * @param p1
	 *            the position 1
	 * @param p2
	 *            the position 2
	 * @param range
	 *            the bomb range
	 * @return true, if is straight distance
	 */
	private boolean isStraightDistance(Point p1, Point p2, int range) {
		Block[][] map = getMap();

		if ((p1.x == p2.x) && (p1.y == p2.y))
			return true;

		else if (p1.x == p2.x) {
			int sign = 1;
			if (p1.y < p2.y)
				sign = -1;
			for (int i = 1; i < range; i++) {
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
			for (int i = 1; i < range; i++) {
				if (map[p2.x + sign * i][p2.y] == Block.SOFT || map[p2.x + sign * i][p2.y] == Block.SOLID)
					return false;
				else if ((p1.x == p2.x + sign * i))
					return true;
			}
		}

		return false;
	}

	/**
	 * Checks if is next move is bomb.
	 *
	 * @param updatedPos
	 *            the position after move
	 * @return true, if there is a bomb in the moving position
	 */
	public boolean isNextMoveBomb(Point updatedPos) {
		ArrayList<Bomb> bombs = new ArrayList<>(state.getBombs());
		Optional<Bomb> b = bombs.stream().filter(bomb -> bomb.getGridPos().equals(updatedPos)).findAny();
		return b.isPresent();
	}

	// ----------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------
	// ------------------------EXTREME
	// AI------------------------------------------------
	// ----------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------

	/**
	 * Checks if enemy is in bomb range.
	 *
	 * @return true, if enemy is in bomb range
	 */
	public boolean isEnemyInBombRangeExludeAIs() {
		Point playerPos = gameAI.getGridPos();
		int bombRange = gameAI.getBombRange();
		List<Player> players = state.getPlayers().stream().filter(p -> !(p instanceof GameAI) && p.isAlive())
				.collect(Collectors.toList());
		;
		for (Player p : players) {

			if (isStraightDistance(playerPos, p.getGridPos(), bombRange) || p.getGridPos().equals(gameAI.getGridPos()))
				return true;

		}
		return false;
	}

}
