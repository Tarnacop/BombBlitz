package bomber.physics;

import bomber.game.*;

import java.awt.*;
import java.util.Optional;

/**
 * Created by Alexandruro on 15.01.2017.
 */
public class PhysicsEngine
{

    public static final int playerPixelWidth = 50;
    public static final int playerPixelHeight = 50;

    private GameState gameState;

    /**
     * Creates an engine using a GameState
     * @param gameState The GameState object
     */
    public PhysicsEngine(GameState gameState)
    {
        this.gameState = gameState;
    }

    /**
     * Puts a new player on the map
     */
    public void addPlayer(String name, Point pos, int lives, double speed)
    {
        gameState.getPlayers().add(new Player(name, pos, lives, speed));
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


    public void plantBomb(String playerName, int time, int radius)
    {
        Point pos = getPlayerNamed(playerName).getPos();
        gameState.getBombs().add(new Bomb(playerName,  pos, time, radius));
    }

    private void updatePlayer(Player player)
    {
        int speed = (int)player.getSpeed();
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
        }

        // collision detection
        Map map = gameState.getMap();

        translatePoint(pos, revertPositionDelta(fromDirection, map, pos)); // check up-left corner

        Point upRightCorner = new Point(pos.x+playerPixelWidth, pos.y);
        translatePoint(pos, revertPositionDelta(fromDirection, map, upRightCorner));

        Point downLeftCorner = new Point(pos.x, pos.y + playerPixelHeight);
        translatePoint(pos, revertPositionDelta(fromDirection, map, downLeftCorner));

        Point downRightCorner = new Point(pos.x + playerPixelWidth, pos.y + playerPixelHeight);
        translatePoint(pos, revertPositionDelta(fromDirection, map, downRightCorner));

        player.setPos(pos);
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

    public synchronized void updateAll()
    {
        gameState.getPlayers().forEach(this::updatePlayer);
        gameState.getBombs().forEach(this::updateBomb);
    }

    private void updateBomb(Bomb bomb)
    {
        decreaseBombTimer(bomb);
        if(bomb.getTime()<=0)
        {
            Point pos = bomb.getPos();
            int radius = bomb.getRadius();
            gameState.getBombs().remove(bomb);
            addBlast(pos, radius);
        }

    }

    private void addBlast(Point pos, int radius)
    {
        /*
        if(true) // TODO: if in bounds

        if(gameState.getMap().getGridBlockAt(pos.x, pos.y)==Block.SOLID || radius==0)
            return;
        gameState.getMap().setGridBlockAt(pos, Block.BLAST);
        addBlast(pos);
        */
    }

    private void decreaseBombTimer(Bomb bomb)
    {
        //TODO: add setTimer in Bomb class
    }

}
