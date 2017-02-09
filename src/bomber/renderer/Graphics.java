package bomber.renderer;

import bomber.renderer.constants.RendererConstants;
import bomber.game.GameState;

public class Graphics implements Runnable {

	private final Screen screen;
	private final Thread gameLoopThread;
	private final Timer timer;
	private final Game gameLogic;
	private final GameState state;
	private final Renderer renderer;
	
	public Graphics(Screen screen, Game gameLogic, GameState state) throws Exception {
		
		gameLoopThread = new Thread(this, "_THREAD_GAME_LOOP");
		
		this.state = state;
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
			System.err.println("ERROR!");
			ex.printStackTrace();
		} finally {
			
			dispose();
		}
		
	} // END OF run METHOD
	
	// Initialize the game engine
	private void init() throws Exception {
		
		timer.init();
	} // END OF init METHOD
	
	// Update method
	private void update(float interval) {
		
		// Game logic gets updated
		renderer.render(screen, state);
		gameLogic.update(interval);
	} // END OF update METHOD

	// Render method
	private void render() {
		
		// Render the renderer
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
	
	public void dispose() {
		
		renderer.dispose();
	} // END OF dispose METHOD
} // END OF Application
