package bomber.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix4f;

import bomber.renderer.interfaces.RendererInterface;
import bomber.renderer.interfaces.ScreenInterface;
import bomber.renderer.shaders.ShaderProgram;
import bomber.renderer.utils.FileHandler;
import bomber.renderer.utils.GameEntity;
import bomber.renderer.utils.Transformation;

public class Renderer implements RendererInterface {

	private ShaderProgram shaderConstructor;
	private final Transformation transformation;

	public Renderer() {

		transformation = new Transformation();
	} // END OF CONSTRUCTOR

	@Override
	public void init(ScreenInterface screen) throws Exception {

		shaderConstructor = new ShaderProgram();
		shaderConstructor.createVertexShader(FileHandler.loadResource("res/vertex.vs"));
		shaderConstructor.createFragmentShader(FileHandler.loadResource("res/fragment.fs"));
		shaderConstructor.link();

		shaderConstructor.createUniform("projection");
		shaderConstructor.createUniform("model");

		screen.setClearColour(0f, 0f, 0f, 0f);
	} // END OF init METHOD

	public void render(ScreenInterface screen, GameEntity[] gameEntities) {

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Resize the screen if it needs to be resized
		if (screen.isResized()) {

			screen.setViewport(0, 0, screen.getWidth(), screen.getHeight());
			screen.setResized(false);
		}

		// Bind the shader
		shaderConstructor.bind();

		// Set the uniform
		// float ratio = screen.getWidth() / screen.getHeight();
		Matrix4f projectionMatrix = transformation.getOrthographicProjection(0f, screen.getWidth(), 0f, screen.getHeight(), -1f, 1f);
		shaderConstructor.setUniform("projection", projectionMatrix);

		// Render each gameItem
		for (GameEntity gameEntity : gameEntities) {

			Matrix4f modelMatrix = transformation.getModelMatrix(gameEntity.getPosition(), gameEntity.getRotation(),
					gameEntity.getScale());
			
			shaderConstructor.setUniform("model", modelMatrix);

			gameEntity.getMesh().render();
		}

		// Unbind the shader
		shaderConstructor.unbind();

	} // END OF render METHOD
	
	@Override
	public void dispose() {

		if (shaderConstructor != null) {

			shaderConstructor.dispose();
		}

	} // END OF dispose METHOD
} // END OF Renderer CLASS
