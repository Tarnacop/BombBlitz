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
import bomber.game.GameState;
import bomber.game.Map;
import bomber.game.Player;

public class AITests {
	private Map map;
	private List<Bomb> bombs;
	private List<Player> players;
	private GameState state;	
	private GameAI ai;
	private RouteFinder finder;
	private SafetyChecker checker;
	private final int scalar = 64;
	
	
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
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK},
				  {Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK}};	
	
		map = new Map("",blocks,null);

		bombs = new ArrayList<>();
		bombs.add(new Bomb("", new Point(0, 0), 5, 5));
		bombs.add(new Bomb("", new Point(2*scalar, 2*scalar), 5, 5));
		bombs.add(new Bomb("", new Point(4*scalar, 4*scalar), 5, 5));

		players = new ArrayList<>();
		state = new GameState(map, players);
		state.setBombs(bombs);

		ai = new GameAI("ai", new Point(4*scalar,4*scalar), 3, 10, state, null, AIDifficulty.EXTREME);
		players.add(ai);
		checker = new SafetyChecker(state, ai);
		finder = new RouteFinder(state, ai,checker);
		
	}
	
	
	@Test
	public void AStarRouteFinderTest() {

		int xStart = 0;
		int yStart = 0;
		int xEnd = 12;
		int yEnd = 12;
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
	
	
	@Test
	public void enemyCheckTest() {

		Player player = new Player("name", new Point(2*scalar, 2*scalar), 5, 10, null);
		players.add(player);
		assertFalse(checker.isEnemyInBombRange());

		Player player2 = new Player("name2", new Point(4*scalar, 2*scalar), 5, 10, null);
		players.add(player2);
		assertTrue(checker.isEnemyInBombRange());

		ai.setPos(new Point(8*scalar, 2*scalar));
		assertFalse(checker.isEnemyInBombRange());

		ai.setPos(new Point(6*scalar, 2*scalar));
		assertTrue(checker.isEnemyInBombRange());
		
		Player ai2 = new GameAI("ai", new Point(6*scalar,6*scalar), 3, 10, state, null, AIDifficulty.EXTREME);	
		ai.setPos(new Point(6*scalar,8*scalar));
		players.add(ai2);
		players.add(ai);
	
		assertTrue(checker.isEnemyInBombRange());
		assertFalse(checker.isEnemyInBombRangeExludeAIs());
		
		player.setPos(new Point(6*scalar, 6*scalar));
		assertTrue(checker.isEnemyInBombRangeExludeAIs());
		
		ai2.setAlive(false);
		
		player.setAlive(false);
		assertFalse(checker.isEnemyInBombRange());
		
		player.setAlive(true);
		player.setPos(new Point(4*scalar, 7*scalar));
		ai.setPos(new Point(6*scalar,7*scalar));
		assertFalse(checker.isEnemyInBombRangeExludeAIs());
		
		player.setPos(new Point(5*scalar, 6*scalar));
		ai.setPos(new Point(5*scalar,8*scalar));
		assertFalse(checker.isEnemyInBombRangeExludeAIs());
	
		ai.setPos(new Point(4*scalar, 7*scalar));
		player.setPos(new Point(6*scalar,7*scalar));
		assertFalse(checker.isEnemyInBombRangeExludeAIs());
		
		ai.setPos(new Point(5*scalar, 6*scalar));
		player.setPos(new Point(5*scalar,8*scalar));
		assertFalse(checker.isEnemyInBombRangeExludeAIs());

		
	}
	
	@Test
	public void escapeTestFinder() {
		
		List<AIActions> escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 3);

		ai.setPos(new Point(0, 0));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 5);

		ai.setPos(new Point(2*scalar, 7*scalar));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 0);

		ai.setPos(new Point(6*scalar, 6*scalar));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 0);

		ai.setPos(new Point(3*scalar, 2*scalar));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 4);

	}
	
	
	@Test
	public void inDangerCheckTest() {
		ai.setPos(new Point(2*scalar, 3*scalar));
		assertTrue(checker.inDanger());

		ai.setPos(new Point(6*scalar, 0));
		assertFalse(checker.inDanger());

		ai.setPos(new Point(8*scalar, 4*scalar));
		assertTrue(checker.inDanger());

		ai.setPos(new Point(12*scalar, 12*scalar));
		List<Bomb> bombs = new ArrayList<>();
		bombs.add(new Bomb("name", new Point(12*scalar, 9*scalar), 5, 4));
		state.setBombs(bombs);

		assertTrue(checker.inDanger());
	}
	
	
	@Test
	public void moveSafetyTest() {
		
		ai.setPos(new Point(0,6*scalar));
		assertTrue(checker.checkMoveSafety(AIActions.UP));
		assertTrue(checker.checkMoveSafety(AIActions.RIGHT));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
		
		ai.setPos(new Point(0,5*scalar));
		assertFalse(checker.checkMoveSafety(AIActions.UP));
		
		ai.setPos(new Point(6*scalar,3*scalar));
		assertFalse(checker.checkMoveSafety(AIActions.UP));
		assertFalse(checker.checkMoveSafety(AIActions.DOWN));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
		ai.setPos(new Point(3*scalar,6*scalar));
		assertFalse(checker.checkMoveSafety(AIActions.RIGHT));
		assertFalse(checker.checkMoveSafety(AIActions.LEFT));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
		ai.setPos(new Point(5*scalar,8*scalar));
		assertTrue(checker.checkMoveSafety(AIActions.RIGHT));
		assertFalse(checker.checkMoveSafety(AIActions.LEFT));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
		ai.setPos(new Point(10*scalar,10*scalar));
		assertTrue(checker.checkMoveSafety(AIActions.RIGHT));
		assertTrue(checker.checkMoveSafety(AIActions.LEFT));
		assertTrue(checker.checkMoveSafety(AIActions.UP));
		assertTrue(checker.checkMoveSafety(AIActions.DOWN));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
	}
	
	
	@Test
	public void nereastEnemyTest() {
		ai.setPos(new Point(0,0));
		Player player1 = new Player("nr1",new Point(0,12*scalar),3,0, null);
		players.add(player1);
		Player player2 = new Player("nr1",new Point(12*scalar,0),3,0, null);
		players.add(player2);
		Player player3 = new Player("nr1",new Point(5*scalar,6*scalar),3,0, null);
		players.add(player3);
		state.setPlayers(players);
		
		assertEquals(new Point(5,6),finder.getNearestEnemy());
		
		player3.setPos(new Point(6*scalar,7*scalar));
		assertNotEquals(new Point(6,7),finder.getNearestEnemy());
		
		player3.setPos(new Point(4*scalar,3*scalar));
		Player ai2 = new GameAI("name", new Point(0*scalar, 4*scalar),1, 1, state, null, AIDifficulty.EXTREME);
		players.add(ai2);
		assertEquals(new Point(0,4),finder.getNearestEnemy());
		assertNotEquals(new Point(0,4),finder.getNearestEnemyExcludeAIs());
		assertEquals(new Point(4,3),finder.getNearestEnemyExcludeAIs());

		ai2.setGridPos(new Point(0,0));
	}
	
	
	@Test
	public void canPutBombAndEscapeTest()
	{
		
		assertNull(finder.canPutBombAndEscape());
		assertNull(finder.canPutBombAndEscapeExcludeAIs());
		
		ai.setGridPos(new Point(0,4*scalar));
		Player ai3 = new GameAI("name", new Point(0*scalar, 4*scalar),1, 1, state, null, AIDifficulty.EXTREME);
		players.add(ai3);
		assertNull(finder.canPutBombAndEscape());
		assertNull(finder.canPutBombAndEscapeExcludeAIs());
		
		ai3.setPos(new Point(0,8*scalar));
		ai.setPos(new Point(0,7*scalar));
	
		assertNotNull(finder.canPutBombAndEscape());
		assertNull(finder.canPutBombAndEscapeExcludeAIs());
		
		Player player = new Player("nr1",new Point(0*scalar,9*scalar),3,0, null);
		players.add(player);
		
		assertNotNull(finder.canPutBombAndEscapeExcludeAIs());
		
	}

}
