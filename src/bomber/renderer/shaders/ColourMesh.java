package bomber.renderer.shaders;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

// Class for VAO and VBO usage
public class ColourMesh {

	private final VertexArrayObject vao;
	private final VertexBufferObject vbopos;
	private final VertexBufferObject vbocolour;
	private final VertexBufferObject vboindices;
	private final int vertexCount;

	public ColourMesh(float width, float height, float[] colours) {

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
		vao = new VertexArrayObject();
		vbopos = new VertexBufferObject();
		vbocolour = new VertexBufferObject();
		vboindices = new VertexBufferObject();

		// Allocate memory for a float buffer
		FloatBuffer positionsBuffer = null;
		FloatBuffer colourBuffer = null;
		IntBuffer indicesBuffer = null;

		try {
			positionsBuffer = MemoryUtil.memAllocFloat(positions.length);
			colourBuffer = MemoryUtil.memAllocFloat(colours.length);
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);

			vertexCount = indices.length;

			// Store the data and flip the buffer to 0
			positionsBuffer.put(positions).flip();
			colourBuffer.put(colours).flip();
			indicesBuffer.put(indices).flip();

			// Create a VAO and bind it
			vao.bind();

			// Create the positions VBO and bind it
			vbopos.bind(GL_ARRAY_BUFFER);
			vbopos.uploadData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
			// Show where the VBO's array buffers locations are in the shader
			glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

			// Create the colour VBO and bind it
			vbocolour.bind(GL_ARRAY_BUFFER);
			vbocolour.uploadData(GL_ARRAY_BUFFER, colourBuffer, GL_STATIC_DRAW);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

			// Create the indices VBO and bind it
			vboindices.bind(GL_ELEMENT_ARRAY_BUFFER);
			vboindices.uploadData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

			// Unbind the VAO
			vao.unbind();

		} finally {

			if (positionsBuffer != null) {

				MemoryUtil.memFree(positionsBuffer);
			}

			if (colourBuffer != null) {

				MemoryUtil.memFree(colourBuffer);
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
		vbocolour.unbind(GL_ARRAY_BUFFER);
		vbocolour.dispose();

		// Delete the indices VBO
		vboindices.unbind(GL_ELEMENT_ARRAY_BUFFER);
		vboindices.dispose();

		// Delete the VAO
		vao.unbind();
		vao.dispose();
	} // END OF dispose METHOD

} // END OF Mesh CLASS
