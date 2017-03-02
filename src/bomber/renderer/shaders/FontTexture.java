package bomber.renderer.shaders;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

public class FontTexture {

	private final Font font;
	private final String charSetName;
	private final HashMap<Character, CharInfo> charMap;
	
	private int width;
	private int height;
	
	private Texture texture;
	
	public FontTexture(Font font, String charSetName) throws Exception {
		
		this.font = font;
		this.charSetName = charSetName;
		
		charMap = new HashMap<>();
		
		buildTexture();
	} // END OF CONSTRUCTOR
	
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
	        ImageIO.write(img, "png", out);
	        out.flush();
	        is = new ByteArrayInputStream(out.toByteArray());
	    }
	    
	    byte[] bytes = IOUtils.toByteArray(is);
	    
		texture = new Texture(width, height, ByteBuffer.wrap(bytes));
	} // END OF buildTexture METHOD
	
	public int getHeight() {
		
		return this.height;
	} // END OF getHeight METHOD
	
	public int getWidth() {
		
		return this.width;
	} // END OF getWidth METHOD
	
	public Texture getTexture() {
		
		return this.texture;
	} // END OF getTexture METHOD
	
	public CharInfo getCharInfo(char c) {
		
		return charMap.get(c);
	} // END OF getCharInfo METHOD
	public static class CharInfo {
		
		private final int startX;
		
		private final int width;
		
		public CharInfo(int startX, int width) {
			
			this.startX = startX;
			this.width = width;
		} // END OF CONSTRUCTOR
		
		public int getStartX() {
			
			return startX;
		} // END OF getStartX METHOD
		
		public int getWidth() {
			
			return width;
		} // END OF getWidth METHOD
		
	} // END OF CharInfo CLASS
	
} // END OF FontTexture CLASS
