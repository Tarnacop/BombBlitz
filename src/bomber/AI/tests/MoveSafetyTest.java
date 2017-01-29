package bomber.AI.tests;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import bomber.game.Bomb;
import bomber.game.Movement;

public class MoveSafetyTest extends MainTestTemplate {

	@Test
	public void test() {
		
		ai.setPos(new Point(0,6));
		assertFalse(checker.checkMoveSafety(Movement.UP));
		assertTrue(checker.checkMoveSafety(Movement.RIGHT));
		assertTrue(checker.checkMoveSafety(Movement.NONE));
		
		ai.setPos(new Point(6,3));
		assertFalse(checker.checkMoveSafety(Movement.UP));
		assertFalse(checker.checkMoveSafety(Movement.DOWN));
		assertTrue(checker.checkMoveSafety(Movement.NONE));
		
		ai.setPos(new Point(3,6));
		assertFalse(checker.checkMoveSafety(Movement.RIGHT));
		assertFalse(checker.checkMoveSafety(Movement.LEFT));
		assertTrue(checker.checkMoveSafety(Movement.NONE));
		
		ai.setPos(new Point(5,8));
		assertTrue(checker.checkMoveSafety(Movement.RIGHT));
		assertFalse(checker.checkMoveSafety(Movement.LEFT));
		assertTrue(checker.checkMoveSafety(Movement.NONE));
		
		ai.setPos(new Point(10,10));
		assertTrue(checker.checkMoveSafety(Movement.RIGHT));
		assertTrue(checker.checkMoveSafety(Movement.LEFT));
		assertTrue(checker.checkMoveSafety(Movement.UP));
		assertTrue(checker.checkMoveSafety(Movement.DOWN));
		assertTrue(checker.checkMoveSafety(Movement.NONE));
		
	}

}
