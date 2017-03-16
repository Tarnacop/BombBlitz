package bomber.renderer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.awt.Font;
import java.util.HashMap;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import bomber.AI.GameAI;
import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.Constants;
import bomber.game.GameState;
import bomber.game.Player;
import bomber.renderer.shaders.FontTexture;
import bomber.renderer.shaders.ShaderProgram;
import bomber.renderer.shaders.TextItem;
import bomber.renderer.shaders.Texture;
import bomber.renderer.shaders.TextureMesh;
import bomber.renderer.utils.FileHandler;
import bomber.renderer.utils.Transformation;

/**
 * The class responsible with rendering everything on the screen
 * 
 * @author Alexandru Blinda
 * 
 */
public class Renderer {

	private ShaderProgram sceneShader;
	private ShaderProgram hudShader;
	private ShaderProgram textureShader;

	private final Transformation transformation;
	private Matrix4f projectionMatrix;
	private Matrix4f modelMatrix;
	private TextItem hudTextItem;

	private boolean gameOver;
	private boolean frontScreen;
	private boolean gamePaused;

	private HashMap<String, TextureMesh> textureMeshes;

	private float w_ratio;
	private float h_ratio;
	private float x; // General purpose x coord

	/**
	 * Create a Renderer object
	 */
	public Renderer() {

		transformation = new Transformation();
		textureMeshes = new HashMap<String, TextureMesh>();
		gameOver = false;
		frontScreen = true;
		gamePaused = false;
	} // END OF CONSTRUCTOR

	/**
	 * Initialise the renderer with the given screen
	 * 
	 * @param screen
	 *            The given screen
	 * @throws Exception
	 */
	public void init(Screen screen) throws Exception {

		// setupSceneShader();
		setupTextureShader();
		setupHudShader();
		setupTextures();
		setupHuds();
		w_ratio = Constants.V_WIDTH / screen.getWidth();
		h_ratio = Constants.V_HEIGHT / screen.getHeight();

		screen.setClearColour(0f, 0f, 0f, 0f);
	} // END OF init METHOD
		// Takes a state to render

	/**
	 * Setup the shaders for rendering textures
	 * 
	 * @throws Exception
	 */
	private void setupTextureShader() throws Exception {

		textureShader = new ShaderProgram();

		textureShader.createVertexShader(FileHandler
				.loadShaderResource("src/resources/shaders/texture_vertex.vs"));
		textureShader
				.createFragmentShader(FileHandler
						.loadShaderResource("src/resources/shaders/texture_fragment.fs"));
		textureShader.link();

		textureShader.createUniform("projection");
		textureShader.createUniform("model");
		textureShader.createUniform("texture_sampler");

	} // END OF setupTextureShader METHOD

	/**
	 * Setup the shaders for rendering hud
	 * 
	 * @throws Exception
	 */
	private void setupHudShader() throws Exception {

		hudShader = new ShaderProgram();
		hudShader.createVertexShader(FileHandler
				.loadShaderResource("src/resources/shaders/hud_vertex.vs"));
		hudShader.createFragmentShader(FileHandler
				.loadShaderResource("src/resources/shaders/hud_fragment.fs"));
		hudShader.link();

		hudShader.createUniform("projModelMatrix");
		hudShader.createUniform("colour");
		hudShader.createUniform("texture_sampler");

	} // END OF setupHudShader METHOD

	/**
	 * Setup the textures used for rendering
	 * 
	 * @throws Exception
	 */
	private void setupTextures() throws Exception {

		Texture background = new Texture(
				"src/resources/images/gamebackground.png");
		Texture box = new Texture("src/resources/images/mapbox.png");
		Texture fancybox = new Texture("src/resources/images/fancybox.png");
		Texture heart = new Texture("src/resources/images/heart.png");
		// Texture spritesheet = new
		// Texture("src/resources/images/spritesheet.png");
		Texture newspritesheet = new Texture(
				"src/resources/images/newspritesheet.png");

		TextureMesh blankMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 0f, 0f,
				Constants.SPRITESHEET_COLS, Constants.SPRITESHEET_ROWS,
				newspritesheet);
		textureMeshes.put("blankMesh", blankMesh);

		TextureMesh softMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 0.2f, 0f,
				Constants.SPRITESHEET_COLS, Constants.SPRITESHEET_ROWS,
				newspritesheet);
		textureMeshes.put("softMesh", softMesh);

		TextureMesh solidMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 0.1f, 0f,
				Constants.SPRITESHEET_COLS, Constants.SPRITESHEET_ROWS,
				newspritesheet);
		textureMeshes.put("solidMesh", solidMesh);

		TextureMesh blastMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 0.3f, 0f,
				Constants.SPRITESHEET_COLS, Constants.SPRITESHEET_ROWS,
				newspritesheet);
		textureMeshes.put("blastMesh", blastMesh);

		TextureMesh playerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH,
				Constants.PLAYER_HEIGHT, 0.1f, 0.5f,
				Constants.SPRITESHEET_COLS, Constants.SPRITESHEET_ROWS,
				newspritesheet);
		textureMeshes.put("playerMesh1", playerMesh1);

		TextureMesh playerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH,
				Constants.PLAYER_HEIGHT, 0.2f, 0.5f,
				Constants.SPRITESHEET_COLS, Constants.SPRITESHEET_ROWS,
				newspritesheet);
		textureMeshes.put("playerMesh2", playerMesh2);

		TextureMesh playerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH,
				Constants.PLAYER_HEIGHT, 0.3f, 0.5f,
				Constants.SPRITESHEET_COLS, Constants.SPRITESHEET_ROWS,
				newspritesheet);
		textureMeshes.put("playerMesh3", playerMesh3);

		TextureMesh aiMesh1 = new TextureMesh(Constants.PLAYER_WIDTH,
				Constants.PLAYER_HEIGHT, 0.4f, 0.5f,
				Constants.SPRITESHEET_COLS, Constants.SPRITESHEET_ROWS,
				newspritesheet);
		textureMeshes.put("aiMesh1", aiMesh1);

		TextureMesh aiMesh2 = new TextureMesh(Constants.PLAYER_WIDTH,
				Constants.PLAYER_HEIGHT, 0.5f, 0.5f,
				Constants.SPRITESHEET_COLS, Constants.SPRITESHEET_ROWS,
				newspritesheet);
		textureMeshes.put("aiMesh2", aiMesh2);
		
		TextureMesh aiMesh3 = new TextureMesh(Constants.PLAYER_WIDTH,
				Constants.PLAYER_HEIGHT, 0.6f, 0.5f,
				Constants.SPRITESHEET_COLS, Constants.SPRITESHEET_ROWS,
				newspritesheet);
		textureMeshes.put("aiMesh3", aiMesh3);
		
		TextureMesh bombMesh = new TextureMesh(Constants.BOMB_WIDTH,
				Constants.BOMB_HEIGHT, 0.0f, 0.5f, Constants.SPRITESHEET_COLS,
				Constants.SPRITESHEET_ROWS, newspritesheet);
		textureMeshes.put("bombMesh", bombMesh);

		TextureMesh powerMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 0.4f, 0.0f,
				Constants.SPRITESHEET_COLS, Constants.SPRITESHEET_ROWS,
				newspritesheet);
		textureMeshes.put("powerMesh", powerMesh);

		TextureMesh backgroundMesh = new TextureMesh(
				Constants.BACKGROUND_WIDTH, Constants.BACKGROUND_WIDTH,
				background);
		textureMeshes.put("backgroundMesh", backgroundMesh);

		TextureMesh infoBoxMesh = new TextureMesh(Constants.INFO_BOX_WIDTH,
				Constants.INFO_BOX_HEIGHT, box);
		textureMeshes.put("infoBoxMesh", infoBoxMesh);

		TextureMesh gameBoxMesh = new TextureMesh(Constants.GAME_BOX_WIDTH,
				Constants.GAME_BOX_HEIGHT, box);
		textureMeshes.put("gameBoxMesh", gameBoxMesh);

		TextureMesh fancyBoxMesh = new TextureMesh(Constants.FANCY_BOX_WIDTH,
				Constants.FANCY_BOX_HEIGHT, fancybox);
		textureMeshes.put("fancyBoxMesh", fancyBoxMesh);

		TextureMesh heartMesh = new TextureMesh(Constants.HEART_WIDTH,
				Constants.HEART_HEIGHT, heart);
		textureMeshes.put("heartMesh", heartMesh);

		TextureMesh beginningBoxMesh = new TextureMesh(
				Constants.GENERAL_BOX_WIDTH, Constants.GENERAL_BOX_HEIGHT, box);
		textureMeshes.put("generalBoxMesh", beginningBoxMesh);
	} // END OF setupTextures METHOD

	/**
	 * Setup the huds used for rendering
	 * 
	 * @throws Exception
	 */
	private void setupHuds() throws Exception {

		FontTexture hudFontTexture = new FontTexture(
				"src/resources/minecraftbig.ttf", 25, Font.PLAIN);
		hudTextItem = new TextItem("", hudFontTexture);
	} // END OF setupHuds METHOD

	/**
	 * Render the given game state on the given screen
	 * 
	 * @param screen
	 *            The given screen
	 * @param state
	 *            The given state
	 */
	public void render(Screen screen, GameState state) {

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// Resize the screen if it needs to be resized
		if (screen.isResized()) {

			screen.setViewport(0, 0, screen.getWidth(), screen.getHeight());
			screen.setResized(false);
			w_ratio = Constants.V_WIDTH / screen.getWidth();
			h_ratio = Constants.V_HEIGHT / screen.getHeight();
		}

		renderGameTexture(screen, state);
		renderGameHud(screen, state);

		if (gameOver) {

			renderGameOverTextures(screen, state);
			renderGameOverHud(screen, state);
		}
		if (frontScreen) {

			renderBeginningTextures(screen, state);
			renderBeginningHud(screen, state);
		}
		if (gamePaused) {

			renderPauseTextures(screen, state);
			renderPauseHud(screen, state);
		}
	} // END OF render METHOD

	// -------------------------------------Game Screen
	// Render--------------------------------------------------------------------

	/**
	 * Render the textures of the given game state on the given screen
	 * 
	 * @param screen
	 *            The given screen
	 * @param state
	 *            The given game state
	 */
	private int playerAnimationCounter = 0;

	private void renderGameTexture(Screen screen, GameState state) {

		textureShader.bind();
		textureShader.setUniform("texture_sampler", 0);

		projectionMatrix = transformation.getOrthographicProjection(0f,
				screen.getWidth() * w_ratio, screen.getHeight() * h_ratio, 0f);
		textureShader.setUniform("projection", projectionMatrix);

		// Render background
		modelMatrix = transformation.getModelMatrix(Constants.BACKGROUND_X,
				Constants.BACKGROUND_Y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);
		textureMeshes.get("backgroundMesh").render();

		// Render info box
		modelMatrix = transformation.getModelMatrix(Constants.INFO_BOX_X,
				Constants.INFO_BOX_Y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);
		textureMeshes.get("infoBoxMesh").render();

		// Render game box
		modelMatrix = transformation.getModelMatrix(Constants.GAME_BOX_X,
				Constants.GAME_BOX_Y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);
		textureMeshes.get("gameBoxMesh").render();

		// Render player info box
		if (state.getPlayers().size() > 0) {

			modelMatrix = transformation.getModelMatrix(Constants.FANCY_BOX1_X,
					Constants.FANCY_BOX1_Y, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("fancyBoxMesh").render();

			x = Constants.FANCY_BOX1_X + Constants.FANCY_BOX_WIDTH / 4;
			modelMatrix = transformation.getModelMatrix(x,
					Constants.FANCY_BOX1_Y + Constants.BOX_PADDING - 7, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("heartMesh").render();
		}

		if (state.getPlayers().size() > 1) {

			modelMatrix = transformation.getModelMatrix(Constants.FANCY_BOX2_X,
					Constants.FANCY_BOX2_Y, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("fancyBoxMesh").render();

			x = Constants.FANCY_BOX2_X + Constants.FANCY_BOX_WIDTH / 4;
			modelMatrix = transformation.getModelMatrix(x,
					Constants.FANCY_BOX2_Y + Constants.BOX_PADDING - 7, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("heartMesh").render();
		}

		if (state.getPlayers().size() > 2) {

			modelMatrix = transformation.getModelMatrix(Constants.FANCY_BOX3_X,
					Constants.FANCY_BOX3_Y, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("fancyBoxMesh").render();

			x = Constants.FANCY_BOX3_X + Constants.FANCY_BOX_WIDTH / 4;
			modelMatrix = transformation.getModelMatrix(x,
					Constants.FANCY_BOX3_Y + Constants.BOX_PADDING - 7, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("heartMesh").render();
		}

		if (state.getPlayers().size() > 3) {

			modelMatrix = transformation.getModelMatrix(Constants.FANCY_BOX4_X,
					Constants.FANCY_BOX4_Y, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("fancyBoxMesh").render();

			x = Constants.FANCY_BOX4_X + Constants.FANCY_BOX_WIDTH / 4;
			modelMatrix = transformation.getModelMatrix(x,
					Constants.FANCY_BOX4_Y + Constants.BOX_PADDING - 7, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("heartMesh").render();
		}

		Block[][] blocks = state.getMap().getGridMap();
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[0].length; j++) {
				if (blocks[i][j] == Block.BLANK) {
					modelMatrix = transformation.getModelMatrix(i
							* Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, j
							* Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f,
							1f);
					textureShader.setUniform("model", modelMatrix);
					textureMeshes.get("blankMesh").render();
				} else if (blocks[i][j] == Block.SOFT) {
					modelMatrix = transformation.getModelMatrix(i
							* Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, j
							* Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f,
							1f);
					textureShader.setUniform("model", modelMatrix);
					textureMeshes.get("softMesh").render();
				} else if (blocks[i][j] == Block.SOLID) {
					modelMatrix = transformation.getModelMatrix(i
							* Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, j
							* Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f,
							1f);
					textureShader.setUniform("model", modelMatrix);
					textureMeshes.get("solidMesh").render();
				} else if (blocks[i][j] == Block.BLAST) {
					modelMatrix = transformation.getModelMatrix(i
							* Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, j
							* Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f,
							1f);
					textureShader.setUniform("model", modelMatrix);
					textureMeshes.get("blastMesh").render();
				} else if (blocks[i][j] == Block.PLUS_SPEED
						|| blocks[i][j] == Block.MINUS_SPEED
						|| blocks[i][j] == Block.PLUS_BOMB
						|| blocks[i][j] == Block.MINUS_BOMB
						|| blocks[i][j] == Block.PLUS_RANGE
						|| blocks[i][j] == Block.MINUS_RANGE) {
					modelMatrix = transformation.getModelMatrix(i
							* Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, j
							* Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f,
							1f);
					textureShader.setUniform("model", modelMatrix);
					textureMeshes.get("powerMesh").render();
				}
			}
		}

		List<Bomb> boombList = state.getBombs();
		synchronized (boombList) {
			for (Bomb bomb : boombList) {
				modelMatrix = transformation.getModelMatrix(
						(float) bomb.getPos().x + 15,
						(float) bomb.getPos().y + 15, 0f, 1f);
				textureShader.setUniform("model", modelMatrix);
				textureMeshes.get("bombMesh").render();
			}
		}

		List<Player> playerList = state.getPlayers();
		synchronized (playerList) {
			for (Player player : playerList) {
				if (!player.isAlive())
					continue;
				modelMatrix = transformation.getModelMatrix(
						(float) player.getPos().x + 15,
						(float) player.getPos().y + 15, 0f, 1f);
				textureShader.setUniform("model", modelMatrix);
				if (player instanceof GameAI) {

					if(playerAnimationCounter < 5) {
						textureMeshes.get("aiMesh1").render();
					} else if(playerAnimationCounter < 10) {
						
						textureMeshes.get("aiMesh2").render();
					} else if(playerAnimationCounter < 15) {
						
						textureMeshes.get("aiMesh3").render();
					}
				} else {

					if (playerAnimationCounter < 5) {
						textureMeshes.get("playerMesh1").render();
					} else if (playerAnimationCounter < 10) {
						textureMeshes.get("playerMesh2").render();
					} else if (playerAnimationCounter < 15) {
						textureMeshes.get("playerMesh3").render();
					}
				}
			}
		}

		playerAnimationCounter++;
		if (playerAnimationCounter > 14) {

			playerAnimationCounter = 0;
		}

		textureShader.unbind();
	} // END OF renderGameTexture METHOD

	/**
	 * Render the huds of the given game state on the given screen
	 * 
	 * @param screen
	 *            The given screen
	 * @param state
	 *            The given game state
	 */
	private void renderGameHud(Screen screen, GameState state) {

		hudShader.bind();
		hudShader.setUniform("texture_sampler", 0);
		projectionMatrix = transformation.getOrthographicProjection(0,
				screen.getWidth() * w_ratio, screen.getHeight() * h_ratio, 0f);
		
		// X coord
		if (state.getPlayers().size() > 0) {

			hudTextItem.setText(state.getPlayers().get(0).getName());
			x = Constants.FANCY_BOX1_X
					+ (Constants.FANCY_BOX_WIDTH / 2 - hudTextItem
							.getTextWidth() / 2);
			modelMatrix = transformation.getModelMatrix(x,
					Constants.FANCY_BOX1_Y + 10, hudTextItem.getRotation(),
					hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix,
							projectionMatrix));
			hudShader.setUniform("colour", hudTextItem.getColour());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(0)
					.getLives()));
			x = Constants.FANCY_BOX1_X + Constants.FANCY_BOX_WIDTH / 4
					+ Constants.HEART_WIDTH;
			modelMatrix = transformation.getModelMatrix(x,
					Constants.FANCY_BOX1_Y + Constants.BOX_PADDING, hudTextItem.getRotation(),
					hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix,
							projectionMatrix));
			hudShader.setUniform("colour", hudTextItem.getColour());
			hudTextItem.getMesh().render();
		}

		if (state.getPlayers().size() > 1) {

			hudTextItem.setText(state.getPlayers().get(1).getName());
			x = Constants.FANCY_BOX2_X
					+ (Constants.FANCY_BOX_WIDTH / 2 - hudTextItem
							.getTextWidth() / 2);
			modelMatrix = transformation.getModelMatrix(x,
					Constants.FANCY_BOX2_Y + 10, hudTextItem.getRotation(),
					hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix,
							projectionMatrix));
			hudShader.setUniform("colour", hudTextItem.getColour());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(1)
					.getLives()));
			x = Constants.FANCY_BOX2_X + Constants.FANCY_BOX_WIDTH / 4
					+ Constants.HEART_WIDTH;
			modelMatrix = transformation.getModelMatrix(x,
					Constants.FANCY_BOX2_Y + Constants.BOX_PADDING, hudTextItem.getRotation(),
					hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix,
							projectionMatrix));
			hudShader.setUniform("colour", hudTextItem.getColour());
			hudTextItem.getMesh().render();
		}

		if (state.getPlayers().size() > 2) {

			hudTextItem.setText(state.getPlayers().get(2).getName());
			x = Constants.FANCY_BOX3_X
					+ (Constants.FANCY_BOX_WIDTH / 2 - hudTextItem
							.getTextWidth() / 2);
			modelMatrix = transformation.getModelMatrix(x,
					Constants.FANCY_BOX3_Y + 10, hudTextItem.getRotation(),
					hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix,
							projectionMatrix));
			hudShader.setUniform("colour", hudTextItem.getColour());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(2)
					.getLives()));
			x = Constants.FANCY_BOX3_X + Constants.FANCY_BOX_WIDTH / 4
					+ Constants.HEART_WIDTH;
			modelMatrix = transformation.getModelMatrix(x,
					Constants.FANCY_BOX3_Y + Constants.BOX_PADDING, hudTextItem.getRotation(),
					hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix,
							projectionMatrix));
			hudShader.setUniform("colour", hudTextItem.getColour());
			hudTextItem.getMesh().render();
		}

		if (state.getPlayers().size() > 3) {

			hudTextItem.setText(state.getPlayers().get(3).getName());
			x = Constants.FANCY_BOX4_X
					+ (Constants.FANCY_BOX_WIDTH / 2 - hudTextItem
							.getTextWidth() / 2);
			modelMatrix = transformation.getModelMatrix(x,
					Constants.FANCY_BOX4_Y + 10, hudTextItem.getRotation(),
					hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix,
							projectionMatrix));
			hudShader.setUniform("colour", hudTextItem.getColour());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(3)
					.getLives()));
			x = Constants.FANCY_BOX4_X + Constants.FANCY_BOX_WIDTH / 4
					+ Constants.HEART_WIDTH;
			modelMatrix = transformation.getModelMatrix(x,
					Constants.FANCY_BOX4_Y + Constants.BOX_PADDING, hudTextItem.getRotation(),
					hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix,
							projectionMatrix));
			hudShader.setUniform("colour", hudTextItem.getColour());
			hudTextItem.getMesh().render();
		}

		hudShader.unbind();

	} // END OF renderHud METHOD

	// -------------------------------------- Game Over Render
	// ----------------------------

	/**
	 * Render the textures of game over scree of the given game state on the
	 * given screen
	 * 
	 * @param screen
	 *            The given screen.
	 * @param state
	 *            The given game state.
	 */
	private void renderGameOverTextures(Screen screen, GameState state) {

		textureShader.bind();
		textureShader.setUniform("texture_sampler", 0);

		projectionMatrix = transformation.getOrthographicProjection(0f,
				screen.getWidth() * w_ratio, screen.getHeight() * h_ratio, 0f);
		textureShader.setUniform("projection", projectionMatrix);

		modelMatrix = transformation.getModelMatrix(Constants.GENERAL_BOX_X,
				Constants.GENERAL_BOX_Y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);
		textureMeshes.get("generalBoxMesh").render();

		textureShader.unbind();

	} // END OF renderGameOverTextures METHOD

	/**
	 * Render the huds of the game over screen of the given game state on the
	 * given screen
	 * 
	 * @param screen
	 *            The given screen
	 * @param state
	 *            The given state
	 */
	private void renderGameOverHud(Screen screen, GameState state) {

		hudShader.bind();
		hudShader.setUniform("texture_sampler", 0);
		projectionMatrix = transformation.getOrthographicProjection(0,
				screen.getWidth() * w_ratio, screen.getHeight() * h_ratio, 0f);

		hudTextItem.setText("GAME OVER");
		x = Constants.GENERAL_BOX_X
				+ (Constants.GENERAL_BOX_WIDTH / 2 - hudTextItem.getTextWidth() / 2);
		float y = Constants.GENERAL_BOX_Y
				+ (Constants.GENERAL_BOX_HEIGHT / 2 - hudTextItem
						.getTextHeight() / 2);
		modelMatrix = transformation.getModelMatrix(x, y,
				hudTextItem.getRotation(), hudTextItem.getScale());
		hudShader.setUniform("projModelMatrix", transformation
				.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
		hudShader.setUniform("colour", hudTextItem.getColour());
		hudTextItem.getMesh().render();

		hudShader.unbind();
	} // END OF renderGameOverHud METHOD

	// -------------------------------------Front Screen
	// Render------------------------------------------------------------------

	/**
	 * Render the textures of the front screen on the given screen
	 * 
	 * @param screen
	 *            The given screen
	 * @param state
	 *            The given game state
	 */
	private void renderBeginningTextures(Screen screen, GameState state) {

		textureShader.bind();
		textureShader.setUniform("texture_sampler", 0);

		projectionMatrix = transformation.getOrthographicProjection(0f,
				screen.getWidth() * w_ratio, screen.getHeight() * h_ratio, 0f);
		textureShader.setUniform("projection", projectionMatrix);

		modelMatrix = transformation.getModelMatrix(Constants.GENERAL_BOX_X,
				Constants.GENERAL_BOX_Y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);
		textureMeshes.get("generalBoxMesh").render();

		textureShader.unbind();

	} // END OF renderBeginningTextures METHOD

	/**
	 * Render the huds of the front screen on the given screen
	 * 
	 * @param screen
	 *            The given screen
	 * @param state
	 *            The given game state
	 */
	private void renderBeginningHud(Screen screen, GameState state) {

		hudShader.bind();
		hudShader.setUniform("texture_sampler", 0);
		projectionMatrix = transformation.getOrthographicProjection(0,
				screen.getWidth() * w_ratio, screen.getHeight() * h_ratio, 0f);

		hudTextItem.setText("5 SECONDS");
		x = Constants.GENERAL_BOX_X
				+ (Constants.GENERAL_BOX_WIDTH / 2 - hudTextItem.getTextWidth() / 2);
		float y = Constants.GENERAL_BOX_Y
				+ (Constants.GENERAL_BOX_HEIGHT / 2 - hudTextItem
						.getTextHeight() / 2);
		modelMatrix = transformation.getModelMatrix(x, y,
				hudTextItem.getRotation(), hudTextItem.getScale());
		hudShader.setUniform("projModelMatrix", transformation
				.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
		hudShader.setUniform("colour", hudTextItem.getColour());
		hudTextItem.getMesh().render();

		hudShader.unbind();
	} // END OF renderBeginningHud METHOD

	// -------------------------------------Front Screen
	// Render------------------------------------------------------------------

	/**
	 * Render the textures of the pause screen on the given screen
	 * 
	 * @param screen
	 *            The given screen
	 * @param state
	 *            The given game state
	 */
	private void renderPauseTextures(Screen screen, GameState state) {

		textureShader.bind();
		textureShader.setUniform("texture_sampler", 0);

		projectionMatrix = transformation.getOrthographicProjection(0f,
				screen.getWidth() * w_ratio, screen.getHeight() * h_ratio, 0f);
		textureShader.setUniform("projection", projectionMatrix);

		modelMatrix = transformation.getModelMatrix(Constants.GENERAL_BOX_X,
				Constants.GENERAL_BOX_Y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);
		textureMeshes.get("generalBoxMesh").render();

		textureShader.unbind();
	} // END OF renderPauseScreen METHOD

	/**
	 * Render the huds of the pause screen on the given screen
	 * 
	 * @param screen
	 *            The given screen
	 * @param state
	 *            The given game state
	 */
	private void renderPauseHud(Screen screen, GameState state) {

		hudShader.bind();
		hudShader.setUniform("texture_sampler", 0);
		projectionMatrix = transformation.getOrthographicProjection(0,
				screen.getWidth() * w_ratio, screen.getHeight() * h_ratio, 0f);

		hudTextItem.setText("PRESS P TO UNPAUSE THE GAME");
		x = Constants.GENERAL_BOX_X
				+ (Constants.GENERAL_BOX_WIDTH / 2 - hudTextItem.getTextWidth() / 2);
		float y = Constants.GENERAL_BOX_Y
				+ (Constants.GENERAL_BOX_HEIGHT / 2 - hudTextItem
						.getTextHeight() / 2);
		modelMatrix = transformation.getModelMatrix(x, y,
				hudTextItem.getRotation(), hudTextItem.getScale());
		hudShader.setUniform("projModelMatrix", transformation
				.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
		hudShader.setUniform("colour", hudTextItem.getColour());
		hudTextItem.getMesh().render();
		hudShader.unbind();
	} // END OF renderPauseHud METHOD

	/**
	 * Stop the display of the front screen
	 */
	public void stopFrontScreen() {

		frontScreen = false;
	} // END OF stopFrontScreen METHOD

	/**
	 * Display game over screen
	 */
	public void displayGameOver() {

		gameOver = true;
	} // END OF displayGameOver METHOD

	/**
	 * Display the pause screen
	 */
	public void displayPauseScreen() {

		gamePaused = true;
	} // END OF displayPauseScreen METHOD

	/**
	 * Stop displaying the pause screen
	 */
	public void stopPauseScreen() {

		gamePaused = false;
	} // END OF stopPauseScreen METHOD

	/**
	 * Dispose the renderer and all its resources
	 */
	public void dispose() {

		hudTextItem.getMesh().dispose();

		for (String key : textureMeshes.keySet()) {

			textureMeshes.get(key).dispose();
		}

		if (sceneShader != null) {

			sceneShader.dispose();
		}
		if (hudShader != null) {

			hudShader.dispose();
		}
	} // END OF dispose METHOD

} // END OF Renderer CLASS