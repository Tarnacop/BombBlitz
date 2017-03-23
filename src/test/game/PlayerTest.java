package test.game;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.game.KeyboardState;
import bomber.game.Player;

public class PlayerTest {

	private Player testPlayer;

	@Before
	public void setUp() throws Exception {
		testPlayer = new Player("Test1", new Point(64,64), 5, 100);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		testPlayer.setDifficulty(null);
		testPlayer.begin();
		testPlayer.pause();
		testPlayer.resume();
		testPlayer.stop();
		
		assertEquals(testPlayer.getName(), "Test1");
		testPlayer.setName("Player 1");
		assertEquals(testPlayer.getName(), "Player 1");
		
		assertEquals(testPlayer.getGridPos(), new Point(1, 1));
		testPlayer.setGridPos(new Point(1, 1));
		testPlayer.setPos(new Point(1, 1));
		assertEquals(testPlayer.getPos(), new Point(1, 1));
		assertEquals(testPlayer.getGridPos(), new Point(0, 0));
		
		assertEquals(testPlayer.getPlayerID(), 0);
		testPlayer.setPlayerID(1);
		assertEquals(testPlayer.getPlayerID(), 1);
		
		assertEquals(testPlayer.getBombRange(), 3);
		testPlayer.setBombRange(5);
		assertEquals(testPlayer.getBombRange(), 5);
		
		assertEquals(testPlayer.getLives(), 5);
		testPlayer.setLives(10);
		assertEquals(testPlayer.getLives(), 10);
		
		assertEquals(testPlayer.getSpeed(), 100, 0);
		testPlayer.setSpeed(200);
		assertEquals(testPlayer.getSpeed(), 200, 0);
		
		assertEquals(testPlayer.isAlive(), true);
		testPlayer.setAlive(false);
		assertEquals(testPlayer.isAlive(), false);
		
		assertEquals(testPlayer.getInvulnerability(), 0);
		testPlayer.setInvulnerability(10);
		assertEquals(testPlayer.getInvulnerability(), 10);
		
		assertEquals(testPlayer.getMaxNrOfBombs(), 2);
		testPlayer.setMaxNrOfBombs(3);
		assertEquals(testPlayer.getMaxNrOfBombs(), 3);
		
		assertNotNull(testPlayer.getKeyState());
		KeyboardState k = new KeyboardState();
		testPlayer.setKeyState(k);
		assertEquals(testPlayer.getKeyState(), k);
	}

}
