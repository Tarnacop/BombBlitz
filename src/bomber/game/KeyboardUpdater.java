package bomber.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Optional;

public class KeyboardUpdater implements KeyListener{

	private HashMap<Integer, Response> controls;
	private Player player;
	
	public KeyboardUpdater(HashMap<Integer, Response> controls, Player player){
		
		this.player = player;
		this.controls = controls;
	}
	
	private Optional<Response> getResponse(KeyEvent e){
		if(this.controls.containsKey(e.getKeyCode())){
			return Optional.of(this.controls.get(e.getKeyCode()));
		}
		return Optional.empty();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		
		System.out.println("Got keypressed event");
		Optional<Response> request = getResponse(e);
		
		if(request.isPresent()){
			
			Response response = request.get();
			KeyboardState keyState = this.player.getKeyState();
			
			if(response == Response.PLACE_BOMB){
				
				keyState.setBomb(true);
			}
			else{
				
				Movement move = Movement.NONE;
			
				switch(response){
				case LEFT_MOVE: move = Movement.LEFT; break;
				case RIGHT_MOVE: move = Movement.RIGHT; break;
				case UP_MOVE: move = Movement.UP; break;
				case DOWN_MOVE: move = Movement.DOWN; break; 
				}
			
				keyState.setKey(move); 
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		Optional<Response> request = getResponse(e);
		
		if(request.isPresent() && (request.get() != Response.PLACE_BOMB)){
			
			KeyboardState keyState = this.player.getKeyState();
			
			keyState.setKey(Movement.NONE);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

}
