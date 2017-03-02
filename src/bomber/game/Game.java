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
import bomber.renderer.shaders.Mesh;

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

	public Game(UserInterface ui, Map map, String playerName, HashMap<Response, Integer> controls, int aiNum, AIDifficulty aiDiff) {

		this.aiNum = aiNum;
		this.aiDiff = aiDiff;
		this.ui = ui;
		this.map = map;
		this.playerName = playerName;
		this.controlScheme = controls;
		this.bombPressed = false;
		this.input = new KeyboardInput();
		this.renderer = new Renderer();
		audio = new AudioManager();
		audio.playMusic();

		try {
			
			int width = this.map.getPixelMap().length;
			int height = this.map.getPixelMap()[0].length;
			this.graphics = new Graphics("Bomb Blitz", width, height, false, this);
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
			float[] colours = new float[] { 0.1f, 0.3f, 0.5f, 0f, 0.1f, 0.3f, 0.5f, 0f, 0.1f, 0.3f, 0.5f, 0f };

			this.player = new Player(this.playerName, new Point(spawns.get(0).x, spawns.get(0).y), 100, 300, new Mesh(32, 32, colours));
			this.keyState = this.player.getKeyState();
			// System.out.println("Ours: " + this.keyState.toString() + "
			// Theirs: " + this.player.getKeyState().toString());
			ArrayList<Player> list = new ArrayList<Player>();
			list.add(this.player);

			this.gameState = new GameState(map, list);
			this.physics = new PhysicsEngine(gameState);

			for(int x = 1; x <= this.aiNum; x++){
				Player ai = new GameAI("Ai " + x, new Point(spawns.get(x).x, spawns.get(x).y), 5, 300, gameState, new Mesh(32, 32, colours), this.aiDiff);
				list.add(ai);
				ai.begin();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.ui.hide();
	}

	@Override
	public void update(float interval) {

		if(this.gameState.gameOver()){
			//call game over screen
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			dispose();
		}
		this.physics.update((int) (interval * 1000));
		this.keyState.setBomb(false);
		this.keyState.setMovement(Movement.NONE);
		List<Player> players = this.gameState.getPlayers();
		audio.playEventList(gameState.getAudioEvents());
	}

	@Override
	public void render(Screen screen) {

		this.renderer.render(screen, this.gameState);
	}

	@Override
	public void input(Screen screen) {

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
