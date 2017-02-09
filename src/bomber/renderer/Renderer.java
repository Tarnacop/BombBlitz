package bomber.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import bomber.renderer.interfaces.RendererInterface;
import bomber.renderer.interfaces.ScreenInterface;
import bomber.renderer.shaders.ShaderProgram;
import bomber.renderer.utils.FileHandler;

public class Renderer implements RendererInterface {

	private ShaderProgram shaderConstructor;

	private int vaoID;
	private int vboID;

	@Override
	public void init() throws Exception {

		shaderConstructor = new ShaderProgram();
		shaderConstructor.createVertexShader(FileHandler.loadResource("res/vertex.vs"));
		shaderConstructor.createFragmentShader(FileHandler.loadResource("res/fragment.fs"));
		shaderConstructor.link();

		// Coords of a triangle
		float[] vertices = new float[] { 0.0f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f };

		// Allocate memory for a float buffer
		FloatBuffer verticesBuffer = null;
		try {
			verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);

			// Store the data and flip the buffer to 0
			verticesBuffer.put(vertices).flip();

			// Create a VAO and bind it
			vaoID = glGenVertexArrays();
			glBindVertexArray(vaoID);

			// Create a VBO and bind it
			vboID = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vboID);
			glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

			glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

			// Unbind the VBO
			glBindBuffer(GL_ARRAY_BUFFER, 0);

			// Unbind the VAO
			glBindVertexArray(0);

		} catch (Exception ex) {

			System.err.println("ERROR: " + ex.getStackTrace());
		} finally {

			if (verticesBuffer != null) {
				MemoryUtil.memFree(verticesBuffer);
			}
		}

	} // END OF init METHOD

	public void render(ScreenInterface screen) {
		
		clear();

		// Resize the screen if it needs to be resized
		if (screen.isResized()) {
			
			screen.setViewport(0, 0, screen.getWidth(), screen.getHeight());
			screen.setResized(false);
		}
		
		// Bind the shader
		shaderConstructor.bind();
		
		// Bind the VAO
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		
		// Draw the vertices
		glDrawArrays(GL_TRIANGLES, 0, 3);
		
		// Disable the VAO and unbind it
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		
		// Unbind the shader
		shaderConstructor.unbind();
		
	} // END OF render METHOD

	// Clear the screen - TODO REMOVE IT IF IT IS USED ONLY IN RENDER
	@Override
	public void clear() {

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	} // END OF clear METHOD

	@Override
	public void dispose() {
		
		if(shaderConstructor != null) {
			
			shaderConstructor.dispose();
		}
		
		glDisableVertexAttribArray(0);
		
		// Delete the VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboID);
		
		// Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoID);
		
	} // END OF dispose METHOD
} // END OF Renderer CLASS
