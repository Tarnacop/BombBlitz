package bomber.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import bomber.game.GameState;
import bomber.game.Player;
import bomber.renderer.interfaces.RendererInterface;
import bomber.renderer.interfaces.ScreenInterface;
import bomber.renderer.shaders.Mesh;
import bomber.renderer.shaders.ShaderProgram;
import bomber.renderer.utils.FileHandler;
import bomber.renderer.utils.GameEntity;
import bomber.renderer.utils.Transformation;

public class Renderer {

	private ShaderProgram shaderConstructor;
	private final Transformation transformation;
	private Mesh meshConstructor;
	
	public Renderer() {

		transformation = new Transformation();
	} // END OF CONSTRUCTOR

	public void init(ScreenInterface screen) throws Exception {

		shaderConstructor = new ShaderProgram();
		shaderConstructor.createVertexShader(FileHandler.loadResource("res/vertex.vs"));
		shaderConstructor.createFragmentShader(FileHandler.loadResource("res/fragment.fs"));
		shaderConstructor.link();

		shaderConstructor.createUniform("projection");
		shaderConstructor.createUniform("model");

		
		float[] positions = new float[] { 
				// V0
				0f, 100f, 
				// V1
				0f, 0f, 
				// V2
				100f, 0f, 
				// V3
				100f, 100f };
		int[] indices = new int[] { 0, 1, 3, 3, 1, 2, };
		float[] colours = new float[] { 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.5f, 0.5f, };
		meshConstructor = new Mesh(positions, colours, indices);
		
		screen.setClearColour(0f, 0f, 0f, 0f);
	} // END OF init METHOD

	public void render(ScreenInterface screen, GameState state) {

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
		Matrix4f projectionMatrix = transformation.getOrthographicProjection(0f, screen.getWidth(), 0f,
				screen.getHeight(), -1f, 1f);
		shaderConstructor.setUniform("projection", projectionMatrix);
		
		Player player = state
				.getPlayers()
				.get(0);
		Vector2f playerPos = new Vector2f((float) player.getPos().getX(), (float) player.getPos().getY());
		Matrix4f modelMatrix = transformation.getModelMatrix(playerPos, 0f, 1f);
		shaderConstructor.setUniform("model", modelMatrix);
		meshConstructor.render();
		// Render each gameItem
		/*
		 * for (GameEntity gameEntity : gameEntities) {
		 * 
		 * Matrix4f modelMatrix =
		 * transformation.getModelMatrix(gameEntity.getPosition(),
		 * gameEntity.getRotation(), gameEntity.getScale());
		 * 
		 * shaderConstructor.setUniform("model", modelMatrix);
		 * 
		 * gameEntity.getMesh().render(); }
		 */

		// Unbind the shader
		shaderConstructor.unbind();

	} // END OF render METHOD

	public void dispose() {

		if (shaderConstructor != null) {

			shaderConstructor.dispose();
		}
		meshConstructor.dispose();

	} // END OF dispose METHOD
} // END OF Renderer CLASS
