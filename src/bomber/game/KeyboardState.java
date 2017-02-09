package bomber.game;

public class KeyboardState {

	private boolean bomb;			//true if the bomb key is pressed

	private Movement movement;			//the current movement key pressed, default NONE
	
	public KeyboardState(){
		
		this.movement = Movement.NONE;
		this.bomb = false;
	}

	public boolean placeBomb(){
		
		return this.bomb;//return true if the player has placed the bomb press key
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
}
