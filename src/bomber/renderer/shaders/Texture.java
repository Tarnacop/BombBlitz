package bomber.renderer.shaders;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

	private final int textureID;
	private final int width;
	private final int height;
	
	public Texture(String path) {
		
		ByteBuffer image;
		try (MemoryStack stack = MemoryStack.stackPush()) {

			// Prepare image buffers
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);
			
			// Load image using stbi library
			image = stbi_load(path, w, h, comp, 4);

			if (image == null) {
				throw new RuntimeException(
						"Failed to load a texture file!" + System.lineSeparator() + stbi_failure_reason());
			}

			width = w.get();
			height = h.get();
		}

		// Create a new OpenGL texture
		textureID = glGenTextures();
		
		// Bind the texture
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		// Upload the texture data
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
		
		// Generate Mipmap 
		glGenerateMipmap(GL_TEXTURE_2D);
	} // END OF CONSTRUCTOR

	public int getTextureID() {
		
		return textureID;
	} // END OF getTextureID METHOD
	
	public void dispose() {
		
		glDeleteTextures(textureID);
	} // END OF dispose METHOD
	
	public int getWidth() {
		
		return width;
	} // END OF getWidth METHOD
	
	public int getHeight() {
		
		return height;
	} // END OF getHeight METHOD
	
} // END OF Texture CLASS
