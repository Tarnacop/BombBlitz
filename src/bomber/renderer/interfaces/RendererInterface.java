package bomber.renderer.interfaces;

import bomber.renderer.utils.GameEntity;

public interface RendererInterface {

	// Initialize the renderer
	public void init(ScreenInterface screen) throws Exception;
	
	// Use the renderer's render method
	public void render(ScreenInterface screen, GameEntity[] gameEntities);
	
	// Dispose the resources
	public void dispose();
} // END OF RendererInterface
