package bomber.AI.tests;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.AI.AIDifficulty;
import bomber.AI.GameAI;
import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.Map;
import bomber.game.Movement;
import bomber.game.Player;
public class AIManagerTest {
	
	private Map map;
	private List<Bomb> bombs;
	private List<Player> players;
	private GameState state;	
	private final static int scalar = 64;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//	Map
/*
	[b][-][-][-][-][-][ ][ ][ ][ ][ ][ ][ ]
	[-][x][-][x][-][x][ ][x][ ][x][ ][x][ ]
	[-][-][b][-][-][-][-][-][ ][ ][ ][ ][ ]
	[-][x][-][x][-][x][ ][x][ ][x][ ][x][ ]
	[-][-][-][-][b][-][-][-][-][-][ ][ ][ ]
	[-][x][-][x][-][x][ ][x][ ][x][ ][x][ ]
	[ ][ ][-][ ][-][ ][ ][ ][ ][ ][ ][ ][ ]
	[ ][x][-][x][-][x][ ][x][ ][x][ ][x][ ]
	[ ][ ][ ][ ][-][ ][ ][ ][ ][ ][ ][ ][ ]
	[ ][x][ ][x][-][x][ ][x][ ][x][ ][x][ ]
	[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]
	[ ][x][ ][x][ ][x][ ][x][ ][x][ ][x][ ]
	[ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ][ ]*/
	@Before
	public void setUp() throws Exception {
		Block[][] blocks = {{Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK},
				  {Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK},
				  {Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK}};	

		map = new Map("",blocks,null);
	
		bombs = new ArrayList<>();
		bombs.add(new Bomb("",new Point(0,0),5,5));
		bombs.add(new Bomb("",new Point(2*scalar,2*scalar),5,5));
		bombs.add(new Bomb("",new Point(4*scalar,4*scalar),5,5));
		
		players = new ArrayList<>();
		
		state = new GameState(map, players);
		state.setBombs(bombs);
		
		System.setOut(new PrintStream(outContent));
		
	}
		
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws InterruptedException {
		GameAI ai = new GameAI("ai",new Point(2*scalar,3*scalar),3,10,state, null, AIDifficulty.HARD);
		Player ai2 = new GameAI("ai",new Point(6*scalar,5*scalar),3,10,state, null, AIDifficulty.HARD);
		players.add(ai);
		players.add(ai2);
		state.setPlayers(players);
		((GameAI) ai).begin();
		Thread.sleep(1);
		assertEquals("not", outContent.toString());
		outContent.reset();
		((GameAI) ai2).begin();
		Thread.sleep(1);
		assertEquals("danger", outContent.toString());
		
		
	}

}
