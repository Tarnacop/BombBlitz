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
	private AIState currentState;
	private AIState escapeState;
	private AIState findState;
	private AIState attackState;
	
	public AIManager(GameAI ai, GameState gameState) {
		this.gameAI = ai;
		this.gameState = gameState;
		this.escapeState = new EscapeState(this);
		this.findState = new FindState();
		this.attackState = new AttackState();
		this.currentState = findState;
	}

	public void run(){
		while(gameAI.isAlive())
		{
		}
	}
	
	


}
