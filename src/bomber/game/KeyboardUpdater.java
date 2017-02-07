package bomber.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Optional;

<<<<<<< HEAD
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
=======
import org.lwjgl.input.Keyboard;

public class KeyboardUpdater extends Thread{

	private HashMap<Response, Integer> controls;
	private Player player;
	private boolean stop;
	
	public KeyboardUpdater(HashMap<Response, Integer> controls, Player player){
		
		super("Keyboard Updater");
		this.stop = false;
		this.player = player;
		this.controls = controls;
		
		
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
		
		while(!stop){
			
			//System.out.println("Checking again...");
			
			//Event for bomb
			while(Keyboard.next()) {
				if (getKey(Response.PLACE_BOMB).isPresent() && (Keyboard.isKeyDown(getKey(Response.PLACE_BOMB).get()))) {
					keyState.setBomb(true);
					//System.out.println("X");
				}
			}
			//Poll for up
			if (getKey(Response.UP_MOVE).isPresent() && (Keyboard.isKeyDown(getKey(Response.UP_MOVE).get()))) {
				keyState.setMovement(Movement.UP);
				//System.out.println("^");
				//System.out.println("|");
			}
			//Poll for down
			if (getKey(Response.DOWN_MOVE).isPresent() && (Keyboard.isKeyDown(getKey(Response.DOWN_MOVE).get()))) {
				keyState.setMovement(Movement.DOWN);
				//System.out.println("|");
				//System.out.println("v");
			}
			//Poll for left
			if (getKey(Response.LEFT_MOVE).isPresent() && (Keyboard.isKeyDown(getKey(Response.LEFT_MOVE).get()))) {
				keyState.setMovement(Movement.LEFT);
				//System.out.println("<-");
			}
			//Poll for right
			if (getKey(Response.RIGHT_MOVE).isPresent() && (Keyboard.isKeyDown(getKey(Response.RIGHT_MOVE).get()))) {
				keyState.setMovement(Movement.RIGHT);
				//System.out.println("->");
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
>>>>>>> dev

}
