package bomber.game;

import java.util.ArrayList;
import java.util.List;

public class Maps {

	private static List<Map> maps;
	
	public Maps(){
	
		Block[][] gridMap1 = new Block[][]{{Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID},
			{Block.SOLID,Block.BLANK,Block.BLANK,Block.BLANK,Block.SOLID},
			{Block.SOLID,Block.BLANK,Block.BLANK,Block.BLANK,Block.SOLID},
			{Block.SOLID,Block.BLANK,Block.BLANK,Block.BLANK,Block.SOLID},
			{Block.SOLID,Block.SOFT,Block.SOFT,Block.SOFT,Block.SOLID},
			
			{Block.SOLID,Block.SOLID,Block.SOFT,Block.SOLID,Block.SOLID},
			{Block.SOLID,Block.SOLID,Block.SOFT,Block.SOLID,Block.SOLID},
			{Block.SOLID,Block.SOLID,Block.BLANK,Block.SOLID,Block.SOLID},
			{Block.SOLID,Block.SOLID,Block.BLANK,Block.SOLID,Block.SOLID},
			{Block.SOLID,Block.SOLID,Block.BLANK,Block.SOLID,Block.SOLID},
			
			{Block.SOLID,Block.SOFT,Block.SOFT,Block.SOFT,Block.SOLID},
			{Block.SOLID,Block.BLANK,Block.BLANK,Block.SOFT,Block.SOLID},
			{Block.SOLID,Block.BLANK,Block.BLANK,Block.SOFT,Block.SOLID},
			{Block.SOLID,Block.SOFT,Block.BLANK,Block.SOFT,Block.SOLID},
			{Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID}};

		//		HHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
		//		HH      OOHHHHHHHHHHOO    OOHH
		//		HH      OOOOOO      OO	    HH
		//		HH      OOHHHHHHHHHHOOOOOO  HH
		//		HHHHHHHHHHHHHHHHHHHHHHHHHHHHHH

			
			Block[][] gridMap2 = {{Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID},
					{Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK, Block.BLANK,Block.SOLID},
			{Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID}};
			
		Map map1 = new Map("Level 1", gridMap1);
		Map map2 = new Map("", gridMap2);
		maps = new ArrayList<Map>();
		maps.add(map1);
		maps.add(map2);
	}

	public List<Map> getMaps() {

		return maps;
	}
}
