package bomber.game;

import java.awt.Point;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bomber.AI.GameAI;
import bomber.networking.ClientServerLobbyRoom;
import bomber.networking.ClientServerPlayer;
import bomber.networking.ClientThread;
import bomber.physics.PhysicsEngine;
import bomber.renderer.Graphics;
import bomber.renderer.Screen;


public class Game{
	
	private String playerName;
	private Map map;
	private HashMap<Response, Integer> controlScheme;
	private Screen screen;
	private PhysicsEngine physics;
	private GameState gameState;
	private KeyboardState keyState;
	private Graphics graphics;
	private boolean bombPressed;

	public Game(Map map, String playerName, HashMap<Response, Integer> controls){
		
		this.map = map;
		this.playerName = playerName;
		this.controlScheme = controls;
		this.bombPressed = false;
		
		init();
	}
	
	private void init(){
		
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
		
		Player p1 = new Player(this.playerName, new Point(64,64), 5, 300, null);
		this.keyState = p1.getKeyState();
		
		ArrayList<Player> list = new ArrayList<Player>();
		list.add(p1);
		
		this.gameState = new GameState(map, list);
		this.physics = new PhysicsEngine(gameState);
		Player ai = new GameAI("   dasda", new Point(128,128),5, 300, gameState, null);
		list.add(ai);
		try {
			
			this.graphics = new Graphics("Bomb Blitz", 1200, 600, this, this.gameState, controlScheme, p1);
			this.graphics.start();
			ai.begin();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Player ai = new GameAI("Player2", new Point(128, 128), 5, 3, gameState);
		
		//gameState.getPlayers().add(ai);
		
		
		//ai.begin();
		
//		while(true){
//			
//			this.update(0);
//			
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
	
		
	}
	
	public void update(Screen screen, float interval){
		
		this.bombPressed = screen.input(this.bombPressed);	
		this.physics.update((int) (interval * 1000));
		
		System.out.println(this.gameState);
		this.keyState.setBomb(false);
		this.keyState.setMovement(Movement.NONE);
		
		//UI.update();
		//audio.playEventList(gameState.getAudioEvents);
//		this.screen.update();
		
		//}
		
	}
}
