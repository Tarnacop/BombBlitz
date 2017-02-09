package bomber.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import bomber.AI.GameAI;
import bomber.physics.PhysicsEngine;
import bomber.renderer.Graphics;
import bomber.renderer.Screen;
import bomber.renderer.shaders.Mesh;


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
		
		Player p1 = new Player(this.playerName, new Point(64,64), 5, 300, null);
		this.keyState = p1.getKeyState();
		
		ArrayList<Player> list = new ArrayList<Player>();
		list.add(p1);
		
		this.gameState = new GameState(map, list);
		this.physics = new PhysicsEngine(gameState);
		Player ai = new GameAI("   dasda", new Point(128,128),5, 300, gameState,null);
		list.add(ai);
		try {
			
			this.graphics = new Graphics("Bomb Blitz", 600, 600, this, this.gameState, controlScheme, p1);
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
