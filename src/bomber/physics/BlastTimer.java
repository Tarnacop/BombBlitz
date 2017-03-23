package bomber.physics;

import bomber.game.Block;
import bomber.game.Constants;

import java.awt.*;
import java.util.Random;

/**
 * Keeps track of the explosion at a particular grid location
 *
 * @author Alexandru Rosu
 */
public class BlastTimer
{

    private final Point location;
    private final Block reveal;
    private int timer;

    /**
     * Constructs a timer for an explosion that will reveal a powerup
     *
     * @param location The grid point of the explosion
     */
    BlastTimer(Point location)
    {
        this.location = location;
        this.reveal = getRandomBlock();
        timer = Constants.EXPLOSION_LENGTH;
    }

    /**
     * Constructs a timer for an explosion that will reveal a particular block
     *
     * @param location The grid point of the explosion
     * @param reveal The block that will be revealed after the explosion
     */
    BlastTimer(Point location, Block reveal)
    {
        this.location = location;
        this.reveal = reveal;
        timer = Constants.EXPLOSION_LENGTH;
    }

    /**
     * Gets the location of the explosion
     *
     * @return The point corresponding to the explosion
     */
    Point getLocation()
    {
        return location;
    }

    /**
     * Sets the timer to a different amount
     *
     * @param timer The amount of milliseconds left until the explosion is cleared
     */
    void setTimer(int timer)
    {
        this.timer = timer;
    }

    /**
     * Decreases the timer by an amount of milliseconds
     *
     * @param milliseconds The amount of milliseconds
     */
    void decreaseTimer(int milliseconds)
    {
        timer-=milliseconds;
    }

    /**
     * Checks whether the explosion should clear
     *
     * @return True if it should clear, false if not
     */
    boolean isDone()
    {
        return timer<=0;
    }

    /**
     * Gets the block that will be revealed by the explosion
     *
     * @return A block to be put on the location
     */
    Block getReveal()
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
