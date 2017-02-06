package bomber.AI.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;

import org.junit.Test;

import bomber.AI.AIActions;

public class MoveSafetyTest extends MainTestTemplate {

	@Test
	public void test() {
		
		ai.setPos(new Point(0,6));
		assertFalse(checker.checkMoveSafety(AIActions.UP));
		assertTrue(checker.checkMoveSafety(AIActions.RIGHT));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
		ai.setPos(new Point(6,3));
		assertFalse(checker.checkMoveSafety(AIActions.UP));
		assertFalse(checker.checkMoveSafety(AIActions.DOWN));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
		ai.setPos(new Point(3,6));
		assertFalse(checker.checkMoveSafety(AIActions.RIGHT));
		assertFalse(checker.checkMoveSafety(AIActions.LEFT));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
		ai.setPos(new Point(5,8));
		assertTrue(checker.checkMoveSafety(AIActions.RIGHT));
		assertFalse(checker.checkMoveSafety(AIActions.LEFT));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
		ai.setPos(new Point(10,10));
		assertTrue(checker.checkMoveSafety(AIActions.RIGHT));
		assertTrue(checker.checkMoveSafety(AIActions.LEFT));
		assertTrue(checker.checkMoveSafety(AIActions.UP));
		assertTrue(checker.checkMoveSafety(AIActions.DOWN));
		assertTrue(checker.checkMoveSafety(AIActions.NONE));
		
	}

}
