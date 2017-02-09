package bomber.renderer.tests;

import bomber.renderer.Graphics;
import bomber.renderer.interfaces.GameLogicInterface;

public class MainTest {

	public static void main(String[] args) {
		
		try {
			
            boolean vSync = true;
            GameLogicInterface gameLogic = new TestGame();
            Graphics gameEng = new Graphics("GAME",
                640, 640, vSync, gameLogic);
            gameEng.start();
        } catch (Exception excp) {
        	
            excp.printStackTrace();
            System.exit(-1);
        }

	}

} // END OF MainTest CLASS
