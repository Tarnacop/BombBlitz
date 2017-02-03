package bomber.game;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import bomber.UI.UserInterface;

public class main {

	public static void main(String[] args) {
		
		Block[][] gridMap = new Block[][]{{Block.SOLID, Block.BLANK},{Block.SOFT, Block.BLAST}};
		//	SOLID	SOFT
		//	BLANK	BLAST
		
		Map map = new Map(gridMap);
		
		Player p1 = new Player("Player1", new Point(0,0), 0, 0);
		ArrayList<Player> list = new ArrayList<Player>();
		list.add(p1);
		
		ArrayList<Bomb> bombs = new ArrayList<Bomb>();
		bombs.add(new Bomb("Player1", new Point(0,0), 0, 0));
		
		GameState g = new GameState(map, list, bombs);
		
		
		HashMap<Integer, Response> keymap = new HashMap<Integer, Response>();
		keymap.put(KeyEvent.VK_RIGHT, Response.RIGHT_MOVE);
		
		KeyboardUpdater updater = new KeyboardUpdater(keymap, p1);
		
		EventQueue.invokeLater(() -> {
            UserInterface ui = new UserInterface();
            ui.setVisible(true);
        });
		
		boolean noMove = true;
		
		while(noMove){
			
			Movement move = p1.getKeyState().getKey();
			if(move == Movement.RIGHT){
				noMove = false;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Moved right");
	}

}
