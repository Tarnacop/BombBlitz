package bomber.game;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import bomber.UI.UserInterface;

public class main {

	public static void main(String[] args) {
		
		Block[][] gridMap = new Block[][]{{Block.SOLID, Block.BLANK},{Block.SOLID, Block.BLANK}};
		//	SOLID	SOLID
		//	BLANK	BLANK
		
		Map map = new Map(gridMap);
		
		Player p1 = new Player("Player1", new Point(0,0), 0, 0);
		ArrayList<Player> list = new ArrayList<Player>();
		list.add(p1);
		
		ArrayList<Bomb> bombs = new ArrayList<Bomb>();
		
		GameState gameState = new GameState(map, list, bombs);
		
		HashMap<Response, Integer> keymap = new HashMap<Response, Integer>();
		keymap.put(Response.RIGHT_MOVE, Keyboard.KEY_RIGHT);
		keymap.put(Response.LEFT_MOVE, Keyboard.KEY_LEFT);
		keymap.put(Response.UP_MOVE, Keyboard.KEY_UP);
		keymap.put(Response.DOWN_MOVE, Keyboard.KEY_DOWN);
		keymap.put(Response.PLACE_BOMB, Keyboard.KEY_SPACE);
		
		KeyboardUpdater updater = new KeyboardUpdater(keymap, p1);
		
		//Create UI with lwjgl
		try {
			Display.setDisplayMode(new DisplayMode(700, 700));
			Display.setTitle("Bomb Blitz");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			Display.destroy();
			System.exit(1);
		}
        
        //new Physics(gameState...)
        
		updater.start();
		
		KeyboardState keyState = p1.getKeyState();
		
		while (!Display.isCloseRequested()) {
			
			//if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
			//	System.out.println("RIGHT FROM main");
			//}
			
			//Physics.update()
        	//Renderer.update()
			
			if(keyState.isBomb()){
				System.out.println("X");
			}
			if(keyState.getMovement() == Movement.UP){
				System.out.println("^");
				System.out.println("|");
			}
			if(keyState.getMovement() == Movement.DOWN){
				System.out.println("|");
				System.out.println("V");
			}
			if(keyState.getMovement() == Movement.LEFT){
				System.out.println("<-");
			}
			if(keyState.getMovement() == Movement.RIGHT){
				System.out.println("->");
			}
			
			keyState.setBomb(false);
			keyState.setMovement(Movement.NONE);
			
            Display.update();
            Display.sync(60);
        }
 
		updater.die();
		try {
			updater.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Display.destroy();
        System.exit(0);
        
		}
	}


