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
		
		GameState g = new GameState(map, list, bombs);
		
		
		HashMap<Integer, Response> keymap = new HashMap<Integer, Response>();
		keymap.put(KeyEvent.VK_RIGHT, Response.RIGHT_MOVE);
		keymap.put(KeyEvent.VK_LEFT, Response.LEFT_MOVE);
		keymap.put(KeyEvent.VK_UP, Response.UP_MOVE);
		keymap.put(KeyEvent.VK_DOWN, Response.DOWN_MOVE);
		keymap.put(KeyEvent.VK_SPACE, Response.PLACE_BOMB);
		
		KeyboardUpdater updater = new KeyboardUpdater(keymap, p1);
		
		UserInterface ui = new UserInterface();
        ui.addKeyListener(updater);
        ui.setVisible(true);
        ui.setFocusable(true);
        
		
		}
	}


