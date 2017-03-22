package test.UI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static bomber.game.Block.BLANK;
import static bomber.game.Block.SOLID;
import static bomber.game.Response.*;
import bomber.AI.AIDifficulty;
import bomber.AI.GameAI;
import bomber.audio.AudioManager;
import bomber.game.Block;
import bomber.game.GameState;
import bomber.game.KeyboardInput;
import bomber.game.KeyboardState;
import bomber.game.Map;
import bomber.game.Movement;
import bomber.game.Player;
import bomber.game.Response;
import bomber.physics.PhysicsEngine;
import bomber.renderer.Graphics;
import bomber.renderer.Renderer;
import bomber.renderer.Screen;
import bomber.renderer.interfaces.GameInterface;

public class TestGame implements GameInterface {

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
	private int aiNum = 0;
	private AIDifficulty aiDiff = AIDifficulty.EASY;

	public TestGame() {
		HashMap<Response, Integer> controls = new HashMap<Response, Integer>();
		controls.put(PLACE_BOMB, GLFW_KEY_SPACE);
		controls.put(UP_MOVE, GLFW_KEY_UP);
		controls.put(DOWN_MOVE, GLFW_KEY_DOWN);
		controls.put(LEFT_MOVE, GLFW_KEY_LEFT);
		controls.put(RIGHT_MOVE, GLFW_KEY_RIGHT);	
		controls.put(PAUSE_GAME, GLFW_KEY_P);
		
		this.controlScheme = controls;
		
		Block[][] grid = new Block[][]{{SOLID, BLANK}, {BLANK, BLANK}};
		List<Point> spawns = new ArrayList<Point>();
		spawns.add(new Point(64,64));
		this.map = new Map("TestMap", grid, spawns);
		
		this.bombPressed = false;
		this.pausePressed = false;
		this.input = new KeyboardInput();
		this.renderer = new Renderer(false);

		try {

			this.graphics = new Graphics("Test", 100, 100, false, this, false);
			this.graphics.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(Screen screen) {
		try {
			this.renderer.init(screen);

			List<Point> spawns = this.map.getSpawnPoints();

			this.player = new Player("Test", new Point(spawns.get(0).x, spawns.get(0).y), 5, 300);
			this.keyState = this.player.getKeyState();
			ArrayList<Player> list = new ArrayList<Player>();
			list.add(this.player);

			this.gameState = new GameState(map, list);
			this.physics = new PhysicsEngine(gameState);

			for (int x = 1; x <= this.aiNum; x++) {
				Player ai = new GameAI("Ai " + x, new Point(spawns.get(x).x, spawns.get(x).y), 5, 300, gameState,
						this.aiDiff);
				ai.setPlayerID(32);
				list.add(ai);
			}



		} catch (Exception e) {
			e.printStackTrace();
		}

		AudioManager.playMusic();
	}

	private float gameOverCounter = 0;
	private float frontScreenCounter = 0f;
	private boolean startAIs = true;
	private boolean gamePaused = false;
	private boolean playMusic = true;
	
	@Override
	public void update(float interval) {

		if (this.gameState.gameOver()) {

			if (gameOverCounter < 3) {

				gameOverCounter += interval;
				if(this.player.isAlive()){
					renderer.displayGameOver(true);
					if(playMusic){
						AudioManager.pauseMusic();
						AudioManager.playGameOverWon();
						playMusic = false;
					}
				}
				else{
					renderer.displayGameOver(false);
					if(playMusic){
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

					this.physics.update((int) (interval * 1000));
					AudioManager.playEventList(gameState.getAudioEvents());
				}
			}
		}
	}

	@Override
	public void render(Screen screen) {

		this.renderer.render(screen, this.gameState);
	}

	@Override
	public void input(Screen screen) {

		this.keyState.setBomb(false);
		this.keyState.setMovement(Movement.NONE);
		this.pausePressed = this.input.pauseCheck(screen, this.keyState, this.controlScheme, this.pausePressed);
		this.bombPressed = this.input.update(screen, this.keyState, this.controlScheme, this.bombPressed);
	}

	@Override
	public void dispose() {
		AudioManager.pauseMusic();
		for (Player player : this.gameState.getPlayers()) {

			player.setAlive(false);
		}
		renderer.dispose();
	}
}
