package bomber.physics;

import bomber.game.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Alexandruro on 15.01.2017.
 */
public class PhysicsEngine
{

    public static final int playerPixelWidth = 32;
    public static final int playerPixelHeight = 32;
    public static final int bombPixelWidth = 50;
    public static final int bombPixelHeight = 50;
    public static final int default_time = 2000;
    public static final int mapBlockToGridMultiplier = 64;

    private GameState gameState;

    private HashMap<String, Boolean> okToPlaceBomb;

    /**
     * Creates an engine using a GameState
     * @param gameState The GameState object
     */
    public PhysicsEngine(GameState gameState)
    {
        this.gameState = gameState;
        okToPlaceBomb = new HashMap<>();
    }

    /*
    public void deletePlayer(in)
    {
        players.remove(id);
    }

    public Point getPlayerPosition(int id)
    {
        return players.get(id).getPosition();
    }

    public int getPlayerOrientation(int id)
    {
        return players.get(id).getOrientation();
    }

    public boolean isPlayerAlive(int id)
    {
        return players.get(id).isAlive();
    }
    */

    /**
     * Puts a new player on the map
     */
    /*
    public void addPlayer(String name, Point pos, int lives, double speed)
    {
        gameState.getPlayers().add(new Player(name, pos, lives, speed));
    }

    /**
     * Gets the player corresponding to a certain name
     * Assumes there is at most one player named that way
     * @param name The name
     * @return The player object
     */
    public Player getPlayerNamed(String name)
    {
        Optional<Player> maybePlayer =  gameState.getPlayers().stream().filter(p -> p.getName().equals(name)).findAny();
        return maybePlayer.orElse(null);

        /*Iterator<Player> iterator = gameState.getPlayers().iterator();
        while(iterator.hasNext())
        {
            Player player = iterator.next();
            if(player.getName().equals(name))
                return player;
        }
        return null;
*/    }


    private Point getBombLocation(Point playerPosition)
    {
        int xOffset = (mapBlockToGridMultiplier - bombPixelWidth)/2;
        int YOffset = (mapBlockToGridMultiplier - bombPixelHeight)/2;
        return new Point((playerPosition.x+playerPixelWidth/2)/64 * 64+xOffset, (playerPosition.y+playerPixelHeight/2)/64*64+YOffset);
    }

    public void plantBomb(String playerName, int time, int radius)
    {
        Point playerPos = getPlayerNamed(playerName).getPos();
        gameState.getBombs().add(new Bomb(playerName,  getBombLocation(playerPos), time, radius));
        gameState.getAudioEvents().add(AudioEvent.PLACE_BOMB);
    }

    private void updatePlayer(Player player, int milliseconds)
    {
        int speed = (int) (milliseconds*player.getSpeed()/1000);
        Point pos = player.getPos();
        Movement movement = player.getKeyState().getMovement();
        Point fromDirection = null;
        switch (movement)
        {
            case UP:
                pos.translate(0, -speed);
                fromDirection = new Point(0, 1);
                break;
            case DOWN:
                pos.translate(0, speed);
                fromDirection = new Point(0, -1);
                break;
            case LEFT:
                pos.translate(-speed, 0);
                fromDirection = new Point(1, 0);
                break;
            case RIGHT:
                pos.translate(speed, 0);
                fromDirection = new Point(-1, 0);
                break;
        }


        Map map = gameState.getMap();


        // check for bomb placement
        if(player.getKeyState().isBomb() && okToPlaceBomb.get(player.getName()))
        {
            plantBomb(player.getName(), default_time, player.getBombRange());
            okToPlaceBomb.put(player.getName(), false);
        }
        if(!player.getKeyState().isBomb())
            okToPlaceBomb.put(player.getName(), true);

        // collision detection
        // TODO refactor (put this test up higher and test speed & direction)
        if (fromDirection != null)
        {
            translatePoint(pos, revertPositionDelta(fromDirection, map, pos)); // check up-left corner

            Point upRightCorner = new Point(pos.x + playerPixelWidth, pos.y);
            translatePoint(pos, revertPositionDelta(fromDirection, map, upRightCorner));

            Point downLeftCorner = new Point(pos.x, pos.y + playerPixelHeight);
            translatePoint(pos, revertPositionDelta(fromDirection, map, downLeftCorner));

            Point downRightCorner = new Point(pos.x + playerPixelWidth, pos.y + playerPixelHeight);
            translatePoint(pos, revertPositionDelta(fromDirection, map, downRightCorner));
        }

        //player.setPos(pos); // should work without this

        // check if player is killed
        if (map.getPixelBlockAt(pos.x, pos.y)==Block.BLAST)
        {
            player.setAlive(false);
            player.setLives(Math.max(player.getLives()-1,0));
            gameState.getAudioEvents().add(AudioEvent.PLAYER_DEATH);
        }
    }

    private void translatePoint(Point point, Point delta)
    {
        point.translate(delta.x, delta.y);
    }

    private Point revertPositionDelta(Point fromDirection, Map map, Point initialCorner)
    {
        Point corner = new Point(initialCorner);
        while(map.getPixelBlockAt((int)corner.getX(), (int)corner.getY()) == Block.SOLID || map.getPixelBlockAt((int)corner.getX(), (int)corner.getY()) == Block.SOFT)
            corner.translate((int)fromDirection.getX(), (int)fromDirection.getY());
        return new Point(corner.x-initialCorner.x, corner.y-initialCorner.y);
    }

    public synchronized void update(int miliseconds)
    {
        // update map (blast)
        Map map = gameState.getMap();
        int width = gameState.getMap().getGridMap().length;
        int height = gameState.getMap().getGridMap()[0].length;
        for(int x=0; x<width; x++)
            for(int y=0; y<height; y++)
                if (map.getGridBlockAt(x,y) == Block.BLAST)
                    map.setGridBlockAt(new Point(x,y), Block.BLANK);

        // update bombs
        ArrayList<Bomb> toBeDeleted = new ArrayList<>();
        gameState.getBombs().forEach(b -> updateBomb(b, toBeDeleted, miliseconds));
        toBeDeleted.forEach(b -> gameState.getBombs().remove(b));

        // update players
        gameState.getPlayers().forEach(p -> updatePlayer(p, miliseconds));
    }

    public synchronized void update()
    {
        update(1000);
    }

    private void updateBomb(Bomb bomb, ArrayList<Bomb> toBeDeleted, int milliseconds)
    {
        decreaseBombTimer(bomb, milliseconds);
        if(bomb.getTime()<=0)
        {
            toBeDeleted.add(bomb);
            Point pos = bomb.getPos();
            int radius = bomb.getRadius();
            addBlast(pos.x/64, pos.y/64, radius, 0);
            gameState.getAudioEvents().add(AudioEvent.EXPLOSION);
        }
    }

    // direction: 0 all, 1 up, 2 right, 3 down, 4 left
    private void addBlast(int x, int y, int radius, int direction)
    {
        Point pos = new Point(x, y);
        if(!gameState.getMap().isInGridBounds(pos))
            return;
        if(radius==0 || gameState.getMap().getGridBlockAt(x, y)==Block.SOLID)
            return;
        gameState.getMap().setGridBlockAt(pos, Block.BLAST);
        switch (direction)
        {
            case 0:
                addBlast(x, y-1, radius-1, 1);
                addBlast(x+1, y, radius-1, 2);
                addBlast(x, y+1, radius-1, 3);
                addBlast(x-1, y, radius-1, 4);
                break;
            case 1:
                addBlast(x, y-1, radius-1, 1);
                break;
            case 2:
                addBlast(x+1, y, radius-1, 2);
                break;
            case 3:
                addBlast(x, y+1, radius-1, 3);
                break;
            case 4:
                addBlast(x-1, y, radius-1, 4);
                break;
        }
    }

    private void decreaseBombTimer(Bomb bomb, int milliseconds)
    {
        bomb.setTime(bomb.getTime()-milliseconds);
    }

}