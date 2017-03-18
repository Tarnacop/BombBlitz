package bomber.game;

import java.io.IOException;
import java.util.HashMap;

import bomber.AI.GameAI;
import bomber.UI.UserInterface;
import bomber.audio.AudioManager;
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
	// private Player player;
	private AudioManager audio;
	private UserInterface ui;
	// private int aiNum;
	private ClientThread client;
	private boolean fullScreen;

	public OnlineGame(UserInterface ui, ClientThread client, GameState gameState, String playerName,
			HashMap<Response, Integer> controls, boolean fullScreen, int width, int height) {

		this.ui = ui;
		this.gameState = gameState;
		this.client = client;
		// this.playerName = playerName;
		this.controlScheme = controls;
		this.bombPressed = false;
		this.fullScreen = fullScreen;
		this.input = new KeyboardInput();
		this.renderer = new Renderer();
		audio = new AudioManager();
		audio.playMusic();

		try {

			this.graphics = new Graphics("Bomb Blitz", width, height, false, this, fullScreen);
			this.graphics.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
				this.gameState = this.client.getGameState();
				Thread.sleep(100);
			}

			this.keyState = new KeyboardState();

		} catch (Exception e) {
			e.printStackTrace();
		}

		this.ui.hide();
	}

	private float gameOverCounter = 0;
	private	float frontScreenCounter = 0f;
	
	@Override
	public void update(float interval) {

		this.gameState = this.client.getGameState();
		if(this.gameState.gameOver()){
			
			if(gameOverCounter < 3) {
				
				gameOverCounter += interval;
				renderer.displayGameOver();
			} else {
				this.graphics.getScreen().close();
			}
		}else {
			// Wait 5 seconds
			if(frontScreenCounter <= 5) {
				
				frontScreenCounter += interval;
			} 
			else {
				renderer.stopFrontScreen();
				this.keyState.setBomb(false);
				this.keyState.setMovement(Movement.NONE);
				audio.playEventList(gameState.getAudioEvents());
			}
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

		this.ui.show(this.fullScreen);
		System.out.println("RETURNED TO MENU");
		renderer.dispose();
		audio.stopAudio();

	}
}
