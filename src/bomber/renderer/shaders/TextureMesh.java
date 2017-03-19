package bomber.renderer.shaders;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

// TextureMesh
/**
 * Class that represents a TextureMesh handles the Texture rendering
 * @author Alexandru Blinda
 *
 */
public class TextureMesh {

	private final VertexArrayObject vao;
	private final VertexBufferObject vbopos;
	private final VertexBufferObject vbotexture;
	private final VertexBufferObject vboindices;
	private final int vertexCount;
	private final Texture texture;

	/**
	 * Create a TextureMesh with a width, height, x and y texture coordinates, colsNumber and rowsNumber and a Texture
	 * This constructor is used especially for sprite sheet to draw exactly the portion of the texture needed
	 * @param width The width of the drawn "object"
	 * @param height The height of the drawn "object"
	 * @param textX The starting x texture coordinate
	 * @param textY The starting y texture coordinate
	 * @param colsNumber The number of columns of the sprite sheet
	 * @param rowsNumber The number of rows of the sprite sheet
	 * @param texture The sprite sheet given as a texture
	 */
	public TextureMesh(float width, float height, float textX, float textY, float textWidth, float textHeight, Texture texture) {
		
		int[] indices = new int[] { 0, 1, 3, 3, 1, 2, };
		float[] positions = {
				// V0
				0f, height,
				// V1
				0f, 0f,
				// V2
				width, 0f,
				// V3
				width, height
		};

		float[] textureCoords = new float[] {
				
				// For V0
				textX, textY + textHeight,
				// For V1
				textX, textY,
				// For V2
				textX + textWidth, textY,
				// FOR V3
				textX + textWidth, textY + textHeight
		};
		
		vao = new VertexArrayObject();
		vbopos = new VertexBufferObject();
		vbotexture = new VertexBufferObject();
		vboindices = new VertexBufferObject();
		this.texture = texture;

		// Allocate memory for a float buffer
		FloatBuffer positionsBuffer = null;
		FloatBuffer textureBuffer = null;
		IntBuffer indicesBuffer = null;

		try {
			positionsBuffer = MemoryUtil.memAllocFloat(positions.length);
			textureBuffer = MemoryUtil.memAllocFloat(textureCoords.length);
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);

			vertexCount = indices.length;

			// Store the data and flip the buffer to 0
			positionsBuffer.put(positions).flip();
			textureBuffer.put(textureCoords).flip();
			indicesBuffer.put(indices).flip();

			// Create a VAO and bind it
			vao.bind();

			// Create the positions VBO and bind it
			vbopos.bind(GL_ARRAY_BUFFER);
			vbopos.uploadData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
			// Show where the VBO's array buffers locations are in the shader
			glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

			// Create the colour VBO and bind it
			vbotexture.bind(GL_ARRAY_BUFFER);
			vbotexture.uploadData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

			// Create the indices VBO and bind it
			vboindices.bind(GL_ELEMENT_ARRAY_BUFFER);
			vboindices.uploadData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

			// Unbind the VAO
			vao.unbind();

		} finally {

			if (positionsBuffer != null) {

				MemoryUtil.memFree(positionsBuffer);
			}

			if (textureBuffer != null) {

				MemoryUtil.memFree(textureBuffer);
			}

			if (indicesBuffer != null) {

				MemoryUtil.memFree(indicesBuffer);
			}
		}
		
	} // END OF CONSTRUCTOR
	
	/**
	 * Create a TextureMesh with a width, height and a texture
	 * @param width The given width of the "object"
	 * @param height The given height of the "object"
	 * @param texture The given texture to be drawn
	 */
	public TextureMesh(float width, float height, Texture texture) {
		
		int[] indices = new int[] { 0, 1, 3, 3, 1, 2, };
		float[] positions = {
				// V0
				0f, height,
				// V1
				0f, 0f,
				// V2
				width, 0f,
				// V3
				width, height
		};

		float[] textureCoords = new float[] {
				
				// For V0
				0f, 1f,
				// For V1
				0f, 0f,
				// For V2
				1f, 0f,
				// FOR V3
				1f, 1f
		};
		
		vao = new VertexArrayObject();
		vbopos = new VertexBufferObject();
		vbotexture = new VertexBufferObject();
		vboindices = new VertexBufferObject();
		this.texture = texture;

		// Allocate memory for a float buffer
		FloatBuffer positionsBuffer = null;
		FloatBuffer textureBuffer = null;
		IntBuffer indicesBuffer = null;

		try {
			positionsBuffer = MemoryUtil.memAllocFloat(positions.length);
			textureBuffer = MemoryUtil.memAllocFloat(textureCoords.length);
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);

			vertexCount = indices.length;

			// Store the data and flip the buffer to 0
			positionsBuffer.put(positions).flip();
			textureBuffer.put(textureCoords).flip();
			indicesBuffer.put(indices).flip();

			// Create a VAO and bind it
			vao.bind();

			// Create the positions VBO and bind it
			vbopos.bind(GL_ARRAY_BUFFER);
			vbopos.uploadData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
			// Show where the VBO's array buffers locations are in the shader
			glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

			// Create the colour VBO and bind it
			vbotexture.bind(GL_ARRAY_BUFFER);
			vbotexture.uploadData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

			// Create the indices VBO and bind it
			vboindices.bind(GL_ELEMENT_ARRAY_BUFFER);
			vboindices.uploadData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

			// Unbind the VAO
			vao.unbind();

		} finally {

			if (positionsBuffer != null) {

				MemoryUtil.memFree(positionsBuffer);
			}

			if (textureBuffer != null) {

				MemoryUtil.memFree(textureBuffer);
			}

			if (indicesBuffer != null) {

				MemoryUtil.memFree(indicesBuffer);
			}
		}
		
	} // END OF CONSTRUCTOR
	
	/**
	 * Create a TextureMesh with the positions of the vertexes, the texture coordinates and the indices and a given texture
	 * Used only in special cases
	 * @param positions A float array representing the given positions of each vertex
	 * @param textureCoords A float array representing the given texture coordinates
	 * @param indices An int array representing the indices of the vertexes
	 * @param texture The texture to be drawn
	 */
	public TextureMesh(float[] positions, float[] textureCoords, int[] indices, Texture texture) {

		vao = new VertexArrayObject();
		vbopos = new VertexBufferObject();
		vbotexture = new VertexBufferObject();
		vboindices = new VertexBufferObject();
		this.texture = texture;

		// Allocate memory for a float buffer
		FloatBuffer positionsBuffer = null;
		FloatBuffer textureBuffer = null;
		IntBuffer indicesBuffer = null;

		try {
			positionsBuffer = MemoryUtil.memAllocFloat(positions.length);
			textureBuffer = MemoryUtil.memAllocFloat(textureCoords.length);
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);

			vertexCount = indices.length;

			// Store the data and flip the buffer to 0
			positionsBuffer.put(positions).flip();
			textureBuffer.put(textureCoords).flip();
			indicesBuffer.put(indices).flip();

			// Create a VAO and bind it
			vao.bind();

			// Create the positions VBO and bind it
			vbopos.bind(GL_ARRAY_BUFFER);
			vbopos.uploadData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
			// Show where the VBO's array buffers locations are in the shader
			glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

			// Create the colour VBO and bind it
			vbotexture.bind(GL_ARRAY_BUFFER);
			vbotexture.uploadData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

			// Create the indices VBO and bind it
			vboindices.bind(GL_ELEMENT_ARRAY_BUFFER);
			vboindices.uploadData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

			// Unbind the VAO
			vao.unbind();

		} finally {

			if (positionsBuffer != null) {

				MemoryUtil.memFree(positionsBuffer);
			}

			if (textureBuffer != null) {

				MemoryUtil.memFree(textureBuffer);
			}

			if (indicesBuffer != null) {

				MemoryUtil.memFree(indicesBuffer);
			}
		}
	} // END OF CONSTRUCTOR

	/**
	 * Get the id of the vertex array object
	 * @return The id of the vertex array object
	 */
	public int getVaoId() {

		return vao.getVaoId();
	} // END OF getVaoId METHOD

	/**
	 * Get the number of vertexes
	 * @return The number of vertexes
	 */
	public int getVertexCount() {

		return vertexCount;
	} // END OF getVertexCount METHOD

	/**
	 * Render the mesh on the screen
	 */
	// Render the mesh
	public void render() {
		
		// Activate first texture unit
		glActiveTexture(GL_TEXTURE0);
		// Bind the texture
		glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
		// Draw the mesh
		// Bind the VAO
		glBindVertexArray(getVaoId());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		// Draw the vertices
		glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

		// Disable the VAO and unbind it
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);

	} // END OF render METHOD

	/** 
	 * Dispose the mesh
	 */
	public void dispose() {

		glDisableVertexAttribArray(0);

		// Delete the VBO
		vbopos.unbind(GL_ARRAY_BUFFER);
		vbopos.dispose();

		// Delete the colour VBO
		vbotexture.unbind(GL_ARRAY_BUFFER);
		vbotexture.dispose();

		// Delete the indices VBO
		vboindices.unbind(GL_ELEMENT_ARRAY_BUFFER);
		vboindices.dispose();

		// Delete the VAO
		vao.unbind();
		vao.dispose();
		texture.dispose();
	} // END OF dispose METHOD

	/**
	 * Get the texture used in the mesh
	 * @return The texture used in the mesh
 	 */
	public Texture getTexture() {
		
		return texture;
	} // END OF getTexture METHOD
	
	/**
	 * Delete the vertex array object and vertex buffer objects used
	 */
	public void deleteBuffers() {
		
		glDisableVertexAttribArray(0);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		// Delete VBO's
		vbopos.dispose();
		vbotexture.dispose();
		vboindices.dispose();
		
		// Delete the VAO
		glBindVertexArray(0);
		vao.dispose();
	} // END OF deleteBuffers METHOD
	
} // END OF AuxMesh CLASS
