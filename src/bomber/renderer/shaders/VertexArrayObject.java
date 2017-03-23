package bomber.renderer.shaders;

import static org.lwjgl.opengl.GL30.*;

/**
 * Wrapper class for a Vertex Array Object
 * 
 * @author Alexandru Blinda
 *
 */
public class VertexArrayObject {

	private final int id;

	/**
	 * Create a Vertex Array Object
	 */
	public VertexArrayObject() {

		id = glGenVertexArrays();
	}

	/**
	 * Bind the Vertex Array Object
	 */
	public void bind() {

		glBindVertexArray(id);
	}

	/**
	 * Unbind the Vertex Array Object
	 */
	public void unbind() {

		glBindVertexArray(0);
	}

	/**
	 * Dispose the Vertex Array Object
	 */
	public void dispose() {

		glDeleteVertexArrays(id);
	}

	/**
	 * Return the id of the Vertex Array Object
	 * 
	 * @return The id of the Vertex Array Object
	 */
	public int getVaoId() {

		return id;
	}
}