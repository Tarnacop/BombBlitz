package bomber.renderer.interfaces;

public interface ScreenInterface {

	// Render graphics given a delta time
	void render(float deltaTime);
	
	// Resize the game
	void resize(int width, int height);
	
	// Pause method 
	void pause();
	
	// Resume method
	void resume();
	
	// Show method
	void show();
	
	// Hide method
	void hide();
	
	// Dispose method
	void dispose();
} // END OF ScreenInterface
