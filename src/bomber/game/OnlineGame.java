package bomber.game;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import bomber.UI.UserInterface;
import bomber.audio.AudioManager;
import bomber.networking.ClientServerPlayer;
import bomber.networking.ClientThread;
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
 *        OnlineGame class for "Bomb Blitz" Game Application (2017 Year 2 Team
 *        Project, Team B1). Represents an online game. Implements GameInterface
 *        for integration with the Renderer subsystem.
 */
public class OnlineGame implements GameInterface {

	private HashMap<Response, Integer> controlScheme;
	private GameState gameState;
	private KeyboardState keyState;
	private Graphics graphics;
	private Renderer renderer;
	private boolean bombPressed;
	private KeyboardInput input;
	private UserInterface ui;
	private ClientThread client;
	private boolean fullScreen;
	private boolean gameEnded;
	private String playerName;
	private List<ClientServerPlayer> onlinePlayers;
	private float musicVolume;
	private float soundVolume;
	private float gameOverCounter = 0;
	private boolean playMusic = true;
	private Player player;
	private boolean mutePressed;

	/**
	 * Create a new OnlineGame object.
	 * 
	 * @param ui
	 *            the user interface
	 * @param client
	 *            the client object
	 * @param gameState
	 *            the gamestate object received from the server
	 * @param playerName
	 *            the player name
	 * @param musicVolume
	 *            the music volume
	 * @param soundVolume
	 *            the sound effects volume
	 * @param onlinePlayers
	 *            the list of online players
	 * @param controls
	 *            the control scheme
	 * @param fullScreen
	 *            true if the game should be fullscreen
	 * @param width
	 *            the width of the game screen
	 * @param height
	 *            the height of the game screen
	 */
	public OnlineGame(UserInterface ui, ClientThread client,
			GameState gameState, String playerName, float musicVolume,
			float soundVolume, List<ClientServerPlayer> onlinePlayers,
			HashMap<Response, Integer> controls, boolean fullScreen, int width,
			int height) {

		this.ui = ui;
		this.gameState = gameState;
		this.client = client;
		this.playerName = playerName;
		this.onlinePlayers = onlinePlayers;
		this.controlScheme = controls;
		this.bombPressed = false;
		this.fullScreen = fullScreen;
		this.input = new KeyboardInput();
		this.renderer = new Renderer(false);
		this.mutePressed = false;
		this.musicVolume = musicVolume;
		this.soundVolume = soundVolume;

		try {

			this.graphics = new Graphics("Bomb Blitz", width, height, false,
					this, fullScreen);
			this.graphics.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called when the game starts.
	 */
	@Override
	public void init(Screen screen) {
		try {
			this.renderer.init(screen);
			renderer.stopFrontScreen();

			// Get the gamestate from the server.
			while (this.gameState == null) {
				Thread.sleep(100);
				this.gameState = this.client.getGameState();
			}

			// Set the player names to display.
			int inc = 1;
			for (Player player : this.gameState.getPlayers()) {
				if (player.getPlayerID() <= 31) {
					for (ClientServerPlayer onlinePlayer : this.onlinePlayers) {
						if (player.getPlayerID() == onlinePlayer.getID()) {
							player.setName(onlinePlayer.getName());
							break;
						}
					}
					if (player.getName() == null) {
						player.setName("Player " + player.getPlayerID());
					}
					if (player.getName().equals(this.playerName)) {
						this.player = player;
					}
				} else {
					player.setName("AI " + inc);
					inc++;
				}
			}

			// Create a keyboard state to use for input.
			this.keyState = new KeyboardState();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Hide the UI.
		this.ui.hide();

		// Play music.
		AudioManager.playMusic();
	}

	/**
	 * Called when the game updates.
	 */
	@Override
	public void update(float interval) {

		// If the game is over, show the game over screen.
		if (gameEnded) {

			if (gameOverCounter < 3) {

				gameOverCounter += interval;
				if (this.player != null && this.player.isAlive()) {
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
			// Update the game and play audio.
			renderer.stopFrontScreen();
			this.keyState.setBomb(false);
			this.keyState.setMovement(Movement.NONE);
			AudioManager.playEventList(gameState.getAudioEvents());
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
	 * Called when the game checks for input.
	 */
	@Override
	public void input(Screen screen) {

		// Check for mute.
		this.mutePressed = this.input.muteCheck(screen, this.keyState,
				this.controlScheme, this.mutePressed, this.musicVolume,
				this.soundVolume);

		// Check for other inputs.
		this.bombPressed = this.input.update(screen, this.keyState,
				this.controlScheme, this.bombPressed);
		try {
			// Send the move to the server.
			this.client.sendMove(keyState);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called when the game is over.
	 */
	@Override
	public void dispose() {
		AudioManager.pauseMusic();

		// Show the UI
		this.ui.show(this.fullScreen, this.keyState.isMuted(), true,
				this.gameEnded);
		System.out.println("RETURNED TO MENU");
		renderer.dispose();

	}

	/**
	 * Set the game ended status.
	 * 
	 * @param gameEnded
	 *            true if the game has ended
	 */
	public void setGameEnded(boolean gameEnded) {
		this.gameEnded = gameEnded;
	}

}
