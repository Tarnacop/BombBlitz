package bomber.renderer;

import bomber.renderer.constants.RendererConstants;
import bomber.renderer.interfaces.GameInterface;
import bomber.renderer.interfaces.GameLogicInterface;

public class Game implements GameInterface {

	private final Screen screen;
	private final Thread gameLoopThread;
	private final Timer timer;
	private final GameLogicInterface gameLogic;
	
	public Game(String screenTitle, int screenWidth, int screenHeight, boolean vSync, GameLogicInterface gameLogic) throws Exception {
		
		gameLoopThread = new Thread(this, "_THREAD_GAME_LOOP");
		screen = new Screen(screenWidth, screenHeight, screenTitle, true);
		this.gameLogic = gameLogic;
		timer = new Timer();
	} // END OF CONSTRUCTOR
	
	@Override
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
			System.err.println("ERROR!");
			ex.printStackTrace();
		} finally {
			
			dispose();
		}
		
	} // END OF run METHOD
	
	// Initialize the game engine
	private void init() throws Exception {
		
		screen.init();
		timer.init();
		gameLogic.init(screen);
	} // END OF init METHOD
	
	// Update method
	private void update(float interval) {
		
		// Game logic gets updated
		gameLogic.update(interval);
	} // END OF update METHOD

	// Render method
	private void render() {
		
		gameLogic.render(screen);
		screen.update();
	} // END OF render METHOD
	
	private void gameLoop() {
		
		float deltaTime = 0f;
		float accumulator = 0f;
		float interval = 1f / RendererConstants.TARGET_UPS;
		
		boolean gameRunning = true;
		
		// The main loop of the game
		while(gameRunning && !screen.screenShouldClose()) {
			
			deltaTime = timer.getDeltaTime();
			accumulator = accumulator + deltaTime;
			
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
		
	} // END OF gameLoop METHOD
	
	private void sync() {
		
		float loopInterval = 1f / RendererConstants.TARGET_FPS;
		double endTime = timer.getLastLoopTime() + loopInterval;
		
		while(timer.getTime() < endTime) {
			
			try {
				
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            	
            	System.err.println("ERROR!");
            	ie.printStackTrace();
            }
		}
		
	} // END OF sync METHOD
	
	@Override
	public void dispose() {
		
		gameLogic.dispose();
	} // END OF dispose METHOD
} // END OF Application
