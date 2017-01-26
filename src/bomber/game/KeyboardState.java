package bomber.game;

public class KeyboardState {

	private boolean bomb;			//true if the bomb key is pressed
	private Movement key;			//the current movement key pressed
	
	public KeyboardState(){}

	public boolean placeBomb(){
		
		return this.bomb;//return true if the player has placed the bomb press key
	}
		
	public Movement getMovement(){
		
		return this.key;
	}
}
