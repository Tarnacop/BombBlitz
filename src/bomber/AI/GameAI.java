package bomber.AI;

import java.awt.Point;

import bomber.game.GameState;
import bomber.game.Player;


public class GameAI extends Player	{
	
	public GameAI(String name, Point pos, int lives, double speed, GameState gameState	) {
		super(name, pos, lives, speed);
		(new AIManager(this, gameState)).start();	
	}

	
}
