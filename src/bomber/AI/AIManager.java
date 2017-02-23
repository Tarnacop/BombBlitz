
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
public class AIManager extends AITemplate{


	/**
	 * Instantiates a new AI manager.
	 *
	 * @param ai
	 *            the AI
	 * @param gameState
	 *            the game state
	 */
	public AIManager(GameAI ai, GameState gameState) {
		super(ai, gameState);
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
	protected void performMoves(LinkedList<AIActions> moves, boolean inDanger) {
		if (inDanger)
			while (moves != null && !moves.isEmpty() && gameAI.isAlive() ) {
				makeSingleMove(moves.removeFirst());
			}
		else
			while (moves != null && !moves.isEmpty() && !safetyCh.inDanger() && safetyCh.checkMoveSafety(moves.peek())
					&& !safetyCh.isEnemyInBombRange() && gameAI.isAlive()) {
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
	protected void performPlannedMoves(LinkedList<AIActions> moves) {
		AIActions action;

		while (moves != null && !moves.isEmpty() && getMovesToEnemy() == null && gameAI.isAlive()) {
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
					while (!safetyCh.checkMoveSafety(moves.peek()) && gameAI.isAlive()) {
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
	 * Main method for controlling what moves to make.
	 */
	protected void move() {
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
