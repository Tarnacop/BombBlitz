package bomber.AI;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import bomber.game.Block;
import bomber.game.GameState;
import bomber.game.Movement;
import bomber.game.Player;


public class GameAI extends Player	{
	
	private GameState state;
	public GameAI(String name, Point pos, int lives, double speed, GameState gameState	) {
		super(name, pos, lives, speed);
		this.state = gameState;
	
	}
	
	public void begin()
	{
		(new AIManager(this, state)).start();	
	}
	
	
}
