package bomber.renderer.shaders;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

// Aux Mesh to work with textures - TODO to be merged with Mesh after we find textures
public class TextureMesh {

	private final VertexArrayObject vao;
	private final VertexBufferObject vbopos;
	private final VertexBufferObject vbotexture;
	private final VertexBufferObject vboindices;
	private final int vertexCount;
	private final Texture texture;

	public TextureMesh(float width, float height, float textX, float textY, float colsNumber, float rowsNumber, Texture texture) {
		
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
				textX, textY + (1f / rowsNumber),
				// For V1
				textX, textY,
				// For V2
				textX + (1f / colsNumber), textY,
				// FOR V3
				textX + (1f / colsNumber), textY + (1f / rowsNumber)
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

	public int getVaoId() {

		return vao.getVaoId();
	} // END OF getVaoId METHOD

	public int getVertexCount() {

		return vertexCount;
	} // END OF getVertexCount METHOD

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

	public Texture getTexture() {
		
		return texture;
	} // END OF getTexture METHOD
	
	public void deleteBuffers() {
		
		glDisableVertexAttribArray(0);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		// Delete VBO's
		glDeleteBuffers(vbopos.getVboId());
		glDeleteBuffers(vbotexture.getVboId());
		glDeleteBuffers(vboindices.getVboId());
		
		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vao.getVaoId());
	} // END OF deleteBuffers METHOD
	
} // END OF AuxMesh CLASS
