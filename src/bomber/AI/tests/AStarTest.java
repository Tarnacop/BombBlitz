package bomber.AI.tests;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.AI.RouteFinder;
import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.Map;
import bomber.game.Movement;
import bomber.game.Player;

public class AStarTest {

	private Map map;
	private List<Bomb> bombs;
	private List<Player> players;
	private GameState state;	
	@Before
	public void setUp() throws Exception {
		
		Block[][] blocks = {{Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK}};	

		map = new Map(blocks);
	
		bombs = new ArrayList<>();
		bombs.add(new Bomb("",new Point(0,0),5,5));
		bombs.add(new Bomb("",new Point(2,2),5,5));
		bombs.add(new Bomb("",new Point(4,4),5,5));
		
		players = new ArrayList<>();
		
		state = new GameState(map, players, bombs);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		RouteFinder finder = new RouteFinder(state);
		List<Movement> moves = finder.findRoute(new Point(0,0), new Point(12,12));
//		for(Movement m : moves)
//		{
//			System.out.print(m + " ");
//		}
	}

}
