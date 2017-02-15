package bomber.renderer;

import bomber.renderer.constants.RendererConstants;
import bomber.renderer.interfaces.GameInterface;

import java.util.HashMap;

import bomber.game.GameState;
import bomber.game.Player;
import bomber.game.Response;

public class Graphics implements Runnable {

	private final Thread gameLoopThread;
	private final Screen screen;
	private final Timer timer;
	private final GameInterface gameLogic;
	
	public Graphics(String screenTitle, int screenWidth, int screenHeight, boolean vSync, GameInterface gameLogic) throws Exception {
		
		gameLoopThread = new Thread(this, "_THREAD_GAME_LOOP");
		this.screen = new Screen(screenTitle, screenWidth, screenHeight, vSync);
		System.out.println("Made new screen in Graphics");
		this.gameLogic = gameLogic;
		timer = new Timer();
	} // END OF CONSTRUCTOR
	
	public void start() {
	
		// Start the thread for the game engine loop
		gameLoopThread.start();
	} // END OF start METHOD

	@Override
	public void run() {
	
		try {
			
			// Try to initialize the game and loop
			init();
			gameLoop();
		}catch(Exception ex) {
			
			// Catch an exception
			System.err.println("ERROR1!" + ex.getMessage());
			ex.printStackTrace();
		} finally {
			
			dispose();
			System.out.println("DISPOSED");
		}
		
	} // END OF run METHOD
	
	// Initialize the game engine
	private void init() throws Exception {
		
		this.screen.init();
		this.timer.init();
		this.gameLogic.init(screen);
	} // END OF init METHOD
	
	// Update method
	private void update(float interval) {
		
		// Game logic gets updated
		gameLogic.update(interval);
	} // END OF update METHOD

	// Render method
	private void render() {
		
		// Render the renderer
		gameLogic.render(screen);
		screen.update();
	} // END OF render METHOD
	
	private void gameLoop() {
		
		float deltaTime = 0f;
		float accumulator = 0f;
		float interval = 1f / RendererConstants.TARGET_UPS;
		
		boolean gameRunning = true;
		
		// The main loop of the game
		while(gameRunning && (!screen.screenShouldClose())) {
			deltaTime = timer.getDeltaTime();
			accumulator = accumulator + deltaTime;
			input();
			// Update game and timer UPS if enough time passed
			while(accumulator >= interval) {

				
				update(interval);
				timer.updateUPS();
				accumulator = accumulator - interval;
			}
			
			// Render game and update timer fps
			render();
			timer.updateFPS();
			
			// Update the timer so we get accurat FPS and UPS
			timer.update();
			
			// If the vSync is off, use our sync method
			if(!screen.isVsyncOn()) {
				
				sync();
			}
		}
		System.out.println("FINISHED GAMELOOP");
	} // END OF gameLoop METHOD
	
	private void sync() {
		
		float loopInterval = 1f / RendererConstants.TARGET_FPS;
		double endTime = timer.getLastLoopTime() + loopInterval;
		
		while(timer.getTime() < endTime) {
			
			try {
				
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            	
            	System.err.println("ERROR2!");
            	ie.printStackTrace();
            }
		}
		
		
	} // END OF sync METHOD
	
	
	public void input() {
		
		gameLogic.input(screen);;
	}
	public void dispose() {
		
		gameLogic.dispose();
	} // END OF dispose METHOD
} // END OF Application
