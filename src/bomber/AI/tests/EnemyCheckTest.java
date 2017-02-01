package bomber.AI.tests;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

import bomber.game.Player;

public class EnemyCheckTest extends MainTestTemplate{

	@Test
	public void test() {
		Player player = new Player("name", new Point(2,2), 5, 10);
		players.add(player);
		assertFalse(checker.isEnemyInBombRange());
		
		Player player2 = new Player("name2", new Point(4,2), 5, 10);
		players.add(player2);
		assertTrue(checker.isEnemyInBombRange());
		
		ai.setPos(new Point(8,2));
		assertFalse(checker.isEnemyInBombRange());
		
		ai.setPos(new Point(7,2));
		assertTrue(checker.isEnemyInBombRange());
	}

}
