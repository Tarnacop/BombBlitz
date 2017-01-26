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
	}

}
