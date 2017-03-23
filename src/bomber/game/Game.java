package bomber.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bomber.AI.AIDifficulty;
import bomber.AI.GameAI;
import bomber.UI.UserInterface;
import bomber.audio.AudioManager;
import bomber.physics.PhysicsEngine;
import bomber.renderer.Graphics;
import bomber.renderer.Renderer;
import bomber.renderer.Screen;
import bomber.renderer.interfaces.GameInterface;

/**
 *
 * @author Owen Jenkins
 * @version 1.4
 * @since 2017-03-23
 * 
 *        Game class for "Bomb Blitz" Game Application (2017 Year 2 Team
 *        Project, Team B1). Represents an offline game. Implements
 *        GameInterface for integration with the Renderer subsystem.
 */
public class Game implements GameInterface {

	private String playerName;
	private Map map;
	private HashMap<Response, Integer> controlScheme;
	private PhysicsEngine physics;
	private GameState gameState;
	private KeyboardState keyState;
	private Graphics graphics;
	private Renderer renderer;
	private boolean bombPressed;
	private boolean pausePressed;
	private KeyboardInput input;
	private Player player;
	private UserInterface ui;
	private int aiNum;
	private AIDifficulty aiDiff;
	private boolean fullScreen;
	private float musicVolume;
	private float soundVolume;
	private float gameOverCounter = 0;
	private float frontScreenCounter = 0f;
	private boolean startAIs = true;
	private boolean gamePaused = false;
	private boolean playMusic = true;
	private boolean mutePressed;

	/**
	 * Create a new Game object.
	 * 
	 * @param ui
	 *            the user interface object
	 * @param map
	 *            the map to play on
	 * @param playerName
	 *            the player name
	 * @param controls
	 *            the control scheme
	 * @param aiNum
	 *            the number of AI players
	 * @param aiDiff
	 *            the difficulty of the AI players
	 * @param musicVolume
	 *            the music volume
	 * @param soundVolume
	 *            the sound effects volume
	 * @param fullScreen
	 *            true if the game should be fullscreen
	 * @param width
	 *            the width of the game window
	 * @param height
	 *            the height of the game window
	 * @param wasd
	 *            true if the game should be wasd controls
	 */
	public Game(UserInterface ui, Map map, String playerName,
			HashMap<Response, Integer> controls, int aiNum,
			AIDifficulty aiDiff, float musicVolume, float soundVolume,
			boolean fullScreen, int width, int height, boolean wasd) {

		this.aiNum = aiNum;
		this.aiDiff = aiDiff;
		this.ui = ui;
		this.map = map;
		this.playerName = playerName;
		this.controlScheme = controls;
		this.bombPressed = false;
		this.pausePressed = false;
		this.mutePressed = false;
		this.fullScreen = fullScreen;
		this.input = new KeyboardInput();
		this.renderer = new Renderer(wasd);
		this.musicVolume = musicVolume;
		this.soundVolume = soundVolume;

		try {

			// Create the OpenGL screen.
			this.graphics = new Graphics("Bomb Blitz", width, height, false,
					this, fullScreen);
			this.graphics.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called when the game begins.
	 */
	@Override
	public void init(Screen screen) {
		try {
			this.renderer.init(screen);

			List<Point> spawns = this.map.getSpawnPoints();

			// Create all the players at the correct spawn points.
			this.player = new Player(this.playerName, new Point(
					spawns.get(0).x, spawns.get(0).y), 5, 300);

			// Create a keyboard state to use for our player's input.
			this.keyState = this.player.getKeyState();
			ArrayList<Player> list = new ArrayList<Player>();
			list.add(this.player);

			// Create a GameState object to use.
			this.gameState = new GameState(map, list);

			// Create the physics engine.
			this.physics = new PhysicsEngine(gameState);

			// Create the AI players.
			for (int x = 1; x <= this.aiNum; x++) {
				Player ai = new GameAI("Ai " + x, new Point(spawns.get(x).x,
						spawns.get(x).y), 5, 300, gameState, this.aiDiff);
				ai.setPlayerID(32);
				list.add(ai);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Hide the user interface.
		this.ui.hide();

		// Start playing music.
		AudioManager.playMusic();
	}

	/**
	 * Called when the game updates.
	 */
	@Override
	public void update(float interval) {

		// Display the game over screen if the game is over.
		if (this.gameState.gameOver()) {

			if (gameOverCounter < 3) {

				gameOverCounter += interval;
				if (this.player.isAlive()) {
					renderer.displayGameOver(true);
					if (playMusic) {
						AudioManager.pauseMusic();
						AudioManager.playGameOverWon();
						playMusic = false;
					}
				} else {
					renderer.displayGameOver(false);
					if (playMusic) {
						AudioManager.pauseMusic();
						AudioManager.playGameOverLost();
						playMusic = false;
					}
				}

			} else {

				this.graphics.getScreen().close();
			}
		} else {
			// Wait 5 seconds
			if (frontScreenCounter <= 5) {

				frontScreenCounter += interval;
			} else if (startAIs) {
				for (Player p : this.gameState.getPlayers()) {
					if (p instanceof GameAI)
						p.begin();
				}

				startAIs = false;
			} else {
				renderer.stopFrontScreen();

				// If the game is paused, display the pause screen.
				if (this.player.getKeyState().isPaused()) {

					if (!this.gamePaused) {
						this.gamePaused = true;
						for (Player player : this.gameState.getPlayers()) {
							player.pause();
						}
						renderer.displayPauseScreen();
					}
				} else {
					if (this.gamePaused) {
						this.gamePaused = false;
						for (Player player : this.gameState.getPlayers()) {
							player.resume();
						}
						renderer.stopPauseScreen();
					}

					// Update the game and play audio events.
					this.physics.update((int) (interval * 1000));
					AudioManager.playEventList(gameState.getAudioEvents());
				}
			}
		}
	}

	/**
	 * Called when the game is rendered.
	 */
	@Override
	public void render(Screen screen) {

		this.renderer.render(screen, this.gameState);
	}

	/**
	 * Called when input is checked.
	 */
	@Override
	public void input(Screen screen) {

		this.keyState.setBomb(false);
		this.keyState.setMovement(Movement.NONE);

		// Check for mute.
		this.mutePressed = this.input.muteCheck(screen, this.keyState,
				this.controlScheme, this.mutePressed, this.musicVolume,
				this.soundVolume);

		// Check for pause.
		this.pausePressed = this.input.pauseCheck(screen, this.keyState,
				this.controlScheme, this.pausePressed);

		// Check for other input.
		this.bombPressed = this.input.update(screen, this.keyState,
				this.controlScheme, this.bombPressed);
	}

	/**
	 * Called when the game is over.
	 */
	@Override
	public void dispose() {
		AudioManager.pauseMusic();

		// Show the user interface again.
		this.ui.show(this.fullScreen, this.keyState.isMuted(), false, true);
		for (Player player : this.gameState.getPlayers()) {

			player.setAlive(false);
		}
		renderer.dispose();
	}
}
