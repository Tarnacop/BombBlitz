package bomber.renderer.shaders;

import static org.lwjgl.opengl.GL15.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Wrapper class for a Vertex Buffer Object
 * 
 * @author Alexandru Blinda
 *
 */
public class VertexBufferObject {

	private final int id;

	/**
	 * Create a Vertex Buffer Object
	 */
	public VertexBufferObject() {

		id = glGenBuffers();
	}

	/**
	 * Bind the Vertex Buffer Object to the given target
	 * 
	 * @param target
	 *            The given target
	 */
	public void bind(int target) {

		glBindBuffer(target, id);
	}

	/**
	 * Unbind the Vertex Buffer Object from the given target
	 * 
	 * @param target
	 *            The given target
	 */
	public void unbind(int target) {

		glBindBuffer(target, 0);
	}

	/**
	 * Upload the given float data with the given target and the given usage
	 * 
	 * @param target
	 *            The given target
	 * @param data
	 *            The given float data buffer
	 * @param usage
	 *            The given usage
	 */
	public void uploadData(int target, FloatBuffer data, int usage) {

		glBufferData(target, data, usage);
	}

	/**
	 * Upload the given integer data with the given target and the given usage
	 * 
	 * @param target
	 *            The given target
	 * @param data
	 *            The given integer data buffer
	 * @param usage
	 *            The given usage
	 */
	public void uploadData(int target, IntBuffer data, int usage) {

		glBufferData(target, data, usage);
	}

	/**
	 * Dispose the resources
	 */
	public void dispose() {

		glDeleteBuffers(id);
	}

	/**
	 * Get the id of the Vertex Buffer Object
	 * 
	 * @return The id of the Vertex Buffer Object
	 */
	public int getVboId() {

		return id;
	}
}