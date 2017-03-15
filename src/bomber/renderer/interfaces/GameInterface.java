package bomber.renderer.interfaces;

import bomber.renderer.Screen;

/**
 * Interface for the GameLogic
 * @author Alexandru
 *
 */
public interface GameInterface {

	/**
	 * Initialize the game logic with the given screen
	 * @param screen The given screen
	 */
	public void init(Screen screen);
	
	/**
	 * Update the game logic at a given interval
	 * @param interval The given interval
	 */
	public void update(float interval);
	
	/**
	 * Render on the given screen
	 * @param screen The given screen
	 */
	public void render(Screen screen);
	
	/**
	 * Listen to input on the given screen
	 * @param screen Listen to input
	 */
	public void input(Screen screen);
	
	/**
	 * Dispose the game logic
	 */
	public void dispose();
} // END OF GameInterface
