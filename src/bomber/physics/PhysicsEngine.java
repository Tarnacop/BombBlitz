/*
Game Logic
{
	- KeyListeners that put flags
	- Starts the Physics Engine
	- Starts the Rendering Engine
}

Dependencies for PhysicsEngine
{
	- a parameter for the speed of the robot (it could change in the case of powerups)
    - the time when the bomb has been planted should be transmitted through networking in order to sync
}

The Physics engine subsystem should be able to keep track of the positions of objects and players in the game world.
This includes different types of blocks (explodable and non-explodable), bombs and power-ups.
It must be able to change the players' positions (when it is requested by other subsystems) and place bombs according to inputs sent from the keyboard or other subsystems.
Bombs should explode after a predetermined amount of time and with that kill players and destroy soft blocks inside the radius of the explosion.
The subsystem should take care of collisions, making sure that the players don't go through blocks and can pick up power-ups.

The Map class should keep track of the different types of blocks present in the game and change due to explosions.
The Player class should keep track of a player's position on the map and speed, as well as whether he/she is alive.
The MapBuilder class should be able to build a screen resolution sized map using predetermined models.
The Bomb class should be able to keep track of a bomb's position and explosion radius, as well as the time it has been planted
*/

package bomber.physics;

import bomber.game.Bomb;
import bomber.game.GameState;
import bomber.game.Movement;
import bomber.game.Player;

import java.awt.*;
import java.util.Iterator;

/**
 * Created by Alexandruro on 15.01.2017.
 */
public class PhysicsEngine
{

    GameState gameState;

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

    public Player getPlayerNamed(String name)
    {
        Iterator<Player> iterator = gameState.getPlayers().iterator();
        while(iterator.hasNext())
        {
            Player player = iterator.next();
            if(player.getName().equals(name))
                return player;
        }
        return null;
    }


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
        switch (movement)
        {
            case UP:
                pos.translate(0, -speed);
                break;
            case DOWN:
                pos.translate(0, speed);
                break;
            case LEFT:
                pos.translate(-speed, 0);
                break;
            case RIGHT:
                pos.translate(speed, 0);
        }
        player.setPos(pos);
    }

    private boolean bombExplodes(Bomb bomb)
    {
        return false;
    }

    public void updateAll()
    {
        gameState.getPlayers().forEach(p -> updatePlayer(p));
    }

}
