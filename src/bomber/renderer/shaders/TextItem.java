package bomber.renderer.shaders;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Class that builds a TextItem for a text
 * A TextItem stores the mesh and other information about the text
 * @author Alexandru Blinda
 *
 */
//Class which holds a text
public class TextItem {
	
	private static final int VERTICES_PER_QUAD = 4;
   
	private final Vector2f pos;
	private float angle;
	private float scale;
	
	private String text;
	private FontTexture fontTexture;
    private TextureMesh mesh;
    private Vector3f colour;
    
    private float stringWidth;
    private float stringHeight;
    
    /**
     * Create a TextItem object that stores the given text and the texture for that text
     * @param text The given text
     * @param fontTexture The given texture for that text with that font
     * @throws Exception
     */
    public TextItem(String text, FontTexture fontTexture) throws Exception {
    	
    	pos = new Vector2f(0f, 0f);
		angle = 0f;
		scale = 1f;
		colour = new Vector3f(1f, 1f, 1f);
    	this.text = text;
    	this.fontTexture = fontTexture;
    	this.mesh = buildMesh();
    } // END OF CONSTRUCTOR
    
    /**
     * Build the Mesh for the given text
     * @return The Mesh for the given text
     */
    private TextureMesh buildMesh() {
    	    	
        ArrayList<Float> positions = new ArrayList<Float>();
        ArrayList<Float> textCoords = new ArrayList<Float>();
        
        ArrayList<Integer> indices = new ArrayList<Integer>();
        
        char[] characters = text.toCharArray();
        int numChars = characters.length;

        
        float startX = 0;
        // Create the positions, textureCoords and indices
        for(int i = 0; i < numChars; i++) {
        	
        	FontTexture.CharInfo charInfo = fontTexture.getCharInfo(characters[i]);
        	// Build a character tile composed of 2 triangles ( => a quad)
        	
        	// Left top vertex
        	positions.add(startX); // x coord
        	positions.add(0.0f); // y coord
        	textCoords.add((float)charInfo.getStartX() / (float)fontTexture.getWidth());
        	textCoords.add(0.0f);
        	indices.add(i*VERTICES_PER_QUAD);
        	
        	// Left bottom vertex
        	positions.add(startX); // x coord
        	positions.add((float)fontTexture.getHeight()); // y coord
        	textCoords.add((float)charInfo.getStartX() / (float)fontTexture.getWidth());
        	textCoords.add(1.0f);
        	indices.add(i*VERTICES_PER_QUAD +  1);
        	
        	// Right bottom vertex
        	positions.add(startX + charInfo.getWidth());
        	positions.add((float)fontTexture.getHeight());
        	textCoords.add((float)(charInfo.getStartX() + charInfo.getWidth() )/ (float)fontTexture.getWidth());
        	textCoords.add(1.0f);
        	indices.add(i*VERTICES_PER_QUAD + 2);
        	
        	// Right top vertex
        	positions.add(startX + charInfo.getWidth());
        	positions.add(0.0f);
        	textCoords.add((float)(charInfo.getStartX() + charInfo.getWidth() )/ (float)fontTexture.getWidth());
        	textCoords.add(0.0f);
        	indices.add(i*VERTICES_PER_QUAD + 3);
        	
            // Add indices por left top and bottom right vertices
            indices.add(i*VERTICES_PER_QUAD);
            indices.add(i*VERTICES_PER_QUAD + 2);
            
            startX += charInfo.getWidth();
        }
        
        stringWidth = startX;
        stringHeight = (float) fontTexture.getHeight();
        
    	float[] positionsArray = new float[positions.size()];
    	for(int i = 0; i < positionsArray.length; i++) {
    		
    		positionsArray[i] = positions.get(i);
    	}
    	
    	float[] textCoordsArray = new float[textCoords.size()];
    	for(int i = 0; i < textCoordsArray.length; i++) {
    		
    		textCoordsArray[i] = textCoords.get(i);
    	}
    	
    	int[] indicesArray = new int[indices.size()];
    	for(int i = 0; i < indicesArray.length; i++) {
    		
    		indicesArray[i] = indices.get(i);
    	}
    	
    	return new TextureMesh(positionsArray, textCoordsArray, indicesArray, fontTexture.getTexture());
    } // END OF buildMesh METHOD 
    
    /**
     * Set the mesh of the text to a given mesh
     * @param mesh The given mesh
     */
    public void setMesh(TextureMesh mesh) {
    	
    	this.mesh = mesh;
    } // END OF setMesh METHOD
    
    /**
     * Get the mesh of the text
     * @return The mesh of the text
     */
    public TextureMesh getMesh() {
    	
    	return this.mesh;
    } // END OF getMesh METHOD
    
    /**
     * Get the position of the text item on the screen
     * @return A Vector2f representing the position (x, y) on the screen
     */
    public Vector2f getPosition() {
		
		return pos;
	} // END OF getPosition METHOD
	
    /**
     * Set the position of the text to the given x, y coordinates
     * @param x The x coordinate
     * @param y The y coordinate
     */
	public void setPosition(float x, float y) {
		
		pos.x = x;
		pos.y = y;
	} // END OF setPosition METHOD
	
	/**
	 * Get the scale of the text
	 * @return The scale of the text
	 */
	public float getScale() {
		
		return scale;
	} // END OF getScale METHOD
	
	/**
	 * Set the scale of the text to a new scale
	 * @param scale The given scale
	 */
	public void setScale(float scale) {
		
		this.scale = scale;
	} // END OF scale METHOD
	
	/**
	 * Get the rotation angle of the text item
	 * @return The rotation angle of the text item
	 */
	public float getRotation() {
		
		return angle;
	} // END OF getRotation METHOD
	
	/**
	 * Set the rotation of the text to a given angle
	 * @param angle The given angle
	 */
	public void setRotation(float angle) {
		
		this.angle = angle;
	} // END OF setRotation METHOD
	
	/**
	 * Get the text stored by the TextItem object
	 * @return The text stored in the TextItem object
	 */
	public String getText() {
		
		return text;
	} // END OF getText METHOD
	
	/**
	 * Set the text of the TextItem to the given text
	 * @param text The new text
	 */
	public void setText(String text) {
		
		this.text = text;
		this.getMesh().deleteBuffers();
		this.setMesh(buildMesh());
	} // END OF setText METHOD

	/**
	 * Set the colour of the text to a given colour
	 * @param colour The given colour represented as a Vector3f
	 */
	public void setColour(Vector3f colour) {
		
		this.colour = colour;
	} // END OF setColour METHOD
	
	/**
	 * Get the colour of the text
	 * @return The colour of the text
	 */
	public Vector3f getColour() {
		
		return colour;
	} // END OF getColour METHOD
	
	/**
	 * Get the width of the texture of the text
	 * @return The width of the texture of the text
	 */
	public float getTextWidth() {
		
		return this.stringWidth;
	} // END OF getTextWidth METHOD
	
	/**
	 * Get the height of the texture of the text
	 * @return The height of the texture of the text
	 */
	public float getTextHeight() {
		
		return this.stringHeight;
	} // END OF getTextWidth METHOD
	
} // END OF TextItem CLASS
