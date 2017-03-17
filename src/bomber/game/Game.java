package bomber.game;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import bomber.AI.AIDifficulty;
import bomber.AI.GameAI;
import bomber.UI.UserInterface;
import bomber.audio.AudioManager;
import bomber.physics.PhysicsEngine;
import bomber.renderer.Graphics;
import bomber.renderer.Renderer;
import bomber.renderer.Screen;
import bomber.renderer.interfaces.GameInterface;

public class Game implements GameInterface {

	private String playerName;
	private Map map;
	private HashMap<Response, Integer> controlScheme;
	private Screen screen;
	private PhysicsEngine physics;
	private GameState gameState;
	private KeyboardState keyState;
	private Graphics graphics;
	private Renderer renderer;
	private boolean bombPressed;
	private KeyboardInput input;
	private Player player;
	private AudioManager audio;
	private UserInterface ui;
	private int aiNum;
	private AIDifficulty aiDiff;
	private boolean fullScreen;
	
	public Game(UserInterface ui, Map map, String playerName, HashMap<Response, Integer> controls, int aiNum, AIDifficulty aiDiff, float musicVolume, float soundVolume, boolean fullScreen, int width, int height) {

		this.aiNum = aiNum;
		this.aiDiff = aiDiff;
		this.ui = ui;
		this.map = map;
		this.playerName = playerName;
		this.controlScheme = controls;
		this.bombPressed = false;
		this.fullScreen = fullScreen;
		this.input = new KeyboardInput();
		this.renderer = new Renderer();
		audio = new AudioManager();
		System.out.println("Game Music " + musicVolume + " Sound " + soundVolume);
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
			
			List<Point> spawns = this.map.getSpawnPoints();

			this.player = new Player(this.playerName, new Point(spawns.get(0).x, spawns.get(0).y), 5, 300);
			this.keyState = this.player.getKeyState();
			// System.out.println("Ours: " + this.keyState.toString() + "
			// Theirs: " + this.player.getKeyState().toString());
			ArrayList<Player> list = new ArrayList<Player>();
			list.add(this.player);

			this.gameState = new GameState(map, list);
			this.physics = new PhysicsEngine(gameState);

			for(int x = 1; x <= this.aiNum; x++){
				Player ai = new GameAI("Ai " + x, new Point(spawns.get(x).x, spawns.get(x).y), 5, 300, gameState, this.aiDiff);
				list.add(ai);
//				ai.begin();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.ui.hide();
	}

	private float gameOverCounter = 0;
	private	float frontScreenCounter = 0f;
	private boolean startAIs = true;
	private boolean gamePaused = false;
	
	@Override
	public void update(float interval) {

		if(this.player.getKeyState().isPaused()){
			if(!this.gamePaused){
				this.gamePaused = true;
				for(Player player : this.gameState.getPlayers()){
					player.pause();
				}
				renderer.displayPauseScreen();
			}
		}
		else if(this.gameState.gameOver()){
			
			if(gameOverCounter < 3) {
				
				gameOverCounter += interval;
				renderer.displayGameOver();
			} else {
				
				dispose();
			}
		} else {
			
			if(this.gamePaused){
				this.gamePaused = false;
				for(Player player : this.gameState.getPlayers()){
					player.resume();
				}
				renderer.stopPauseScreen();
			}
			
			// Wait 5 seconds
			if(frontScreenCounter <= 5) {
				
				frontScreenCounter += interval;
			} 
			else if (startAIs) {
				for (Player p : this.gameState.getPlayers()) {
					if (p instanceof GameAI)
						p.begin();
				}

				startAIs = false;
			} 
			else {
				
				renderer.stopFrontScreen();
				this.physics.update((int) (interval * 1000));
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

		if(screen.getKeyState(GLFW.GLFW_KEY_ESCAPE) == GLFW_PRESS){
		    dispose();
		}
		this.bombPressed = this.input.update(screen, this.keyState, this.controlScheme, this.bombPressed);
	}

	@Override
	public void dispose() {

		this.ui.show();
		System.out.println("RETURNED TO MENU");
		for (Player player : this.gameState.getPlayers()) {

			player.setAlive(false);
			System.out.println("Player " + player.getName() + " is alive: " + player.isAlive());
		}
		renderer.dispose();
		this.graphics.getScreen().close();
		audio.stopAudio();
		
	}
}
