package bomber.AI;

import java.awt.Point;
import java.util.LinkedList;

import bomber.game.GameState;
import bomber.game.Movement;
import bomber.game.Player;

/**
 * The Class AIManager for making moves.
 */
public class AIManager extends Thread {

	/** The game AI. */
	private GameAI gameAI;

	/** The finder for finding the route. */
	private RouteFinder finder;

	private GameState gameState;
	/** The safety checker for AI. */
	private SafetyChecker safetyCh;
	private final int scalar = 64;
	private final int playerSize = 32;

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
		this.gameState = gameState;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		move();
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

	private boolean checkMoving(Point currentPixel, Point updatedFinalPixelPos) {
		boolean check = (currentPixel.x - updatedFinalPixelPos.x) <= (scalar - playerSize);
		check &= (updatedFinalPixelPos.x <= currentPixel.x);
		check &= (currentPixel.y - updatedFinalPixelPos.y) <= (scalar - playerSize);
		check &= (updatedFinalPixelPos.y <= currentPixel.y);
		return !check;
	}

	/**
	 * Make single move.
	 *
	 * @param move
	 *            the move
	 */
	private void makeSingleMove(AIActions move) {
		Point updatedPos = updatedPos(move);
		updatedPos.setLocation(updatedPos.x * scalar, updatedPos.y * scalar);
		gameAI.getKeyState().setMovement(FromAIMovesToGameMoves(move));
		while (checkMoving(gameAI.getPos(), updatedPos)) {
			 try {
			 sleep(10);
			 } catch (InterruptedException e) {
			
			 }
		}
		gameAI.getKeyState().setMovement(Movement.NONE);
		System.out.println("done");

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
		System.out.println("called");
		if (inDanger)
			while (moves != null && !moves.isEmpty()) {
				makeSingleMove(moves.removeFirst());
			}
		else
			while (moves != null && !moves.isEmpty() && !safetyCh.inDanger() && safetyCh.checkMoveSafety(moves.peek())
					&& !safetyCh.isEnemyInBombRange()) {
				makeSingleMove(moves.removeFirst());
			}	
	}

	private void performPlannedMoves(LinkedList<AIActions> moves) {
		AIActions action;
		System.out.println(moves);
		
		while (moves != null && !moves.isEmpty() && !safetyCh.inDanger()/* && getMovesToEnemy() == null*/) {
			System.out.println("not called");
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
					while (!safetyCh.checkMoveSafety(moves.peek())){
						System.out.println("waiting");
					}
				}
			} else
			{
				makeSingleMove(action);
			}
		}
		System.out.println("finished");
	}

	private LinkedList<AIActions> getMovesToEnemy() {
		LinkedList<AIActions> moves = finder.findRoute(gameAI.getGridPos(), finder.getNearestEnemy());
		if (moves != null)
			return moves;
//		for (Player p : gameState.getPlayers()) {
//			if (!p.equals(gameAI)) {
//				moves = finder.findRoute(gameAI.getGridPos(), p.getGridPos());
//				if (moves != null)
//					return moves;
//			}
//		}
		return null;

	}

	/**
	 * Main method for controlling what moves to make
	 */
	private void move() {
		LinkedList<AIActions> moves;
		while (gameAI.isAlive()) {

			// if AI is in danger then find the escape route
			if (safetyCh.inDanger()) {
				System.out.println("1");
				moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
				performMoves(moves, true);
			}
			// if enemy is in bomb range then place the bomb and go to the
			// safe
			// location
			else if (safetyCh.isEnemyInBombRange()) {
				System.out.println("2");
				gameAI.getKeyState().setBomb(true);
				moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
				performMoves(moves, true);
			}
			// if enemy is accessible(no boxes are blocking the path) then
			// find
			// a
			// route to it and make moves
			else if ((moves = getMovesToEnemy()) != null) {
				System.out.println("3");
				System.out.println(moves);
				performMoves(moves, false);
			}
			// if enemy is not in the range get the plan how to reach enemy
			// and fullfill it
			else if ((moves = finder.getPlanToEnemy(gameAI.getGridPos(), finder.getNearestEnemy())) != null) {
				System.out.println("happens");
				performPlannedMoves(moves);
			}

			gameAI.getKeyState().setBomb(false);
		}
	}

}
