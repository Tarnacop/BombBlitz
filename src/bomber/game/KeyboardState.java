package bomber.game;

public class KeyboardState {

	private boolean bomb;			//true if the bomb key is pressed
	private boolean pause;
	private Movement movement;			//the current movement key pressed, default NONE
	
	public KeyboardState(){
		
		this.movement = Movement.NONE;
		this.bomb = false;
		this.pause = false;
	}
		
	public boolean isBomb() {
		return bomb;
	}

	public void setBomb(boolean bomb) {
		this.bomb = bomb;
	}

	public void setMovement(Movement movement) {
		this.movement = movement;
	}

	public Movement getMovement(){
		
		return this.movement;
	}
	
	public boolean isPaused(){
		return this.pause;
	}
	
	public void setPaused(boolean paused){
		this.pause = paused;
	}
}
