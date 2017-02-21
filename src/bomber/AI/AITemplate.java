package bomber.AI;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import bomber.game.GameState;
import bomber.game.Movement;
import bomber.game.Player;

public abstract class AITemplate extends Thread {
	/** The game AI. */
	protected GameAI gameAI;

	/** The finder for finding the route. */
	protected RouteFinder finder;

	/** The safety checker for AI. */
	protected SafetyChecker safetyCh;

	/** The game state. */
	protected GameState gameState;

	/** The Constant scalar. */
	protected static final int scalar = 64;

	/** The Constant playerSize. */
	protected static final int playerSize = 32;

	/**
	 * Instantiates a new AI manager.
	 *
	 * @param ai
	 *            the AI
	 * @param gameState
	 *            the game state
	 */
	public AITemplate(GameAI ai, GameState gameState) {
		this.gameAI = ai;
		this.gameState = gameState;
		this.safetyCh = new SafetyChecker(gameState, ai);
		this.finder = new RouteFinder(gameState, ai, safetyCh);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		move();
		System.out.println("AI Stopped.");
	}

	/**
	 * Updated position.
	 *
	 * @param move
	 *            the AI move
	 * @return the updated position after the move
	 */
	protected Point updatedPos(AIActions move) {
		Point aiPos = (Point) gameAI.getGridPos().clone();

		switch (move) {
		case UP:
			aiPos.setLocation(aiPos.x, (aiPos.y - 1));
			break;
		case DOWN:
			aiPos.setLocation(aiPos.x, (aiPos.y + 1));
			break;
		case LEFT:
			aiPos.setLocation((aiPos.x - 1), aiPos.y);
			break;
		case RIGHT:
			aiPos.setLocation((aiPos.x + 1), aiPos.y);
			break;
		default:
			break;
		}
		return aiPos;
	}

	/**
	 * From AI moves to game moves. Changes the AI moves to the general game
	 * moves
	 *
	 * @param action
	 *            the move to be changed
	 * @return move in Movement representation
	 */
	protected Movement FromAIMovesToGameMoves(AIActions action) {
		Movement m = null;
		switch (action) {
		case UP:
			m = Movement.UP;
			break;
		case DOWN:
			m = Movement.DOWN;
			break;
		case LEFT:
			m = Movement.LEFT;
			break;
		case RIGHT:
			m = Movement.RIGHT;
			break;
		default:
			break;
		}

		return m;
	}

	/**
	 * Check ig the AI reached destination when making a single move
	 *
	 * @param currentPixel
	 *            the pixel position of the AI
	 * @param updatedFinalPixelPos
	 *            the updated final pixel position which to be reached
	 * @return true, if needs to stop moving
	 */
	protected boolean checkIfReachedDestination(Point currentPixel, Point updatedFinalPixelPos) {
		boolean check = (currentPixel.x - updatedFinalPixelPos.x) < (scalar - playerSize);
		check &= (updatedFinalPixelPos.x <= currentPixel.x);
		check &= (currentPixel.y - updatedFinalPixelPos.y) < (scalar - playerSize);
		check &= (updatedFinalPixelPos.y <= currentPixel.y);
		return !check;
	}

	/**
	 * Make single move.
	 *
	 * @param move
	 *            the move to be made
	 */
	protected void makeSingleMove(AIActions move) {
		Point updatedPos = updatedPos(move);
		updatedPos.setLocation(updatedPos.x * scalar, updatedPos.y * scalar);
		gameAI.getKeyState().setMovement(FromAIMovesToGameMoves(move));
		while (checkIfReachedDestination(gameAI.getPos(), updatedPos) && gameAI.isAlive()) {
			try {
				sleep(10);
			} catch (InterruptedException e) {

			}
		}
		gameAI.getKeyState().setMovement(Movement.NONE);

	}

	/**
	 * Gets the moves to enemy.
	 *
	 * @return the moves to enemy
	 */
	protected LinkedList<AIActions> getMovesToEnemy() {
		LinkedList<AIActions> moves = finder.findRoute(gameAI.getGridPos(), finder.getNearestEnemy());
		if (moves != null)
			return moves;
		for (Player p : gameState.getPlayers()) {
			if (!p.equals(gameAI)) {
				moves = finder.findRoute(gameAI.getGridPos(), p.getGridPos());
				if (moves != null)
					return moves;
			}
		}
		return null;
	}

	/**
	 * Gets the moves to enemy.
	 *
	 * @return the moves to enemy
	 */
	protected LinkedList<AIActions> getMovesToEnemyExcludeAIs() {
		LinkedList<AIActions> moves = finder.findRoute(gameAI.getGridPos(), finder.getNearestEnemyExcludeAIs());
		if (moves != null)
			return moves;
		List<Player> players = gameState.getPlayers().stream().filter(p -> !(p instanceof GameAI) && p.isAlive())
				.collect(Collectors.toList());
		for (Player p : players) {

			moves = finder.findRoute(gameAI.getGridPos(), p.getGridPos());
			if (moves != null)
				return moves;

		}
		return null;
	}

	protected abstract void performMoves(LinkedList<AIActions> moves, boolean inDanger);

	protected abstract void performPlannedMoves(LinkedList<AIActions> moves);

	protected abstract void move();
}
