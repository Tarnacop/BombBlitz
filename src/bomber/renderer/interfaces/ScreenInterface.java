package bomber.renderer.interfaces;

public interface ScreenInterface {

	// Method to initialize the screen
	public void init();
	
	// Method to set clear color
	public void setClearColour(float red, float green, float blue, float alpha);
	
	// public boolean isKeyPressed(int keyCode) - maybe useful
	
	// Check if the screen should close
	public boolean screenShouldClose();
	
	// Get the screen title
	public String getTitle();
	
	// Get the screen width
	public int getWidth();
	
	// Get the screen height
	public int getHeight();

	// Check if the Screen is resized
	public boolean isResized();
	
	// Check if V-sync is on
	public boolean isVsyncOn();
	
	// Set if a Screen is resized;
	public void setResized(boolean resized);
	
	// Set if V-sync is on
	public void setVsyncOn(boolean vSync);
	
	// Set the viewport
	public void setViewport(int originX, int originY, int width, int height);
	
	// Method to update the Screen as needed
	public void update();

} // END OF ScreenInterface
