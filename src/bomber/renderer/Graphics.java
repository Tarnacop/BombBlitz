package bomber.renderer;

import bomber.game.Constants;
import bomber.renderer.interfaces.GameInterface;
/**
 * Class that holds the Graphic Thread with all its features
 * @author Alexandru Blinda
 * 
 */
public class Graphics implements Runnable {

	private final Thread gameLoopThread;
	private final Screen screen;
	private final Timer timer;
	private final GameInterface gameLogic;
	
	/**
	 * Create a Graphics object with the given parameters.
	 * @param screenTitle The title of the screen
	 * @param screenWidth The width of the screen
	 * @param screenHeight The height of the screen
	 * @param vSync	Boolean to use vSync
	 * @param gameLogic The game logic for the graphics
	 * @param fullScreen The game runs in full screen or window
	 * @throws Exception
	 */
	public Graphics(String screenTitle, int screenWidth, int screenHeight, boolean vSync, GameInterface gameLogic, boolean fullScreen) throws Exception {
		
		gameLoopThread = new Thread(this, "_THREAD_GAME_LOOP");
		this.screen = new Screen(screenTitle, screenWidth, screenHeight, vSync, fullScreen);
		this.gameLogic = gameLogic;
		timer = new Timer();
	} // END OF CONSTRUCTOR
	
	/**
	 * Start the graphics
	 */
	public void start() {
	
		// Start the thread for the game engine loop
		gameLoopThread.start();
	} // END OF start METHOD

	/**
	 * Run the graphics
	 */
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
	
	/**
	 * Initialise the graphics
	 * @throws Exception
	 */
	// Initialize the game engine
	private void init() throws Exception {
		
		this.screen.init();
		this.timer.init();
		this.gameLogic.init(screen);
	} // END OF init METHOD
	
	/**
	 * Update the game logic at the given interval
	 * @param interval The interval at everything is updated
	 */
	// Update method
	private void update(float interval) {
		
		// Game logic gets updated
		gameLogic.update(interval);
	} // END OF update METHOD

	/**
	 * Render everything on the screen
	 */
	// Render method
	private void render() {
		
		// Render the renderer
		gameLogic.render(screen);
		screen.update();
	} // END OF render METHOD
	
	/**
	 * The game loop responsible with calculating UPS and FPS
	 */
	private void gameLoop() {
		
		float deltaTime = 0f;
		float accumulator = 0f;
		float interval = 1f / Constants.TARGET_UPS;
		
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
	} // END OF gameLoop METHOD
	
	/**
	 * Synchronise everything if vSync is not on
	 */
	private void sync() {
		
		float loopInterval = 1f / Constants.TARGET_FPS;
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
	
	/**
	 * Listen for inputs from the game logic
	 */
	public void input() {
		
		gameLogic.input(screen);;
	} // END OF input METHOD
	
	/**
	 * Dispose the graphics and game logic
	 */
	public void dispose() {
		
		gameLogic.dispose();
	} // END OF dispose METHOD

	/**
	 * Get the screen of the graphics
	 * @return The screen
	 */
	public Screen getScreen() {
		
		return this.screen;
	} // END OF getScreeb METHOD
} // END OF Application
