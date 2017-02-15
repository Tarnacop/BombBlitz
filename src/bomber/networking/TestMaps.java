package bomber.networking;

import java.util.ArrayList;
import java.util.List;

import bomber.game.Block;
import bomber.game.Map;

public class TestMaps {

	// private static List<Map> maps;

	public TestMaps() {

	}

	public static List<Map> getMaps() {
		Block[][] gridMap1 = new Block[][] { { Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.BLANK, Block.SOLID },
				{ Block.SOLID, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOLID },

				{ Block.SOLID, Block.SOLID, Block.SOFT, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.SOFT, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.BLANK, Block.SOLID, Block.SOLID },

				{ Block.SOLID, Block.SOFT, Block.SOFT, Block.SOFT, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.SOFT, Block.SOLID },
				{ Block.SOLID, Block.BLANK, Block.BLANK, Block.SOFT, Block.SOLID },
				{ Block.SOLID, Block.SOFT, Block.BLANK, Block.SOFT, Block.SOLID },
				{ Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID, Block.SOLID } };
				
				//		HHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
				//		HH      OOHHHHHHHHHHOO    OOHH
				//		HH      OOOOOO      OO	    HH
				//		HH      OOHHHHHHHHHHOOOOOO  HH
				//		HHHHHHHHHHHHHHHHHHHHHHHHHHHHHH

		Map map1 = new Map(gridMap1);

		List<Map> maps = new ArrayList<Map>();
		maps.add(map1);

		return maps;
	}
}
