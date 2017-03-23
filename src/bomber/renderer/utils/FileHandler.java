package bomber.renderer.utils;

import bomber.game.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class to handle the loading of the shader programs
 * 
 * @author Alexandru Blinda
 *
 */
public class FileHandler {

	/**
	 * Load the shader program from the given path and return it
	 * 
	 * @param path
	 *            The given path
	 * @return The shader program returned
	 * @throws Exception
	 */
	public static String loadShaderResource(String path) throws Exception {

		StringBuilder builder = new StringBuilder();

		try (InputStream in = main.class.getResourceAsStream(path);

			 BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			String line;
			while ((line = reader.readLine()) != null) {

				builder.append(line).append("\n");
			}
		} catch (IOException ex) {

			throw new RuntimeException("Failed to load a shader file!" + System.lineSeparator() + ex.getMessage());
		}

		CharSequence source = builder.toString();
		return source.toString();
	}
}
