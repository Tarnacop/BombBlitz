package test.game;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.game.Bomb;

public class BombTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		Point pos = new Point(); //(0,0)
		Bomb bomb = new Bomb("player1", pos, 5, 3);
		
		assertNotNull(bomb);
		
		bomb.setPlayerName("Player 1");
		assertNotNull(bomb.getPlayerName());
		assertEquals(bomb.getPlayerName(), "Player 1");
		
		bomb.setPlayerID(0);
		assertNotNull(bomb.getPlayerID());
		assertEquals(bomb.getPlayerID(), 0);
		
		bomb.setPos(new Point(64,64));
		assertNotNull(bomb.getPos());
		assertEquals(bomb.getPos(), new Point(64, 64));
		
		bomb.setGridPos(new Point(1, 1));
		
		assertNotNull(bomb.getGridPos());
		assertEquals(bomb.getGridPos(), new Point(1, 1));
		
		bomb.setGridPos(new Point(0, 0));
		
		assertNotNull(bomb.getGridPos());
		assertEquals(bomb.getGridPos(), new Point(1, 1));
		
		bomb.setRadius(3);
		assertNotNull(bomb.getRadius());
		assertEquals(bomb.getRadius(), 3);
		
		bomb.setTime(5);
		assertNotNull(bomb.getTime());
		assertEquals(bomb.getTime(), 5);
	}

}
