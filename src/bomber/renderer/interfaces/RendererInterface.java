package bomber.renderer.interfaces;

public interface RendererInterface {

	// Initialize the renderer
	public void init() throws Exception;
	
	// Use the renderer's render method
	public void render(ScreenInterface screen);
	
	// Clear the screen
	public void clear();
	
	// Dispose the resources
	public void dispose();
} // END OF RendererInterface
