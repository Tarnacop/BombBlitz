package bomber.AI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.Player;

/**
 * The Class RouteFinder. For finding different routes and planning.
 */
public class RouteFinder {

	/** The game state. */
	private GameState state;

	/** The game AI. */
	private GameAI gameAI;

	/** The safety checker. */
	private SafetyChecker safetyCh;

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

		int hValue = Math.abs(goal.x - start.x) + Math.abs(goal.y - start.y);
		Node startNode = new Node(0, hValue, null, start);
		open.add(startNode);

		// loop until the queue is not empty
		Node finish = null;
		while (!open.isEmpty()) {
			// take the head of the queue
			Node temp = open.poll();

			// if the head is final position we finish
			if (temp.getCoord().equals(goal)) {
				finish = temp;
				break;
			}
			// else we loop through all the neighbours
			for (Point p : getNeighbours(temp)) {
				checkNeighbour(temp, goal, temp.getgValue() + 1, p, open, closed);
			}

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

		if ((x < 0) || (y < 0) || map.length <= x || map[0].length <= y || map[x][y] == Block.SOFT
				|| map[x][y] == Block.SOLID)
			return;

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
	 * Check neighbour if the neighbours tile is possible move
	 *
	 * @param parent
	 *            the parent node
	 * @param tile
	 *            the position of the tile
	 * @param open
	 *            the open list of positions to be visited
	 * @param closed
	 *            the closed list of positions already visited
	 */
	private void checkNeighbour(Node parent, Point tile, LinkedList<Node> open, HashSet<Node> closed) {
		int x = tile.x;
		int y = tile.y;
		Block[][] map = getMap();

		if ((x < 0) || (y < 0) || map.length <= x || map[0].length <= y || map[x][y] == Block.SOFT
				|| map[x][y] == Block.SOLID)
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
		Point pos = gameAI.getPos();
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
				checkNeighbour(temp, p, open, closed);
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
		Point aiPos = gameAI.getPos();
		Point pos = null;
		int distance = Integer.MAX_VALUE;
		for (Player p : state.getPlayers()) {
			if (!p.equals(gameAI) && (Math.abs(aiPos.x - p.getPos().x) + Math.abs(aiPos.y - p.getPos().y)) < distance) {
				distance = (Math.abs(aiPos.x - p.getPos().x) + Math.abs(aiPos.y - p.getPos().y));
				pos = p.getPos();
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
			aiPos.setLocation(aiPos.x, aiPos.y - 1);
			break;
		case DOWN:
			if (map[aiPos.x][aiPos.y + 1] == Block.SOFT)
				return true;
			aiPos.setLocation(aiPos.x, aiPos.y + 1);
			break;
		case LEFT:
			if (map[aiPos.x - 1][aiPos.y] == Block.SOFT)
				return true;
			aiPos.setLocation(aiPos.x - 1, aiPos.y);
			break;
		case RIGHT:
			if (map[aiPos.x + 1][aiPos.y] == Block.SOFT)
				return true;
			aiPos.setLocation(aiPos.x + 1, aiPos.y);
			break;
		default:
			break;
		}
		return false;
	}

	/*
	 * private LinkedList<AIActions>
	 * fromMovememntToAIActions(LinkedList<AIActions> moves) {
	 * LinkedList<AIActions> newMoves = new LinkedList<>(); for(AIActions m:
	 * moves) { switch(m) { case UP: newMoves.add(AIActions.UP); break; case
	 * DOWN: newMoves.add(AIActions.DOWN); break; case LEFT:
	 * newMoves.add(AIActions.LEFT); break; case RIGHT:
	 * newMoves.add(AIActions.RIGHT); break; default: break; } }
	 * 
	 * return newMoves; }
	 */

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
	 *            the moves without obstacles (moves which doesn't consider soft blocks)
	 * @param pos
	 *            the position of ai
	 * @return the planned actions of the AI including bomb placement and moves.
	 */
	private LinkedList<AIActions> getPathWithBombs(LinkedList<AIActions> movesWithoutObstacles, Point pos) {
		Block[][] map = getMap().clone();
		// TODO finish not implemented
		LinkedList<AIActions> realMoves = new LinkedList<>();

		AIActions move = null;
		while ((move = movesWithoutObstacles.removeFirst()) != null) {
			if (isSoftBlockAfterMove(move, pos, map)) {
				realMoves.add(AIActions.BOMB);
				LinkedList<AIActions> escapeMoves = (escapeFromExplotion(
						safetyCh.getBombCoverage(new Bomb(null, pos, 0, gameAI.getBombRange()))));
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
	 * Gets the planned sequence of actions to enemy
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

			for (Point p : getNeighbours(temp)) {
				checkNeighbourWithSoftTiles(temp, goal, temp.getgValue() + 1, p, open, closed);
			}

			closed.add(temp);

		}

		if (finish == null)
			return null;

		return getPathWithBombs(getMovesFromPoints(finish), start);

	}

}
