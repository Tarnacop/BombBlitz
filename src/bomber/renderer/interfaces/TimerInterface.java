package bomber.renderer.interfaces;

public interface TimerInterface {

	// Initialize the Timer
	public void init();
	
	// Get the time GLFW was initialized
	public double getTime();
	
	// Get the deltaTime (time passed since the last loop)
	public float getDeltaTime();
	
	// Get the last loop time
	public double getLastLoopTime();
	
	// Update the FPS and UPS
	public void update();
	
	// Get the FPS rate
	public int getFPS();
	
	// Get the UPS rate
	public int getUPS();
	
	// Update FPS
	public void updateFPS();
	
	// Update UPS
	public void updateUPS();
	
} // END OF TimerInterface
