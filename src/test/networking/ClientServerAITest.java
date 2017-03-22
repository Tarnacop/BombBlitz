package test.networking;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.AI.AIDifficulty;
import bomber.networking.ClientServerAI;

public class ClientServerAITest {
	private ClientServerAI ai;
	private ClientServerAI ai2;

	@Before
	public void setUp() throws Exception {
		ai = new ClientServerAI((byte) 133, null);
		ai2 = new ClientServerAI((byte) 134, AIDifficulty.EXTREME);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertEquals((byte) 133, ai.getID());
		assertEquals((byte) 134, ai2.getID());

		assertEquals(AIDifficulty.MEDIUM, ai.getDifficulty());
		assertEquals(AIDifficulty.EXTREME, ai2.getDifficulty());
	}

}
