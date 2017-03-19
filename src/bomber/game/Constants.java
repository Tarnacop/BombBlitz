package bomber.game;

/**
 * Created by Alexandru Rosu on 20.02.2017.
 */
public abstract class Constants
{

	//Game Name:
	public static final String GAME_NAME = "Bomb Blitz";
	
    // Game World Constants
    public static final int PLAYER_WIDTH = 36;
    public static final int PLAYER_HEIGHT = 36;
    public static final int BOMB_WIDTH = 50;
    public static final int BOMB_HEIGHT = 50;
    public static final float GENERAL_BLOCK_WIDTH = 64f;
    public static final float GENERAL_BLOCK_HEIGHT = 64f;
    public static final int MAP_BLOCK_TO_GRID_MULTIPLIER = 64;

    // Game Rules
    public static final int DEFAULT_BOMB_TIME = 2000;
    public static final int MINIMUM_BOMB_RANGE = 2;
    public static final int MAXIMUM_BOMB_RANGE = 6;
    public static final int BOMB_RANGE_CHANGE = 1;
    public static final int LOW_PLAYER_SPEED = 200;
    public static final int HIGH_PLAYER_SPEED = 400;
    public static final int POWERUP_PROBABILITY = 25;
    public static final int POSITIVE_POWERUP_PROBABILITY = 75;

    // Audio files
    public static final String AUDIO_FILES_PATH = "/resources/sounds/";
    public static final String MUSIC_FILENAME = "01_A_Night_Of_Dizzy_Spells.wav";
    public static final String EXPLOSION_FILENAME = "sfx_exp_medium3.wav";
    public static final String BOMB_PLACE_FILENAME = "sfx_sound_neutral6.wav";
    public static final String MOVEMENT_FILENAME = "sfx_movement_footsteps1a.wav";
    public static final String PLAYER_DEATH_FILENAME = "sfx_sounds_error1.wav";
    public static final String POWERUP_FILENAME = "sfx_sounds_interaction24.wav";
    public static final String MENU_SOUND_FILENAME = "271139_strange-dragoon_menu-select_converted.wav";
    public static final String GAME_OVER_WON_FILENAME = "270333__littlerobotsoundfactory__jingle-win-00.wav";
    public static final String GAME_OVER_LOST_FILENAME = "270329__littlerobotsoundfactory__jingle-lose-00.wav";

    // Other files
    public static final String SETTING_XML_PATH = "src/resources/settings.xml";

    // Default settings
    public static final float DEFAULT_VOLUME = 75;
    public static final String DEFAULT_PLAYER_NAME = "Player";
    public static final String DEFAULT_SERVER_NAME = "Default server";
    public static final String DEFAULT_SERVER_IP = "localhost";
    public static final int DEFAULST_SERVER_PORT = 1234;
    
    // Renderer constants
    public static final int TARGET_FPS = 60;
	public static final int TARGET_UPS = 60;
	public static final String IMAGE_FORMAT = "png";
	public static final String CHARSET_NAME = "ISO-8859-1";
	
	public static final float V_WIDTH = 1262f;
	public static final float V_HEIGHT = 869f;
	
	public static final float SPRITESHEET_ROWS = 8f;
	public static final float SPRITESHEET_COLS = 18f;
	public static final float SPRITESHEET_ELEM_WIDTH = 1f / SPRITESHEET_COLS;
	public static final float SPRITESHEET_ELEM_HEIGHT = 1f / SPRITESHEET_ROWS;
	
	public static final float BACKGROUND_WIDTH = 1262f;
	public static final float BACKGROUND_HEIGHT = 869f;
	public static final float BACKGROUND_X = 0f;
	public static final float BACKGROUND_Y = 0f;
	
	public static final float INFO_BOX_WIDTH = 390f;
	public static final float INFO_BOX_HEIGHT = 859f;
	public static final float INFO_BOX_X = 867f;
	public static final float INFO_BOX_Y = 5f;
	
	public static final float GAME_BOX_WIDTH = 857f;
	public static final float GAME_BOX_HEIGHT = 859f;
	public static final float GAME_BOX_X = 5f;
	public static final float GAME_BOX_Y = 5f;
	
	public static final float GENERAL_BOX_WIDTH = 1257f;
	public static final float GENERAL_BOX_HEIGHT = 864f;
	public static final float GENERAL_BOX_X = 5f;
	public static final float GENERAL_BOX_Y = 5f;
	
	public static final float FANCY_BOX_WIDTH = 300f;
	public static final float FANCY_BOX_HEIGHT = 173f;
	public static final float FANCY_BOX1_X = 918f;
	public static final float FANCY_BOX1_Y = 25f;
	public static final float FANCY_BOX2_X = 918f;
	public static final float FANCY_BOX2_Y = 228f;
	public static final float FANCY_BOX3_X = 918f;
	public static final float FANCY_BOX3_Y = 441f;
	public static final float FANCY_BOX4_X = 918f;
	public static final float FANCY_BOX4_Y = 649f;
	
	public static final float HEART_WIDTH = 32f;
	public static final float HEART_HEIGHT = 32f;
	public static final float BOX_PADDING = 60f;
	
	public static final int CONTROLS_WIDTH = 400;
	public static final int CONTROLS_HEIGHT = 300;

	//User Interface Constants
	public static final float SCREEN_HEIGHT_SCALAR = 0.9f;
	public static final float SCREEN_WIDTH_SCALAR = 0.8f;
	
	public static final float MENU_BOX_HEIGHT_SCALAR = 3.5f;
	public static final float MENU_BOX_WIDTH_SCALAR = 5.0f;
	
	public static final float MENU_BUTTON_WIDTH = 200;
	public static final float MENU_BUTTON_HEIGHT = 50;
	public static final float LARGE_MENU_BUTTON_WIDTH = 250;
	public static final float LARGE_MENU_BUTTON_HEIGHT = 75;
	
	public static final float TINY_PAD = 5;
	public static final float SMALL_PAD = 10;
	public static final float LITTLE_PAD = 15;
	public static final float MEDIUM_PAD = 20;
	public static final float LARGE_PAD = 30;
	public static final float HUGE_PAD = 80;
	public static final float MASSIVE_PAD = 100;
	
	public static final float TORCH_WIDTH = 70;
	public static final float TORCH_HEIGHT = 90;
	
	public static final float MAP_CANVAS_WIDTH = 300;
	public static final float MAP_CANVAS_HEIGHT = 300;
}