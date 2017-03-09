package bomber.renderer.shaders;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

// Class which holds a text
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
    
    public TextItem(String text, FontTexture fontTexture) throws Exception {
    	
    	pos = new Vector2f(0f, 0f);
		angle = 0f;
		scale = 1f;
		colour = new Vector3f(1f, 1f, 1f);
    	this.text = text;
    	this.fontTexture = fontTexture;
    	this.mesh = buildMesh();
    } // END OF CONSTRUCTOR
    
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
    
    public void setMesh(TextureMesh mesh) {
    	
    	this.mesh = mesh;
    } // END OF setMesh METHOD
    
    public TextureMesh getMesh() {
    	
    	return this.mesh;
    } // END OF getMesh METHOD
    
    public Vector2f getPosition() {
		
		return pos;
	} // END OF getPosition METHOD
	
	public void setPosition(float x, float y) {
		
		pos.x = x;
		pos.y = y;
	} // END OF setPosition METHOD
	
	public float getScale() {
		
		return scale;
	} // END OF getScale METHOD
	
	public void setScale(float scale) {
		
		this.scale = scale;
	} // END OF scale METHOD
	
	public float getRotation() {
		
		return angle;
	} // END OF getRotation METHOD
	
	public void setRotation(float angle) {
		
		this.angle = angle;
	} // END OF setRotation METHOD
	
	public String getText() {
		
		return text;
	} // END OF getText METHOD
	
	public void setText(String text) {
		
		this.text = text;
		this.getMesh().deleteBuffers();
		this.setMesh(buildMesh());
	} // END OF setText METHOD

	public void setColour(Vector3f colour) {
		
		this.colour = colour;
	} // END OF setColour METHOD
	
	public Vector3f getColour() {
		
		return colour;
	} // END OF getColour METHOD
	
	public float getTextWidth() {
		
		return this.stringWidth;
	} // END OF getTextWidth METHOD
	
	public float getTextHeight() {
		
		return this.stringHeight;
	} // END OF getTextWidth METHOD
	
} // END OF TextItem CLASS
