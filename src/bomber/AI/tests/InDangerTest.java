package bomber.AI.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import bomber.game.Bomb;

public class InDangerTest extends MainTestTemplate {

	@Test
	public void test() {
		ai.setPos(new Point(2, 3));
		assertTrue(checker.inDanger());

		ai.setPos(new Point(6, 0));
		assertFalse(checker.inDanger());

		ai.setPos(new Point(9, 4));
		assertTrue(checker.inDanger());

		ai.setPos(new Point(12, 12));
		List<Bomb> bombs = new ArrayList<>();
		bombs.add(new Bomb("name", new Point(12, 9), 5, 4));
		state.setBombs(bombs);

		assertTrue(checker.inDanger());
	}
}
