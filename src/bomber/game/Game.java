package bomber.game;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import bomber.AI.GameAI;
import bomber.physics.PhysicsEngine;
import bomber.renderer.Graphics;
import bomber.renderer.Renderer;
import bomber.renderer.Screen;
import bomber.renderer.interfaces.GameInterface;
import bomber.renderer.shaders.Mesh;


public class Game implements GameInterface{
	
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

	public Game(Map map, String playerName, HashMap<Response, Integer> controls){
		
		this.map = map;
		this.playerName = playerName;
		this.controlScheme = controls;
		this.bombPressed = false;
		this.input = new KeyboardInput();
		this.renderer = new Renderer();
		
//		ClientThread client = null;
//		try {
//			client = new ClientThread("localhost", 1234);
//		} catch (SocketException e1) {
//			//Can't resolve hostname or port, or can't create socket
//			e1.printStackTrace();
//		}
//		
//		Thread networkThread = new Thread(client);
//		networkThread.start();
//		
//		try {
//			//connect to a lobby
//			client.connect("nickname");
//			//update my player list
//			client.updatePlayerList();
//			//get my player list
//			List<ClientServerPlayer> playerList = client.getPlayerList();
//			client.updateRoomList();
//			//simple room to display in lobby
//			List<ClientServerLobbyRoom> roomList = client.getRoomList();
//			//TODO complex room desc
//			client.createRoom("room name", (byte) 4, 0);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		try {
			this.graphics = new Graphics("Bomb Blitz", 1200, 600, true, this);
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
			float[] colours = new float[] { 0.1f, 0.3f, 0.5f, 0f, 0.1f, 0.3f, 0.5f, 0f, 0.1f, 0.3f, 0.5f, 0f };
			this.player = new Player(this.playerName, new Point(64,64), 5, 300, new Mesh(32, 32, colours));
			this.keyState = this.player.getKeyState();
			
			ArrayList<Player> list = new ArrayList<Player>();
			list.add(this.player);
			
			this.gameState = new GameState(map, list);
			this.physics = new PhysicsEngine(gameState);
			Player ai = new GameAI("   dasda", new Point(128,128),5, 300, gameState, new Mesh(32, 32, colours));
			list.add(ai);
			ai.begin();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void update(float interval) {
		
		this.physics.update((int) (interval * 1000));
		System.out.println(this.gameState);
	}

	@Override
	public void render(Screen screen) {
		
		this.renderer.render(screen, this.gameState);
	}

	@Override
	public void input(Screen screen) {
		
		this.bombPressed = this.input.update(screen, this.player, this.controlScheme, this.bombPressed);
		this.keyState.setBomb(false);
		this.keyState.setMovement(Movement.NONE);
	}

	@Override
	public void dispose() {
		
		renderer.dispose();	
	}
}
