package bomber.AI.tests;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import bomber.AI.GameAI;
import bomber.AI.RouteFinder;
import bomber.AI.SafetyChecker;
import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.Map;
import bomber.game.Player;

public class PlanningTest {

	private Map map;
	protected GameState state;	
	protected GameAI ai;
	protected RouteFinder finder;
	protected SafetyChecker checker;
	@Before
	public void setUp() throws Exception {
		
//		Map
		/*
			[b][-][-][-][-][-][ ][ ][ ][ ][s][ ][ ]
			[-][x][-][x][-][x][ ][x][ ][x][s][x][ ]
			[-][-][b][-][-][-][-][-][ ][ ][s][ ][ ]
			[-][x][-][x][-][x][ ][x][ ][x][s][x][S]
			[-][-][-][-][b][-][-][-][-][-][s][ ][ ]
			[-][x][-][x][-][x][ ][x][ ][x][s][x][ ]
			[ ][ ][-][ ][-][ ][ ][ ][ ][ ][s][ ][ ]
			[ ][x][-][x][-][x][ ][x][ ][x][s][x][ ]
			[ ][ ][ ][ ][-][ ][ ][ ][ ][ ][s][ ][ ]
			[ ][x][ ][x][-][x][ ][x][ ][x][s][x][ ]
			[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][s][ ][ ]
			[ ][x][ ][x][ ][x][ ][x][ ][x][s][x][S]
			[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][s][ ][ ]*/
	
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
				  {Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT,Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.SOFT, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOFT, Block.BLANK}};	
		
		map = new Map(blocks);

		state = new GameState(map, null, null);

		ai = new GameAI("ai", new Point(12,0), 3, 10, state);
		ai.setBombRange(3);
		checker = new SafetyChecker(state, ai);
		finder = new RouteFinder(state, ai,checker);
		
	}

	@Test
	public void test() {
		System.out.println(finder.getPlanToEnemy(new Point(12,0), new Point(12,12)));
	}

}
