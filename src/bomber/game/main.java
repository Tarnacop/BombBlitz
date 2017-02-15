package bomber.game;

import java.util.HashMap;

import bomber.UI.UserInterface;

public class main {
	
	public static void main(String[] args) {
		
		Maps maps = new Maps();
		UserInterface ui = new UserInterface("Bomb Blitz v1");
		Game game = new Game(maps.getMaps().get(1), "Player 1", new HashMap<Response, Integer>());
	}
}
