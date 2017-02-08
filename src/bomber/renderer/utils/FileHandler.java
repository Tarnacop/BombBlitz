package bomber.renderer.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileHandler {

	public static String loadResource(String path) throws Exception {
		
		StringBuilder builder = new StringBuilder();

		try (InputStream in = new FileInputStream(path);

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
} // END OF FileHandler CLASS
