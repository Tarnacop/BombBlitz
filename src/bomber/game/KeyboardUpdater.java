package bomber.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Optional;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class KeyboardUpdater extends Thread{

	private long window;
	private HashMap<Response, Integer> controls;
	private Player player;
	private boolean stop;
	
	public KeyboardUpdater(long window, HashMap<Response, Integer> controls, Player player){
		
		super("Keyboard Updater");
		this.window = window;
		this.stop = false;
		this.player = player;
		this.controls = controls;
		
		glfwSetInputMode(this.window, GLFW_STICKY_KEYS, GLFW_TRUE);
	}
	
	private Optional<Integer> getKey(Response r){
		if(this.controls.containsKey(r)){
			return Optional.of(this.controls.get(r));
		}
		return Optional.empty();
	}

	@Override
	public void run() {
		
		System.out.println("Started polling the keyboard");
		
		KeyboardState keyState = this.player.getKeyState();
		
		int state = GLFW_RELEASE;
		
		
		while(!stop){
			
			//System.out.println("Checking again...");
			
			//check for bomb
			if(getKey(Response.PLACE_BOMB).isPresent()){
				state = glfwGetKey(this.window, getKey(Response.PLACE_BOMB).get());
			}
			if(state == GLFW_PRESS){
			    keyState.setBomb(true);
			    state = GLFW_RELEASE;
			}
			
			//Check for up
			
			if(getKey(Response.UP_MOVE).isPresent()){
				state = glfwGetKey(this.window, getKey(Response.UP_MOVE).get());
			}
			if(state == GLFW_PRESS){
			    keyState.setMovement(Movement.UP);
			    state = GLFW_RELEASE;
			}
			
			//check for down
			if(getKey(Response.DOWN_MOVE).isPresent()){
				state = glfwGetKey(this.window, getKey(Response.DOWN_MOVE).get());
			}
			if(state == GLFW_PRESS){
			    keyState.setMovement(Movement.DOWN);
			    state = GLFW_RELEASE;
			}
			
			//check for left
			if(getKey(Response.LEFT_MOVE).isPresent()){
				state = glfwGetKey(this.window, getKey(Response.LEFT_MOVE).get());
			}
			if(state == GLFW_PRESS){
			    keyState.setMovement(Movement.LEFT);
			    state = GLFW_RELEASE;
			}
			
			//check for right
			if(getKey(Response.RIGHT_MOVE).isPresent()){
				state = glfwGetKey(this.window, getKey(Response.RIGHT_MOVE).get());
			}
			if(state == GLFW_PRESS){
			    keyState.setMovement(Movement.RIGHT);
			    state = GLFW_RELEASE;
			}
		
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
	}
	
	public void die(){
		
		this.stop = true;
	}
	
//	@Override
//	public void keyPressed(KeyEvent e) {
//		
//		
//		System.out.println("Got keypressed event");
//		Optional<Response> request = getResponse(e);
//		
//		if(request.isPresent()){
//			
//			Response response = request.get();
//			KeyboardState keyState = this.player.getKeyState();
//			
//			if(response == Response.PLACE_BOMB){
//				
//				keyState.setBomb(true);
//			}
//			else{
//				
//				Movement move = Movement.NONE;
//			
//				switch(response){
//				case LEFT_MOVE: move = Movement.LEFT; break;
//				case RIGHT_MOVE: move = Movement.RIGHT; break;
//				case UP_MOVE: move = Movement.UP; break;
//				case DOWN_MOVE: move = Movement.DOWN; break; 
//				}
//			
//				keyState.setKey(move); 
//			}
//		}
//	}
//
//	@Override
//	public void keyReleased(KeyEvent e) {
//		
//		Optional<Response> request = getResponse(e);
//		
//		if(request.isPresent() && (request.get() != Response.PLACE_BOMB)){
//			
//			KeyboardState keyState = this.player.getKeyState();
//			
//			keyState.setKey(Movement.NONE);
//		}
//	}
//
//	@Override
//	public void keyTyped(KeyEvent e) {
//		
//	}

}
