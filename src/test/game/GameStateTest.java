package test.game;

import static bomber.game.Block.BLANK;
import static bomber.game.Block.SOLID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.AI.AIDifficulty;
import bomber.AI.GameAI;
import bomber.game.AudioEvent;
import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.Map;
import bomber.game.Player;

public class GameStateTest {

	private GameState gameState;
	private Player testPlayer1;
	private GameAI testAI;
	private Map map;

	@Before
	public void setUp() throws Exception {
		Block[][] grid = new Block[][]{{SOLID, BLANK}, {BLANK, BLANK}};
		List<Point> spawns = new ArrayList<Point>();
		spawns.add(new Point(64,64));
		map = new Map("Test", grid, spawns);
		
		List<Player> players = new ArrayList<Player>();
		testPlayer1 = new Player("Test1", new Point(64,64), 5, 100);
		players.add(testPlayer1);
		
		testAI = new GameAI("TestAI", new Point(128,128), 5, 100, gameState, AIDifficulty.EASY);
		players.add(testAI);
		testAI.begin();
		
		gameState = new GameState(map, players);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGameState() {
		
		gameState.getBombs().add(new Bomb("Test1", new Point(64,64), 1000, 1000));
		testPlayer1.getKeyState().setBomb(true);
		String testState = gameState.toString();
		System.out.println(testState);
		String myState = "Gamestate of: "
				+ "\nPlayers:"
				+ "\n"
				+ "\nName: Test1, Pos: java.awt.Point[x=64,y=64], Speed: 100.0, Lives: 5, Bomb Range: 3"
				+ "\nWith Keyboard State = BOMB, Current Movement: NONE"
				+ "\nName: TestAI, Pos: java.awt.Point[x=128,y=128], Speed: 100.0, Lives: 5, Bomb Range: 3"
				+ "\nWith Keyboard State = NO BOMB, Current Movement: NONE"
				+ "\nBombs:"
				+ "\n"
				+ "\nOwner: Test1Pos: java.awt.Point[x=64,y=64], Radius: 1000, Detonation Time: 1000"
				+ "\nAnd Map:" + "\nHH  " + "\n  PP\n";
		
		assertNotNull(gameState.toString());
		assertEquals(testState, myState);
		assertFalse(gameState.gameOver());
		
		System.out.println("livingHumans 0, livingAi 1??");
		testPlayer1.setAlive(false);
		assertTrue(gameState.gameOver());
		
		System.out.println("livingHumans 1, livingAi 1??");
		testPlayer1.setAlive(true);
		assertFalse(gameState.gameOver());
		
		System.out.println("livingHumans 1, livingAi 0??");
		testAI.stop();
		assertTrue(gameState.gameOver());
		
		System.out.println("livingHumans 0, livingAi 0??");
		testPlayer1.setAlive(false);
		assertTrue(gameState.gameOver());
		
		List<AudioEvent> testAudio = new ArrayList<AudioEvent>();
		testAudio.add(AudioEvent.PLACE_BOMB);
		
		assertEquals(gameState.getAudioEvents().size(), 0);
		gameState.setAudioEvents(testAudio);
		assertEquals(gameState.getAudioEvents().size(), 1);
		
		assertEquals(gameState.getBlastList().size(), 0);
		
		assertEquals(gameState.getBombs().size(), 1);
		gameState.setBombs(new ArrayList<Bomb>());
		assertEquals(gameState.getBombs().size(), 0);
		
		assertEquals(gameState.getMap(), map);
		Block[][] grid = new Block[][]{{SOLID, BLANK}, {BLANK, BLANK}};
		List<Point> spawns = new ArrayList<Point>();
		spawns.add(new Point(64,64));
		Map newMap = new Map("Test1", grid, spawns);
		gameState.setMap(newMap);
		assertEquals(gameState.getMap(), newMap);
		
		assertEquals(gameState.getPlayers().size(), 2);
		List<Player> newPlayers = new ArrayList<Player>();
		Player testPlayer2 = new Player("Test2", new Point(128,64), 5, 100);
		newPlayers.add(testPlayer2);
		gameState.setPlayers(newPlayers);
		assertEquals(gameState.getPlayers().size(), 1);
		assertEquals(gameState.getPlayers().get(0), testPlayer2);
	}

}
