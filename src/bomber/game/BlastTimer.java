package bomber.game;

import java.awt.*;
import java.util.Random;

/**
 * Created by Alexandru Rosu on 18/03/2017.
 */
public class BlastTimer
{

    private final Point location;
    private final Block reveal;
    private int timer;

    // Used when the block will reveal a powerup
    public BlastTimer(Point location)
    {
        this.location = location;
        this.reveal = getRandomBlock();
        timer = Constants.EXPLOSION_LENGTH;
    }

    // Used when the block will reveal some other block
    public BlastTimer(Point location, Block reveal)
    {
        this.location = location;
        this.reveal = reveal;
        timer = Constants.EXPLOSION_LENGTH;
    }

    public Point getLocation()
    {
        return location;
    }

    public int getTimer()
    {
        return timer;
    }

    public void setTimer(int timer)
    {
        this.timer = timer;
    }

    public void decreaseTimer(int milliseconds)
    {
        timer-=milliseconds;
    }

    public boolean isDone()
    {
        return timer<=0;
    }

    public Block getReveal()
    {
        return reveal;
    }

    /**
     * Randomly generates a block type, according to the probabilities in Constants
     *
     * @return A random Block object
     */
    private Block getRandomBlock()
    {
        Random generator = new Random();
        boolean isPowerup = generator.nextInt(100) < Constants.POWERUP_PROBABILITY;
        if (!isPowerup)
            return Block.BLANK;
        int isPositive = generator.nextInt(100) < Constants.POSITIVE_POWERUP_PROBABILITY ? 0 : 10; // 0 is positive, 10 is negative
        int powerup = generator.nextInt(3); // first, second or third power-up

        switch (isPositive + powerup)
        {
            case 0:
                return Block.PLUS_BOMB;
            case 1:
                return Block.PLUS_RANGE;
            case 2:
                return Block.PLUS_SPEED;
            case 10:
                return Block.MINUS_BOMB;
            case 11:
                return Block.MINUS_RANGE;
            case 12:
                return Block.MINUS_SPEED;
            default:
                System.err.println("Unexpected result in Physics.getRandomBlock().");
                return Block.BLANK;
        }

    }
}
