package bomber.renderer.shaders;

import static org.lwjgl.opengl.GL30.*;

// Wrapper for VAO
public class VertexArrayObject {

	private final int id;
	
	public VertexArrayObject() {
		
		id = glGenVertexArrays();
	} // END OF CONSTRUCTOR
	
	public void bind() {
		
		glBindVertexArray(id);
	} // END OF bind METHOD
	
	public void unbind() {
		
		glBindVertexArray(0);
	} // END OF unbind METHOD
	
	public void dispose() {
		
		glDeleteVertexArrays(id);
	} // END OF unbind METHOD
	
	public int getVaoId() {
		
		return id;
	} // END OF getVaoId METHOD
} // END OF VertexArrayObject CLASS
