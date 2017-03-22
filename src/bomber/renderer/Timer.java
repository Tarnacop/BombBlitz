package bomber.renderer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

/**
 * Class to calculate FPS, UPS, deltaTime and other render utils.
 * 
 * @author Alexandru Blinda
 * 
 */
public class Timer {

	private double lastLoopTime;

	private float timeCount;

	private int fps;

	private int fpsCount;

	private int ups;

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
	}

	/**
	 * Get the time since the screen started
	 * 
	 * @return The time since the screen started
	 */
	public double getTime() {

		return glfwGetTime();
	}

	/**
	 * Get the delta time
	 * 
	 * @return The delta time
	 */
	public float getDeltaTime() {

		double time = getTime();
		float deltaTime = (float) (time - lastLoopTime);
		lastLoopTime = time;
		timeCount = timeCount + deltaTime;
		return deltaTime;
	}

	/**
	 * Get the time since the last loop
	 * 
	 * @return The time since the last loop
	 */
	public double getLastLoopTime() {

		return lastLoopTime;
	}

	/**
	 * Update the timer
	 */
	public void update() {

		if (timeCount > 1f) {

			fps = fpsCount;
			fpsCount = 0;

			ups = upsCount;
			upsCount = 0;

			timeCount = timeCount - 1f;
		}

	}

	/**
	 * Get the FPS rate
	 * 
	 * @return The FPS rate
	 */
	public int getFPS() {

		if (fps > 0) {

			return fps;
		}
		return fpsCount;
	}

	/**
	 * Get the UPS rate
	 * 
	 * @return The UPS rate
	 */
	public int getUPS() {

		if (ups > 0) {

			return ups;
		}
		return upsCount;
	}

	/**
	 * Update the FPS rate
	 */
	public void updateFPS() {

		fps = fps + 1;
	}

	/**
	 * Update the UPS rate
	 */
	public void updateUPS() {

		ups = ups + 1;
	}

}