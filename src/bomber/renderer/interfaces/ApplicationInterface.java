package bomber.renderer.interfaces;

public interface ApplicationInterface {

	// Create an application
	void create();
	
	// Render the application
	void render();
	
	// Pause the application
	void pause();
	
	// Resume the application
	void resume();
	
	// Resize the application
	void resize(int width, int height);

	// Dispose the application
	void dispose();
	
} // END OF ApplicationInterface
