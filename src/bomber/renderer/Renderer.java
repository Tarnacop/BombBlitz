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

public class Renderer {

	private ShaderProgram shaderConstructor;
	private final Transformation transformation;
	private Mesh blockMesh;
	private Mesh softMesh;
	private Mesh blastMesh;
	
	public Renderer() {

		transformation = new Transformation();
	} // END OF CONSTRUCTOR

	public void init(Screen screen) throws Exception {

		shaderConstructor = new ShaderProgram();
		shaderConstructor.createVertexShader(FileHandler.loadResource("res/vertex.vs"));
		shaderConstructor.createFragmentShader(FileHandler.loadResource("res/fragment.fs"));
		shaderConstructor.link();

		shaderConstructor.createUniform("projection");
		shaderConstructor.createUniform("model");
		
		float[] colours = new float[] { 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f };
		blockMesh = new Mesh(0, 0, 64, 64, colours);
		colours = new float[] { 1f, 1f, 1f, 0.0f, 1f, 1f, 1f, 0.0f, 1f, 1f, 1f, 0.0f };
		softMesh = new Mesh(0, 0, 64, 64, colours);
		colours = new float[] {0f, 1f, 1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f, 0f}; 
		blastMesh = new Mesh(0, 0, 64, 64, colours);
		screen.setClearColour(0f, 0f, 0f, 0f);
	} // END OF init METHOD

	// Takes a state to render
	public void render(Screen screen, GameState state) {

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		// Resize the screen if it needs to be resized
		if (screen.isResized()) {

			screen.setViewport(0, 0, screen.getWidth(), screen.getHeight());
			screen.setResized(false);
		}

		// Bind the shader
		shaderConstructor.bind();

		// Set the uniform
		Matrix4f projectionMatrix = transformation.getOrthographicProjection(0f, screen.getWidth(), screen.getHeight(), 0f, -1f, 1f);
		shaderConstructor.setUniform("projection", projectionMatrix);

		// Render each entity of the state
		
		/*
		for (GameEntity gameEntity : gameEntities) {

			Matrix4f modelMatrix = transformation.getModelMatrix(gameEntity.getPosition(), gameEntity.getRotation(),
					gameEntity.getScale());
			
			shaderConstructor.setUniform("model", modelMatrix);

			gameEntity.getMesh().render();
		}*/
		
		Block[][] blocks = state.getMap().getGridMap();
		
		for(int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[0].length; j++) {
				
				if(blocks[i][j] == Block.SOLID) {
				
					Vector2f blockCoords = new Vector2f(i*64f, j*64f);
					Matrix4f modelMatrix = transformation.getModelMatrix(blockCoords
							,0f, 1f);
					shaderConstructor.setUniform("model", modelMatrix);
					blockMesh.render();
				} else if(blocks[i][j] == Block.SOFT) {
					Vector2f blockCoords = new Vector2f(i*64f, j*64f);
					Matrix4f modelMatrix = transformation.getModelMatrix(blockCoords
							,0f, 1f);
					shaderConstructor.setUniform("model", modelMatrix);
					softMesh.render();
					
				} else if(blocks[i][j] == Block.BLAST) {
					Vector2f blockCoords = new Vector2f(i*64f, j*64f);
					Matrix4f modelMatrix = transformation.getModelMatrix(blockCoords
							,0f, 1f);
					shaderConstructor.setUniform("model", modelMatrix);
					blastMesh.render();
				}

			}
		}
		
		for (Player player : state.getPlayers()) {
			
			Matrix4f modelMatrix = transformation.getModelMatrix(new Vector2f((float) player.getPos().x, (float) player.getPos().y)
					,0f, 1f);
			
			shaderConstructor.setUniform("model", modelMatrix);
			
			player.getMesh().render();
		}
		
		for (Bomb bomb : state.getBombs()) {
			
			Matrix4f modelMatrix = transformation.getModelMatrix(new Vector2f((float) bomb.getPos().x, (float) bomb.getPos().y)
			,0f, 1f);
	
			shaderConstructor.setUniform("model", modelMatrix);
			
			bomb.getMesh().render();
		}

		// Unbind the shader
		shaderConstructor.unbind();

	} // END OF render METHOD
	
	public void dispose() {

		if (shaderConstructor != null) {

			shaderConstructor.dispose();
		}
	} // END OF dispose METHOD
} // END OF Renderer CLASS
