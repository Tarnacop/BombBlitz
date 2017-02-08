package bomber.renderer.tests;

import bomber.renderer.Game;
import bomber.renderer.interfaces.GameLogicInterface;

public class MainTest {

	public static void main(String[] args) {
		
		try {
			
            boolean vSync = true;
            GameLogicInterface gameLogic = new TestGame();
            Game gameEng = new Game("GAME",
                640, 640, vSync, gameLogic);
            gameEng.start();
        } catch (Exception excp) {
        	
            excp.printStackTrace();
            System.exit(-1);
        }

	}

} // END OF MainTest CLASS
