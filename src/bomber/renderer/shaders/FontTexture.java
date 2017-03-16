package bomber.renderer.shaders;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;

import javax.imageio.ImageIO;

import bomber.game.Constants;

/**
 * Class to create the texture for a font
 * @author Alexandru Blinda
 *
 */
public class FontTexture {

	private final Font font;
	private final String charSetName;
	private final HashMap<Character, CharInfo> charMap;
	
	private int width;
	private int height;
	
	private Texture texture;
	
	/**
	 * Create a FontTexture object with the path to the font, size of the font and fontType
	 * @param path The given path to the font
	 * @param size The given size of the font
	 * @param fontType The given font type
	 * @throws Exception
	 */
	public FontTexture(String path, int size, int fontType) throws Exception {
		
		this(Font.createFont(Font.TRUETYPE_FONT, new File(path)).deriveFont(fontType, size));
	} // END OF CONSTRUCTOR
	
	/**
	 * Create a FontTexture object with the given font
	 * @param font The given font
	 * @throws Exception
	 */
	public FontTexture(Font font) throws Exception {
		
		this.font = font;
		this.charSetName = Constants.CHARSET_NAME;
		
		charMap = new HashMap<>();
		
		buildTexture();
	} // END OF CONSTRUCTOR
	
	/**
	 * Get all the available characters of the font for a char set
	 * @param charSetName The given char set
	 * @return
	 */
	// Given a character set, we return all the characters that can be rendered
	private String getAllAvailableCharacters(String charSetName) {
		
		CharsetEncoder charEnc = Charset.forName(charSetName).newEncoder();
		StringBuilder result = new StringBuilder();
		
		for(char c = 0; c < Character.MAX_VALUE; c++) {
			
			if(charEnc.canEncode(c)) {
				
				result.append(c);
			}
		}
		
		return result.toString();
	} // END OF getAllAvailableCharacters METHOD
	
	/**
	 * Build the texture of the font
	 * @throws Exception
	 */
	private void buildTexture() throws Exception {
		
	    // Get the font metrics for each character for the selected font by using image
	    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	    
	    Graphics2D g2D = img.createGraphics();
	    g2D.setFont(font);
	    
	    FontMetrics fontMetrics = g2D.getFontMetrics();
	    
	    String allChars = getAllAvailableCharacters(this.charSetName);
	    
	    this.width = 0;
	    this.height = 0;
	    for(char c : allChars.toCharArray()) {
	    	
	    	// Get the size for each character and update the global dimensions
	        CharInfo charInfo = new CharInfo(width, fontMetrics.charWidth(c));
	        charMap.put(c, charInfo);
	        width += charInfo.getWidth();
	        height = Math.max(height, fontMetrics.getHeight());
	    }
	    
	    g2D.dispose();
	    
	    // Create the image associated to the charset (the .png of the font)
	    img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    g2D = img.createGraphics();
	    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2D.setFont(font);
	    fontMetrics = g2D.getFontMetrics();
	    g2D.setColor(Color.WHITE);
	    g2D.drawString(allChars, 0, fontMetrics.getAscent());
	    g2D.dispose();
	    
	    // Dump image to a byte buffer
	    InputStream is;
	    try (
	        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	        ImageIO.write(img, Constants.IMAGE_FORMAT, out);
	        out.flush();
	        is = new ByteArrayInputStream(out.toByteArray());
	    }
	    
		texture = new Texture(is);
	} // END OF buildTexture METHOD
	
	/**
	 * Return the height of the font
	 * @return The height of the font
	 */
	public int getHeight() {
		
		return this.height;
	} // END OF getHeight METHOD
	
	/**
	 * Return the width of the font
	 * @return The width of the font
	 */
	public int getWidth() {
		
		return this.width;
	} // END OF getWidth METHOD
	
	/**
	 * Return the texture of the font
	 * @return The texture of the font
	 */
	public Texture getTexture() {
		
		return this.texture;
	} // END OF getTexture METHOD
	
	/**
	 * Return information about a given character
	 * @param c The given character
	 * @return A CharInfo object representing information about the character
	 */
	public CharInfo getCharInfo(char c) {
		
		return charMap.get(c);
	} // END OF getCharInfo METHOD
	
	/**
	 * Class that stores information about a character
	 * @author Alexandru Blinda
	 *
	 */
	public static class CharInfo {
		
		private final int startX;
		
		private final int width;
		
		/**
		 * Create a CharInfo object with the given startX and width
		 * @param startX The starting position of a character in the texture
		 * @param width The width of the character
		 */
		public CharInfo(int startX, int width) {
			
			this.startX = startX;
			this.width = width;
		} // END OF CONSTRUCTOR
		
		/**
		 * Get the starting position of the character in the texture
		 * @return The starting position of the character
		 */
		public int getStartX() {
			
			return startX;
		} // END OF getStartX METHOD
		
		/**
		 * Get the width of the character
		 * @return The width of the character
		 */
		public int getWidth() {
			
			return width;
		} // END OF getWidth METHOD
		
	} // END OF CharInfo CLASS
	
} // END OF FontTexture CLASS
