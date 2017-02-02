package bomber.AI.tests;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.List;

import org.junit.Test;

import bomber.game.Movement;

public class EscapeRouteFinderTest extends MainTestTemplate {
	
	@Test
	public void test() {

		List<Movement> escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 3);

		ai.setPos(new Point(0, 0));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 6);

		ai.setPos(new Point(2, 7));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 1);

		ai.setPos(new Point(6, 6));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 0);

		ai.setPos(new Point(3, 2));
		escapeMoves = finder.escapeFromExplotion(checker.getTilesAffectedByBombs());
		assertEquals(escapeMoves.size(), 4);

	}

}
