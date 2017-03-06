package bomber.renderer;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import java.awt.Font;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.Player;
import bomber.renderer.shaders.FontTexture;
import bomber.renderer.shaders.Mesh;
import bomber.renderer.shaders.ShaderProgram;
import bomber.renderer.shaders.TextItem;
import bomber.renderer.utils.FileHandler;
import bomber.renderer.utils.Transformation;
public class Renderer {
	private ShaderProgram sceneShader;
	private ShaderProgram hudShader;
	private final Transformation transformation;
	private Matrix4f projectionMatrix;
	private Matrix4f modelMatrix;
	private TextItem playerName;
	private Mesh solidMesh;
	private Mesh softMesh;
	private Mesh blastMesh;
	private Mesh bombMesh;
	
	boolean gameOver;
	public Renderer() {
		transformation = new Transformation();
		gameOver = false;
	} // END OF CONSTRUCTOR
	public void init(Screen screen) throws Exception {
		setupSceneShader();
		setupHudShader();
		float[] colours = new float[] { 0f, 0f, 0.5f, 0f, 0f, 0f, 0.5f, 0f, 0f, 0f, 0.5f, 0f };
		solidMesh = new Mesh(64, 64, colours);
		colours = new float[] { 1f, 1f, 1f, 0f, 1f, 1f, 1f, 0f, 1f, 1f, 1f, 0f };
		softMesh = new Mesh(64, 64, colours);
		colours = new float[] { 0f, 1f, 1f, 0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f, 0f };
		blastMesh = new Mesh(64, 64, colours);
		colours = new float[] { 0.7f, 0.4f, 0.1f, 0f, 0.7f, 0.4f, 0.1f, 0f, 0.7f, 0.4f, 0.1f, 0f };
		bombMesh = new Mesh(50, 50, colours);
		playerName = new TextItem("ALEX", new FontTexture("/res/minecraft.ttf", 20, Font.PLAIN));
		playerName.setPosition(300f, 300f);
		
		screen.setClearColour(0f, 0f, 0f, 0f);
	} // END OF init METHOD
		// Takes a state to render
	public void setupSceneShader() throws Exception {
		sceneShader = new ShaderProgram();
		sceneShader.createVertexShader(FileHandler.loadResource("res/vertex.vs"));
		sceneShader.createFragmentShader(FileHandler.loadResource("res/fragment.fs"));
		sceneShader.link();
		sceneShader.createUniform("projection");
		sceneShader.createUniform("model");
	} // END OF setupSceneShader METHOD
	
	public void setupHudShader() throws Exception {
		
		hudShader = new ShaderProgram();
		hudShader.createVertexShader(FileHandler.loadResource("res/hud_vertex.vs"));
		hudShader.createFragmentShader(FileHandler.loadResource("res/hud_fragment.fs"));
		hudShader.link();
		
		hudShader.createUniform("projModelMatrix");
		hudShader.createUniform("colour");
		
	} // END OF setupHudShader METHOD
	public void render(Screen screen, GameState state) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// Resize the screen if it needs to be resized
		if (screen.isResized()) {
			screen.setViewport(0, 0, screen.getWidth(), screen.getHeight());
			screen.setResized(false);
		}
		
		if(!gameOver) {
			renderScene(screen, state);
			renderHud(screen, state);
		}
	} // END OF render METHOD
	public void renderScene(Screen screen, GameState state) {
		// Bind the shader
		sceneShader.bind();
		// Set the uniform for the projection matrix
		projectionMatrix = transformation.getOrthographicProjection(0f, screen.getWidth(), screen.getHeight(), 0f);
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
		List<Player> playerList = state.getPlayers();
		synchronized (playerList) {
			for (Player player : playerList) {
				if (!player.isAlive())
					continue;
				modelMatrix = transformation
						.getModelMatrix(new Vector2f((float) player.getPos().x, (float) player.getPos().y), 0f, 1f);
				sceneShader.setUniform("model", modelMatrix);
				player.getMesh().render();
			}
		}
		List<Bomb> boombList = state.getBombs();
		synchronized (boombList) {
			for (Bomb bomb : boombList) {
				modelMatrix = transformation
						.getModelMatrix(new Vector2f((float) bomb.getPos().x, (float) bomb.getPos().y), 0f, 1f);
				sceneShader.setUniform("model", modelMatrix);
				bombMesh.render();
			}
		}
		// Unbind the shader
		sceneShader.unbind();
	} // END OF renderScene METHOD
	
	public void renderHud(Screen screen, GameState state) {
		
		hudShader.bind();
		projectionMatrix = transformation.getOrthographicProjection(0f, screen.getWidth(), screen.getHeight(), 0f);
		
		// playerName.setText(state.getPlayers().get(0).getName());
		modelMatrix = transformation.getModelMatrix(playerName.getPosition(), playerName.getRotation(), playerName.getScale());
		hudShader.setUniform("projModelMatrix", transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
		hudShader.setUniform("colour", playerName.getColour());
		playerName.getMesh().render();
		
		hudShader.unbind();
		
	} // END OF renderHud METHOD
	public void displayGameOver() {
		
		gameOver = true;
	} // END OF displayGameOver METHOD
	
	public void dispose() {
		solidMesh.dispose();
		softMesh.dispose();
		blastMesh.dispose();
		bombMesh.dispose();
		playerName.getMesh().dispose();
		if (sceneShader != null) {
			sceneShader.dispose();
		}
		if(hudShader != null) {
			
			hudShader.dispose();
		}
	} // END OF dispose METHOD
} // END OF Renderer CLASS