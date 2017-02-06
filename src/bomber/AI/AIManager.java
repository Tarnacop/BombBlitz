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

public class AIManager extends Thread {

	private GameAI gameAI;
	private GameState gameState;
	private RouteFinder finder;
	private SafetyChecker safetyCh;

	public AIManager(GameAI ai, GameState gameState) {
		this.gameAI = ai;
		this.gameState = gameState;
		this.safetyCh = new SafetyChecker(gameState, ai);
		this.finder = new RouteFinder(gameState, ai, safetyCh);
		
	}

	public void run() {
		while (gameAI.isAlive()) {
			move();
		}
	}

	private Point updatedPos(Movement move) {
		Point aiPos = (Point) gameAI.getPos().clone();

		switch (move) {
		case UP:
			aiPos.setLocation(aiPos.x, aiPos.y - 1);
			break;
		case DOWN:
			aiPos.setLocation(aiPos.x, aiPos.y + 1);
			break;
		case LEFT:
			aiPos.setLocation(aiPos.x-1, aiPos.y );
			break;
		case RIGHT:
			aiPos.setLocation(aiPos.x+1, aiPos.y );
			break;
		default:
			break;
		}
		return aiPos;
	}

	private void makeSingleMove(Movement move) {
		Point updatedPos = updatedPos(move);
		gameAI.getKeyState().setKey(move);
		while (gameAI.getPos().equals(updatedPos)) {
		}
		gameAI.getKeyState().setKey(Movement.NONE);
	}

	private void performMoves(LinkedList<Movement> moves, boolean inDanger) {
		if (inDanger)
			while (!moves.isEmpty()) {
				makeSingleMove(moves.removeFirst());
			}
		else
			while (!moves.isEmpty() && !safetyCh.inDanger() && safetyCh.checkMoveSafety(moves.peek()) && !safetyCh.isEnemyInBombRange() ) {
				makeSingleMove(moves.removeFirst());
			}
		move();
	}

	private void move() {
		LinkedList<Movement> moves;
		//if AI is in danger then find the escape route
		if (safetyCh.inDanger()) {
			moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
			performMoves(moves, true);
		}
		//if enemy is in bomb range then place the bomb and go to the safe location
		else if(safetyCh.isEnemyInBombRange())
		{
			gameAI.getKeyState().placeBomb();
			moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
			performMoves(moves,true);
		}
		//if enemy is accessable(no boxes are blocking the path) then find a route to it and make moves
		else if( (moves = finder.findRoute(gameAI.getPos(), finder.getNearestEnemy())) != null)
		{
			performMoves(moves, false);
		}
	}

}
