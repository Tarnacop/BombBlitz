package bomber.renderer.tests;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import bomber.game.GameState;
import bomber.game.Player;
import bomber.renderer.Game;
import bomber.renderer.interfaces.GameLogicInterface;

public class MainTest {

	public static void main(String[] args) {
		
		try {
			
			Player playerTest = new Player("Player Test", new Point(0, 0), 3, 3);
			List<Player> players = new ArrayList<Player>();
			players.add(playerTest);
			GameState state = new GameState(null, players, null);
			
            boolean vSync = true;
            GameLogicInterface gameLogic = new TestGame(state);
            Game gameEng = new Game("GAME",
                640, 640, vSync, gameLogic);
            gameEng.start();
        } catch (Exception excp) {
        	
            excp.printStackTrace();
            System.exit(-1);
        }

	}

} // END OF MainTest CLASS
