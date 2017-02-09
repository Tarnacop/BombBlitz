package bomber.renderer.interfaces;

public interface GameLogicInterface {

	// Initialization part
	public void init(ScreenInterface screen) throws Exception;
	
	// Update the game logic at an interval
	public void update(float interval);
	
	// Render graphics on the given screen
	public void render(ScreenInterface screen);
	
	// Dispose the resources used
	public void dispose();
	
} // END OF GameLogicInterface
