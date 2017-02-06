package bomber.renderer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

import bomber.renderer.interfaces.TimerInterface;

public class Timer implements TimerInterface {

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
	
	
	@Override
	public void init() {
		
		lastLoopTime = getTime();
		timeCount = 0f;
		fps = 0;
		fpsCount = 0;
		ups = 0;
		upsCount = 0;
	} // END OF init METHOD

	@Override
	public double getTime() {
		
		return glfwGetTime();
	} // END OF getTime METHOD

	@Override
	public float getDeltaTime() {
		
		double time = getTime();
		float deltaTime = (float) (time - lastLoopTime);
		lastLoopTime = time;
		timeCount = timeCount + deltaTime;
		return deltaTime;
	} // END OF getDeltaTime METHOD

	@Override
	public double getLastLoopTime() {
		
		return lastLoopTime;
	} // END OF getLastLoopTime METHOD

	@Override
	public void update() {
		
		if(timeCount > 1f) {
			
			fps = fpsCount;
			fpsCount = 0;
			
			ups = upsCount;
			upsCount = 0;
			
			timeCount = timeCount - 1f;
		}
		
	} // END OF update METHOD

	@Override
	public int getFPS() {
		
		if(fps > 0) {
			
			return fps;
		}
		return fpsCount;
	} // END OF getFPS METHOD

	@Override
	public int getUPS() {
		
		if(ups > 0) {
		
			return ups;
		}
		return upsCount;
	} // END OF getUPS METHOD

	@Override
	public void updateFPS() {
		
		fps = fps + 1;
	} // END OF fps METHOD

	@Override
	public void updateUPS() {
		
		ups = ups + 1;
	} // END OF ups METHOD

} // END OF Timer CLASS
