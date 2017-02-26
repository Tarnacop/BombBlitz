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
		
		assertNotNull(bomb.getPlayerName());
		assertEquals(bomb.getPlayerName(), "player1");
		
		assertNotNull(bomb.getPos());
		assertEquals(bomb.getPos(), new Point(0, 0));
		
		assertNotNull(bomb.getRadius());
		assertEquals(bomb.getRadius(), 3);
		
		assertNotNull(bomb.getTime());
		assertEquals(bomb.getTime(), 5);
	}

}
