package bomber.AI;

import java.awt.Point;

import bomber.game.GameState;
import bomber.game.Player;
import bomber.renderer.shaders.ColourMesh;

/**
 * @author Jokubas Liutkus 
 * The Class GameAI.
 */
public class GameAI extends Player {

	/** The game state. */
	private GameState state;

	/** The AI manager thread. */
	private AITemplate ai;
	

	/**
	 * Instantiates a new game AI.
	 *
	 * @param name
	 *            the name of the AI
	 * @param pos
	 *            the starting position of the AI
	 * @param lives
	 *            the lives
	 * @param speed
	 *            the speed
	 * @param gameState
	 *            the game state
	 * @param mesh
	 *            the mesh
	 */
	public GameAI(String name, Point pos, int lives, double speed, GameState gameState, ColourMesh mesh, AIDifficulty diff) {
		super(name, pos, lives, speed, mesh);
		this.state = gameState;
		setDifficulty(diff);
	}

	/**
	 * Begin. Run this when the game starts
	 * 
	 */
	public void begin() {
		ai.start();
		
	}

	public void stop()
	{
		this.setAlive(false);
	}
	
	public void setDifficulty(AIDifficulty diff)
	{
		System.out.println("DIFFICULTY: " + diff);
		switch(diff)
		{
		case EASY:
			ai = new EasyAI(this, state);
			break;
		case MEDIUM:
			ai = new MediumAI(this, state);
			break;
		case HARD:
			ai = new HardAI(this, state);
			break;
		case EXTREME:
			ai = new ExtremeAI(this, state);
			break;
		default:
			ai = new HardAI(this, state);
			break;
		}

	}
	
	/**
	 * Pause the AI.
	 */
	public void pause() {
		stop();
	}

	/**
	 * Resume the AI.
	 */
	public void resume() {
		this.setAlive(true);
		ai.start();
	}

}
