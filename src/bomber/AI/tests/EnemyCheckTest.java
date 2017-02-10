package bomber.AI.tests;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

import bomber.game.Player;

public class EnemyCheckTest extends MainTestTemplate {

	@Test
	public void test() {
		Player player = new Player("name", new Point(2*scalar, 2*scalar), 5, 10, null);
		players.add(player);
		assertFalse(checker.isEnemyInBombRange());

		Player player2 = new Player("name2", new Point(4*scalar, 2*scalar), 5, 10, null);
		players.add(player2);
		assertTrue(checker.isEnemyInBombRange());

		ai.setPos(new Point(8*scalar, 2*scalar));
		assertFalse(checker.isEnemyInBombRange());

		ai.setPos(new Point(7*scalar, 2*scalar));
		assertTrue(checker.isEnemyInBombRange());
	}

}
