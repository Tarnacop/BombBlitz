package bomber.game;

/**
 * Created by Alexandru Rosu on 20.02.2017.
 */
public class Constants
{

    // Game World Constants
    public static final int playerPixelWidth = 32;
    public static final int playerPixelHeight = 32;
    public static final int bombPixelWidth = 50;
    public static final int bombPixelHeight = 50;
    public static final int mapBlockToGridMultiplier = 64;

    // Game Rules
    public static final int defaultBombTime = 2000;
    public static final int minimumBombRange = 2;
    public static final int maximumBombRange = 6;
    public static final int bombRangeChange = 1;
    public static final int lowPlayerSpeed = 200;
    public static final int highPlayerSpeed = 400;
    public static final int powerupProbability = 25;
    public static final int positivePowerupProbability = 75;

    // Audio files
    public static final String audioFilesPath = "/resources/sounds/";
    public static final String musicFilename = "01_A_Night_Of_Dizzy_Spells.wav";
    public static final String explosionFilename = "sfx_exp_medium3.wav";
    public static final String bombPlaceFilename = "sfx_sound_neutral6.wav";
    public static final String movementFilename = "sfx_movement_footsteps1a.wav";
    public static final String playerDeathFilename = "sfx_sounds_error1.wav";
    public static final String powerupFilename = "sfx_sounds_interaction24.wav";
    public static final String menuSoundFilename = "271139_strange-dragoon_menu-select_converted.wav";

    // Settings
    public static final float defaultVolume = 75;
}
