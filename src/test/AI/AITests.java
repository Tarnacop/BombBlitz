package test.AI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bomber.AI.AIActions;
import bomber.AI.AIDifficulty;
import bomber.AI.GameAI;
import bomber.AI.RouteFinder;
import bomber.AI.SafetyChecker;
import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.Constants;
import bomber.game.GameState;
import bomber.game.Map;
import bomber.game.Player;

// TODO: Auto-generated Javadoc
/**
 * The Class AITests.
 */
public class AITests {
	
	/** The map. */
	private Map map;
	
	/** The bombs. */
	private List<Bomb> bombs;
	
	/** The players. */
	private List<Player> players;
	
	/** The state. */
	private GameState state;	
	
	/** The ai. */
	private GameAI ai;
	
	/** The finder. */
	private RouteFinder finder;
	
	/** The checker. */
	private SafetyChecker checker;

	
	
	/**
	 * The up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		
//		Map
		/*
			[b][-][-][-][-][ ][ ][ ][ ][ ][ ][x][ ]
			[-][x][-][x][-][x][ ][x][ ][x][ ][x][x]
			[-][-][b][-][-][-][-][ ][ ][ ][ ][ ][ ]
			[-][x][-][x][-][x][ ][x][ ][x][ ][x][ ]
			[-][-][-][-][b][-][-][-][-][ ][ ][ ][ ]
			[ ][x][-][x][-][x][ ][x][ ][x][ ][x][ ]
			[ ][ ][-][ ][-][ ][ ][ ][ ][ ][ ][ ][ ]
			[ ][x][ ][x][-][x][ ][x][ ][x][ ][x][ ]
			[ ][ ][ ][ ][-][ ][ ][ ][ ][ ][ ][ ][ ]
			[ ][x][ ][x][ ][x][ ][x][ ][x][ ][x][ ]
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
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOFT},
				  {Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOFT, Block.BLANK, Block.BLANK}};	
	
		map = new Map("",blocks,null);

		bombs = new ArrayList<>();
		bombs.add(new Bomb("", new Point(0, 0), 5, 5));
		bombs.add(new Bomb("", new Point(2*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 2*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER), 5, 5));
		bombs.add(new Bomb("", new Point(4*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 4*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER), 5, 5));

		players = new ArrayList<>();
		state = new GameState(map, players);
		state.setBombs(bombs);

		ai = new GameAI("ai", new Point(4*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,4*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER), 3, 10, state, AIDifficulty.EXTREME);
		players.add(ai);
		checker = new SafetyChecker(state, ai);
		finder = new RouteFinder(state, ai,checker);
		
	}
	
	
	/**
	 * A route finder test using A*
	 */
	@Test
	public void AStarRouteFinderTest() {

		int xStart = 0;
		int yStart = 0;
		int xEnd = 10;
		int yEnd = 10;
		List<AIActions> moves = finder.findRoute(new Point(xStart, yStart), new Point(xEnd, yEnd));
		assertEquals(moves.size(), (Math.abs(xStart - xEnd) + Math.abs(yStart - yEnd)));

		xStart = 2;
		yStart = 2;
		xEnd = 10;
		yEnd = 10;
		moves = finder.findRoute(new Point(xStart, yStart), new Point(xEnd, yEnd));
		assertEquals(moves.size(), (Math.abs(xStart - xEnd) + Math.abs(yStart - yEnd)));

		xStart = 2;
		yStart = 2;
		xEnd = 2;
		yEnd = 2;
		moves = finder.findRoute(new Point(xStart, yStart), new Point(xEnd, yEnd));
		assertEquals(moves.size(), (Math.abs(xStart - xEnd) + Math.abs(yStart - yEnd)));

		xStart = 12;
		yStart = 0;
		xEnd = 12;
		yEnd = 12;
		moves = finder.findRoute(new Point(xStart, yStart), new Point(xEnd, yEnd));
		assertEquals(moves, null);

	}
	
	
	/**
	 * Enemy check test.
	 */
	@Test
	public void enemyCheckTest() {

		Player player = new Player("name", new Point(2*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 2*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER), 5, 10);
		players.add(player);
		assertFalse(checker.isEnemyInBombRange());

		Player player2 = new Player("name2", new Point(4*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 2*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER), 5, 10);
		players.add(player2);
		assertTrue(checker.isEnemyInBombRange());

		ai.setPos(new Point(8*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 2*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertFalse(checker.isEnemyInBombRange());

		ai.setPos(new Point(6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 2*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertTrue(checker.isEnemyInBombRange());
		
		Player ai2 = new GameAI("ai", new Point(6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER), 3, 10, state, AIDifficulty.EXTREME);	
		ai.setPos(new Point(6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,8*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		players.add(ai2);
		players.add(ai);
	
		assertTrue(checker.isEnemyInBombRange());
		assertFalse(checker.isEnemyInBombRangeExludeAIs());
		
		player.setPos(new Point(6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertTrue(checker.isEnemyInBombRangeExludeAIs());
		
		ai2.setAlive(false);
		
		player.setAlive(false);
		assertFalse(checker.isEnemyInBombRange());
		
		player.setAlive(true);
		player.setPos(new Point(4*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 7*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		ai.setPos(new Point(6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,7*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertFalse(checker.isEnemyInBombRangeExludeAIs());
		
		player.setPos(new Point(5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		ai.setPos(new Point(5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,8*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertFalse(checker.isEnemyInBombRangeExludeAIs());
	
		ai.setPos(new Point(4*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 7*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		player.setPos(new Point(6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,7*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertFalse(checker.isEnemyInBombRangeExludeAIs());
		
		ai.setPos(new Point(5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		player.setPos(new Point(5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,8*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertFalse(checker.isEnemyInBombRangeExludeAIs());

		
	}
	
	/**
	 * Escape test finder.
	 */
	@Test
	public void escapeTestFinder() {
		
		List<AIActions> escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 3);

		ai.setPos(new Point(0, 0));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 5);

		ai.setPos(new Point(2*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 7*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 0);

		ai.setPos(new Point(6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 0);

		ai.setPos(new Point(3*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 2*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 4);

	}
	
	
	/**
	 * In danger check test.
	 */
	@Test
	public void inDangerCheckTest() {
		ai.setPos(new Point(2*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 3*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertTrue(checker.inDanger());

		ai.setPos(new Point(6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 0));
		assertFalse(checker.inDanger());

		ai.setPos(new Point(8*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 4*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertTrue(checker.inDanger());

		ai.setPos(new Point(12*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 12*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		List<Bomb> bombs = new ArrayList<>();
		bombs.add(new Bomb("name", new Point(12*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 9*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER), 5, 4));
		state.setBombs(bombs);

		assertTrue(checker.inDanger());
	}
	
	
	/**
	 * Move safety test.
	 */
	@Test
	public void moveSafetyTest() {
		
		ai.setPos(new Point(0,6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertTrue(checker.checkMoveSafety(AIActions.UP));
		assertTrue(checker.checkMoveSafety(AIActions.RIGHT));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
		
		ai.setPos(new Point(0,5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertFalse(checker.checkMoveSafety(AIActions.UP));
		
		ai.setPos(new Point(6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,3*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertFalse(checker.checkMoveSafety(AIActions.UP));
		assertFalse(checker.checkMoveSafety(AIActions.DOWN));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
		ai.setPos(new Point(3*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertFalse(checker.checkMoveSafety(AIActions.RIGHT));
		assertFalse(checker.checkMoveSafety(AIActions.LEFT));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
		ai.setPos(new Point(5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,8*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertTrue(checker.checkMoveSafety(AIActions.RIGHT));
		assertFalse(checker.checkMoveSafety(AIActions.LEFT));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
		ai.setPos(new Point(10*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,10*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertTrue(checker.checkMoveSafety(AIActions.RIGHT));
		assertTrue(checker.checkMoveSafety(AIActions.LEFT));
		assertTrue(checker.checkMoveSafety(AIActions.UP));
		assertTrue(checker.checkMoveSafety(AIActions.DOWN));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
	}
	
	
	/**
	 * Nearest enemy test.
	 */
	@Test
	public void nereastEnemyTest() {
		ai.setPos(new Point(0,0));
		Player player1 = new Player("nr1",new Point(0,12*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER),3,0);
		players.add(player1);
		Player player2 = new Player("nr1",new Point(12*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,0),3,0);
		players.add(player2);
		Player player3 = new Player("nr1",new Point(5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER),3,0);
		players.add(player3);
		state.setPlayers(players);
		
		assertEquals(new Point(5,6),finder.getNearestEnemy());
		
		player3.setPos(new Point(6*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,7*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		assertNotEquals(new Point(6,7),finder.getNearestEnemy());
		
		player3.setPos(new Point(4*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,3*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		Player ai2 = new GameAI("name", new Point(0*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 4*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER),1, 1, state, AIDifficulty.EXTREME);
		players.add(ai2);
		assertEquals(new Point(0,4),finder.getNearestEnemy());
		assertNotEquals(new Point(0,4),finder.getNearestEnemyExcludeAIs());
		assertEquals(new Point(4,3),finder.getNearestEnemyExcludeAIs());

		ai2.setGridPos(new Point(0,0));
	}
	
	
	/**
	 * Can put bomb and escape test.
	 */
	@Test
	public void canPutBombAndEscapeTest()
	{
		
		assertNull(finder.canPutBombAndEscape());
		assertNull(finder.canPutBombAndEscapeExcludeAIs());
		
		ai.setGridPos(new Point(0,4*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		Player ai3 = new GameAI("name", new Point(0*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 4*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER),1, 1, state, AIDifficulty.EXTREME);
		players.add(ai3);
		assertNull(finder.canPutBombAndEscape());
		assertNull(finder.canPutBombAndEscapeExcludeAIs());
		
		ai3.setPos(new Point(0,8*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		ai.setPos(new Point(0,7*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
	
		assertNotNull(finder.canPutBombAndEscape());
		assertNull(finder.canPutBombAndEscapeExcludeAIs());
		
		Player player = new Player("nr1",new Point(0*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,9*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER),3,0);
		players.add(player);
		
		assertNotNull(finder.canPutBombAndEscapeExcludeAIs());
		
	}

	
	/**
	 * Checks if is enclosure test.
	 */
	@Test
	public void isEnclosureTest()
	{
		assertFalse(finder.isEnclosure(new ArrayList<>(), new Point(0,0)));
		assertTrue(finder.isEnclosure(new ArrayList<>(), new Point(12*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,12*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER)));
		
	}
}
