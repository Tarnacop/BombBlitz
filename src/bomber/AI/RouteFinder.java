package bomber.AI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.Movement;
import bomber.game.Player;

public class RouteFinder {

	private GameState state;
	private GameAI gameAI;
	private SafetyChecker safetyCh;

	public RouteFinder(GameState state, GameAI gameAI, SafetyChecker safetyCh) {
		this.state = state;
		this.gameAI = gameAI;
		this.safetyCh = safetyCh;
	}

	// finds the fastest route to the certain tile place in the map
	public LinkedList<Movement> findRoute(Point start, Point goal) {
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
				checkNeighbour(temp, goal, temp.getgValue() + 1, p, open, closed);
			}

			closed.add(temp);

		}

		if (finish == null)
			return null;

		return getMovesFromPoints(finish);
	}

	// getting the map from the game state
	private Block[][] getMap() {
		return state.getMap().getGridMap();
	}

	// checks if the neighbour tile is a possible move
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

	// return exact moves from the final Node
	// backtracks the route
	private LinkedList<Movement> getMovesFromPoints(Node finish) {
		LinkedList<Movement> moves = new LinkedList<>();

		while (finish.getParent() != null) {
			int x = finish.getCoord().x;
			int y = finish.getCoord().y;
			int xParent = finish.getParent().getCoord().x;
			int yParent = finish.getParent().getCoord().y;

			if (x - 1 == xParent)
				moves.addFirst(Movement.RIGHT);
			else if (x + 1 == xParent)
				moves.addFirst(Movement.LEFT);
			else if (y - 1 == yParent)
				moves.addFirst(Movement.DOWN);
			else
				moves.addFirst(Movement.UP);

			finish = finish.getParent();
		}

		return moves;
	}

	// return the neighbour points of the map
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

	// checks if the neighbous tile is possible move
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

	// find the route for escaping from bomb explotion
	public LinkedList<Movement> escapeFromExplotion(ArrayList<Point> dangerTiles) {
		Point pos = gameAI.getPos();
		LinkedList<Node> open = new LinkedList<>();
		HashSet<Node> closed = new HashSet<>();

		Node startNode = new Node(null, pos);
		open.add(startNode);

		// loop until the queue is not empty
		Node finish = null;
		while (!open.isEmpty()) {
			// take the head of the queu
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

	private boolean isSoftBlockAfterMove(Movement move, Point aiPos) {
		Block[][] map = getMap();

		switch (move) {
		case UP:
			if (map[aiPos.x][aiPos.y - 1] == Block.SOFT)
				return true;
			aiPos.setLocation(aiPos.x, aiPos.y - 1);
			break;
		case DOWN:
			if (map[aiPos.x][ aiPos.y + 1] == Block.SOFT)
				return true;
			aiPos.setLocation(aiPos.x, aiPos.y + 1);
			break;
		case LEFT:
			if (map[aiPos.x - 1][ aiPos.y] == Block.SOFT)
				return true;
			aiPos.setLocation(aiPos.x - 1, aiPos.y);
			break;
		case RIGHT:
			if (map[aiPos.x + 1][ aiPos.y] == Block.SOFT)
				return true;
			aiPos.setLocation(aiPos.x + 1, aiPos.y);
			break;
		default:
			break;
		}
		return false;
	}

	
	
	private LinkedList<AIActions> fromMovememntToAIActions(LinkedList<Movement> moves)
	{
		LinkedList<AIActions> newMoves = new LinkedList<>();
		for(Movement m: moves)
		{
			switch(m)
			{
			case UP:
				newMoves.add(AIActions.UP);
				break;
			case DOWN:
				newMoves.add(AIActions.DOWN);
				break;
			case LEFT:
				newMoves.add(AIActions.LEFT);
				break;
			case RIGHT:
				newMoves.add(AIActions.RIGHT);
				break;
			case default:
				break;
			}
		}
		
		return newMoves;
	}
	
	
	private LinkedList<Movement> getPathWithBombs(LinkedList<Movement> movesWithoutObstacles, Point pos)
	{
		// TODO finish not implemented
		LinkedList<AIActions> realMoves = new LinkedList<>();
		
		Movement move = null;
		while( (move = movesWithoutObstacles.removeFirst()) != null)
		{
			if(isSoftBlockAfterMove(move, pos))
			{
				realMoves.add(AIActions.BOMB);
				LinkedList escapeMoves = (escapeFromExplotion(safetyCh.getBombCoverage(new Bomb(null, pos, 0, gameAI.getBombRange()))));
				realMoves.addAll(fromMovememntToAIActions(escapeMoves));
				realMoves.add(AIActions.NONE);
				
			}
		}
	}

	public LinkedList<Movement> getPlanToEnemy(Point start, Point goal) {

		
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

		return getMovesFromPoints(finish);

	}

}
