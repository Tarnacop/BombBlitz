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

public class MapsParser extends DefaultHandler {

	/** The Constant scalar. */
	protected static final int scalar = 64;

	/** The Constant playerSize. */
	protected static final int playerSize = 32;

	
	private ArrayList<String> map;
	private String temp;
	private String name;
	private ArrayList<Map> maps = new ArrayList<>();
	private ArrayList<Point> spawnPoints;
	

	public MapsParser() throws ParserConfigurationException, SAXException, IOException {
		
		// Create a "parser factory" for creating SAX parsers
		SAXParserFactory spfac = SAXParserFactory.newInstance();

		// Now use the parser factory to create a SAXParser object
		SAXParser sp = spfac.newSAXParser();

		// Finally, tell the parser to parse the input and notify the handler
		sp.parse("src/resources/maps/maps.xml", this);
		
	
	}

	/*
	 * When the parser encounters plain text (not XML elements), it calls(this
	 * method, which accumulates them in a string buffer
	 */
	public void characters(char[] buffer, int start, int length) {
		temp = new String(buffer, start, length);
	}

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
	
	
	public List<Map> getMaps()
	{
		return maps;
	}

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
				}
			}	
			mapHelper[j] = block;
		}
		Map singleMap = new Map(name, mapHelper, spawnPoints);
		return singleMap;
	}

}
