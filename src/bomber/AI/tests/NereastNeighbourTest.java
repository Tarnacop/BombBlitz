package bomber.AI.tests;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

import bomber.game.Player;

public class NereastNeighbourTest extends MainTestTemplate {

	@Test
	public void test() {
		ai.setPos(new Point(0,0));
		Player player1 = new Player("nr1",new Point(0,12*scalar),3,0);
		players.add(player1);
		Player player2 = new Player("nr1",new Point(12*scalar,0),3,0);
		players.add(player2);
		Player player3 = new Player("nr1",new Point(5*scalar,6*scalar),3,0);
		players.add(player3);
		state.setPlayers(players);
		
		assertEquals(new Point(5,6),finder.getNearestEnemy());
		
		player3.setPos(new Point(6,7));
		
		assertNotEquals(new Point(6,7),finder.getNearestEnemy());
	}

}
