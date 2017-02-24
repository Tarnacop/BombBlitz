package bomber.AI.tests;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

import bomber.AI.AIDifficulty;
import bomber.AI.GameAI;
import bomber.game.Player;

public class EnemyCheckTest extends MainTestTemplate {

	@Test
	public void test() {
//		Map
		/*
			[b][-][-][-][-][ ][ ][ ][ ][ ][ ][x][ ]
			[-][x][-][x][-][x][ ][x][ ][x][ ][x][x]
			[-][-][b][-][-][-][-][ ][ ][ ][ ][ ][ ]
			[-][x][-][x][-][x][ ][x][ ][x][ ][x][ ]
			[-][-][-][-][b][-][-][-][-][ ][ ][ ][ ]
			[ ][x][-][x][-][x][ ][x][ ][x][ ][x][ ]
			[ ][ ][-][ ][-][ ][ ][ ][ ][ ][ ][ ][ ]
			[ ][x][ ][x][-][x][ ][x][ ][x][ ][x][ ]
			[ ][ ][ ][ ][-][ ][ ][ ][ ][ ][ ][ ][ ]
			[ ][x][ ][x][ ][x][ ][x][ ][x][ ][x][ ]
			[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
			[ ][x][ ][x][ ][x][ ][x][ ][x][ ][x][ ]
			[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]*/
		
		Player player = new Player("name", new Point(2*scalar, 2*scalar), 5, 10, null);
		players.add(player);
		assertFalse(checker.isEnemyInBombRange());

		Player player2 = new Player("name2", new Point(4*scalar, 2*scalar), 5, 10, null);
		players.add(player2);
		assertTrue(checker.isEnemyInBombRange());

		ai.setPos(new Point(8*scalar, 2*scalar));
		assertFalse(checker.isEnemyInBombRange());

		ai.setPos(new Point(6*scalar, 2*scalar));
		assertTrue(checker.isEnemyInBombRange());
		
		Player ai2 = new GameAI("ai", new Point(6*scalar,6*scalar), 3, 10, state, null, AIDifficulty.EXTREME);	
		ai.setPos(new Point(6*scalar,8*scalar));
		players.add(ai2);
		players.add(ai);
	
		assertTrue(checker.isEnemyInBombRange());
		assertFalse(checker.isEnemyInBombRangeExludeAIs());
		
		player.setPos(new Point(6*scalar, 6*scalar));
		assertTrue(checker.isEnemyInBombRangeExludeAIs());
		
		ai2.setAlive(false);
		
		player.setAlive(false);
		assertFalse(checker.isEnemyInBombRange());
		
		player.setAlive(true);
		player.setPos(new Point(4*scalar, 7*scalar));
		ai.setPos(new Point(6*scalar,7*scalar));
		assertFalse(checker.isEnemyInBombRangeExludeAIs());
		
		player.setPos(new Point(5*scalar, 6*scalar));
		ai.setPos(new Point(5*scalar,8*scalar));
		assertFalse(checker.isEnemyInBombRangeExludeAIs());
	
		ai.setPos(new Point(4*scalar, 7*scalar));
		player.setPos(new Point(6*scalar,7*scalar));
		assertFalse(checker.isEnemyInBombRangeExludeAIs());
		
		ai.setPos(new Point(5*scalar, 6*scalar));
		player.setPos(new Point(5*scalar,8*scalar));
		assertFalse(checker.isEnemyInBombRangeExludeAIs());

		
	}
}
