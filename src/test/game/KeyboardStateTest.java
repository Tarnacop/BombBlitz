package test.game;

import static org.junit.Assert.*;
import static bomber.game.Movement.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.game.KeyboardState;

public class KeyboardStateTest {

	private KeyboardState state;

	@Before
	public void setUp() throws Exception {
		state = new KeyboardState();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertEquals(state.isBomb(), false);
		state.setBomb(true);
		assertEquals(state.isBomb(), true);
		
		assertEquals(state.getMovement(), NONE);
		state.setMovement(UP);
		assertEquals(state.getMovement(), UP);
		
		assertEquals(state.isPaused(), false);
		state.setPaused(true);
		assertEquals(state.isPaused(), true);
	}

}
