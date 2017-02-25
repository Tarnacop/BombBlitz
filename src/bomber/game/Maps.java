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

		return parser.getMaps();
	}
}
