package bomber.game;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Class MapsParser.
 * 
 * @author Jokubas Liutkus
 */
public class MapsParser extends DefaultHandler {

	/** The Constant scalar. */
	private static final int scalar = 64;

	/** The map. */
	private ArrayList<String> map;
	
	/** The temp. */
	private String temp;
	
	/** The name. */
	private String name;
	
	/** The maps. */
	private ArrayList<Map> maps = new ArrayList<>();
	
	/** The spawn points. */
	private ArrayList<Point> spawnPoints;
	

	/**
	 * Instantiates a new maps parser.
	 *
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public MapsParser() throws ParserConfigurationException, SAXException, IOException {
		
		// Create a "parser factory" for creating SAX parsers
		SAXParserFactory spfac = SAXParserFactory.newInstance();

		// Now use the parser factory to create a SAXParser object
		SAXParser sp = spfac.newSAXParser();

		// Finally, tell the parser to parse the input and notify the handler
		sp.parse(getClass().getResourceAsStream("/maps/maps.xml"), this);
		
//		for(Map bl: maps)
//		{
//			for(int x =0; x<bl.getGridMap().length; x++)
//			{
//				for(int y=0; y<bl.getGridMap()[0].length; y++)
//				{
//					System.out.print(bl.getGridMap()[x][y] + " ");
//				}
//				System.out.println();
//			}
//			System.out.println('\n');
//		}

	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	/*
	 * When the parser encounters plain text (not XML elements), it calls(this
	 * method, which accumulates them in a string buffer
	 */
	public void characters(char[] buffer, int start, int length) {
		temp = new String(buffer, start, length);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	/*
	 * Every time the parser encounters the beginning of a new element, it calls
	 * this method, which resets the string buffer
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		temp = "";
		if (qName.equalsIgnoreCase("map")) {
			map = new ArrayList<>();
			spawnPoints = new ArrayList<>();
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	/*
	 * When the parser encounters the end of an element, it calls this method
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equalsIgnoreCase("map")) {
			// add it to the list
			maps.add(parseMap(map));

		} else if (qName.equalsIgnoreCase("item")) {
			map.add(temp);
		} else if (qName.equalsIgnoreCase("name")) {
			name = temp;
		}

	}
	
	
	/**
	 * Gets the maps
	 *
	 * @return the maps
	 */
	public List<Map> getMaps()
	{
		return maps;
	}

	/**
	 * Parses the map.
	 *
	 * @param map the map
	 * @return the map
	 */
	private Map parseMap(ArrayList<String> map) {
		String[] t;
		Block[] block;
		Block[][] mapHelper = new Block[map.size()][map.get(0).length()];
		for (int j=0; j<map.size(); j++) {
			
			t = map.get(j).split(",");
			block = new Block[t.length];
			for (int i = 0; i < t.length; i++) {
				switch (t[i]) {
				case "X":
					block[i] = Block.SOLID;
					break;
				case "_":
					block[i] = Block.BLANK;
					break;
				case "O":
					block[i] = Block.SOFT;
					break;
				case "*":
					block[i] = Block.BLANK;
					spawnPoints.add(new Point(i*scalar, j*scalar));
					break;
				case "H":
					block[i] = Block.HOLE;
					break;
				default: 
					block[i] = Block.SOFT;
					break;
				}
				
			}	
			mapHelper[j] = block;
		}
		
				
		Map singleMap = new Map(name, transpose(mapHelper), spawnPoints);
		return singleMap;
	}
	
	
	private Block[][] transpose(Block[][] map)
	{
		Block[][] transposedMap = new Block[map[0].length][map.length];
		for(int i=0; i<map.length; i++)
			for(int j=0; j<map[0].length; j++)
				transposedMap[j][i] = map[i][j];
		
		return transposedMap;
	}

}
