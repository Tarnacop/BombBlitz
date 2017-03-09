package bomber.renderer.shaders;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Texture {

	private final int textureID;
	private int width;
	private int height;

	public Texture(String path) throws Exception {

		ByteBuffer buffer;
		int width;
		int height;
		try (MemoryStack stack = MemoryStack.stackPush()) {

			// Prepare image buffers
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			// Load image using stbi library
			buffer = stbi_load(path, w, h, comp, 4);

			if (buffer == null) {
				throw new RuntimeException(
						"Failed to load a texture file!" + System.lineSeparator() + stbi_failure_reason());
			}

			width = w.get();
			height = h.get();
		}

		// Create a new OpenGL texture
		// Create a new OpenGL texture
		textureID = glGenTextures();

		// Bind the texture
		glBindTexture(GL_TEXTURE_2D, textureID);

		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		// Upload the texture data
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		// Generate Mipmap
		glGenerateMipmap(GL_TEXTURE_2D);
		;

	} // END OF CONSTRUCTOR

	public Texture(InputStream is) throws Exception {

		PNGDecoder decoder = new PNGDecoder(is);
		this.width = decoder.getWidth();
		this.height = decoder.getHeight();

		// Load texture contents into a byte buffer
		ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
		decoder.decode(buffer, decoder.getWidth() * 4, Format.RGBA);
		buffer.flip();

		// Create a new OpenGL texture
		textureID = glGenTextures();

		// Bind the texture
		glBindTexture(GL_TEXTURE_2D, textureID);

		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		// Upload the texture data
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

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
