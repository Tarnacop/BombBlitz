package bomber.renderer.shaders;

import static org.lwjgl.opengl.GL30.*;

/**
 * Wrapper class for a Vertex Array Object
 * @author Alexandru Blinda
 *
 */
// Wrapper for VAO
public class VertexArrayObject {

	private final int id;
	
	/**
	 * Create a Vertex Array Object
	 */
	public VertexArrayObject() {
		
		id = glGenVertexArrays();
	} // END OF CONSTRUCTOR
	
	/**
	 * Bind the Vertex Array Object
	 */
	public void bind() {
		
		glBindVertexArray(id);
	} // END OF bind METHOD
	
	/**
	 * Unbind the Vertex Array Object
	 */
	public void unbind() {
		
		glBindVertexArray(0);
	} // END OF unbind METHOD
	
	/**
	 * Dispose the Vertex Array Object
	 */
	public void dispose() {
		
		glDeleteVertexArrays(id);
	} // END OF unbind METHOD
	
	/**
	 * Return the id of the Vertex Array Object
	 * @return The id of the Vertex Array Object
	 */
	public int getVaoId() {
		
		return id;
	} // END OF getVaoId METHOD
} // END OF VertexArrayObject CLASS
