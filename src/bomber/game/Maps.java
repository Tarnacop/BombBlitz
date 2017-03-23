package bomber.game;

import java.util.List;

/**
 *
 * @author Owen Jenkins
 * @version 1.4
 * @since 2017-03-23
 * 
 *        Maps class for "Bomb Blitz" Game Application (2017 Year 2 Team
 *        Project, Team B1). Contains a list of maps parsed from the maps file.
 */
public class Maps {

	private MapsParser parser;

	/**
	 * Create a new Maps object containing a map parser.
	 */
	public Maps() {

		try {
			parser = new MapsParser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the list of maps.
	 * 
	 * @return the list of maps
	 */
	public List<Map> getMaps() {

		return parser.getMaps();
	}
}
