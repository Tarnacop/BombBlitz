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

	public RouteFinder(GameState state, GameAI gameAI) {
		this.state = state;
		this.gameAI = gameAI;
	}

	// finds the fastest route to the certain tile place in the map
	public List<Movement> findRoute(Point start, Point goal) {
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

	//return exact moves from the final Node
	//backtracks the route
	private List<Movement> getMovesFromPoints(Node finish) {
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

	//checks if the neighbous tile is possible move
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
	public List<Movement> escapeFromExplotion(ArrayList<Point> dangerTiles) {
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

	// return if the player is in range of the bomb explosion
	// and can be affeected by that
	private boolean inDanger() {
		Point playerPos = gameAI.getPos();
		return positionSafety(playerPos);
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

	// checks if the position is safe (no bombs are coming)
	private boolean positionSafety(Point position) {
		ArrayList<Point> points = new ArrayList<>();
		List<Bomb> bombs = state.getBombs();
		for (Bomb b : bombs) {
			points.addAll(getBombCoverage(b));
		}
		return points.contains(position);
	}

	// checks if the move from a particular position is safe
	private boolean checkMoveSafety(Movement move) {
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

	// checks if there is an enemy in the bomb distance
	private boolean isEnemyInBombRange() {
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
