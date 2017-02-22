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
}
