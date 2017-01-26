package bomber.game.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bomber.game.Block;
import bomber.game.Map;

public class MapTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		//TESTS FOR convertMap() METHOD------------------------------------
		
		Block[][] correctMap = new Block[128][128];
		
		for(int x = 0; x < correctMap.length/2; x++){
			
			for(int y = 0; y < correctMap[0].length/2; y++){
				
				correctMap[x][y] = Block.SOLID;
			}
			
			for(int y = correctMap[0].length/2; y < correctMap[0].length; y++){
				
				correctMap[x][y] = Block.BLANK;
			}
		}
		
		for(int x = correctMap.length/2; x < correctMap.length; x++){
			
			for(int y = 0; y < correctMap[0].length/2; y++){
				
				correctMap[x][y] = Block.SOFT;
			}
			
			for(int y = correctMap[0].length/2; y < correctMap[0].length; y++){
				
				correctMap[x][y] = Block.BLAST;
			}
		}
		
		Block[][] gridMap = new Block[][]{{Block.SOLID, Block.BLANK},{Block.SOFT, Block.BLAST}};
		//	SOLID	SOFT
		//	BLANK	BLAST
		
		Map map = new Map(gridMap);
		
		Block[][] pixelMap = map.getPixelMap();
		
		assertNotNull(pixelMap);
		assertEquals(128, pixelMap.length);
		assertEquals(128, pixelMap[0].length);
		assertArrayEquals(correctMap, pixelMap);
		
		//TESTS FOR update() METHOD--------------------------------------
		
		Block[][] correctMap2 = new Block[128][128];
		
		for(int x = 0; x < correctMap2.length/2; x++){
			
			for(int y = 0; y < correctMap2[0].length/2; y++){
				
				correctMap2[x][y] = Block.SOLID;
			}
			
			for(int y = correctMap2[0].length/2; y < correctMap2[0].length; y++){
				
				correctMap2[x][y] = Block.SOFT;
			}
		}
		
		for(int x = correctMap2.length/2; x < correctMap2.length; x++){
			
			for(int y = 0; y < correctMap2[0].length/2; y++){
				
				correctMap2[x][y] = Block.BLAST;
			}
			
			for(int y = correctMap2[0].length/2; y < correctMap2[0].length; y++){
				
				correctMap2[x][y] = Block.BLANK;
			}
		}
		
		Block[][] gridMap2 = map.getGridMap();
		
		assertNotNull(gridMap2);
		
		//	SOLID	SOFT		->		SOLID	BLAST
		//	BLANK	BLAST		->		SOFT	BLANK
		
		gridMap2[0][1] = Block.SOFT;
		gridMap2[1][0] = Block.BLAST;
		gridMap2[1][1] = Block.BLANK;
		
		map.update();
		
		Block[][] pixelMap2 = map.getPixelMap();
		
		assertNotNull(pixelMap2);
		assertEquals(128, pixelMap2.length);
		assertEquals(128, pixelMap2[0].length);
		assertArrayEquals(correctMap2, pixelMap2);
		
		//TESTS FOR GETTERS------------------------------------------
		
		assertNotNull(map.getGridBlockAt(1, 1));
		assertEquals(map.getGridBlockAt(1, 1), Block.BLANK);
		
		assertNotNull(map.getPixelBlockAt(10, 10));
		assertEquals(map.getPixelBlockAt(1, 1), Block.SOLID);
		
	}

}
