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

public class OnlineGame implements GameInterface {

	// private String playerName;
	private HashMap<Response, Integer> controlScheme;
	// private Screen screen;
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

	public OnlineGame(UserInterface ui, ClientThread client, GameState gameState, String playerName,
			List<ClientServerPlayer> onlinePlayers, HashMap<Response, Integer> controls, boolean fullScreen, int width,
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


		try {

			this.graphics = new Graphics("Bomb Blitz", width, height, false, this, fullScreen);
			this.graphics.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(Screen screen) {
		try {
			System.out.println("Giving screen to renderer");
			this.renderer.init(screen);
			renderer.stopFrontScreen();

			while (this.gameState == null) {
				Thread.sleep(100);
				this.gameState = this.client.getGameState();
			}

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

			this.keyState = new KeyboardState();

		} catch (Exception e) {
			e.printStackTrace();
		}

		this.ui.hide();

		AudioManager.playMusic();
	}

	private float gameOverCounter = 0;
	private boolean playMusic = true;
	private Player player;

	@Override
	public void update(float interval) {

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
			renderer.stopFrontScreen();
			this.keyState.setBomb(false);
			this.keyState.setMovement(Movement.NONE);
			AudioManager.playEventList(gameState.getAudioEvents());
		}

	}

	@Override
	public void render(Screen screen) {

		this.renderer.render(screen, this.gameState);
	}

	@Override
	public void input(Screen screen) {

		this.bombPressed = this.input.update(screen, this.keyState, this.controlScheme, this.bombPressed);
		try {
			this.client.sendMove(keyState);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		AudioManager.pauseMusic();
		this.ui.show(this.fullScreen, true, this.gameEnded);
		System.out.println("RETURNED TO MENU");
		renderer.dispose();

	}

	public void setGameEnded(boolean gameEnded) {
		this.gameEnded = gameEnded;
	}

}
