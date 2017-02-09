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
import bomber.game.Map;
import bomber.game.Movement;
import bomber.game.Player;
import bomber.game.Response;
import bomber.physics.PhysicsEngine;
import bomber.renderer.Screen;

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
		
		HashMap<Response, Integer> keymap = new HashMap<Response, Integer>();
		keymap.put(Response.PLACE_BOMB, GLFW_KEY_SPACE);
		keymap.put(Response.UP_MOVE, GLFW_KEY_UP);
		keymap.put(Response.DOWN_MOVE, GLFW_KEY_DOWN);
		keymap.put(Response.LEFT_MOVE, GLFW_KEY_LEFT);
		keymap.put(Response.RIGHT_MOVE, GLFW_KEY_RIGHT);
		
		Game game =  new Game(map, "Player1", keymap);
	}
}
