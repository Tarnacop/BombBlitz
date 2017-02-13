package bomber.AI;

import java.awt.Point;

import bomber.game.GameState;
import bomber.game.Player;
import bomber.renderer.shaders.Mesh;

/**
 * @author Jokubas Liutkus 
 * The Class GameAI.
 */
public class GameAI extends Player {

	/** The game state. */
	private GameState state;

	/** The AI manager thread. */
	private Thread aiManager;

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
	public GameAI(String name, Point pos, int lives, double speed, GameState gameState, Mesh mesh) {
		super(name, pos, lives, speed, mesh);
		this.state = gameState;
		aiManager = new AIManager(this, state);
	}

	/**
	 * Begin. Run this when the game starts
	 * 
	 */
	public void begin() {
		aiManager.start();
	}

	/**
	 * Pause thread.
	 */
	public void pauseThread() {
		try {
			aiManager.wait();
		} catch (InterruptedException e) {
			
		}
	}

	/**
	 * Resume thread.
	 */
	public void resumeThread() {
		aiManager.notify();
	}

}
