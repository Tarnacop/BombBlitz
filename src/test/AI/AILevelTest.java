package test.AI;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bomber.AI.AIDifficulty;
import bomber.AI.GameAI;
import bomber.AI.RouteFinder;
import bomber.AI.SafetyChecker;
import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.Constants;
import bomber.game.GameState;
import bomber.game.Map;
import bomber.game.Player;
import bomber.physics.PhysicsEngine;

public class AILevelTest{
	
	private Map map;
	private List<Bomb> bombs;
	private List<Player> players;
	private GameState state;	
	private Player ai;
	private RouteFinder finder;
	private SafetyChecker checker;
	private PhysicsEngine physics;
	private Player player;
	@Before
	public void setUp() throws Exception
	{
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
		
		Block[][] blocks = 
				 {{Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,   Block.SOLID, Block.SOLID ,Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID},
				  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID},
				  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID},
				  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID},
				  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID},
				  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID},
				  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID },
				  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID},
				  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID},
				  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID},
				  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID},
				  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOFT, Block.SOLID},
				  {Block.SOLID,Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.SOFT, Block.SOLID},
				  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOFT, Block.SOFT, Block.BLANK, Block.SOLID},
				  {Block.SOLID,Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID},
				  {Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,   Block.SOLID, Block.SOLID ,Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID}};	
	
		map = new Map("",blocks,null);
		
		bombs = new ArrayList<>();
		bombs.add(new Bomb("", new Point(1*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 1*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER), 500000, 5));
		bombs.add(new Bomb("", new Point(3*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 3*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER), 500000, 5));
		bombs.add(new Bomb("", new Point(5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER, 5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER), 500000, 5));
		players = new ArrayList<>();
		state = new GameState(map, players);
		state.setBombs(bombs);

		ai = new GameAI("ai", new Point(5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER), 30000, 10, state, AIDifficulty.HARD);
		players.add(ai);

//		player = new Player("name", new Point(12*scalar,12*scalar),1,30000, null );
//		players.add(player);
		checker = new SafetyChecker(state, (GameAI)ai);
		finder = new RouteFinder(state, (GameAI)ai,checker);

		this.physics = new PhysicsEngine(state);	
		state.setBombs(bombs);
	}
	

	
	
	@Test
	public void extremeAITest() throws InterruptedException
	{
		players.clear();
		players.add(ai);
		player = new Player("name2", new Point(13*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,13*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER),1,30000);
		players.add(player);
		ai.setPos(new Point(5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		ai.setAlive(true);
		Player ai2 = new GameAI("ai", new Point(5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER), 30000, 10, state, AIDifficulty.EXTREME);
		ai.setDifficulty(AIDifficulty.EXTREME);
		players.add(ai2);
		ai.begin();
		ai2.begin();
		while(checker.inDanger()){
			Thread.sleep(10);
			this.physics.update();
		}
		assertFalse(checker.inDanger());	
		state.setBombs(new ArrayList<>());
		
		while(player.isAlive())
		{
			Thread.sleep(20);
			this.physics.update();
		}
		assertFalse(player.isAlive());
		ai.setAlive(false);
		ai2.setAlive(false);
		this.physics.update();

	}


	@Test
	public void mediumTest() throws InterruptedException
	{
		players.clear();
		players.add(ai);
		player = new Player("name", new Point(13*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,13*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER),1,30000);
		players.add(player);
		ai.setPos(new Point(5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		ai.setAlive(true);
		ai.setDifficulty(AIDifficulty.MEDIUM);
		ai.begin();
	
		int counter = 500;
		while(counter>0)
		{
			Thread.sleep(10);
			this.physics.update();
			counter--;
		}
		assertTrue(ai.isAlive());
		ai.setAlive(false);
		player.setAlive(false);
		assertFalse(ai.isAlive());
		this.physics.update();
	}
	
	@Test
	public void hardAITest() throws InterruptedException
	{
		players.clear();
		players.add(ai);
		player = new Player("name", new Point(13*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,13*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER),1,30000);
		players.add(player);
		ai.setDifficulty(AIDifficulty.HARD);
		ai.setPos( new Point(5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		ai.setAlive(true);
		ai.begin();
		while(checker.inDanger()){
			Thread.sleep(10);
			this.physics.update();
		}
		assertFalse(checker.inDanger());	
		state.setBombs(new ArrayList<>());
		
		while(player.isAlive())
		{
			Thread.sleep(10);
			this.physics.update();
		}
		assertFalse(player.isAlive());
		ai.setAlive(false);
		this.physics.update();
	} 
	
	@Test
	public void easyTest() throws InterruptedException
	{
		players.clear();
		players.add(ai);
		player = new Player("name", new Point(13*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,13*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER),1,30000);
		players.add(player);
		ai.setAlive(true);
		ai.setDifficulty(AIDifficulty.EASY);
		ai.setPos(new Point(5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER,5*Constants.MAP_BLOCK_TO_GRID_MULTIPLIER));
		ai.begin();
		assertTrue(checker.inDanger());
		while(checker.inDanger()){
			Thread.sleep(10);
			this.physics.update();
		}
	
		int counter = 500;
		while(counter>0)
		{
			Thread.sleep(10);
			this.physics.update();
			counter--;
		}
		assertTrue(ai.isAlive());
		ai.setAlive(false);
		player.setAlive(false);
		assertFalse(ai.isAlive());
		this.physics.update();
	}


}
