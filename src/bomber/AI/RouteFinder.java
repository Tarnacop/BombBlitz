package bomber.AI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.Constants;
import bomber.game.GameState;
import bomber.game.Player;
import bomber.physics.PhysicsEngine;

/**
 * The Class RouteFinder.
 *
 * @author Jokubas Liutkus The Class RouteFinder. For finding different routes
 *         and planning.
 */
public class RouteFinder {

	/** The game state. */
	private GameState state;

	/** The game AI. */
	private GameAI gameAI;

	/** The safety checker. */
	private SafetyChecker safetyCh;

	/** The Constant scalar for pixel based representation. */
	private static final int scalar = 64;

	/**
	 * Instantiates a new route finder.
	 *
	 * @param state
	 *            the game state
	 * @param gameAI
	 *            the game AI
	 * @param safetyCh
	 *            the safety checker
	 */
	public RouteFinder(GameState state, GameAI gameAI, SafetyChecker safetyCh) {
		this.state = state;
		this.gameAI = gameAI;
		this.safetyCh = safetyCh;
	}

	/**
	 * Find route. Finds the fastest route to the certain place in the map using
	 * A* search algorithm
	 * 
	 * @param start
	 *            the start position
	 * @param goal
	 *            the goal position
	 * @return the sequence of moves
	 */
	public LinkedList<AIActions> findRoute(Point start, Point goal) {
		PriorityQueue<Node> open = new PriorityQueue<>();
		HashSet<Node> closed = new HashSet<>();

		if (start == null || goal == null)
			return null;
		// heuristic value h
		int hValue = Math.abs(goal.x - start.x) + Math.abs(goal.y - start.y);
		Node startNode = new Node(0, hValue, null, start);

		// adding start node to the queue
		open.add(startNode);

		// finish node
		Node finish = null;

		// loop until the queue is not empty
		while (!open.isEmpty()) {

			// take the head of the queue
			Node temp = open.poll();

			// if the head is final position we finish
			if (temp.getCoord().equals(goal)) {
				finish = temp;
				break;
			}
			// else we loop through all the neighbours
			// adding them to the queue
			getNeighbours(temp).stream()
					.forEach(p -> checkNeighbour(temp, goal, temp.getgValue() + 1, p, open, closed));

			// adding the head of the queue to visited list
			closed.add(temp);

		}

		if (finish == null)
			return null;

		return getMovesFromPoints(finish);
	}

	/**
	 * Gets the map from the game state.
	 *
	 * @return the map
	 */
	private Block[][] getMap() {
		return state.getMap().getGridMap();
	}

	/**
	 * Check neighbour. Checks if the neighbour tile is a possible move
	 *
	 * @param parent
	 *            the parent node
	 * @param goal
	 *            the goal position
	 * @param cost
	 *            the cost from starting position (g value)
	 * @param neigh
	 *            the neighbouring tile
	 * @param open
	 *            the open list of tile
	 * @param closed
	 *            the closed list of tiles
	 */
	private void checkNeighbour(Node parent, Point goal, int cost, Point neigh, PriorityQueue<Node> open,
			HashSet<Node> closed) {
		int x = neigh.x;
		int y = neigh.y;
		Block[][] map = getMap();

		// we check if the coordiantes are valid
		// if not we return
		if ((x < 0) || (y < 0) || map.length <= x || map[0].length <= y || map[x][y] == Block.SOFT
				|| map[x][y] == Block.SOLID)
			return;
//
//		List<Bomb> bombs = new ArrayList<Bomb>(state.getBombs());
//
//		if (bombs.stream().filter(bomb -> bomb.getGridPos().equals(neigh)).findFirst().isPresent())
//			return;
		// if the neighbour is in the visited list we return
		for (Node nd : closed)
			if (nd.getCoord().equals(neigh))
				return;

		// else we iterate through the queue and add the new element to it if
		// the path is better that the
		// previous already in the queue
		for (Node nd : open) {
			if (nd.getCoord().equals(neigh) && cost < nd.getgValue()) {
				open.remove(nd);
				int hValue = Math.abs(goal.x - neigh.x) + Math.abs(goal.y - neigh.y);
				Node neighNode = new Node(cost, hValue, parent, neigh);
				open.add(neighNode);
				return;
			}
		}

		// else we add it to the queue
		int hValue = Math.abs(goal.x - neigh.x) + Math.abs(goal.y - neigh.y);
		Node neighNode = new Node(cost, hValue, parent, neigh);
		open.add(neighNode);

	}

	/**
	 * Check neighbour with soft tiles. Checks if the neighbour tile is a
	 * possible move including the soft blocks.
	 *
	 * @param parent
	 *            the parent node
	 * @param goal
	 *            the goal position
	 * @param cost
	 *            the cost from starting position (g value)
	 * @param neigh
	 *            the neighbouring tile
	 * @param open
	 *            the open list of tile
	 * @param closed
	 *            the closed list of tiles
	 */
	private void checkNeighbourWithSoftTiles(Node parent, Point goal, int cost, Point neigh, PriorityQueue<Node> open,
			HashSet<Node> closed) {
		int x = neigh.x;
		int y = neigh.y;
		Block[][] map = getMap();

		if ((x < 0) || (y < 0) || map.length <= x || map[0].length <= y || map[x][y] == Block.SOLID)
			return;

//		List<Bomb> bombs = new ArrayList<Bomb>(state.getBombs());
//
//		if (bombs.stream().filter(bomb -> bomb.getGridPos().equals(neigh)).findFirst().isPresent())
//			return;
		for (Node nd : closed)
			if (nd.getCoord().equals(neigh))
				return;

		for (Node nd : open) {
			if (nd.getCoord().equals(neigh) && cost < nd.getgValue()) {
				open.remove(nd);
				int hValue = Math.abs(goal.x - neigh.x) + Math.abs(goal.y - neigh.y);
				Node neighNode = new Node(cost, hValue, parent, neigh);
				open.add(neighNode);
				return;
			}
		}

		int hValue = Math.abs(goal.x - neigh.x) + Math.abs(goal.y - neigh.y);
		Node neighNode = new Node(cost, hValue, parent, neigh);
		open.add(neighNode);
	}

	/**
	 * Returns the sequence of moves from the final finish node. Loops
	 * recursively to get all the sequence of actions. Backtracks the route from
	 * final node.
	 *
	 * @param finish
	 *            the finish node
	 * @return the sequence of moves
	 */
	private LinkedList<AIActions> getMovesFromPoints(Node finish) {
		LinkedList<AIActions> moves = new LinkedList<>();

		while (finish.getParent() != null) {
			int x = finish.getCoord().x;
			int y = finish.getCoord().y;
			int xParent = finish.getParent().getCoord().x;
			int yParent = finish.getParent().getCoord().y;

			if (x - 1 == xParent)
				moves.addFirst(AIActions.RIGHT);
			else if (x + 1 == xParent)
				moves.addFirst(AIActions.LEFT);
			else if (y - 1 == yParent)
				moves.addFirst(AIActions.DOWN);
			else
				moves.addFirst(AIActions.UP);

			finish = finish.getParent();
		}
		return moves;
	}

	/**
	 * Gets four neighbours from a particular position.
	 *
	 * @param parent
	 *            the node
	 * @return the neighbours of the position
	 */
	private ArrayList<Point> getNeighbours(Node parent) {
		int x = parent.getCoord().x;
		int y = parent.getCoord().y;
		ArrayList<Point> neighbours = new ArrayList<>();
		neighbours.add(new Point(x + 1, y));
		neighbours.add(new Point(x - 1, y));
		neighbours.add(new Point(x, y + 1));
		neighbours.add(new Point(x, y - 1));

		return neighbours;
	}

	/**
	 * Check neighbour if the neighbours tile is possible move.
	 *
	 * @param parent
	 *            the parent node
	 * @param tile
	 *            the position of the tile
	 * @param open
	 *            the open list of positions to be visited
	 * @param closed
	 *            the closed list of positions already visited
	 * @param map
	 *            the map
	 */
	private void checkNeighbour(Node parent, Point tile, LinkedList<Node> open, HashSet<Node> closed, Block[][] map) {
		int x = tile.x;
		int y = tile.y;
		// Block[][] map = getMap();

		if ((x < 0) || (y < 0) || map.length <= x || map[0].length <= y || map[x][y] == Block.SOFT
				|| map[x][y] == Block.SOLID)
			return;
		List<Bomb> bombs = new ArrayList<Bomb>(state.getBombs());

		if (bombs.stream().filter(bomb -> bomb.getGridPos().equals(tile)).findFirst().isPresent())
			return;
		for (Node nd : closed)
			if (nd.getCoord().equals(tile))
				return;

		for (Node nd : open) {
			if (nd.getCoord().equals(tile))
				return;
		}

		Node neighNode = new Node(parent, tile);
		open.add(neighNode);
	}

	/**
	 * Escape from explotion. Finds and returns the fastest route from the
	 * explotion when the AI is in danger. Using breadth-first search
	 *
	 * @param dangerTiles
	 *            the danger tiles which might damage the AI
	 * @return the list of moves to be made to escape from explotion.
	 */
	public LinkedList<AIActions> escapeFromExplotion(ArrayList<Point> dangerTiles) {
		Point pos = gameAI.getGridPos();
		LinkedList<Node> open = new LinkedList<>();
		HashSet<Node> closed = new HashSet<>();

		Node startNode = new Node(null, pos);
		open.add(startNode);

		// loop until the queue is not empty
		Node finish = null;
		while (!open.isEmpty()) {

			// take the head of the queue
			Node temp = open.poll();

			// if the head is final position we finish
			if (!dangerTiles.contains(temp.getCoord())) {
				finish = temp;
				break;
			}

			for (Point p : getNeighbours(temp)) {
				checkNeighbour(temp, p, open, closed, getMap());
			}

			// else we loop through all the neighbours
			closed.add(temp);
		}

		if (finish == null)
			return null;
		return getMovesFromPoints(finish);

	}

	/**
	 * Gets the nearest enemy.
	 *
	 * @return the nearest enemy of the AI.
	 */
	public Point getNearestEnemy() {
		Point aiPos = gameAI.getGridPos();
		Point pos = null;
		int distance = Integer.MAX_VALUE;
		for (Player p : state.getPlayers()) {
			if (!p.equals(gameAI) && p.isAlive()
					&& (Math.abs(aiPos.x - p.getGridPos().x) + Math.abs(aiPos.y - p.getGridPos().y)) < distance) {
				distance = (Math.abs(aiPos.x - p.getGridPos().x) + Math.abs(aiPos.y - p.getGridPos().y));
				pos = p.getGridPos();
			}
		}

		return pos;
	}

	/**
	 * Checks if is soft block after move.
	 *
	 * @param move
	 *            the move
	 * @param aiPos
	 *            the AI position
	 * @param map
	 *            the map
	 * @return true, if the block is soft after move
	 */
	private boolean isSoftBlockAfterMove(AIActions move, Point aiPos, Block[][] map) {

		switch (move) {
		case UP:
			if (map[aiPos.x][aiPos.y - 1] == Block.SOFT)
				return true;
			break;
		case DOWN:
			if (map[aiPos.x][aiPos.y + 1] == Block.SOFT)
				return true;
			break;
		case LEFT:
			if (map[aiPos.x - 1][aiPos.y] == Block.SOFT)
				return true;
			break;
		case RIGHT:
			if (map[aiPos.x + 1][aiPos.y] == Block.SOFT)
				return true;
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * Reverse moves. Reverses the moves in the planning phase. For example when
	 * AI places the bomb, finds the escape route and after the bomb exploded it
	 * wants to get back to the previous position where he places the bomb.
	 * 
	 * @param moves
	 *            the moves
	 * @return the reversed sequence of moves
	 */
	private LinkedList<AIActions> reverseMoves(LinkedList<AIActions> moves) {
		LinkedList<AIActions> revMoves = new LinkedList<>();
		for (AIActions m : moves) {
			switch (m) {
			case UP:
				revMoves.addFirst(AIActions.DOWN);
				break;
			case DOWN:
				revMoves.addFirst(AIActions.UP);
				break;
			case LEFT:
				revMoves.addFirst(AIActions.RIGHT);
				break;
			case RIGHT:
				revMoves.addFirst(AIActions.LEFT);
				break;
			default:
				break;
			}
		}

		return revMoves;
	}

	/**
	 * Update planned position of AI and map according to the move.
	 *
	 * @param action
	 *            the action (move)
	 * @param pos
	 *            the position of AI
	 * @param map
	 *            the map
	 */
	private void updatePositionAndMap(AIActions action, Point pos, Block[][] map) {

		switch (action) {
		case UP:
			pos.setLocation(pos.x, pos.y - 1);
			break;
		case DOWN:
			pos.setLocation(pos.x, pos.y + 1);
			break;
		case LEFT:
			pos.setLocation(pos.x - 1, pos.y);
			break;
		case RIGHT:
			pos.setLocation(pos.x + 1, pos.y);
			break;
		default:
			break;
		}
		map[pos.x][pos.y] = Block.BLANK;
	}

	/**
	 * Returns the planned actions of the AI including bomb placement and moves.
	 *
	 * @param movesWithoutObstacles
	 *            the moves without obstacles (moves which doesn't consider soft
	 *            blocks)
	 * @param position
	 *            the position
	 * @return the planned actions of the AI including bomb placement and moves.
	 */
	private LinkedList<AIActions> getPathWithBombs(LinkedList<AIActions> movesWithoutObstacles, Point position) {

		Block[][] map2 = getMap();
		// copy the real map
		Block[][] map = new Block[map2.length][map2[0].length];// Arrays.stream(getMap()).map(Block[]::clone).toArray(Block[][]::new);

		for (int x = 0; x < map2.length; x++)
			for (int y = 0; y < map2[0].length; y++)
				map[x][y] = map2[x][y];

		// TODO finish not implemented
		LinkedList<AIActions> realMoves = new LinkedList<>();
		Point pos = new Point(position.x, position.y);
		AIActions move = null;
		while ((move = movesWithoutObstacles.peek()) != null) {
			movesWithoutObstacles.removeFirst();
			if (isSoftBlockAfterMove(move, pos, map)) {
				realMoves.add(AIActions.BOMB);

				LinkedList<AIActions> escapeMoves = (escapeFromExplotion(safetyCh.getBombCoverage(
						new Bomb(null, new Point(pos.x * scalar, pos.y * scalar), 0, gameAI.getBombRange()), map), pos,
						map));
				if(escapeMoves == null) return realMoves;
				realMoves.addAll(escapeMoves);
				realMoves.add(AIActions.NONE);
				realMoves.addAll(reverseMoves(escapeMoves));

			}

			realMoves.addLast(move);
			updatePositionAndMap(move, pos, map);
		}

		return realMoves;
	}

	/**
	 * Gets the planned sequence of actions to enemy.
	 *
	 * @param start
	 *            the starting position
	 * @param goal
	 *            the goal position
	 * @return the planned sequence of actions to enemy
	 */
	public LinkedList<AIActions> getPlanToEnemy(Point start, Point goal) {

		// TODO finish not implemented
		PriorityQueue<Node> open = new PriorityQueue<>();
		HashSet<Node> closed = new HashSet<>();
		if (start == null || goal == null)
			return null;
		int hValue = Math.abs(goal.x - start.x) + Math.abs(goal.y - start.y);
		Node startNode = new Node(0, hValue, null, start);
		open.add(startNode);

		// loop until the queue is not empty
		Node finish = null;
		while (!open.isEmpty()) {
			// take the head of the queu
			Node temp = open.poll();
			// if the head is final position we finish
			if (temp.getCoord().equals(goal)) {
				finish = temp;
				break;
			}

			// else we loop through all the neighbours
			getNeighbours(temp).stream()
					.forEach(p -> checkNeighbourWithSoftTiles(temp, goal, temp.getgValue() + 1, p, open, closed));

			closed.add(temp);

		}

		if (finish == null)
			return null;
		return getPathWithBombs(getMovesFromPoints(finish), start);

	}

	public LinkedList<AIActions> canPutBombAndEscape() {
		LinkedList<AIActions> moves = null;
		if (safetyCh.isEnemyInBombRange()) {
			ArrayList<Point> bombs = safetyCh.getTilesAffectedByBombs();
			ArrayList<Point> coverage = safetyCh.getBombCoverage(
					new Bomb(gameAI.getName(), gameAI.getPos(), Constants.defaultBombTime, gameAI.getBombRange()),
					getMap());
			bombs.addAll(coverage);
			moves = escapeFromExplotion(bombs);

		}
		if ((moves != null) && (moves.size() < 4))
			return moves;
		return null;
	}

	/**
	 * Escape from explotion.
	 *
	 * @param dangerTiles
	 *            the danger tiles
	 * @param pos
	 *            the pos
	 * @param map
	 *            the map
	 * @return the linked list
	 */
	// change this method
	private LinkedList<AIActions> escapeFromExplotion(ArrayList<Point> dangerTiles, Point pos, Block[][] map) {
		LinkedList<Node> open = new LinkedList<>();
		HashSet<Node> closed = new HashSet<>();
		Node startNode = new Node(null, pos);
		open.add(startNode);

		// loop until the queue is not empty
		Node finish = null;
		while (!open.isEmpty()) {

			// take the head of the queue
			Node temp = open.poll();

			// if the head is final position we finish
			if (!dangerTiles.contains(temp.getCoord())) {
				finish = temp;
				break;
			}

			getNeighbours(temp).stream().forEach(p -> checkNeighbour(temp, p, open, closed, map));

			// else we loop through all the neighbours
			closed.add(temp);
		}

		if (finish == null)
			return null;
		return getMovesFromPoints(finish);

	}

	private class Pair {
		private Point position;
		private LinkedList<AIActions> actions;

		private Pair(Point position, LinkedList<AIActions> actions) {
			this.position = position;
			this.actions = actions;
		}

		private Point getPos() {
			return position;
		}

		private LinkedList<AIActions> getActions() {
			return actions;
		}
	}

	// ------------------------

	/**
	 * Gets the nearest enemy.
	 *
	 * @return the nearest enemy of the AI.
	 */
	public Point getNearestEnemyExcludeAIs() {
		Point aiPos = gameAI.getGridPos();
		Point pos = null;
		int distance = Integer.MAX_VALUE;
		List<Player> players = state.getPlayers().stream().filter(p -> !(p instanceof GameAI) && p.isAlive())
				.collect(Collectors.toList());
		
		for (Player p : players) {
			if ((Math.abs(aiPos.x - p.getGridPos().x) + Math.abs(aiPos.y - p.getGridPos().y)) < distance) {
				distance = (Math.abs(aiPos.x - p.getGridPos().x) + Math.abs(aiPos.y - p.getGridPos().y));
				pos = p.getGridPos();
			}
		}

		return pos;
	}

	public LinkedList<AIActions> canPutBombAndEscapeExcludeAIs() {
		LinkedList<AIActions> moves = null;
		if (safetyCh.isEnemyInBombRangeExludeAIs()) {
			ArrayList<Point> bombs = safetyCh.getTilesAffectedByBombs();
			ArrayList<Point> coverage = safetyCh.getBombCoverage(
					new Bomb(gameAI.getName(), gameAI.getPos(), 0, gameAI.getBombRange()),
					getMap());
			bombs.addAll(coverage);
			moves = escapeFromExplotion(bombs);

		}
		if ((moves != null) && (moves.size() < 4))
			return moves;
		return null;
	}

}
