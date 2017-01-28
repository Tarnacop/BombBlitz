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
	public AIManager(GameAI ai, GameState gameState) {
		this.gameAI = ai;
		this.gameState = gameState;
	}

	public void run(){
		while(gameAI.isAlive())
		{
			
		}
	}
	
	


}
