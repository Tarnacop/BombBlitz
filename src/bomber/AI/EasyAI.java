package bomber.AI;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;

import bomber.game.GameState;

public class EasyAI extends AITemplate {

	/**
	 * Instantiates a new AI manager.
	 *
	 * @param ai
	 *            the AI
	 * @param gameState
	 *            the game state
	 */
	public EasyAI(GameAI ai, GameState gameState) {
		super(ai, gameState);
	}

	@Override
	protected void performMoves(LinkedList<AIActions> moves, boolean inDanger) {

		if (inDanger)
			while (moves != null && !moves.isEmpty() && gameAI.isAlive()) {
				makeSingleMove(moves.removeFirst());
			}
		else
			while (moves != null && !moves.isEmpty() && !safetyCh.inDanger() && safetyCh.checkMoveSafety(moves.peek())
					&& !safetyCh.isEnemyInBombRange() && gameAI.isAlive()) {
				makeSingleMove(moves.removeFirst());
			}
	}

	
	@Override
	protected void performPlannedMoves(LinkedList<AIActions> moves) {

		AIActions action;

		while (moves != null && !moves.isEmpty() && gameAI.isAlive() ){
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

	@Override
	protected void move() {
		LinkedList<AIActions> moves;
		Random random = new Random();
		
		while (gameAI.isAlive()) {

			// if AI is in dangger then escape only with 50% possibility
			if (safetyCh.inDanger() && random.nextBoolean()) {
				System.out.println("1");
				moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
				performMoves(moves, true);

			}

			// if enemy is in bomb range then place the bomb and go to the
			//// // safe location only with 30% possibility
			else if (safetyCh.isEnemyInBombRange() && random.nextInt(10) > 4) {
				System.out.println("2");
				gameAI.getKeyState().setBomb(true); 
				moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
				performMoves(moves, true);
			}
			
//			otherwise just generate a random goal and star fullfilling it
			else{
				System.out.println("3");
				int x = random.nextInt(gameState.getMap().getGridMap()[0].length);
				int y = random.nextInt(gameState.getMap().getGridMap().length);
				System.out.println(x + "  "  + y);
				moves = finder.getPlanToEnemy(gameAI.getGridPos(), new Point(x,y));
				performPlannedMoves(moves);
			}

			gameAI.getKeyState().setBomb(false);
		}

	}

}
