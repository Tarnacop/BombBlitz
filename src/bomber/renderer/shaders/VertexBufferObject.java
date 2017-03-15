package bomber.renderer.shaders;

import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Wrapper class for a Vertex Buffer Object
 * @author Alexandru
 *
 */
// Wrapper for VBO
public class VertexBufferObject {
	
	private final int id;
	
	/**
	 * Create a Vertex Buffer Object
	 */
	public VertexBufferObject() {
		
		id = glGenBuffers();
	} // END OF CONSTRUCTOR
	
	/**
	 * Bind the Vertex Buffer Object to the target with the given id
	 * @param target The id of the target
	 */
	public void bind(int target) {
		
		glBindBuffer(target, id);
	} // END OF bind METHOD
	
	/**
	 * Unbind the Vertex Buffer Object from the target with the given id
	 * @param target The id of the target
	 */
	public void unbind(int target) {
		
		glBindBuffer(target, 0);
	} // END OF unbind METHOD
 
	/**
	 * Upload the given float data with the given target and the given usage into the VBO
	 * @param target The id of the target
	 * @param data The float data to be uploaded
	 * @param usage The usage type
	 */
	public void uploadData(int target, FloatBuffer data, int usage) {
		
		glBufferData(target, data, usage);
	} // END OF uploadData METHOD
	
	/**
	 * Upload the given int data with the given target and the given usage into the VBO
	 * @param target The id of the target
	 * @param data The int data to be uploaded
	 * @param usage The usage type
	 */
	public void uploadData(int target, IntBuffer data, int usage) {
		
		glBufferData(target, data, usage);
	} // END OF uploadData METHOD
	
	/**
	 * Dispose the Vertex Buffer Object 
	 */
	public void dispose() {
		
		glDeleteBuffers(id);
	} // END OF dispose METHOD
	
	/**
	 * Get the id of the Vertex Buffer Object
	 * @return The id of the Vertex Buffer Object
	 */
	public int getVboId() {
		
		return id;
	} // END OF getVboId METHOD
} // END OF VertexBufferObject METHOD
