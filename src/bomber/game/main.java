package bomber.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import bomber.AI.GameAI;
import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.KeyboardState;
import bomber.game.KeyboardUpdater;
import bomber.game.Map;
import bomber.game.Movement;
import bomber.game.Player;
import bomber.game.Response;
import bomber.physics.PhysicsEngine;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class main {

	private static GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
	
	public static void main(String[] args) {
		
		Block[][] gridMap = new Block[][]{{Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID},
										{Block.SOLID,Block.BLANK,Block.BLANK,Block.BLANK,Block.SOLID},
										{Block.SOLID,Block.BLANK,Block.BLANK,Block.BLANK,Block.SOLID},
										{Block.SOLID,Block.BLANK,Block.BLANK,Block.BLANK,Block.SOLID},
										{Block.SOLID,Block.SOFT,Block.SOFT,Block.SOFT,Block.SOLID},
										
										{Block.SOLID,Block.SOLID,Block.SOFT,Block.SOLID,Block.SOLID},
										{Block.SOLID,Block.SOLID,Block.SOFT,Block.SOLID,Block.SOLID},
										{Block.SOLID,Block.SOLID,Block.BLANK,Block.SOLID,Block.SOLID},
										{Block.SOLID,Block.SOLID,Block.BLANK,Block.SOLID,Block.SOLID},
										{Block.SOLID,Block.SOLID,Block.BLANK,Block.SOLID,Block.SOLID},
										
										{Block.SOLID,Block.SOFT,Block.SOFT,Block.SOFT,Block.SOLID},
										{Block.SOLID,Block.BLANK,Block.BLANK,Block.SOFT,Block.SOLID},
										{Block.SOLID,Block.BLANK,Block.BLANK,Block.SOFT,Block.SOLID},
										{Block.SOLID,Block.SOFT,Block.BLANK,Block.SOFT,Block.SOLID},
										{Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID}};
		
		//		HHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
		//		HH      OOHHHHHHHHHHOO    OOHH
		//		HH      OOOOOO      OO	    HH
		//		HH      OOHHHHHHHHHHOOOOOO  HH
		//		HHHHHHHHHHHHHHHHHHHHHHHHHHHHHH

		Map map = new Map(gridMap);
		
		Player p1 = new Player("Player1", new Point(64,64), 5, 3);
		
		ArrayList<Player> list = new ArrayList<Player>();
		list.add(p1);
		
		ArrayList<Bomb> bombs = new ArrayList<Bomb>();
		
		GameState gameState = new GameState(map, list, bombs);

		Player ai = new GameAI("Player2", new Point(128, 128), 5, 3, gameState);
		
		gameState.getPlayers().add(ai);
		
		HashMap<Response, Integer> keymap = new HashMap<Response, Integer>();
		keymap.put(Response.PLACE_BOMB, GLFW_KEY_SPACE);
		keymap.put(Response.UP_MOVE, GLFW_KEY_UP);
		keymap.put(Response.DOWN_MOVE, GLFW_KEY_DOWN);
		keymap.put(Response.LEFT_MOVE, GLFW_KEY_LEFT);
		keymap.put(Response.RIGHT_MOVE, GLFW_KEY_RIGHT);
		
		glfwSetErrorCallback(errorCallback);
		glfwInit();
		if (!glfwInit()) {
		    throw new IllegalStateException("Unable to initialize GLFW");
		}
		
		long window = glfwCreateWindow(640, 480, "Bomb Blitz", NULL, NULL);
		if (window == NULL) {
		    glfwTerminate();
		    throw new RuntimeException("Failed to create the GLFW window");
		}
		
		glfwSetKeyCallback(window, keyCallback);
		
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		
		KeyboardState keyState = p1.getKeyState();
		
		KeyboardUpdater updater = new KeyboardUpdater(window, keymap, p1);
		
		PhysicsEngine physics = new PhysicsEngine(gameState);
		
		updater.start();
		ai.begin();
		
		while (!glfwWindowShouldClose(window)) {
		   
			glfwSwapBuffers(window);
			glfwPollEvents();

			physics.update();
			
			
			
			System.out.println(gameState);
			keyState.setBomb(false);
			keyState.setMovement(Movement.NONE);
			
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		updater.die();
		
		glfwDestroyWindow(window);
		keyCallback.free();
		
		glfwTerminate();
		errorCallback.free();
	}
	
	private static GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
	    @Override
	    public void invoke(long window, int key, int scancode, int action, int mods) {
	        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
	            glfwSetWindowShouldClose(window, true);
	        }
	    }
	};
}

//		keymap.put(Response.RIGHT_MOVE, Keyboard.KEY_RIGHT);
//		keymap.put(Response.LEFT_MOVE, Keyboard.KEY_LEFT);
//		keymap.put(Response.UP_MOVE, Keyboard.KEY_UP);
//		keymap.put(Response.DOWN_MOVE, Keyboard.KEY_DOWN);
//		keymap.put(Response.PLACE_BOMB, Keyboard.KEY_SPACE);
//		
//		KeyboardUpdater updater = new KeyboardUpdater(keymap, p1);
//		
//		//Create UI with lwjgl
//		try {
//			Display.setDisplayMode(new DisplayMode(700, 700));
//			Display.setTitle("Bomb Blitz");
//			Display.create();
//		} catch (LWJGLException e) {
//			e.printStackTrace();
//			Display.destroy();
//			System.exit(1);
//		}
//        
//        //new Physics(gameState...)
//        
//		updater.start();
//		
//		KeyboardState keyState = p1.getKeyState();
//		
//		while (!Display.isCloseRequested()) {
//			
//			//if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
//			//	System.out.println("RIGHT FROM main");
//			//}
//			
//			//Physics.update()
//        	//Renderer.update()
//			
//			if(keyState.isBomb()){
//				System.out.println("X");
//			}
//			if(keyState.getMovement() == Movement.UP){
//				System.out.println("^");
//				System.out.println("|");
//			}
//			if(keyState.getMovement() == Movement.DOWN){
//				System.out.println("|");
//				System.out.println("V");
//			}
//			if(keyState.getMovement() == Movement.LEFT){
//				System.out.println("<-");
//			}
//			if(keyState.getMovement() == Movement.RIGHT){
//				System.out.println("->");
//			}
//			
//			keyState.setBomb(false);
//			keyState.setMovement(Movement.NONE);
//			
//            Display.update();
//            Display.sync(60);
//        }
// 
//		updater.die();
//		try {
//			updater.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        Display.destroy();
//        System.exit(0);
//        
//		}
//	}


