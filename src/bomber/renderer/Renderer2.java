package bomber.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.Player;
import bomber.renderer.shaders.Mesh;
import bomber.renderer.shaders.ShaderProgram;
import bomber.renderer.utils.FileHandler;
import bomber.renderer.utils.Transformation;

public class Renderer2 {

	private ShaderProgram sceneShader;
	private final Transformation transformation;
	private Matrix4f projectionMatrix;
	private Matrix4f modelMatrix;
	private Mesh solidMesh;
	private Mesh softMesh;
	private Mesh blastMesh;
	private Mesh bombMesh;

	public Renderer() {

		transformation = new Transformation();
	} // END OF CONSTRUCTOR

	public void init(Screen screen) throws Exception {

		setupSceneShader();
		float[] colours = new float[] { 0f, 0f, 0.5f, 0f, 0f, 0f, 0.5f, 0f, 0f, 0f, 0.5f, 0f };
		solidMesh = new Mesh(64, 64, colours);
		colours = new float[] { 1f, 1f, 1f, 0f, 1f, 1f, 1f, 0f, 1f, 1f, 1f, 0f };
		softMesh = new Mesh(64, 64, colours);
		colours = new float[] { 0f, 1f, 1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f, 0f };
		blastMesh = new Mesh(64, 64, colours);
		colours = new float[] { 0.7f, 0.4f, 0.1f, 0f, 0.7f, 0.4f, 0.1f, 0f, 0.7f, 0.4f, 0.1f, 0f };
		bombMesh = new Mesh(50, 50, colours);

		screen.setClearColour(0f, 0f, 0f, 0f);
	} // END OF init METHOD

	public void setupSceneShader() throws Exception {

		sceneShader = new ShaderProgram();
		sceneShader.createVertexShader(FileHandler.loadResource("res/vertex.vs"));
		sceneShader.createFragmentShader(FileHandler.loadResource("res/fragment.fs"));
		sceneShader.link();

		sceneShader.createUniform("projection");
		sceneShader.createUniform("model");

	} // END OF setupSceneShader METHOD

	// Takes a state to render
	public void render(Screen screen, GameState state) {

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Resize the screen if it needs to be resized
		if (screen.isResized()) {

			screen.setViewport(0, 0, screen.getWidth(), screen.getHeight());
			screen.setResized(false);
		}

		renderScene(screen, state);

	} // END OF render METHOD

	public void renderScene(Screen screen, GameState state) {

		// Bind the shader
		sceneShader.bind();

		// Set the uniform for the projection matrix
		projectionMatrix = transformation.getOrthographicProjection(0f, screen.getWidth(), screen.getHeight(), 0f, -1f,
				1f);
		sceneShader.setUniform("projection", projectionMatrix);

		// Render each entity of the state
		Block[][] blocks = state.getMap().getGridMap();

		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[0].length; j++) {

				if (blocks[i][j] == Block.SOLID) {

					Vector2f blockCoords = new Vector2f(i * 64f, j * 64f);
					modelMatrix = transformation.getModelMatrix(blockCoords, 0f, 1f);
					sceneShader.setUniform("model", modelMatrix);
					solidMesh.render();
				} else if (blocks[i][j] == Block.SOFT) {

					Vector2f blockCoords = new Vector2f(i * 64f, j * 64f);
					modelMatrix = transformation.getModelMatrix(blockCoords, 0f, 1f);
					sceneShader.setUniform("model", modelMatrix);
					softMesh.render();

				} else if (blocks[i][j] == Block.BLAST) {

					Vector2f blockCoords = new Vector2f(i * 64f, j * 64f);
					modelMatrix = transformation.getModelMatrix(blockCoords, 0f, 1f);
					sceneShader.setUniform("model", modelMatrix);
					blastMesh.render();
				}

			}
		}

		for (Player player : state.getPlayers()) {
			if (!player.isAlive())
				continue;
			modelMatrix = transformation
					.getModelMatrix(new Vector2f((float) player.getPos().x, (float) player.getPos().y), 0f, 1f);

			sceneShader.setUniform("model", modelMatrix);

			player.getMesh().render();
		}

		for (Bomb bomb : state.getBombs()) {

			modelMatrix = transformation.getModelMatrix(new Vector2f((float) bomb.getPos().x, (float) bomb.getPos().y),
					0f, 1f);

			sceneShader.setUniform("model", modelMatrix);

			bombMesh.render();
		}

		// Unbind the shader
		sceneShader.unbind();

	} // END OF renderScene METHOD

	public void dispose() {

		solidMesh.dispose();
		softMesh.dispose();
		blastMesh.dispose();
		bombMesh.dispose();
		if (sceneShader != null) {

			sceneShader.dispose();
		}
	} // END OF dispose METHOD
} // END OF Renderer CLASS
