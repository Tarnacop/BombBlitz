package bomber.renderer.shaders;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

	private final int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	public ShaderProgram() throws Exception {
		
		programID = glCreateProgram();
		
		if(programID == 0) {
			throw new Exception("Could not create Shader");
		}
		
	} // END OF CONSTRUCTOR
	
	// Method to create a vertex shader
	public void createVertexShader(String shaderCode) throws Exception {
		
		vertexShaderID = createShader(shaderCode, GL_VERTEX_SHADER);
	} // END OF createVertexShader METHOD
	
	// Method to create a fragment shader
	public void createFragmentShader(String shaderCode) throws Exception {
		
		fragmentShaderID = createShader(shaderCode, GL_FRAGMENT_SHADER);
 	} // END OF createFragmentShader METHOD
	
	// Method to create the shaders given the shader code and type
	private int createShader(String shaderCode, int shaderType) throws Exception {
		
		int shaderID = glCreateShader(shaderType);
		
		if(shaderID == 0) {
			
			throw new Exception("ERROR CREATING SHADER OF TYPE:" + shaderType);
		}
		
		glShaderSource(shaderID, shaderCode);
		glCompileShader(shaderID);
		
		
		// Only for debugging ---------------------------- TODO REMOVE LATER --------------
		if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0) {
			
			throw new Exception("Error compiling shader code:" + glGetShaderInfoLog(shaderID, 1024));
		}
		// --------------------------------------------------------------------------------
		glAttachShader(programID, shaderID);
		
		return shaderID;
	} // END OF createShader METHOD
	
	// Method to link the shaders to the program
	public void link() throws Exception {
		
		glLinkProgram(programID);
		
		if(glGetProgrami(programID, GL_LINK_STATUS) == 0) {
			
			throw new Exception("Error linking shader code:" + glGetProgramInfoLog(programID, 1024));
		}
		
		if(vertexShaderID != 0) {
			
			glDetachShader(programID, vertexShaderID);
		}
		
		if(fragmentShaderID != 0) {
			
			glDetachShader(programID, fragmentShaderID);
		}
		
		glValidateProgram(programID);
		
		if(glGetProgrami(programID, GL_VALIDATE_STATUS) == 0) {
			
			System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programID, 1024));
		}
		
	} // END OF link METHOD
	
	// Method to bind openGL to the program
	public void bind() {
		
		glUseProgram(programID);
	} // END OF bind METHOD
	
	// Method to unbind openGL to the program
	public void unbind() {
		
		glUseProgram(0);
	} // END OF unbind METHOD
	
	// Method to dispose the resources
	public void dispose() {
		
		unbind();
		if(programID != 0) {
			
			glDeleteProgram(programID);
		}
	} // END OF dispose METHOD
} // END OF ShaderProgram
