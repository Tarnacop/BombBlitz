package bomber.AI;

import java.awt.Point;
import java.util.LinkedList;

import bomber.game.GameState;
import bomber.game.Movement;

/**
 * The Class AIManager for making moves.
 */
public class AIManager extends Thread {

	/** The game AI. */
	private GameAI gameAI;

	/** The finder for finding the route. */
	private RouteFinder finder;

	/** The safety checker for AI. */
	private SafetyChecker safetyCh;
	private final int scalar = 64;
	/**
	 * Instantiates a new AI manager.
	 *
	 * @param ai
	 *            the AI
	 * @param gameState
	 *            the game state
	 */
	public AIManager(GameAI ai, GameState gameState) {
		this.gameAI = ai;
		this.safetyCh = new SafetyChecker(gameState, ai);
		this.finder = new RouteFinder(gameState, ai, safetyCh);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		while (gameAI.isAlive()) {
			move();
		}
	}

	/**
	 * Updated position.
	 *
	 * @param move
	 *            the move
	 * @return the position
	 */
	private Point updatedPos(AIActions move) {
		Point aiPos = (Point) gameAI.getGridPos().clone();

		switch (move) {
		case UP:
			aiPos.setLocation(aiPos.x*scalar, (aiPos.y - 1)*scalar);
			break;
		case DOWN:
			aiPos.setLocation(aiPos.x, (aiPos.y + 1)*scalar);
			break;
		case LEFT:
			aiPos.setLocation((aiPos.x - 1)*scalar, aiPos.y);
			break;
		case RIGHT:
			aiPos.setLocation((aiPos.x + 1)*scalar, aiPos.y);
			break;
		default:
			break;
		}
		return aiPos;
	}

	/**
	 * From AI moves to game moves.
	 *
	 * @param action
	 *            the move to be made
	 * @return move in Movement representation
	 */
	private Movement FromAIMovesToGameMoves(AIActions action) {
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
	 * Make single move.
	 *
	 * @param move
	 *            the move
	 */
	private void makeSingleMove(AIActions move) {
		Point updatedPos = updatedPos(move);
		gameAI.getKeyState().setMovement(FromAIMovesToGameMoves(move));
		while (gameAI.getGridPos().equals(updatedPos)) {
		}
		gameAI.getKeyState().setMovement(Movement.NONE);
	}

	/**
	 * Perform sequence of moves.
	 *
	 * @param moves
	 *            the list of moves
	 * @param inDanger
	 *            the variable determining if the escape moves are passed
	 */
	private void performMoves(LinkedList<AIActions> moves, boolean inDanger) {
		if (inDanger)
			while (!moves.isEmpty()) {
				makeSingleMove(moves.removeFirst());
			}
		else
			while (!moves.isEmpty() && !safetyCh.inDanger() && safetyCh.checkMoveSafety(moves.peek())
					&& !safetyCh.isEnemyInBombRange()) {
				makeSingleMove(moves.removeFirst());
			}
		move();
	}

	/**
	 * Main method for controlling what moves to make
	 */
	private void move() {
		LinkedList<AIActions> moves;
		System.out.println("AI moving");
		// if AI is in danger then find the escape route
		if (safetyCh.inDanger()) {
			moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
			performMoves(moves, true);
		}
		
		// if enemy is in bomb range then place the bomb and go to the safe
		// location
		else if (safetyCh.isEnemyInBombRange()) {
			gameAI.getKeyState().setBomb(true);
			moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
			performMoves(moves, true);
		}
		
		// if enemy is accessible(no boxes are blocking the path) then find a
		// route to it and make moves
		else if ((moves = finder.findRoute(gameAI.getGridPos(), finder.getNearestEnemy())) != null) {
			performMoves(moves, false);
		}
	}

}
