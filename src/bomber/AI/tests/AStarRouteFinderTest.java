package bomber.AI.tests;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.List;

import org.junit.Test;

import bomber.AI.AIActions;

public class AStarRouteFinderTest extends MainTestTemplate {

	@Test
	public void test() {

		int xStart = 0;
		int yStart = 0;
		int xEnd = 12;
		int yEnd = 12;
		List<AIActions> moves = finder.findRoute(new Point(xStart, yStart), new Point(xEnd, yEnd));
		assertEquals(moves.size(), (Math.abs(xStart - xEnd) + Math.abs(yStart - yEnd)));

		xStart = 2;
		yStart = 2;
		xEnd = 10;
		yEnd = 10;
		moves = finder.findRoute(new Point(xStart, yStart), new Point(xEnd, yEnd));
		assertEquals(moves.size(), (Math.abs(xStart - xEnd) + Math.abs(yStart - yEnd)));

		xStart = 2;
		yStart = 2;
		xEnd = 2;
		yEnd = 2;
		moves = finder.findRoute(new Point(xStart, yStart), new Point(xEnd, yEnd));
		assertEquals(moves.size(), (Math.abs(xStart - xEnd) + Math.abs(yStart - yEnd)));

		xStart = 12;
		yStart = 0;
		xEnd = 12;
		yEnd = 12;
		moves = finder.findRoute(new Point(xStart, yStart), new Point(xEnd, yEnd));
		assertEquals(moves, null);

	}

}
