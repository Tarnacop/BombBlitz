package bomber.renderer.shaders;

import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

// Wrapper for VBO
public class VertexBufferObject {
	
	private final int id;
	
	public VertexBufferObject() {
		
		id = glGenBuffers();
	} // END OF CONSTRUCTOR
	
	public void bind(int target) {
		
		glBindBuffer(target, id);
	} // END OF bind METHOD
	
	public void unbind(int target) {
		
		glBindBuffer(target, 0);
	} // END OF unbind METHOD
 
	public void uploadData(int target, FloatBuffer data, int usage) {
		
		glBufferData(target, data, usage);
	} // END OF uploadData METHOD
	
	public void uploadData(int target, IntBuffer data, int usage) {
		
		glBufferData(target, data, usage);
	} // END OF uploadData METHOD
	
	public void dispose() {
		
		glDeleteBuffers(id);
	} // END OF dispose METHOD
	
	public int getVboId() {
		
		return id;
	} // END OF getVboId METHOD
} // END OF VertexBufferObject METHOD
