package bomber.game;

public class KeyboardState {

	private boolean bomb;			//true if the bomb key is pressed
	private Movement key;			//the current movement key pressed, default NONE
	
	public KeyboardState(){
		
		this.key = Movement.NONE;
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

	public Movement getKey() {
		return key;
	}

	public void setKey(Movement key) {
		this.key = key;
	}

	public Movement getMovement(){
		
		return this.key;
	}
}
