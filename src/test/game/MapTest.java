package test.game;

import static bomber.game.Block.*;
import static org.junit.Assert.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.Map;
import bomber.game.Maps;
import bomber.game.Player;

public class MapTest {

	private Map map;

	@Before
	public void setUp() throws Exception {
		Block[][] grid = new Block[][]{{SOLID, BLANK}, {BLANK, BLANK}};
		List<Point> spawns = new ArrayList<Point>();
		spawns.add(new Point(64,64));
		map = new Map("Test", grid, spawns);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMap() {
		
		Maps maps = new Maps();
		assertNotNull(maps);
		assertNotNull(maps.getMaps());
		
		assertEquals(map.getName(), "Test");
		
		List<Point> newSpawns = new ArrayList<Point>();
		newSpawns.add(new Point(64,64));
		newSpawns.add(new Point(128,128));
		map.setSpawnPoints(newSpawns);
		assertNotNull(map.getSpawnPoints());
		assertEquals(map.getSpawnPoints().size(), 2);
		assertEquals(map.getSpawnPoints().get(1), new Point(128,128));
		
		assertArrayEquals(map.getGridMap(), new Block[][]{{SOLID, BLANK}, {BLANK, BLANK}});
		assertArrayEquals(map.getPixelMap(), map.convertToPixel(new Block[][]{{SOLID, BLANK}, {BLANK, BLANK}}));
		
		assertEquals(map.isInGridBounds(new Point(0,0)), true);
		assertEquals(map.isInGridBounds(new Point(1000,-1)), false);
		assertEquals(map.isInGridBounds(new Point(-1,-1)), false);
		assertEquals(map.isInGridBounds(new Point(0,1000)), false);
		assertEquals(map.isInGridBounds(new Point(1000,1000)), false);
		
		map.setGridBlockAt(new Point(0,0), SOFT);
		assertEquals(map.getGridBlockAt(0, 0), SOFT);
		
		map.setPixelBlockAt(new Point(0,0), SOFT);
		assertEquals(map.getPixelBlockAt(0, 0), SOFT);
		List<Player> players = new ArrayList<Player>();
		players.add(new Player("Test 1", new Point(0,0), 1, 100));
		List<Bomb> bombs = new ArrayList<Bomb>();
		bombs.add(new Bomb("Test 1", new Point(64, 64), 1000, 1000));
		assertEquals(map.toStringWithPlayersBombs(players, bombs),"PP  " +  
				  "\n  o*\n");
		
		map.setGridBlockAt(new Point(1,0), BLAST);
		assertEquals(map.toStringWithPlayersBombs(players, bombs),"PPXX" +  
				  "\n  o*\n");
		
		map.setGridBlockAt(new Point(1,0), SOFT);
		assertEquals(map.toStringWithPlayersBombs(players, bombs),"PPOO" +  
				  "\n  o*\n");
		
		map.setGridBlockAt(new Point(1,0), SOLID);
		assertEquals(map.toStringWithPlayersBombs(players, bombs),"PPHH" +  
				  "\n  o*\n");
		
		map.setGridBlockAt(new Point(1,0), HOLE);
		assertEquals(map.toStringWithPlayersBombs(players, bombs),"PP__" +  
				  "\n  o*\n");
		
		map.setGridBlockAt(new Point(1,0), MINUS_BOMB);
		assertEquals(map.toStringWithPlayersBombs(players, bombs),"PP-b" +  
				  "\n  o*\n");
		
		map.setGridBlockAt(new Point(1,0), MINUS_RANGE);
		assertEquals(map.toStringWithPlayersBombs(players, bombs),"PP-r" +  
				  "\n  o*\n");
		
		map.setGridBlockAt(new Point(1,0), MINUS_SPEED);
		assertEquals(map.toStringWithPlayersBombs(players, bombs),"PP-s" +  
				  "\n  o*\n");
		
		map.setGridBlockAt(new Point(1,0), PLUS_BOMB);
		assertEquals(map.toStringWithPlayersBombs(players, bombs),"PP+b" +  
				  "\n  o*\n");
		
		map.setGridBlockAt(new Point(1,0), PLUS_RANGE);
		assertEquals(map.toStringWithPlayersBombs(players, bombs),"PP+r" +  
				  "\n  o*\n");
		
		map.setGridBlockAt(new Point(1,0), PLUS_SPEED);
		assertEquals(map.toStringWithPlayersBombs(players, bombs),"PP+s" +  
				  "\n  o*\n");
		
		map.setGridBlockAt(new Point(1,0), BLANK);
		assertEquals(map.toStringWithPlayersBombs(players, bombs),"PP  " +  
				  "\n  o*\n");
	}

}