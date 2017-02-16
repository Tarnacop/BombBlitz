package bomber.game;

import java.awt.Point;
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
			{Block.SOLID,Block.SOFT,Block.BLANK,Block.BLANK,Block.SOLID},
			{Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID}};

		//		0 HHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
		//		1 HH[]    OOHHHHHHHHHHOO  []OOHH
		//		2 HH      OOOOOO      OO	  HH
		//		3 HH[]    OOHHHHHHHHHHOOOOOO  HH
		//		4 HHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
//				  0 1 2 3 4 5 6 7 8 9 1011121314
			
			Block[][] gridMap2 = {{Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID},
					{Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK,  Block.SOFT, Block.BLANK, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT, Block.BLANK, Block.BLANK,Block.SOLID},
					{Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,Block.SOLID},
					{Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK,  Block.SOFT, Block.BLANK, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT, Block.BLANK, Block.BLANK,Block.SOLID},
					{Block.SOLID,Block.SOFT, Block.SOLID, Block.BLANK,  Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK,  Block.SOFT, Block.BLANK, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT, Block.BLANK, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,  Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK,  Block.SOFT, Block.BLANK, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT, Block.BLANK, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,  Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK,  Block.SOFT, Block.BLANK, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT, Block.BLANK, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.SOFT, Block.SOLID, Block.BLANK,  Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.SOFT, Block.BLANK, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT, Block.BLANK, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID,Block.BLANK, Block.SOLID, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.BLANK, Block.BLANK, Block.BLANK, Block.SOFT, Block.BLANK, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOFT, Block.BLANK, Block.BLANK,Block.SOLID},
					  {Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID,Block.SOLID}};
			
			
		List<Point> spawns = new ArrayList<Point>();
		spawns.add(new Point(64, 64));
		spawns.add(new Point(832, 128));
		spawns.add(new Point(128, 128));
		spawns.add(new Point(64, 192));
		Map map1 = new Map("Level 1", gridMap1, spawns);
		List<Point> spawns1 = new ArrayList<Point>();
		spawns1.add(new Point(64, 64));
		spawns1.add(new Point(64, 64));
		spawns1.add(new Point(64, 64));
		spawns1.add(new Point(64, 64));
		Map map2 = new Map("Level 2", gridMap2, spawns1);
		maps = new ArrayList<Map>();
		maps.add(map1);
		maps.add(map2);
	}

	public List<Map> getMaps() {

		return maps;
	}
}
