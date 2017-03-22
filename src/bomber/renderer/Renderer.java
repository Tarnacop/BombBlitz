package bomber.renderer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.awt.Font;
import java.util.HashMap;
import java.util.List;

import org.joml.Matrix4f;

import com.sun.prism.paint.Color;

import bomber.game.Block;
import bomber.game.Bomb;
import bomber.game.Constants;
import bomber.game.GameState;
import bomber.game.Movement;
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

	private ShaderProgram hudShader;
	private ShaderProgram textureShader;

	private final Transformation transformation;
	private Matrix4f projectionMatrix;
	private Matrix4f modelMatrix;

	private TextItem hudTextItem;
	private TextItem hudTextItemBig;

	private boolean gameOver;
	private boolean frontScreen;
	private boolean gamePaused;
	private boolean youWon;
	private boolean wasd;

	private HashMap<String, TextureMesh> textureMeshes;

	// 2D array mapping the map block to a different texture
	// Used for creating the blast animation
	private int[][] mapMapping;

	private float w_ratio;
	private float h_ratio;
	private float x; // General purpose x coord

	/**
	 * Create a Renderer object
	 */
	public Renderer(boolean wasd) {

		transformation = new Transformation();
		textureMeshes = new HashMap<String, TextureMesh>();
		gameOver = false;
		frontScreen = true;
		gamePaused = false;
		this.wasd = wasd;
		mapMapping = new int[13][13];

		for (int i = 0; i < mapMapping.length; i++) {
			for (int j = 0; j < mapMapping[i].length; j++) {

				mapMapping[i][j] = 0;
			}

		}
	}

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
	}

	/**
	 * Setup the shaders for rendering textures
	 * 
	 * @throws Exception
	 */
	private void setupTextureShader() throws Exception {

		textureShader = new ShaderProgram();

		textureShader.createVertexShader(FileHandler.loadShaderResource("src/resources/shaders/texture_vertex.vs"));
		textureShader.createFragmentShader(FileHandler.loadShaderResource("src/resources/shaders/texture_fragment.fs"));
		textureShader.link();

		textureShader.createUniform("projection");
		textureShader.createUniform("model");
		textureShader.createUniform("texture_sampler");

	}

	/**
	 * Setup the shaders for rendering hud
	 * 
	 * @throws Exception
	 */
	private void setupHudShader() throws Exception {

		hudShader = new ShaderProgram();
		hudShader.createVertexShader(FileHandler.loadShaderResource("src/resources/shaders/hud_vertex.vs"));
		hudShader.createFragmentShader(FileHandler.loadShaderResource("src/resources/shaders/hud_fragment.fs"));
		hudShader.link();

		hudShader.createUniform("projModelMatrix");
		hudShader.createUniform("colour");
		hudShader.createUniform("texture_sampler");

	}

	/**
	 * Setup the textures used for rendering
	 * 
	 * @throws Exception
	 */
	private void setupTextures() throws Exception {

		Texture background = new Texture("src/resources/images/gamebackground.png");
		Texture box = new Texture("src/resources/images/mapbox.png");
		Texture fancybox = new Texture("src/resources/images/fancybox.png");
		Texture heart = new Texture("src/resources/images/heart.png");
		Texture ingameBomb = new Texture("src/resources/images/bomb.png");
		Texture boot = new Texture("src/resources/images/boot.png");
		Texture blast = new Texture("src/resources/images/blast.png");
		Texture newspritesheet = new Texture("src/resources/images/newspritesheet.png");
		Texture controls = new Texture("src/resources/images/controls.png");
		Texture altcontrols = new Texture("src/resources/images/altcontrols.png");

		// Blocks
		TextureMesh blankMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				0 * Constants.SPRITESHEET_ELEM_WIDTH, 0 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("blankMesh", blankMesh);

		TextureMesh solidMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				1 * Constants.SPRITESHEET_ELEM_WIDTH, 0 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("solidMesh", solidMesh);

		TextureMesh softMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				2 * Constants.SPRITESHEET_ELEM_WIDTH, 0 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("softMesh", softMesh);

		TextureMesh holeMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				0 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("holeMesh", holeMesh);

		TextureMesh blastMesh1 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				0 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("blastMesh1", blastMesh1);

		TextureMesh blastMesh2 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				1 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("blastMesh2", blastMesh2);

		TextureMesh blastMesh3 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				0 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("blastMesh3", blastMesh3);

		TextureMesh blastMesh4 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				1 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("blastMesh4", blastMesh4);

		TextureMesh blastMesh5 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				0 * Constants.SPRITESHEET_ELEM_WIDTH, 6 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("blastMesh5", blastMesh5);

		TextureMesh blastMesh6 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				1 * Constants.SPRITESHEET_ELEM_WIDTH, 6 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("blastMesh6", blastMesh6);

		// Player 1
		TextureMesh ingamePlayerMesh1 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				2 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("ingamePlayerMesh1", ingamePlayerMesh1);

		TextureMesh ingamePlayerMesh2 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				3 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("ingamePlayerMesh2", ingamePlayerMesh2);

		TextureMesh ingamePlayerMesh3 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				4 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("ingamePlayerMesh3", ingamePlayerMesh3);

		TextureMesh downPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				2 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("downPlayerMesh1", downPlayerMesh1);

		TextureMesh downPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				3 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("downPlayerMesh2", downPlayerMesh2);

		TextureMesh downPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				4 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("downPlayerMesh3", downPlayerMesh3);

		TextureMesh deadPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				5 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("deadPlayerMesh1", deadPlayerMesh1);

		TextureMesh deadPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				6 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("deadPlayerMesh2", deadPlayerMesh2);

		TextureMesh deadPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				7 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("deadPlayerMesh3", deadPlayerMesh3);

		TextureMesh leftPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				8 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("leftPlayerMesh1", leftPlayerMesh1);

		TextureMesh leftPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				9 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("leftPlayerMesh2", leftPlayerMesh2);

		TextureMesh leftPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				10 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("leftPlayerMesh3", leftPlayerMesh3);

		TextureMesh upPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				11 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("upPlayerMesh1", upPlayerMesh1);

		TextureMesh upPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				12 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("upPlayerMesh2", upPlayerMesh2);

		TextureMesh upPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				13 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("upPlayerMesh3", upPlayerMesh3);

		TextureMesh rightPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				14 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("rightPlayerMesh1", rightPlayerMesh1);

		TextureMesh rightPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				15 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("rightPlayerMesh2", rightPlayerMesh2);

		TextureMesh rightPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				16 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("rightPlayerMesh3", rightPlayerMesh3);

		// Player 2
		TextureMesh secondIngamePlayerMesh1 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 2 * Constants.SPRITESHEET_ELEM_WIDTH,
				3 * Constants.SPRITESHEET_ELEM_HEIGHT, Constants.SPRITESHEET_ELEM_WIDTH,
				Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondIngamePlayerMesh1", secondIngamePlayerMesh1);

		TextureMesh secondIngamePlayerMesh2 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 3 * Constants.SPRITESHEET_ELEM_WIDTH,
				3 * Constants.SPRITESHEET_ELEM_HEIGHT, Constants.SPRITESHEET_ELEM_WIDTH,
				Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondIngamePlayerMesh2", secondIngamePlayerMesh2);

		TextureMesh secondIngamePlayerMesh3 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 4 * Constants.SPRITESHEET_ELEM_WIDTH,
				3 * Constants.SPRITESHEET_ELEM_HEIGHT, Constants.SPRITESHEET_ELEM_WIDTH,
				Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondIngamePlayerMesh3", secondIngamePlayerMesh3);

		TextureMesh secondDownPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				2 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondDownPlayerMesh1", secondDownPlayerMesh1);

		TextureMesh secondDownPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				3 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondDownPlayerMesh2", secondDownPlayerMesh2);

		TextureMesh secondDownPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				4 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondDownPlayerMesh3", secondDownPlayerMesh3);

		TextureMesh secondDeadPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				5 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondDeadPlayerMesh1", secondDeadPlayerMesh1);

		TextureMesh secondDeadPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				6 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondDeadPlayerMesh2", secondDeadPlayerMesh2);

		TextureMesh secondDeadPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				7 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondDeadPlayerMesh3", secondDeadPlayerMesh3);

		TextureMesh secondLeftPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				8 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondLeftPlayerMesh1", secondLeftPlayerMesh1);

		TextureMesh secondLeftPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				9 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondLeftPlayerMesh2", secondLeftPlayerMesh2);

		TextureMesh secondLeftPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				10 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondLeftPlayerMesh3", secondLeftPlayerMesh3);

		TextureMesh secondUpPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				11 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondUpPlayerMesh1", secondUpPlayerMesh1);

		TextureMesh secondUpPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				12 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondUpPlayerMesh2", secondUpPlayerMesh2);

		TextureMesh secondUpPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				13 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondUpPlayerMesh3", secondUpPlayerMesh3);

		TextureMesh secondRightPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				14 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondRightPlayerMesh1", secondRightPlayerMesh1);

		TextureMesh secondRightPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				15 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondRightPlayerMesh2", secondRightPlayerMesh2);

		TextureMesh secondRightPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				16 * Constants.SPRITESHEET_ELEM_WIDTH, 3 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("secondRightPlayerMesh3", secondRightPlayerMesh3);

		// Player 3
		TextureMesh thirdIngamePlayerMesh1 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 2 * Constants.SPRITESHEET_ELEM_WIDTH,
				4 * Constants.SPRITESHEET_ELEM_HEIGHT, Constants.SPRITESHEET_ELEM_WIDTH,
				Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdIngamePlayerMesh1", thirdIngamePlayerMesh1);

		TextureMesh thirdIngamePlayerMesh2 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 3 * Constants.SPRITESHEET_ELEM_WIDTH,
				4 * Constants.SPRITESHEET_ELEM_HEIGHT, Constants.SPRITESHEET_ELEM_WIDTH,
				Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdIngamePlayerMesh2", thirdIngamePlayerMesh2);

		TextureMesh thirdIngamePlayerMesh3 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 4 * Constants.SPRITESHEET_ELEM_WIDTH,
				4 * Constants.SPRITESHEET_ELEM_HEIGHT, Constants.SPRITESHEET_ELEM_WIDTH,
				Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdIngamePlayerMesh3", thirdIngamePlayerMesh3);

		TextureMesh thirdDownPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				2 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdDownPlayerMesh1", thirdDownPlayerMesh1);

		TextureMesh thirdDownPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				3 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdDownPlayerMesh2", thirdDownPlayerMesh2);

		TextureMesh thirdDownPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				4 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdDownPlayerMesh3", thirdDownPlayerMesh3);

		TextureMesh thirdDeadPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				5 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdDeadPlayerMesh1", thirdDeadPlayerMesh1);

		TextureMesh thirdDeadPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				6 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdDeadPlayerMesh2", thirdDeadPlayerMesh2);

		TextureMesh thirdDeadPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				7 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdDeadPlayerMesh3", thirdDeadPlayerMesh3);

		TextureMesh thirdLeftPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				8 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdLeftPlayerMesh1", thirdLeftPlayerMesh1);

		TextureMesh thirdLeftPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				9 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdLeftPlayerMesh2", thirdLeftPlayerMesh2);

		TextureMesh thirdLeftPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				10 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdLeftPlayerMesh3", thirdLeftPlayerMesh3);

		TextureMesh thirdUpPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				11 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdUpPlayerMesh1", thirdUpPlayerMesh1);

		TextureMesh thirdUpPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				12 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdUpPlayerMesh2", thirdUpPlayerMesh2);

		TextureMesh thirdUpPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				13 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdUpPlayerMesh3", thirdUpPlayerMesh3);

		TextureMesh thirdRightPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				14 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdRightPlayerMesh1", thirdRightPlayerMesh1);

		TextureMesh thirdRightPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				15 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdRightPlayerMesh2", thirdRightPlayerMesh2);

		TextureMesh thirdRightPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				16 * Constants.SPRITESHEET_ELEM_WIDTH, 4 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("thirdRightPlayerMesh3", thirdRightPlayerMesh3);

		// Player 4
		TextureMesh fourthIngamePlayerMesh1 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 2 * Constants.SPRITESHEET_ELEM_WIDTH,
				5 * Constants.SPRITESHEET_ELEM_HEIGHT, Constants.SPRITESHEET_ELEM_WIDTH,
				Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthIngamePlayerMesh1", fourthIngamePlayerMesh1);

		TextureMesh fourthIngamePlayerMesh2 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 3 * Constants.SPRITESHEET_ELEM_WIDTH,
				5 * Constants.SPRITESHEET_ELEM_HEIGHT, Constants.SPRITESHEET_ELEM_WIDTH,
				Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthIngamePlayerMesh2", fourthIngamePlayerMesh2);

		TextureMesh fourthIngamePlayerMesh3 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH,
				Constants.GENERAL_BLOCK_HEIGHT, 4 * Constants.SPRITESHEET_ELEM_WIDTH,
				5 * Constants.SPRITESHEET_ELEM_HEIGHT, Constants.SPRITESHEET_ELEM_WIDTH,
				Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthIngamePlayerMesh3", fourthIngamePlayerMesh3);

		TextureMesh fourthDownPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				2 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthDownPlayerMesh1", fourthDownPlayerMesh1);

		TextureMesh fourthDownPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				3 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthDownPlayerMesh2", fourthDownPlayerMesh2);

		TextureMesh fourthDownPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				4 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthDownPlayerMesh3", fourthDownPlayerMesh3);

		TextureMesh fourthDeadPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				5 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthDeadPlayerMesh1", fourthDeadPlayerMesh1);

		TextureMesh fourthDeadPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				6 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthDeadPlayerMesh2", fourthDeadPlayerMesh2);

		TextureMesh fourthDeadPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				7 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthDeadPlayerMesh3", fourthDeadPlayerMesh3);

		TextureMesh fourthLeftPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				8 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthLeftPlayerMesh1", fourthLeftPlayerMesh1);

		TextureMesh fourthLeftPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				9 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthLeftPlayerMesh2", fourthLeftPlayerMesh2);

		TextureMesh fourthLeftPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				10 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthLeftPlayerMesh3", fourthLeftPlayerMesh3);

		TextureMesh fourthUpPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				11 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthUpPlayerMesh1", fourthUpPlayerMesh1);

		TextureMesh fourthUpPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				12 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthUpPlayerMesh2", fourthUpPlayerMesh2);

		TextureMesh fourthUpPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				13 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthUpPlayerMesh3", fourthUpPlayerMesh3);

		TextureMesh fourthRightPlayerMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				14 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthRightPlayerMesh1", fourthRightPlayerMesh1);

		TextureMesh fourthRightPlayerMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				15 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthRightPlayerMesh2", fourthRightPlayerMesh2);

		TextureMesh fourthRightPlayerMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				16 * Constants.SPRITESHEET_ELEM_WIDTH, 5 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("fourthRightPlayerMesh3", fourthRightPlayerMesh3);

		// AI
		TextureMesh ingameAiMesh1 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				2 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("ingameAiMesh1", ingameAiMesh1);

		TextureMesh ingameAiMesh2 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				3 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("ingameAiMesh2", ingameAiMesh2);

		TextureMesh ingameAiMesh3 = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				4 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("ingameAiMesh3", ingameAiMesh3);

		TextureMesh downAiMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				2 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("downAiMesh1", downAiMesh1);

		TextureMesh downAiMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				3 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("downAiMesh2", downAiMesh2);

		TextureMesh downAiMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				4 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("downAiMesh3", downAiMesh3);

		TextureMesh deadAiMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				5 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("deadAiMesh1", deadAiMesh1);

		TextureMesh deadAiMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				6 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("deadAiMesh2", deadAiMesh2);

		TextureMesh deadAiMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				7 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("deadAiMesh3", deadAiMesh3);

		TextureMesh leftAiMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				8 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("leftAiMesh1", leftAiMesh1);

		TextureMesh leftAiMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				9 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("leftAiMesh2", leftAiMesh2);

		TextureMesh leftAiMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				10 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("leftAiMesh3", leftAiMesh3);

		TextureMesh upAiMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				11 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("upAiMesh1", upAiMesh1);

		TextureMesh upAiMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				12 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("upAiMesh2", upAiMesh2);

		TextureMesh upAiMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				13 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("upAiMesh3", upAiMesh3);

		TextureMesh rightAiMesh1 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				14 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("rightAiMesh1", rightAiMesh1);

		TextureMesh rightAiMesh2 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				15 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("rightAiMesh2", rightAiMesh2);

		TextureMesh rightAiMesh3 = new TextureMesh(Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT,
				16 * Constants.SPRITESHEET_ELEM_WIDTH, 2 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("rightAiMesh3", rightAiMesh3);

		// Bomb
		TextureMesh bombMesh1 = new TextureMesh(46, 46, 0 * Constants.SPRITESHEET_ELEM_WIDTH,
				1 * Constants.SPRITESHEET_ELEM_HEIGHT, Constants.SPRITESHEET_ELEM_WIDTH,
				Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("bombMesh1", bombMesh1);

		TextureMesh bombMesh2 = new TextureMesh(48, 48, 1 * Constants.SPRITESHEET_ELEM_WIDTH,
				1 * Constants.SPRITESHEET_ELEM_HEIGHT, Constants.SPRITESHEET_ELEM_WIDTH,
				Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("bombMesh2", bombMesh2);

		TextureMesh bombMesh3 = new TextureMesh(Constants.BOMB_WIDTH, Constants.BOMB_HEIGHT,
				0 * Constants.SPRITESHEET_ELEM_WIDTH, 1 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("bombMesh3", bombMesh3);

		// Powerups
		TextureMesh plusSpeedMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				4 * Constants.SPRITESHEET_ELEM_WIDTH, 0 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("plusSpeedMesh", plusSpeedMesh);

		TextureMesh minusSpeedMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				5 * Constants.SPRITESHEET_ELEM_WIDTH, 0 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("minusSpeedMesh", minusSpeedMesh);

		TextureMesh plusBombMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				6 * Constants.SPRITESHEET_ELEM_WIDTH, 0 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("plusBombMesh", plusBombMesh);

		TextureMesh minusBombMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				7 * Constants.SPRITESHEET_ELEM_WIDTH, 0 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("minusBombMesh", minusBombMesh);

		TextureMesh plusRangeMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				8 * Constants.SPRITESHEET_ELEM_WIDTH, 0 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("plusRangeMesh", plusRangeMesh);

		TextureMesh minusRangeMesh = new TextureMesh(Constants.GENERAL_BLOCK_WIDTH, Constants.GENERAL_BLOCK_HEIGHT,
				9 * Constants.SPRITESHEET_ELEM_WIDTH, 0 * Constants.SPRITESHEET_ELEM_HEIGHT,
				Constants.SPRITESHEET_ELEM_WIDTH, Constants.SPRITESHEET_ELEM_HEIGHT, newspritesheet);
		textureMeshes.put("minusRangeMesh", minusRangeMesh);

		// In-game design
		TextureMesh backgroundMesh = new TextureMesh(Constants.BACKGROUND_WIDTH, Constants.BACKGROUND_WIDTH,
				background);
		textureMeshes.put("backgroundMesh", backgroundMesh);

		TextureMesh infoBoxMesh = new TextureMesh(Constants.INFO_BOX_WIDTH, Constants.INFO_BOX_HEIGHT, box);
		textureMeshes.put("infoBoxMesh", infoBoxMesh);

		TextureMesh gameBoxMesh = new TextureMesh(Constants.GAME_BOX_WIDTH, Constants.GAME_BOX_HEIGHT, box);
		textureMeshes.put("gameBoxMesh", gameBoxMesh);

		TextureMesh fancyBoxMesh = new TextureMesh(Constants.FANCY_BOX_WIDTH, Constants.FANCY_BOX_HEIGHT, fancybox);
		textureMeshes.put("fancyBoxMesh", fancyBoxMesh);

		TextureMesh beginningBoxMesh = new TextureMesh(Constants.GENERAL_BOX_WIDTH, Constants.GENERAL_BOX_HEIGHT, box);
		textureMeshes.put("generalBoxMesh", beginningBoxMesh);

		TextureMesh heartMesh = new TextureMesh(Constants.HEART_WIDTH, Constants.HEART_HEIGHT, heart);
		textureMeshes.put("heartMesh", heartMesh);

		TextureMesh ingameBombMesh = new TextureMesh(Constants.HEART_WIDTH, Constants.HEART_HEIGHT, ingameBomb);
		textureMeshes.put("ingameBombMesh", ingameBombMesh);

		TextureMesh bootMesh = new TextureMesh(Constants.HEART_WIDTH, Constants.HEART_HEIGHT, boot);
		textureMeshes.put("bootMesh", bootMesh);

		TextureMesh ingameBlastMesh = new TextureMesh(Constants.HEART_WIDTH, Constants.HEART_HEIGHT, blast);
		textureMeshes.put("ingameBlastMesh", ingameBlastMesh);

		TextureMesh controlsMesh = new TextureMesh(Constants.CONTROLS_WIDTH, Constants.CONTROLS_HEIGHT, controls);
		textureMeshes.put("controlsMesh", controlsMesh);

		TextureMesh altControlsMesh = new TextureMesh(Constants.CONTROLS_WIDTH, Constants.CONTROLS_HEIGHT, altcontrols);
		textureMeshes.put("altControlsMesh", altControlsMesh);
	}

	/**
	 * Setup the huds used for rendering
	 * 
	 * @throws Exception
	 */
	private void setupHuds() throws Exception {

		FontTexture hudFontTexture = new FontTexture("src/resources/minecraftbig.ttf", 25, Font.PLAIN);
		hudTextItem = new TextItem("", hudFontTexture);

		hudFontTexture = new FontTexture("src/resources/minecraftbig.ttf", 35, Font.PLAIN);
		hudTextItemBig = new TextItem("", hudFontTexture);
	}

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
	}

	// -------------------------------------Game Screen
	// Render--------------------------

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

		projectionMatrix = transformation.getOrthographicProjection(0f, screen.getWidth() * w_ratio,
				screen.getHeight() * h_ratio, 0f);
		textureShader.setUniform("projection", projectionMatrix);

		// Render background
		modelMatrix = transformation.getModelMatrix(Constants.BACKGROUND_X, Constants.BACKGROUND_Y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);
		textureMeshes.get("backgroundMesh").render();

		// Render info box
		modelMatrix = transformation.getModelMatrix(Constants.INFO_BOX_X, Constants.INFO_BOX_Y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);
		textureMeshes.get("infoBoxMesh").render();

		// Render game box
		modelMatrix = transformation.getModelMatrix(Constants.GAME_BOX_X, Constants.GAME_BOX_Y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);
		textureMeshes.get("gameBoxMesh").render();

		// Render player info box
		if (state.getPlayers().size() > 0) {

			modelMatrix = transformation.getModelMatrix(Constants.FANCY_BOX1_X, Constants.FANCY_BOX1_Y, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("fancyBoxMesh").render();

			float y = Constants.FANCY_BOX1_Y + (Constants.FANCY_BOX_HEIGHT / 2 - Constants.PLAYER_HEIGHT / 2);
			modelMatrix = transformation.getModelMatrix(Constants.FANCY_BOX1_X + 15, y, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			if (state.getPlayers().get(0).getPlayerID() > 31) {

				if (playerAnimationCounter < 5) {
					textureMeshes.get("ingameAiMesh1").render();
				} else if (playerAnimationCounter < 10) {

					textureMeshes.get("ingameAiMesh2").render();
				} else if (playerAnimationCounter < 15) {

					textureMeshes.get("ingameAiMesh3").render();
				}
			} else {

				if (playerAnimationCounter < 5) {

					textureMeshes.get("ingamePlayerMesh1").render();
				} else if (playerAnimationCounter < 10) {

					textureMeshes.get("ingamePlayerMesh2").render();
				} else if (playerAnimationCounter < 15) {

					textureMeshes.get("ingamePlayerMesh3").render();
				}
			}

			x = Constants.FANCY_BOX1_X + Constants.FANCY_BOX_WIDTH / 4;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX1_Y + Constants.BOX_PADDING - 7, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("heartMesh").render();

			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX1_Y + 2 * Constants.BOX_PADDING - 7, 0f,
					1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("ingameBombMesh").render();

			x = x + Constants.FANCY_BOX_WIDTH / 4 + 20;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX1_Y + Constants.BOX_PADDING - 7, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("bootMesh").render();

			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX1_Y + 2 * Constants.BOX_PADDING - 7, 0f,
					1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("ingameBlastMesh").render();

		}

		if (state.getPlayers().size() > 1) {

			modelMatrix = transformation.getModelMatrix(Constants.FANCY_BOX2_X, Constants.FANCY_BOX2_Y, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("fancyBoxMesh").render();

			float y = Constants.FANCY_BOX2_Y + (Constants.FANCY_BOX_HEIGHT / 2 - Constants.PLAYER_HEIGHT / 2);
			modelMatrix = transformation.getModelMatrix(Constants.FANCY_BOX2_X + 15, y, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			if (state.getPlayers().get(1).getPlayerID() > 31) {

				if (playerAnimationCounter < 5) {

					textureMeshes.get("ingameAiMesh1").render();
				} else if (playerAnimationCounter < 10) {

					textureMeshes.get("ingameAiMesh2").render();
				} else if (playerAnimationCounter < 15) {

					textureMeshes.get("ingameAiMesh3").render();
				}
			} else {

				if (playerAnimationCounter < 5) {

					textureMeshes.get("secondIngamePlayerMesh1").render();
				} else if (playerAnimationCounter < 10) {

					textureMeshes.get("secondIngamePlayerMesh2").render();
				} else if (playerAnimationCounter < 15) {

					textureMeshes.get("secondIngamePlayerMesh3").render();
				}
			}

			x = Constants.FANCY_BOX2_X + Constants.FANCY_BOX_WIDTH / 4;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX2_Y + Constants.BOX_PADDING - 7, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("heartMesh").render();

			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX2_Y + 2 * Constants.BOX_PADDING - 7, 0f,
					1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("ingameBombMesh").render();

			x = x + Constants.FANCY_BOX_WIDTH / 4 + 20;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX2_Y + Constants.BOX_PADDING - 7, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("bootMesh").render();

			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX2_Y + 2 * Constants.BOX_PADDING - 7, 0f,
					1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("ingameBlastMesh").render();
		}

		if (state.getPlayers().size() > 2) {

			modelMatrix = transformation.getModelMatrix(Constants.FANCY_BOX3_X, Constants.FANCY_BOX3_Y, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("fancyBoxMesh").render();

			float y = Constants.FANCY_BOX3_Y + (Constants.FANCY_BOX_HEIGHT / 2 - Constants.PLAYER_HEIGHT / 2);
			modelMatrix = transformation.getModelMatrix(Constants.FANCY_BOX3_X + 15, y, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			if (state.getPlayers().get(2).getPlayerID() > 31) {

				if (playerAnimationCounter < 5) {

					textureMeshes.get("ingameAiMesh1").render();
				} else if (playerAnimationCounter < 10) {

					textureMeshes.get("ingameAiMesh2").render();
				} else if (playerAnimationCounter < 15) {

					textureMeshes.get("ingameAiMesh3").render();
				}
			} else {

				if (playerAnimationCounter < 5) {

					textureMeshes.get("thirdIngamePlayerMesh1").render();
				} else if (playerAnimationCounter < 10) {

					textureMeshes.get("thirdIngamePlayerMesh2").render();
				} else if (playerAnimationCounter < 15) {

					textureMeshes.get("thirdIngamePlayerMesh3").render();
				}

			}

			x = Constants.FANCY_BOX3_X + Constants.FANCY_BOX_WIDTH / 4;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX3_Y + Constants.BOX_PADDING - 7, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("heartMesh").render();

			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX3_Y + 2 * Constants.BOX_PADDING - 7, 0f,
					1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("ingameBombMesh").render();

			x = x + Constants.FANCY_BOX_WIDTH / 4 + 20;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX3_Y + Constants.BOX_PADDING - 7, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("bootMesh").render();

			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX3_Y + 2 * Constants.BOX_PADDING - 7, 0f,
					1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("ingameBlastMesh").render();
		}

		if (state.getPlayers().size() > 3) {

			modelMatrix = transformation.getModelMatrix(Constants.FANCY_BOX4_X, Constants.FANCY_BOX4_Y, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("fancyBoxMesh").render();

			float y = Constants.FANCY_BOX4_Y + (Constants.FANCY_BOX_HEIGHT / 2 - Constants.PLAYER_HEIGHT / 2);
			modelMatrix = transformation.getModelMatrix(Constants.FANCY_BOX4_X + 15, y, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			if (state.getPlayers().get(3).getPlayerID() > 31) {

				if (playerAnimationCounter < 5) {

					textureMeshes.get("ingameAiMesh1").render();
				} else if (playerAnimationCounter < 10) {

					textureMeshes.get("ingameAiMesh2").render();
				} else if (playerAnimationCounter < 15) {

					textureMeshes.get("ingameAiMesh3").render();
				}
			} else {

				if (playerAnimationCounter < 5) {

					textureMeshes.get("fourthIngamePlayerMesh1").render();
				} else if (playerAnimationCounter < 10) {

					textureMeshes.get("fourthIngamePlayerMesh2").render();
				} else if (playerAnimationCounter < 15) {

					textureMeshes.get("fourthIngamePlayerMesh3").render();
				}
			}

			x = Constants.FANCY_BOX4_X + Constants.FANCY_BOX_WIDTH / 4;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX4_Y + Constants.BOX_PADDING - 7, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("heartMesh").render();

			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX4_Y + 2 * Constants.BOX_PADDING - 7, 0f,
					1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("ingameBombMesh").render();

			x = x + Constants.FANCY_BOX_WIDTH / 4 + 20;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX4_Y + Constants.BOX_PADDING - 7, 0f, 1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("bootMesh").render();

			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX4_Y + 2 * Constants.BOX_PADDING - 7, 0f,
					1f);
			textureShader.setUniform("model", modelMatrix);
			textureMeshes.get("ingameBlastMesh").render();
		}

		Block[][] blocks = state.getMap().getGridMap();
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[0].length; j++) {

				switch (blocks[i][j]) {

				case BLANK:
					modelMatrix = transformation.getModelMatrix(i * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15,
							j * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f, 1f);
					textureShader.setUniform("model", modelMatrix);

					if (mapMapping[i][j] > 18) {

						mapMapping[i][j] = 0;
					}

					if (mapMapping[i][j] == 1 || mapMapping[i][j] == 2 || mapMapping[i][j] == 3) {

						textureMeshes.get("blastMesh1").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 4 || mapMapping[i][j] == 5 || mapMapping[i][j] == 6) {

						textureMeshes.get("blastMesh2").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 7 || mapMapping[i][j] == 8 || mapMapping[i][j] == 9) {

						textureMeshes.get("blastMesh3").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 10 || mapMapping[i][j] == 11 || mapMapping[i][j] == 12) {

						textureMeshes.get("blastMesh4").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 13 || mapMapping[i][j] == 14 || mapMapping[i][j] == 15) {

						textureMeshes.get("blastMesh5").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 16 || mapMapping[i][j] == 17 || mapMapping[i][j] == 18) {

						textureMeshes.get("blastMesh6").render();
						mapMapping[i][j]++;
					} else {

						textureMeshes.get("blankMesh").render();
					}
					break;
				case SOFT:
					modelMatrix = transformation.getModelMatrix(i * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15,
							j * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f, 1f);
					textureShader.setUniform("model", modelMatrix);

					if (mapMapping[i][j] > 18) {

						mapMapping[i][j] = 0;
					}

					if (mapMapping[i][j] == 1 || mapMapping[i][j] == 2 || mapMapping[i][j] == 3) {

						textureMeshes.get("blastMesh1").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 4 || mapMapping[i][j] == 5 || mapMapping[i][j] == 6) {

						textureMeshes.get("blastMesh2").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 7 || mapMapping[i][j] == 8 || mapMapping[i][j] == 9) {

						textureMeshes.get("blastMesh3").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 10 || mapMapping[i][j] == 11 || mapMapping[i][j] == 12) {

						textureMeshes.get("blastMesh4").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 13 || mapMapping[i][j] == 14 || mapMapping[i][j] == 15) {

						textureMeshes.get("blastMesh5").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 16 || mapMapping[i][j] == 17 || mapMapping[i][j] == 18) {

						textureMeshes.get("blastMesh6").render();
						mapMapping[i][j]++;
					} else {

						textureMeshes.get("softMesh").render();
					}
					break;
				case SOLID:
					modelMatrix = transformation.getModelMatrix(i * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15,
							j * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f, 1f);
					textureShader.setUniform("model", modelMatrix);
					textureMeshes.get("solidMesh").render();
					break;
				case BLAST:
					modelMatrix = transformation.getModelMatrix(i * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15,
							j * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f, 1f);
					textureShader.setUniform("model", modelMatrix);
					textureMeshes.get("blastMesh1").render();
					mapMapping[i][j] = 1;
					break;
				case PLUS_SPEED:
					modelMatrix = transformation.getModelMatrix(i * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15,
							j * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f, 1f);
					textureShader.setUniform("model", modelMatrix);

					if (mapMapping[i][j] > 18) {

						mapMapping[i][j] = 0;
					}

					if (mapMapping[i][j] == 1 || mapMapping[i][j] == 2 || mapMapping[i][j] == 3) {

						textureMeshes.get("blastMesh1").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 4 || mapMapping[i][j] == 5 || mapMapping[i][j] == 6) {

						textureMeshes.get("blastMesh2").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 7 || mapMapping[i][j] == 8 || mapMapping[i][j] == 9) {

						textureMeshes.get("blastMesh3").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 10 || mapMapping[i][j] == 11 || mapMapping[i][j] == 12) {

						textureMeshes.get("blastMesh4").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 13 || mapMapping[i][j] == 14 || mapMapping[i][j] == 15) {

						textureMeshes.get("blastMesh5").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 16 || mapMapping[i][j] == 17 || mapMapping[i][j] == 18) {

						textureMeshes.get("blastMesh6").render();
						mapMapping[i][j]++;
					} else {

						textureMeshes.get("plusSpeedMesh").render();
					}
					break;
				case MINUS_SPEED:
					modelMatrix = transformation.getModelMatrix(i * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15,
							j * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f, 1f);
					textureShader.setUniform("model", modelMatrix);

					if (mapMapping[i][j] > 18) {

						mapMapping[i][j] = 0;
					}

					if (mapMapping[i][j] == 1 || mapMapping[i][j] == 2 || mapMapping[i][j] == 3) {

						textureMeshes.get("blastMesh1").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 4 || mapMapping[i][j] == 5 || mapMapping[i][j] == 6) {

						textureMeshes.get("blastMesh2").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 7 || mapMapping[i][j] == 8 || mapMapping[i][j] == 9) {

						textureMeshes.get("blastMesh3").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 10 || mapMapping[i][j] == 11 || mapMapping[i][j] == 12) {

						textureMeshes.get("blastMesh4").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 13 || mapMapping[i][j] == 14 || mapMapping[i][j] == 15) {

						textureMeshes.get("blastMesh5").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 16 || mapMapping[i][j] == 17 || mapMapping[i][j] == 18) {

						textureMeshes.get("blastMesh6").render();
						mapMapping[i][j]++;
					} else {

						textureMeshes.get("minusSpeedMesh").render();
					}
					break;
				case PLUS_BOMB:
					modelMatrix = transformation.getModelMatrix(i * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15,
							j * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f, 1f);
					textureShader.setUniform("model", modelMatrix);

					if (mapMapping[i][j] > 18) {

						mapMapping[i][j] = 0;
					}

					if (mapMapping[i][j] == 1 || mapMapping[i][j] == 2 || mapMapping[i][j] == 3) {

						textureMeshes.get("blastMesh1").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 4 || mapMapping[i][j] == 5 || mapMapping[i][j] == 6) {

						textureMeshes.get("blastMesh2").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 7 || mapMapping[i][j] == 8 || mapMapping[i][j] == 9) {

						textureMeshes.get("blastMesh3").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 10 || mapMapping[i][j] == 11 || mapMapping[i][j] == 12) {

						textureMeshes.get("blastMesh4").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 13 || mapMapping[i][j] == 14 || mapMapping[i][j] == 15) {

						textureMeshes.get("blastMesh5").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 16 || mapMapping[i][j] == 17 || mapMapping[i][j] == 18) {

						textureMeshes.get("blastMesh6").render();
						mapMapping[i][j]++;
					} else {

						textureMeshes.get("plusBombMesh").render();
					}
					break;
				case MINUS_BOMB:
					modelMatrix = transformation.getModelMatrix(i * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15,
							j * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f, 1f);
					textureShader.setUniform("model", modelMatrix);

					if (mapMapping[i][j] > 18) {

						mapMapping[i][j] = 0;
					}

					if (mapMapping[i][j] == 1 || mapMapping[i][j] == 2 || mapMapping[i][j] == 3) {

						textureMeshes.get("blastMesh1").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 4 || mapMapping[i][j] == 5 || mapMapping[i][j] == 6) {

						textureMeshes.get("blastMesh2").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 7 || mapMapping[i][j] == 8 || mapMapping[i][j] == 9) {

						textureMeshes.get("blastMesh3").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 10 || mapMapping[i][j] == 11 || mapMapping[i][j] == 12) {

						textureMeshes.get("blastMesh4").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 13 || mapMapping[i][j] == 14 || mapMapping[i][j] == 15) {

						textureMeshes.get("blastMesh5").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 16 || mapMapping[i][j] == 17 || mapMapping[i][j] == 18) {

						textureMeshes.get("blastMesh6").render();
						mapMapping[i][j]++;
					} else {

						textureMeshes.get("minusBombMesh").render();
					}
					break;
				case PLUS_RANGE:
					modelMatrix = transformation.getModelMatrix(i * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15,
							j * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f, 1f);
					textureShader.setUniform("model", modelMatrix);

					if (mapMapping[i][j] > 18) {

						mapMapping[i][j] = 0;
					}

					if (mapMapping[i][j] == 1 || mapMapping[i][j] == 2 || mapMapping[i][j] == 3) {

						textureMeshes.get("blastMesh1").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 4 || mapMapping[i][j] == 5 || mapMapping[i][j] == 6) {

						textureMeshes.get("blastMesh2").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 7 || mapMapping[i][j] == 8 || mapMapping[i][j] == 9) {

						textureMeshes.get("blastMesh3").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 10 || mapMapping[i][j] == 11 || mapMapping[i][j] == 12) {

						textureMeshes.get("blastMesh4").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 13 || mapMapping[i][j] == 14 || mapMapping[i][j] == 15) {

						textureMeshes.get("blastMesh5").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 16 || mapMapping[i][j] == 17 || mapMapping[i][j] == 18) {

						textureMeshes.get("blastMesh6").render();
						mapMapping[i][j]++;
					} else {

						textureMeshes.get("plusRangeMesh").render();
					}
					break;
				case MINUS_RANGE:
					modelMatrix = transformation.getModelMatrix(i * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15,
							j * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f, 1f);
					textureShader.setUniform("model", modelMatrix);

					if (mapMapping[i][j] > 18) {

						mapMapping[i][j] = 0;
					}

					if (mapMapping[i][j] == 1 || mapMapping[i][j] == 2 || mapMapping[i][j] == 3) {

						textureMeshes.get("blastMesh1").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 4 || mapMapping[i][j] == 5 || mapMapping[i][j] == 6) {

						textureMeshes.get("blastMesh2").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 7 || mapMapping[i][j] == 8 || mapMapping[i][j] == 9) {

						textureMeshes.get("blastMesh3").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 10 || mapMapping[i][j] == 11 || mapMapping[i][j] == 12) {

						textureMeshes.get("blastMesh4").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 13 || mapMapping[i][j] == 14 || mapMapping[i][j] == 15) {

						textureMeshes.get("blastMesh5").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 16 || mapMapping[i][j] == 17 || mapMapping[i][j] == 18) {

						textureMeshes.get("blastMesh6").render();
						mapMapping[i][j]++;
					} else {

						textureMeshes.get("minusRangeMesh").render();
					}
					break;
				case HOLE:
					modelMatrix = transformation.getModelMatrix(i * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15,
							j * Constants.MAP_BLOCK_TO_GRID_MULTIPLIER + 15, 0f, 1f);
					textureShader.setUniform("model", modelMatrix);

					if (mapMapping[i][j] > 18) {

						mapMapping[i][j] = 0;
					}

					if (mapMapping[i][j] == 1 || mapMapping[i][j] == 2 || mapMapping[i][j] == 3) {

						textureMeshes.get("blastMesh1").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 5 || mapMapping[i][j] == 5 || mapMapping[i][j] == 6) {

						textureMeshes.get("blastMesh2").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 7 || mapMapping[i][j] == 8 || mapMapping[i][j] == 9) {

						textureMeshes.get("blastMesh3").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 10 || mapMapping[i][j] == 11 || mapMapping[i][j] == 12) {

						textureMeshes.get("blastMesh4").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 13 || mapMapping[i][j] == 14 || mapMapping[i][j] == 15) {

						textureMeshes.get("blastMesh5").render();
						mapMapping[i][j]++;
					} else if (mapMapping[i][j] == 16 || mapMapping[i][j] == 17 || mapMapping[i][j] == 18) {

						textureMeshes.get("blastMesh6").render();
						mapMapping[i][j]++;
					} else {

						textureMeshes.get("holeMesh").render();
					}
					break;
				}
			}
		}

		List<Bomb> boombList = state.getBombs();
		synchronized (boombList) {
			for (Bomb bomb : boombList) {
				modelMatrix = transformation.getModelMatrix((float) bomb.getPos().x + 15, (float) bomb.getPos().y + 15,
						0f, 1f);
				textureShader.setUniform("model", modelMatrix);

				if (playerAnimationCounter < 5) {

					textureMeshes.get("bombMesh1").render();
				} else if (playerAnimationCounter < 10) {

					textureMeshes.get("bombMesh2").render();
				} else if (playerAnimationCounter < 15) {

					textureMeshes.get("bombMesh3").render();
				}
			}
		}

		List<Player> playerList = state.getPlayers();
		synchronized (playerList) {
			for (int i = 0; i < playerList.size(); i++) {
				Player player = playerList.get(i);
				modelMatrix = transformation.getModelMatrix((float) player.getPos().x + 15,
						(float) player.getPos().y + 15, 0f, 1f);
				textureShader.setUniform("model", modelMatrix);
				if (player.getPlayerID() > 31) {
					if (player.isAlive()) {

						if (playerAnimationCounter < 5) {

							if (player.getKeyState().getMovement() == Movement.RIGHT) {

								textureMeshes.get("rightAiMesh1").render();
							} else if (player.getKeyState().getMovement() == Movement.UP) {

								textureMeshes.get("upAiMesh1").render();
							} else if (player.getKeyState().getMovement() == Movement.LEFT) {

								textureMeshes.get("leftAiMesh1").render();
							} else if (player.getKeyState().getMovement() == Movement.DOWN
									|| player.getKeyState().getMovement() == Movement.NONE) {

								textureMeshes.get("downAiMesh1").render();
							}
						} else if (playerAnimationCounter < 10) {

							if (player.getKeyState().getMovement() == Movement.RIGHT) {

								textureMeshes.get("rightAiMesh2").render();
							} else if (player.getKeyState().getMovement() == Movement.UP) {

								textureMeshes.get("upAiMesh2").render();
							} else if (player.getKeyState().getMovement() == Movement.LEFT) {

								textureMeshes.get("leftAiMesh2").render();
							} else if (player.getKeyState().getMovement() == Movement.DOWN
									|| player.getKeyState().getMovement() == Movement.NONE) {

								textureMeshes.get("downAiMesh2").render();
							}
						} else if (playerAnimationCounter < 15) {

							if (player.getKeyState().getMovement() == Movement.RIGHT) {

								textureMeshes.get("rightAiMesh3").render();
							} else if (player.getKeyState().getMovement() == Movement.UP) {

								textureMeshes.get("upAiMesh3").render();
							} else if (player.getKeyState().getMovement() == Movement.LEFT) {

								textureMeshes.get("leftAiMesh3").render();
							} else if (player.getKeyState().getMovement() == Movement.DOWN
									|| player.getKeyState().getMovement() == Movement.NONE) {

								textureMeshes.get("downAiMesh3").render();
							}
						}
					} else {

						if (playerAnimationCounter < 5) {

							textureMeshes.get("deadAiMesh1").render();
						} else if (playerAnimationCounter < 10) {

							textureMeshes.get("deadAiMesh2").render();
						} else if (playerAnimationCounter < 15) {

							textureMeshes.get("deadAiMesh3").render();
						}
					}
				} else {

					if (player.isAlive()) {

						if (i == 0) {

							if (playerAnimationCounter < 5) {

								if (player.getKeyState().getMovement() == Movement.RIGHT) {

									textureMeshes.get("rightPlayerMesh1").render();
								} else if (player.getKeyState().getMovement() == Movement.UP) {

									textureMeshes.get("upPlayerMesh1").render();
								} else if (player.getKeyState().getMovement() == Movement.LEFT) {

									textureMeshes.get("leftPlayerMesh1").render();
								} else if (player.getKeyState().getMovement() == Movement.DOWN
										|| player.getKeyState().getMovement() == Movement.NONE) {

									textureMeshes.get("downPlayerMesh1").render();
								}
							} else if (playerAnimationCounter < 10) {

								if (player.getKeyState().getMovement() == Movement.RIGHT) {

									textureMeshes.get("rightPlayerMesh2").render();
								} else if (player.getKeyState().getMovement() == Movement.UP) {

									textureMeshes.get("upPlayerMesh2").render();
								} else if (player.getKeyState().getMovement() == Movement.LEFT) {

									textureMeshes.get("leftPlayerMesh2").render();
								} else if (player.getKeyState().getMovement() == Movement.DOWN
										|| player.getKeyState().getMovement() == Movement.NONE) {

									textureMeshes.get("downPlayerMesh2").render();
								}
							} else if (playerAnimationCounter < 15) {

								if (player.getKeyState().getMovement() == Movement.RIGHT) {

									textureMeshes.get("rightPlayerMesh3").render();
								} else if (player.getKeyState().getMovement() == Movement.UP) {

									textureMeshes.get("upPlayerMesh3").render();
								} else if (player.getKeyState().getMovement() == Movement.LEFT) {

									textureMeshes.get("leftPlayerMesh3").render();
								} else if (player.getKeyState().getMovement() == Movement.DOWN
										|| player.getKeyState().getMovement() == Movement.NONE) {

									textureMeshes.get("downPlayerMesh3").render();
								}

							}
						} else if (i == 1) {

							if (playerAnimationCounter < 5) {

								if (player.getKeyState().getMovement() == Movement.RIGHT) {

									textureMeshes.get("secondRightPlayerMesh1").render();
								} else if (player.getKeyState().getMovement() == Movement.UP) {

									textureMeshes.get("secondUpPlayerMesh1").render();
								} else if (player.getKeyState().getMovement() == Movement.LEFT) {

									textureMeshes.get("secondLeftPlayerMesh1").render();
								} else if (player.getKeyState().getMovement() == Movement.DOWN
										|| player.getKeyState().getMovement() == Movement.NONE) {

									textureMeshes.get("secondDownPlayerMesh1").render();
								}
							} else if (playerAnimationCounter < 10) {

								if (player.getKeyState().getMovement() == Movement.RIGHT) {

									textureMeshes.get("secondRightPlayerMesh2").render();
								} else if (player.getKeyState().getMovement() == Movement.UP) {

									textureMeshes.get("secondUpPlayerMesh2").render();
								} else if (player.getKeyState().getMovement() == Movement.LEFT) {

									textureMeshes.get("secondLeftPlayerMesh2").render();
								} else if (player.getKeyState().getMovement() == Movement.DOWN
										|| player.getKeyState().getMovement() == Movement.NONE) {

									textureMeshes.get("secondDownPlayerMesh2").render();
								}
							} else if (playerAnimationCounter < 15) {

								if (player.getKeyState().getMovement() == Movement.RIGHT) {

									textureMeshes.get("secondRightPlayerMesh3").render();
								} else if (player.getKeyState().getMovement() == Movement.UP) {

									textureMeshes.get("secondUpPlayerMesh3").render();
								} else if (player.getKeyState().getMovement() == Movement.LEFT) {

									textureMeshes.get("secondLeftPlayerMesh3").render();
								} else if (player.getKeyState().getMovement() == Movement.DOWN
										|| player.getKeyState().getMovement() == Movement.NONE) {

									textureMeshes.get("secondDownPlayerMesh3").render();
								}
							}
						} else if (i == 2) {

							if (playerAnimationCounter < 5) {

								if (player.getKeyState().getMovement() == Movement.RIGHT) {

									textureMeshes.get("thirdRightPlayerMesh1").render();
								} else if (player.getKeyState().getMovement() == Movement.UP) {

									textureMeshes.get("thirdUpPlayerMesh1").render();
								} else if (player.getKeyState().getMovement() == Movement.LEFT) {

									textureMeshes.get("thirdLeftPlayerMesh1").render();
								} else if (player.getKeyState().getMovement() == Movement.DOWN
										|| player.getKeyState().getMovement() == Movement.NONE) {

									textureMeshes.get("thirdDownPlayerMesh1").render();
								}
							} else if (playerAnimationCounter < 10) {

								if (player.getKeyState().getMovement() == Movement.RIGHT) {

									textureMeshes.get("thirdRightPlayerMesh2").render();
								} else if (player.getKeyState().getMovement() == Movement.UP) {

									textureMeshes.get("thirdUpPlayerMesh2").render();
								} else if (player.getKeyState().getMovement() == Movement.LEFT) {

									textureMeshes.get("thirdLeftPlayerMesh2").render();
								} else if (player.getKeyState().getMovement() == Movement.DOWN
										|| player.getKeyState().getMovement() == Movement.NONE) {

									textureMeshes.get("thirdDownPlayerMesh2").render();
								}
							} else if (playerAnimationCounter < 15) {

								if (player.getKeyState().getMovement() == Movement.RIGHT) {

									textureMeshes.get("thirdRightPlayerMesh3").render();
								} else if (player.getKeyState().getMovement() == Movement.UP) {

									textureMeshes.get("thirdUpPlayerMesh3").render();
								} else if (player.getKeyState().getMovement() == Movement.LEFT) {

									textureMeshes.get("thirdLeftPlayerMesh3").render();
								} else if (player.getKeyState().getMovement() == Movement.DOWN
										|| player.getKeyState().getMovement() == Movement.NONE) {

									textureMeshes.get("thirdDownPlayerMesh3").render();
								}
							}
						} else if (i == 3) {
							if (playerAnimationCounter < 5) {

								if (player.getKeyState().getMovement() == Movement.RIGHT) {

									textureMeshes.get("fourthRightPlayerMesh1").render();
								} else if (player.getKeyState().getMovement() == Movement.UP) {

									textureMeshes.get("fourthUpPlayerMesh1").render();
								} else if (player.getKeyState().getMovement() == Movement.LEFT) {

									textureMeshes.get("fourthLeftPlayerMesh1").render();
								} else if (player.getKeyState().getMovement() == Movement.DOWN
										|| player.getKeyState().getMovement() == Movement.NONE) {

									textureMeshes.get("fourthDownPlayerMesh1").render();
								}
							} else if (playerAnimationCounter < 10) {

								if (player.getKeyState().getMovement() == Movement.RIGHT) {

									textureMeshes.get("fourthRightPlayerMesh2").render();
								} else if (player.getKeyState().getMovement() == Movement.UP) {

									textureMeshes.get("fourthUpPlayerMesh2").render();
								} else if (player.getKeyState().getMovement() == Movement.LEFT) {

									textureMeshes.get("fourthLeftPlayerMesh2").render();
								} else if (player.getKeyState().getMovement() == Movement.DOWN
										|| player.getKeyState().getMovement() == Movement.NONE) {

									textureMeshes.get("fourthDownPlayerMesh2").render();
								}
							} else if (playerAnimationCounter < 15) {

								if (player.getKeyState().getMovement() == Movement.RIGHT) {

									textureMeshes.get("fourthRightPlayerMesh3").render();
								} else if (player.getKeyState().getMovement() == Movement.UP) {

									textureMeshes.get("fourthUpPlayerMesh3").render();
								} else if (player.getKeyState().getMovement() == Movement.LEFT) {

									textureMeshes.get("fourthLeftPlayerMesh3").render();
								} else if (player.getKeyState().getMovement() == Movement.DOWN
										|| player.getKeyState().getMovement() == Movement.NONE) {

									textureMeshes.get("fourthDownPlayerMesh3").render();
								}
							}

						}
					} else {

						if (i == 0) {
							if (playerAnimationCounter < 5) {

								textureMeshes.get("deadPlayerMesh1").render();
							} else if (playerAnimationCounter < 10) {

								textureMeshes.get("deadPlayerMesh2").render();
							} else if (playerAnimationCounter < 15) {

								textureMeshes.get("deadPlayerMesh3").render();
							}
						} else if (i == 1) {

							if (playerAnimationCounter < 5) {

								textureMeshes.get("secondDeadPlayerMesh1").render();
							} else if (playerAnimationCounter < 10) {

								textureMeshes.get("secondDeadPlayerMesh2").render();
							} else if (playerAnimationCounter < 15) {

								textureMeshes.get("secondDeadPlayerMesh3").render();
							}
						} else if (i == 2) {

							if (playerAnimationCounter < 5) {

								textureMeshes.get("thirdDeadPlayerMesh1").render();
							} else if (playerAnimationCounter < 10) {

								textureMeshes.get("thirdDeadPlayerMesh2").render();
							} else if (playerAnimationCounter < 15) {

								textureMeshes.get("thirdDeadPlayerMesh3").render();
							}
						} else if (i == 3) {

							if (playerAnimationCounter < 5) {

								textureMeshes.get("fourthDeadPlayerMesh1").render();
							} else if (playerAnimationCounter < 10) {

								textureMeshes.get("fourthDeadPlayerMesh2").render();
							} else if (playerAnimationCounter < 15) {

								textureMeshes.get("fourthDeadPlayerMesh3").render();
							}
						}
					}
				}
			}
		}

		playerAnimationCounter++;
		if (playerAnimationCounter > 14) {

			playerAnimationCounter = 0;
		}

		textureShader.unbind();
	}

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

		projectionMatrix = transformation.getOrthographicProjection(0, screen.getWidth() * w_ratio,
				screen.getHeight() * h_ratio, 0f);

		// X coord
		if (state.getPlayers().size() > 0) {

			hudTextItem.setText(state.getPlayers().get(0).getName());
			x = Constants.FANCY_BOX1_X + (Constants.FANCY_BOX_WIDTH / 2 - hudTextItem.getTextWidth() / 2);
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX1_Y + 20, hudTextItem.getRotation(),
					hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(0).getLives()));
			x = Constants.FANCY_BOX1_X + Constants.FANCY_BOX_WIDTH / 4 + Constants.HEART_WIDTH;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX1_Y + Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(0).getMaxNrOfBombs()));
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX1_Y + 2 * Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString((int) state.getPlayers().get(0).getSpeed()));
			x = x + Constants.FANCY_BOX_WIDTH / 4 + 20;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX1_Y + Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(0).getBombRange()));
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX1_Y + 2 * Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();
		}

		if (state.getPlayers().size() > 1) {

			hudTextItem.setText(state.getPlayers().get(1).getName());
			x = Constants.FANCY_BOX2_X + (Constants.FANCY_BOX_WIDTH / 2 - hudTextItem.getTextWidth() / 2);
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX2_Y + 20, hudTextItem.getRotation(),
					hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(1).getLives()));
			x = Constants.FANCY_BOX2_X + Constants.FANCY_BOX_WIDTH / 4 + Constants.HEART_WIDTH;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX2_Y + Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(1).getMaxNrOfBombs()));
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX2_Y + 2 * Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString((int) state.getPlayers().get(1).getSpeed()));
			x = x + Constants.FANCY_BOX_WIDTH / 4 + 20;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX2_Y + Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(1).getBombRange()));
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX2_Y + 2 * Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();
		}

		if (state.getPlayers().size() > 2) {

			hudTextItem.setText(state.getPlayers().get(2).getName());
			x = Constants.FANCY_BOX3_X + (Constants.FANCY_BOX_WIDTH / 2 - hudTextItem.getTextWidth() / 2);
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX3_Y + 20, hudTextItem.getRotation(),
					hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(2).getLives()));
			x = Constants.FANCY_BOX3_X + Constants.FANCY_BOX_WIDTH / 4 + Constants.HEART_WIDTH;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX3_Y + Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(2).getMaxNrOfBombs()));
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX3_Y + 2 * Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString((int) state.getPlayers().get(2).getSpeed()));
			x = x + Constants.FANCY_BOX_WIDTH / 4 + 20;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX3_Y + Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(2).getBombRange()));
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX3_Y + 2 * Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();
		}

		if (state.getPlayers().size() > 3) {

			hudTextItem.setText(state.getPlayers().get(3).getName());
			x = Constants.FANCY_BOX4_X + (Constants.FANCY_BOX_WIDTH / 2 - hudTextItem.getTextWidth() / 2);
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX4_Y + 20, hudTextItem.getRotation(),
					hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(3).getLives()));
			x = Constants.FANCY_BOX4_X + Constants.FANCY_BOX_WIDTH / 4 + Constants.HEART_WIDTH;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX4_Y + Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(3).getMaxNrOfBombs()));
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX4_Y + 2 * Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString((int) state.getPlayers().get(3).getSpeed()));
			x = x + Constants.FANCY_BOX_WIDTH / 4 + 20;
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX4_Y + Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();

			hudTextItem.setText(Integer.toString(state.getPlayers().get(3).getBombRange()));
			modelMatrix = transformation.getModelMatrix(x, Constants.FANCY_BOX4_Y + 2 * Constants.BOX_PADDING,
					hudTextItem.getRotation(), hudTextItem.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.BLACK.getRed(), Color.BLACK.getGreen(), Color.BLACK.getBlue());
			hudTextItem.getMesh().render();
		}

		hudShader.unbind();

	}

	// --------------------------------- Game Over Render
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

		projectionMatrix = transformation.getOrthographicProjection(0f, screen.getWidth() * w_ratio,
				screen.getHeight() * h_ratio, 0f);
		textureShader.setUniform("projection", projectionMatrix);

		modelMatrix = transformation.getModelMatrix(Constants.GENERAL_BOX_X, Constants.GENERAL_BOX_Y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);
		textureMeshes.get("generalBoxMesh").render();

		textureShader.unbind();

	}

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
		projectionMatrix = transformation.getOrthographicProjection(0, screen.getWidth() * w_ratio,
				screen.getHeight() * h_ratio, 0f);

		hudTextItemBig.setText("GAME OVER");
		x = Constants.GENERAL_BOX_X + (Constants.GENERAL_BOX_WIDTH / 2 - hudTextItemBig.getTextWidth() / 2);
		float y = Constants.GENERAL_BOX_Y + (Constants.GENERAL_BOX_HEIGHT / 2 - hudTextItemBig.getTextHeight() / 2);
		modelMatrix = transformation.getModelMatrix(x, y, hudTextItemBig.getRotation(), hudTextItemBig.getScale());
		hudShader.setUniform("projModelMatrix",
				transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
		hudShader.setUniform("colour", Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue());
		hudTextItemBig.getMesh().render();

		y += hudTextItemBig.getTextHeight();
		if (youWon == true) {

			hudTextItemBig.setText("YOU WON");
			x = Constants.GENERAL_BOX_X + (Constants.GENERAL_BOX_WIDTH / 2 - hudTextItemBig.getTextWidth() / 2);
			y += Constants.GENERAL_BOX_Y + hudTextItemBig.getTextHeight() / 2;
			modelMatrix = transformation.getModelMatrix(x, y, hudTextItemBig.getRotation(), hudTextItemBig.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue());
			hudTextItemBig.getMesh().render();
		} else {

			hudTextItemBig.setText("YOU LOST");
			x = Constants.GENERAL_BOX_X + (Constants.GENERAL_BOX_WIDTH / 2 - hudTextItemBig.getTextWidth() / 2);
			modelMatrix = transformation.getModelMatrix(x, y, hudTextItemBig.getRotation(), hudTextItemBig.getScale());
			hudShader.setUniform("projModelMatrix",
					transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
			hudShader.setUniform("colour", Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue());
			hudTextItemBig.getMesh().render();
		}

		hudShader.unbind();
	}

	// -----------------------------------Front Screen
	// Render--------------------------

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

		projectionMatrix = transformation.getOrthographicProjection(0f, screen.getWidth() * w_ratio,
				screen.getHeight() * h_ratio, 0f);
		textureShader.setUniform("projection", projectionMatrix);

		modelMatrix = transformation.getModelMatrix(Constants.GENERAL_BOX_X, Constants.GENERAL_BOX_Y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);
		textureMeshes.get("generalBoxMesh").render();

		x = Constants.GENERAL_BOX_X + (Constants.GENERAL_BOX_WIDTH / 2 - Constants.CONTROLS_WIDTH / 2);
		float y = Constants.GENERAL_BOX_Y + (Constants.GENERAL_BOX_HEIGHT / 2 - Constants.CONTROLS_HEIGHT / 2);
		modelMatrix = transformation.getModelMatrix(x, y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);

		if (!wasd) {

			textureMeshes.get("controlsMesh").render();
		} else {

			textureMeshes.get("altControlsMesh").render();
		}
		textureShader.unbind();

	}

	/**
	 * Render the huds of the front screen on the given screen
	 * 
	 * @param screen
	 *            The given screen
	 * @param state
	 *            The given game state
	 */
	private float seconds = 5;
	private float interval = 1f / Constants.TARGET_FPS;

	private void renderBeginningHud(Screen screen, GameState state) {

		hudShader.bind();
		hudShader.setUniform("texture_sampler", 0);
		projectionMatrix = transformation.getOrthographicProjection(0, screen.getWidth() * w_ratio,
				screen.getHeight() * h_ratio, 0f);

		hudTextItemBig.setText(Integer.toString((int) seconds));
		x = Constants.GENERAL_BOX_X + (Constants.GENERAL_BOX_WIDTH / 2 - Constants.CONTROLS_WIDTH / 2)
				+ Constants.CONTROLS_WIDTH / 2;
		float y = Constants.GENERAL_BOX_Y + (Constants.GENERAL_BOX_HEIGHT / 2 - Constants.CONTROLS_HEIGHT / 2)
				+ Constants.CONTROLS_HEIGHT + 10;
		modelMatrix = transformation.getModelMatrix(x, y, hudTextItemBig.getRotation(), hudTextItemBig.getScale());
		hudShader.setUniform("projModelMatrix",
				transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
		hudShader.setUniform("colour", Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue());
		hudTextItemBig.getMesh().render();

		seconds -= interval;

		hudShader.unbind();
	}

	// ----------------------------------Front Screen
	// Render----------------------------------------------

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

		projectionMatrix = transformation.getOrthographicProjection(0f, screen.getWidth() * w_ratio,
				screen.getHeight() * h_ratio, 0f);
		textureShader.setUniform("projection", projectionMatrix);

		modelMatrix = transformation.getModelMatrix(Constants.GENERAL_BOX_X, Constants.GENERAL_BOX_Y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);
		textureMeshes.get("generalBoxMesh").render();

		x = Constants.GENERAL_BOX_X + (Constants.GENERAL_BOX_WIDTH / 2 - Constants.CONTROLS_WIDTH / 2);
		float y = Constants.GENERAL_BOX_Y + (Constants.GENERAL_BOX_HEIGHT / 2 - Constants.CONTROLS_HEIGHT / 2);
		modelMatrix = transformation.getModelMatrix(x, y, 0f, 1f);
		textureShader.setUniform("model", modelMatrix);

		if (!wasd) {

			textureMeshes.get("controlsMesh").render();
		} else {

			textureMeshes.get("altControlsMesh").render();
		}
		textureShader.unbind();
	}

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
		projectionMatrix = transformation.getOrthographicProjection(0, screen.getWidth() * w_ratio,
				screen.getHeight() * h_ratio, 0f);

		hudTextItemBig.setText("PRESS P TO UNPAUSE THE GAME");
		x = Constants.GENERAL_BOX_X + (Constants.GENERAL_BOX_WIDTH / 2 - Constants.CONTROLS_WIDTH / 2)
				+ (Constants.CONTROLS_WIDTH / 2 - hudTextItemBig.getTextWidth() / 2);
		float y = Constants.GENERAL_BOX_Y + (Constants.GENERAL_BOX_HEIGHT / 2 - Constants.CONTROLS_HEIGHT / 2)
				+ Constants.CONTROLS_HEIGHT + 10;
		modelMatrix = transformation.getModelMatrix(x, y, hudTextItemBig.getRotation(), hudTextItemBig.getScale());
		hudShader.setUniform("projModelMatrix",
				transformation.getOrtoProjectionModelMatrix(modelMatrix, projectionMatrix));
		hudShader.setUniform("colour", Color.WHITE.getRed(), Color.WHITE.getGreen(), Color.WHITE.getBlue());
		hudTextItemBig.getMesh().render();

		hudShader.unbind();
	}

	/**
	 * Stop the display of the front screen
	 */
	public void stopFrontScreen() {

		frontScreen = false;
	}

	/**
	 * Display game over screen
	 * 
	 * @param youWon
	 *            Boolean showing if this client won or lost
	 */
	public void displayGameOver(boolean youWon) {

		gameOver = true;
		this.youWon = youWon;
	}

	/**
	 * Display the pause screen
	 */
	public void displayPauseScreen() {

		gamePaused = true;
	}

	/**
	 * Stop displaying the pause screen
	 */
	public void stopPauseScreen() {

		gamePaused = false;
	}

	/**
	 * Dispose the renderer and all its resources
	 */
	public void dispose() {

		hudTextItem.getMesh().dispose();

		for (String key : textureMeshes.keySet()) {

			textureMeshes.get(key).dispose();
		}
		if (textureShader != null) {

			textureShader.dispose();
		}
		if (hudShader != null) {

			hudShader.dispose();
		}
		System.gc();
	}

}