package test.networking;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.AI.AIDifficulty;
import bomber.AI.GameAI;
import bomber.networking.ServerAI;

public class ServerAITest {
	private ServerAI ai;
	private GameAI gai;

	@Before
	public void setUp() throws Exception {
		ai = new ServerAI((byte) 0, AIDifficulty.EASY);
		ai.setGameAI(gai);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertEquals((byte) 0, ai.getID());
		assertEquals(AIDifficulty.EASY, ai.getDifficulty());
		assertEquals(gai, ai.getGameAI());
		ai.setDifficulty(AIDifficulty.EXTREME);
		assertEquals(AIDifficulty.EXTREME, ai.getDifficulty());
	}

}
