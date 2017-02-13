package bomber.game;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.util.HashMap;
import java.util.Optional;

import bomber.renderer.Screen;

public class KeyboardInput {

	public KeyboardInput(){
		
	}
	
	public boolean update(Screen screen, KeyboardState keyState, HashMap<Response, Integer> controlScheme, boolean bombPressed){
		
		//System.out.println("polling the keyboard with keyState" + keyState.toString());
		
		
		int state = GLFW_RELEASE;
			
			//System.out.println("Checking again...");
			
			//check for bomb
			if(getKey(Response.PLACE_BOMB, controlScheme).isPresent()){
				state = screen.getKeyState(getKey(Response.PLACE_BOMB, controlScheme).get());
			}
			if(state == GLFW_PRESS && !bombPressed){
			    keyState.setBomb(true);
			    bombPressed = true;
			    state = GLFW_RELEASE;
			}else if(state == GLFW_RELEASE && bombPressed){
				bombPressed = false;
			}
			
			//Check for up
			
			if(getKey(Response.UP_MOVE, controlScheme).isPresent()){
				state = screen.getKeyState(getKey(Response.UP_MOVE, controlScheme).get());
			}
			if(state == GLFW_PRESS){
			    keyState.setMovement(Movement.UP);
			    state = GLFW_RELEASE;
			}
			
			//check for down
			if(getKey(Response.DOWN_MOVE, controlScheme).isPresent()){
				state = screen.getKeyState(getKey(Response.DOWN_MOVE, controlScheme).get());
			}
			if(state == GLFW_PRESS){
			    keyState.setMovement(Movement.DOWN);
			    state = GLFW_RELEASE;
			}
			
			//check for left
			if(getKey(Response.LEFT_MOVE, controlScheme).isPresent()){
				state = screen.getKeyState(getKey(Response.LEFT_MOVE, controlScheme).get());
			}
			if(state == GLFW_PRESS){
			    keyState.setMovement(Movement.LEFT);
			    state = GLFW_RELEASE;
			}
			
			//check for right
			if(getKey(Response.RIGHT_MOVE, controlScheme).isPresent()){
				state = screen.getKeyState(getKey(Response.RIGHT_MOVE, controlScheme).get());
			}
			if(state == GLFW_PRESS){
				//System.out.println("SETTING MOVEMENT ON " + keyState.toString() + " TO RIGHT");
			    keyState.setMovement(Movement.RIGHT);
			    state = GLFW_RELEASE;
			}
			
			return bombPressed;
	}
	
	private Optional<Integer> getKey(Response r, HashMap<Response, Integer> controlScheme){
		if(controlScheme.containsKey(r)){
			return Optional.of(controlScheme.get(r));
		}
		return Optional.empty();
	}

}
