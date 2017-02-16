
package bomber.AI;

import java.awt.Point;
import java.util.LinkedList;
import bomber.game.GameState;
import bomber.game.Movement;
import bomber.game.Player;

// TODO: Auto-generated Javadoc
/**
 * The Class AIManager.
 *
 * @author Jokubas Liutkus The Class AIManager for making moves.
 */
public class AIManager extends Thread {

	/** The game AI. */
	private GameAI gameAI;

	/** The finder for finding the route. */
	private RouteFinder finder;

	/** The safety checker for AI. */
	private SafetyChecker safetyCh;

	/** The game state. */
	private GameState gameState;
	
	/** The Constant scalar. */
	private static final int scalar = 64;

	/** The Constant playerSize. */
	private static final int playerSize = 32;

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
	private Point updatedPos(AIActions move) {
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
	 * Check ig the AI reached destination when making a single move
	 *
	 * @param currentPixel
	 *            the pixel position of the AI
	 * @param updatedFinalPixelPos
	 *            the updated final pixel position which to be reached
	 * @return true, if needs to stop moving
	 */
	private boolean checkIfReachedDestination(Point currentPixel, Point updatedFinalPixelPos) {
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
	private void makeSingleMove(AIActions move) {
		Point updatedPos = updatedPos(move);
		updatedPos.setLocation(updatedPos.x * scalar, updatedPos.y * scalar);
		gameAI.getKeyState().setMovement(FromAIMovesToGameMoves(move));
		while (checkIfReachedDestination(gameAI.getPos(), updatedPos)) {
			try {
				sleep(10);
			} catch (InterruptedException e) {

			}
		}
		gameAI.getKeyState().setMovement(Movement.NONE);

	}

	/**
	 * Perform sequence of moves.
	 *
	 * @param moves
	 *            the list of moves
	 * @param inDanger
	 *            the variable determining if the escape moves are passed in
	 *            that case make moves without considering anything else
	 */
	private void performMoves(LinkedList<AIActions> moves, boolean inDanger) {
		if (inDanger)
			while (moves != null && !moves.isEmpty() ) {
				makeSingleMove(moves.removeFirst());
			}
		else
			while (moves != null && !moves.isEmpty() && !safetyCh.inDanger() && safetyCh.checkMoveSafety(moves.peek())
					&& !safetyCh.isEnemyInBombRange()) {
				makeSingleMove(moves.removeFirst());
			}
	}

	/**
	 * Perform planned moves.
	 * When none of the players are reachable
	 *
	 * @param moves
	 *            the moves
	 */
	private void performPlannedMoves(LinkedList<AIActions> moves) {
		AIActions action;

		while (moves != null && !moves.isEmpty() && getMovesToEnemy() == null) {
			action = moves.removeFirst();
			// if actions is bomb place it
			if (action == AIActions.BOMB) {
				gameAI.getKeyState().setBomb(true);
				try {
					sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				}
				gameAI.getKeyState().setBomb(false);
			}
			// if action is none wait until the next move is safe
			else if (action == AIActions.NONE) {
				if (moves != null) {
					while (!safetyCh.checkMoveSafety(moves.peek())) {
					}
				}
			} 
			// otherwise make a standard move
			else {
				makeSingleMove(action);
			}
		}
	}

	/**
	 * Gets the moves to enemy.
	 *
	 * @return the moves to enemy
	 */
	private LinkedList<AIActions> getMovesToEnemy() {
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
	 * Main method for controlling what moves to make.
	 */
	private void move() {
		LinkedList<AIActions> moves;
		while (gameAI.isAlive()) {

			// if AI is in danger then find the escape route
			if (safetyCh.inDanger()) {
				moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
				performMoves(moves, true);
				
			}
			
//			//if enemy is in range and it is possible to place bomb and escape then do it
//			else if((moves = finder.canPutBombAndEscape()) != null){
//				System.out.println("Bomb");
//				gameAI.getKeyState().setBomb(true);
//				performMoves(moves, true);
//			}
//			
			
			
			// if enemy is in bomb range then place the bomb and go to the
////			// safe location
			else if (safetyCh.isEnemyInBombRange()) {
				gameAI.getKeyState().setBomb(true);
				moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
				performMoves(moves, true);
			}
			// if enemy is accessible(no boxes are blocking the path) then
			// find a route to it and make moves
			else if ((moves = getMovesToEnemy()) != null) {
				performMoves(moves, false);
			}
			// if enemy is not in the range get the plan how to reach enemy and fullfill it
			else if ((moves = finder.getPlanToEnemy(gameAI.getGridPos(), finder.getNearestEnemy())) != null) {
				performPlannedMoves(moves);
			}

			gameAI.getKeyState().setBomb(false);
		}
	}

}
