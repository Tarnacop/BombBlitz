package test.game;

import static bomber.game.Response.DOWN_MOVE;
import static bomber.game.Response.LEFT_MOVE;
import static bomber.game.Response.PAUSE_GAME;
import static bomber.game.Response.PLACE_BOMB;
import static bomber.game.Response.RIGHT_MOVE;
import static bomber.game.Response.UP_MOVE;
import static org.junit.Assert.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.game.KeyboardInput;
import bomber.game.KeyboardState;
import bomber.game.Response;
import bomber.renderer.Screen;

public class KeyboardUpdaterTest {

	private KeyboardInput updater;
	private Screen screen;

	@Before
	public void setUp() throws Exception {
		updater = new KeyboardInput();
		screen = new Screen("Test", 0, 0, false, false);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
	
//		HashMap<Response, Integer> controls = new HashMap<Response, Integer>();
//		
//		KeyboardState keyState = new KeyboardState();
//		
//		updater.pauseCheck(screen, keyState, controls, false);
//		
//		controls.put(PLACE_BOMB, GLFW_KEY_SPACE);
//		controls.put(UP_MOVE, GLFW_KEY_UP);
//		controls.put(DOWN_MOVE, GLFW_KEY_DOWN);
//		controls.put(LEFT_MOVE, GLFW_KEY_LEFT);
//		controls.put(RIGHT_MOVE, GLFW_KEY_RIGHT);	
//		controls.put(PAUSE_GAME, GLFW_KEY_P);
//		
//		updater.pauseCheck(screen, keyState, controls, false);
	}

}
