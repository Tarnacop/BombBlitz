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
		
		Optional<Response> request = getResponse(e);
		
		if(request.isPresent()){
			
			Response response = request.get();
			KeyboardState keyState = this.player.getKeyState();
			
			Movement move;
			
			switch(response){
			case LEFT_MOVE: move = Movement.LEFT;break;
			case RIGHT_MOVE: move = Movement.RIGHT; break;
			case UP_MOVE: move = Movement.UP; break;
			case DOWN_MOVE: move = Movement.DOWN; break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

}
