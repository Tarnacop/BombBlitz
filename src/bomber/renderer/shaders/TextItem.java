package bomber.renderer.shaders;

import java.nio.charset.Charset;
import java.util.ArrayList;

import org.joml.Vector2f;

// Class which holds a text
public class TextItem {
	
	private static final int VERTICES_PER_QUAD = 4;
   
	private final Vector2f pos;
	private float angle;
	private float scale;
	
	private String text;
	
    private final int numCols;
    private final int numRows;
    private AuxMesh mesh;
    
    public TextItem(String text, String fontFileName, int numCols, int numRows) {
    	
    	pos = new Vector2f(0f, 0f);
		angle = 0f;
		scale = 1f;
    	this.text = text;
    	this.numCols = numCols;
    	Texture texture = new Texture(fontFileName);
    	this.numRows = numRows;
    	this.mesh = buildMesh(texture, numCols, numRows);
    } // END OF CONSTRUCTOR
    
    private AuxMesh buildMesh(Texture texture, int numCols, int numRows) {
    	
    	byte[] chars = text.getBytes(Charset.forName("ISO-8859-1"));
    	int numChars = chars.length;
    	
        ArrayList<Float> positions = new ArrayList<Float>();
        ArrayList<Float> textCoords = new ArrayList<Float>();
        
        ArrayList<Integer> indices = new ArrayList<Integer>();
        
        float tileWidth = (float) texture.getWidth()/(float) numCols;
        float tileHeight = (float) texture.getHeight()/(float) numRows;
        
        // Create the positions, textureCoords and indices
        for(int i = 0; i < numChars; i++) {
        	
        	byte currChar = chars[i];
        	int col = currChar % numCols;
        	int row = currChar / numRows;
        	
        	// Build a character tile composed of 2 triangles ( => a quad)
        	
        	// Left top vertex
        	positions.add((float)i*tileWidth); // x coord
        	positions.add(0.0f); // y coord
        	textCoords.add((float) col / (float) numCols);
        	textCoords.add((float) row / (float) numRows);
        	indices.add(i*VERTICES_PER_QUAD);
        	
        	// Left bottom vertex
        	positions.add((float)i * tileWidth); // x coord
        	positions.add(tileHeight); // y coord
        	textCoords.add((float) col / (float) numCols);
        	textCoords.add((float) (row+1) / (float) numRows);
        	indices.add(i*VERTICES_PER_QUAD +  1);
        	
        	// Right bottom vertex
        	positions.add((float)(i + 1)*tileWidth);
        	positions.add(tileHeight);
        	textCoords.add((float) (col + 1) / (float) numCols);
        	textCoords.add((float) (row + 1) / (float) numRows);
        	indices.add(i*VERTICES_PER_QUAD + 2);
        	
        	// Right top vertex
        	positions.add((float)i*tileWidth + tileWidth);
        	positions.add(0.0f);
        	textCoords.add((float) (col + 1) / (float) numCols);
        	textCoords.add((float) row / (float) numRows);
        	indices.add(i*VERTICES_PER_QUAD + 3);
        	
            // Add indices por left top and bottom right vertices
            indices.add(i*VERTICES_PER_QUAD);
            indices.add(i*VERTICES_PER_QUAD + 2);
        }
        
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
    	
    	return new AuxMesh(positionsArray, textCoordsArray, indicesArray, texture);
    } // END OF buildMesh METHOD 
    
    public void setMesh(AuxMesh mesh) {
    	
    	this.mesh = mesh;
    } // END OF setMesh METHOD
    
    public AuxMesh getMesh() {
    	
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
		Texture texture = this.getMesh().getTexture();
		this.getMesh().deleteBuffers();
		this.setMesh(buildMesh(texture, numCols, numRows));
	} // END OF setText METHOD

} // END OF TextItem CLASS
