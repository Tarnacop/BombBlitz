package bomber.game;

import java.util.List;

public class Maps {

	private MapsParser parser;

	public Maps() {

		try {
			parser = new MapsParser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Map> getMaps() {

		System.out.println(parser.getMaps().get(0).getSpawnPoints());
		return parser.getMaps();
	}
}
