package bomber.renderer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

/**
 * 
 * @author Alexandru Blinda
 * Class to calculate FPS, UPS, deltaTime and other render utils
 */
public class Timer {

	private double lastLoopTime;
	
	// Used for FPS and UPS calculation
	private float timeCount;
	
	// Frames per second
	private int fps;
	
	// Counter used to calculate fps
	private int fpsCount;
	
	// Updates per second
	private int ups;
	
	// Counter used to calculate ups
	private int upsCount;
	
	/**
	 * Initialise the Timer
	 */
	public void init() {
		
		lastLoopTime = getTime();
		timeCount = 0f;
		fps = 0;
		fpsCount = 0;
		ups = 0;
		upsCount = 0;
	} // END OF init METHOD

	/**
	 * Get the time since the screen started
	 * @return The time since the screen started
	 */
	public double getTime() {
		
		return glfwGetTime();
	} // END OF getTime METHOD

	/**
	 * Get the delta time
	 * @return The delta time
	 */
	public float getDeltaTime() {
		
		double time = getTime();
		float deltaTime = (float) (time - lastLoopTime);
		lastLoopTime = time;
		timeCount = timeCount + deltaTime;
		return deltaTime;
	} // END OF getDeltaTime METHOD

	/**
	 * Get the time since the last loop
	 * @return The time since the last loop
	 */
	public double getLastLoopTime() {
		
		return lastLoopTime;
	} // END OF getLastLoopTime METHOD

	/**
	 * Update the timer
	 */
	public void update() {
		
		if(timeCount > 1f) {
			
			fps = fpsCount;
			fpsCount = 0;
			
			ups = upsCount;
			upsCount = 0;
			
			timeCount = timeCount - 1f;
		}
		
	} // END OF update METHOD

	/**
	 * Get the FPS rate
	 * @return The FPS rate
	 */
	public int getFPS() {
		
		if(fps > 0) {
			
			return fps;
		}
		return fpsCount;
	} // END OF getFPS METHOD

	/**
	 * Get the UPS rate
	 * @return The UPS rate
	 */
	public int getUPS() {
		
		if(ups > 0) {
		
			return ups;
		}
		return upsCount;
	} // END OF getUPS METHOD

	/**
	 * Update the FPS rate
	 */
	public void updateFPS() {
		
		fps = fps + 1;
	} // END OF fps METHOD

	/**
	 * Update the UPS rate
	 */
	public void updateUPS() {
		
		ups = ups + 1;
	} // END OF ups METHOD

} // END OF Timer CLASS
