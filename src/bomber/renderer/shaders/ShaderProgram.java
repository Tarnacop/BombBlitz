package bomber.renderer.shaders;

import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

/**
 * Class that creates a Shader Program
 * 
 * @author Alexandru
 *
 */
public class ShaderProgram {

	private final int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	private final Map<String, Integer> uniforms;

	/**
	 * Create a ShaderProgram object
	 * 
	 * @throws Exception
	 */
	public ShaderProgram() throws Exception {

		programID = glCreateProgram();

		if (programID == 0) {
			throw new Exception("Could not create Shader");
		}

		uniforms = new HashMap<>();

	}

	/**
	 * Create a Vertex Shader with the given shader code
	 * 
	 * @param shaderCode
	 *            The given shader code used for creating the vertex shader
	 * @throws Exception
	 */
	public void createVertexShader(String shaderCode) throws Exception {

		vertexShaderID = createShader(shaderCode, GL_VERTEX_SHADER);
	}

	/**
	 * Create a Fragment Shader wit the given shader code
	 * 
	 * @param shaderCode
	 *            The given shader code used for creating a fragment shader
	 * @throws Exception
	 */
	public void createFragmentShader(String shaderCode) throws Exception {

		fragmentShaderID = createShader(shaderCode, GL_FRAGMENT_SHADER);
	}

	/**
	 * Create a shader given the shader code and shader type
	 * 
	 * @param shaderCode
	 *            The given shader code
	 * @param shaderType
	 *            The given shader type
	 * @return The shader id
	 * @throws Exception
	 */
	private int createShader(String shaderCode, int shaderType) throws Exception {

		int shaderID = glCreateShader(shaderType);

		if (shaderID == 0) {

			throw new Exception("ERROR CREATING SHADER OF TYPE:" + shaderType);
		}

		glShaderSource(shaderID, shaderCode);
		glCompileShader(shaderID);

		glAttachShader(programID, shaderID);

		return shaderID;
	}

	/**
	 * Method to link the shaders to the program
	 * 
	 * @throws Exception
	 */
	public void link() throws Exception {

		glLinkProgram(programID);

		if (glGetProgrami(programID, GL_LINK_STATUS) == 0) {

			throw new Exception("Error linking shader code:" + glGetProgramInfoLog(programID, 1024));
		}

		if (vertexShaderID != 0) {

			glDetachShader(programID, vertexShaderID);
		}

		if (fragmentShaderID != 0) {

			glDetachShader(programID, fragmentShaderID);
		}

		glValidateProgram(programID);

		if (glGetProgrami(programID, GL_VALIDATE_STATUS) == 0) {

			System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programID, 1024));
		}

	}

	/**
	 * Bind openGL to the program
	 */
	public void bind() {

		glUseProgram(programID);
	}

	/**
	 * Unbind openGL from the program
	 */
	public void unbind() {

		glUseProgram(0);
	}

	/**
	 * Create a uniform with the given uniform name
	 * 
	 * @param uniformName
	 *            The given uniform name
	 * @throws Exception
	 */
	public void createUniform(String uniformName) throws Exception {

		int location = glGetUniformLocation(programID, uniformName);

		if (location < 0) {

			throw new Exception("Uniform could not be found: " + uniformName);
		}

		uniforms.put(uniformName, location);
	}

	/**
	 * Set a uniform with a given name to a given matrix value
	 * 
	 * @param uniformName
	 *            The given name of the uniform
	 * @param matrix
	 *            The value as a matrix
	 */
	public void setUniform(String uniformName, Matrix4f matrix) {

		try (MemoryStack stack = MemoryStack.stackPush()) {

			FloatBuffer dataBuffer = stack.mallocFloat(16);
			matrix.get(dataBuffer);
			glUniformMatrix4fv(uniforms.get(uniformName), false, dataBuffer);
		}
	}

	/**
	 * Set a uniform with a given name to a given integer value
	 * 
	 * @param uniformName
	 *            The given name of the uniform
	 * @param matrix
	 *            The value as an integer
	 */
	public void setUniform(String uniformName, int value) {

		glUniform1i(uniforms.get(uniformName), value);
	}

	/**
	 * Set a uniform with the given name to the given float values
	 * 
	 * @param uniformName
	 *            The given name of the uniform
	 * @param x
	 *            The x value
	 * @param y
	 *            The y value
	 * @param z
	 *            The z value
	 */
	public void setUniform(String uniformName, float x, float y, float z) {

		glUniform3f(uniforms.get(uniformName), x, y, z);
	}

	/**
	 * Dispose the resources
	 */
	public void dispose() {

		unbind();
		if (programID != 0) {

			glDeleteProgram(programID);
		}
	}
}