package bomber.AI.tests;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bomber.AI.GameAI;
import bomber.AI.RouteFinder;
import bomber.AI.SafetyChecker;
import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.Map;
import bomber.game.Player;

public abstract class MainTestTemplate {
	private Map map;
	private List<Bomb> bombs;
	protected List<Player> players;
	protected GameState state;	
	protected GameAI ai;
	protected RouteFinder finder;
	protected SafetyChecker checker;
	@Before
	public void setUp() throws Exception {
		
//		Map
		/*
			[b][-][-][-][-][-][ ][ ][ ][ ][ ][x][ ]
			[-][x][-][x][-][x][ ][x][ ][x][ ][x][x]
			[-][-][b][-][-][-][-][-][ ][ ][ ][ ][ ]
			[-][x][-][x][-][x][ ][x][ ][x][ ][x][ ]
			[-][-][-][-][b][-][-][-][-][-][ ][ ][ ]
			[-][x][-][x][-][x][ ][x][ ][x][ ][x][ ]
			[ ][ ][-][ ][-][ ][ ][ ][ ][ ][ ][ ][ ]
			[ ][x][-][x][-][x][ ][x][ ][x][ ][x][ ]
			[ ][ ][ ][ ][-][ ][ ][ ][ ][ ][ ][ ][ ]
			[ ][x][ ][x][-][x][ ][x][ ][x][ ][x][ ]
			[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
			[ ][x][ ][x][ ][x][ ][x][ ][x][ ][x][ ]
			[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]*/
	
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
				  {Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK}};	
		
		map = new Map(blocks);

		bombs = new ArrayList<>();
		bombs.add(new Bomb("", new Point(0, 0), 5, 5));
		bombs.add(new Bomb("", new Point(2, 2), 5, 5));
		bombs.add(new Bomb("", new Point(4, 4), 5, 5));

		players = new ArrayList<>();

		state = new GameState(map, players, bombs);

		ai = new GameAI("ai", new Point(4,4), 3, 10, state);
		players.add(ai);
		checker = new SafetyChecker(state, ai);
		finder = new RouteFinder(state, ai,checker);
		
	}
	
	@Test
	public abstract void test() ;

}