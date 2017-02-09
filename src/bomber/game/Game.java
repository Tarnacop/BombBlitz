package bomber.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import bomber.AI.GameAI;
import bomber.physics.PhysicsEngine;
import bomber.renderer.Screen;


public class Game{
	
	private String playerName;
	private Map map;
	private HashMap<Response, Integer> controlScheme;
	private Screen screen;
	private PhysicsEngine physics;
	private GameState gameState;
	private KeyboardState keyState;
	private KeyboardUpdater updater;

	public Game(Map map, String playerName, HashMap<Response, Integer> controls){
		
		this.map = map;
		this.playerName = playerName;
		this.controlScheme = controls;
		
		init();
	}
	
	private void init(){
		
		Player p1 = new Player(this.playerName, new Point(64,64), 5, 3);
		
		ArrayList<Player> list = new ArrayList<Player>();
		list.add(p1);
		
		this.gameState = new GameState(map, list);

		//Player ai = new GameAI("Player2", new Point(128, 128), 5, 3, gameState);
		
		//gameState.getPlayers().add(ai);
		
		this.screen  = new Screen(600, 600, "Bomb Blitz", false);
		this.screen.init();
		
		this.keyState = p1.getKeyState();
		
		this.updater = new 
				KeyboardUpdater(screen.getScreenID(), this.controlScheme, p1);
		
		this.physics = new PhysicsEngine(gameState);
		
		this.updater.start();
		//ai.begin();
	}
	
	public void update(float interval){
		
		if(gameState.gameOver()){
			
			this.updater.die();
			this.screen.close();
		}
		else{
			this.screen.update();
		this.physics.update();
		
		System.out.println(this.gameState);
		this.keyState.setBomb(false);
		this.keyState.setMovement(Movement.NONE);
		
		//UI.update();
		//physics.update();
		//audio.playEventList(gameState.getAudioEvents);
//		this.screen.update();
		
		}
		
	}
}
