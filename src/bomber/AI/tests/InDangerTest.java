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
		ai.setPos(new Point(2*scalar, 3*scalar));
		assertTrue(checker.inDanger());

		ai.setPos(new Point(6*scalar, 0));
		assertFalse(checker.inDanger());

		ai.setPos(new Point(9*scalar, 4*scalar));
		assertTrue(checker.inDanger());

		ai.setPos(new Point(12*scalar, 12*scalar));
		List<Bomb> bombs = new ArrayList<>();
		bombs.add(new Bomb("name", new Point(12*scalar, 9*scalar), 5, 4));
		state.setBombs(bombs);

		assertTrue(checker.inDanger());
	}
}
