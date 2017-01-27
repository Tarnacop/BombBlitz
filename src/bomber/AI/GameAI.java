package bomber.AI;

import java.awt.Point;

import bomber.game.GameState;
import bomber.game.Player;


public class GameAI extends Player	{
	
	private GameState state;
	public GameAI(String name, Point pos, int lives, double speed, GameState gameState	) {
		super(name, pos, lives, speed);
		super.setAlive(true);
		this.state = gameState;
		
	}
	
	public void begin()
	{
		(new AIManager(this, state)).start();	
	}

	
}
