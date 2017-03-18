package bomber.game;

import java.awt.*;

/**
 * Created by Alexandru Rosu on 18/03/2017.
 */
public class BlastTimer
{

    private Point location;
    private final boolean makesPowerup;
    private int timer;

    public BlastTimer(Point location, boolean makesPowerup)
    {
        this.location = location;
        this.makesPowerup = makesPowerup;
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

    public void decreaseTimer(int milliseconds)
    {
        timer-=milliseconds;
    }

    public boolean isDone()
    {
        return timer<=0;
    }


    public boolean makesPowerup()
    {
        return makesPowerup;
    }
}
