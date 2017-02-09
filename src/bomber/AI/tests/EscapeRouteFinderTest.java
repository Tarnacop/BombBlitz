package bomber.AI.tests;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.List;

import org.junit.Test;

import bomber.AI.AIActions;
import bomber.game.Movement;

public class EscapeRouteFinderTest extends MainTestTemplate {
	
	@Test
	public void test() {

		List<AIActions> escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 3);

		ai.setPos(new Point(0, 0));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 6);

		ai.setPos(new Point(2*scalar, 7*scalar));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 1);

		ai.setPos(new Point(6*scalar, 6*scalar));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 0);

		ai.setPos(new Point(3*scalar, 2*scalar));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 4);

	}

}
