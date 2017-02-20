package bomber.physics;

import bomber.game.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Alexandru Rosu on 15.01.2017.
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
    }

    private Point getBombLocation(Point playerPosition)
    {
        int xOffset = (mapBlockToGridMultiplier - bombPixelWidth)/2;
        int YOffset = (mapBlockToGridMultiplier - bombPixelHeight)/2;
        return new Point((playerPosition.x+playerPixelWidth/2)/64 * 64+xOffset, (playerPosition.y+playerPixelHeight/2)/64*64+YOffset);
    }

    public synchronized void update(int milliseconds)
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
        gameState.getBombs().forEach(b -> updateBomb(b, toBeDeleted, milliseconds));
        toBeDeleted.forEach(b -> gameState.getBombs().remove(b));

        // update players
        gameState.getPlayers().forEach(p -> updatePlayer(p, milliseconds));
    }

    public synchronized void update()
    {
        update(1000);
    }


    // -----------------------------
    // Player update related methods
    // -----------------------------

    private void updatePlayer(Player player, int milliseconds)
    {

        // Only update if the player is alive
        if (!player.isAlive()) return;

        // Initialise data
        Point pos = player.getPos();
        Map map = gameState.getMap();



        // -------- Movement --------
        Movement movement = player.getKeyState().getMovement();
        if (movement != Movement.NONE)
        {

            // Play sound effects
            gameState.getAudioEvents().add(AudioEvent.MOVEMENT);

            // Initialise data
            int speed = (int) (milliseconds*player.getSpeed()/1000);
            Rectangle initialPlayerRect = new Rectangle(pos.x, pos.y, playerPixelWidth, playerPixelHeight);
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

            assert(fromDirection != null);

            // Collision with bombs
            // If the dimensions ever change, this should be checked
            Rectangle translatedPlayerRect = new Rectangle(pos.x, pos.y, playerPixelWidth, playerPixelHeight);
            for(Bomb bomb: gameState.getBombs())
            {
                Rectangle bombRect = new Rectangle(bomb.getPos().x, bomb.getPos().y, bombPixelWidth, bombPixelHeight);
                if(!bombRect.intersects(initialPlayerRect))
                    while(bombRect.intersects(translatedPlayerRect))
                    {
                        translatedPlayerRect.translate(fromDirection.x, fromDirection.y);
                        translatePoint(pos, fromDirection);
                    }
            }

            // Collision with solid/soft blocks
            translatePoint(pos, revertPositionDelta(fromDirection, map, pos)); // check up-left corner

            Point upRightCorner = new Point(pos.x + playerPixelWidth, pos.y);
            translatePoint(pos, revertPositionDelta(fromDirection, map, upRightCorner));

            Point downLeftCorner = new Point(pos.x, pos.y + playerPixelHeight);
            translatePoint(pos, revertPositionDelta(fromDirection, map, downLeftCorner));

            Point downRightCorner = new Point(pos.x + playerPixelWidth, pos.y + playerPixelHeight);
            translatePoint(pos, revertPositionDelta(fromDirection, map, downRightCorner));

            // TODO: Collision with power-ups

        }

        // -------- Planting bombs --------
        if(player.getKeyState().isBomb() && okToPlaceBomb.get(player.getName())!=null && okToPlaceBomb.get(player.getName()))
        {
            int bombCount = 0;
            for (Bomb bomb : gameState.getBombs())
                if(bomb.getPlayerName().equals(player.getName()))
                    bombCount++;
            if (bombCount < player.getMaxNrOfBombs())
            {
                plantBomb(player, default_time);
                okToPlaceBomb.put(player.getName(), false);
            }
        }
        if(!player.getKeyState().isBomb())
            okToPlaceBomb.put(player.getName(), true);


        // -------- Damage --------
        if (map.getPixelBlockAt(pos.x, pos.y)==Block.BLAST || map.getPixelBlockAt(pos.x+playerPixelWidth,pos.y+playerPixelHeight)==Block.BLAST
                || map.getPixelBlockAt(pos.x,pos.y+playerPixelHeight)==Block.BLAST || map.getPixelBlockAt(pos.x+playerPixelWidth,pos.y)==Block.BLAST)
        {
            player.setLives(player.getLives()-1);
            if (player.getLives() == 0)
                player.setAlive(false);
            gameState.getAudioEvents().add(AudioEvent.PLAYER_DEATH);
        }

    }

    private void translatePoint(Point point, Point delta)
    {
        point.translate(delta.x, delta.y);
    }

    public void plantBomb(Player player, int time)
    {
        Bomb bomb = new Bomb(player.getName(),  getBombLocation(player.getPos()), time, player.getBombRange());
        bomb.setPlayerID(player.getPlayerID());
        gameState.getBombs().add(bomb);
        gameState.getAudioEvents().add(AudioEvent.PLACE_BOMB);
    }

    private Point revertPositionDelta(Point fromDirection, Map map, Point initialCorner)
    {
        Point corner = new Point(initialCorner);
        while(map.getPixelBlockAt(corner.x, corner.y) == Block.SOLID || map.getPixelBlockAt(corner.x, corner.y) == Block.SOFT)
            translatePoint(corner, fromDirection);
        // TODO: can do pos.translate here instead of returning. see if this is cleaner
        return new Point(corner.x-initialCorner.x, corner.y-initialCorner.y);
    }


    // ---------------------------
    // Bomb update related methods
    // ---------------------------

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

    private void addBlast(int x, int y, int radius, int direction)
    {
        // direction: 0 all, 1 up, 2 right, 3 down, 4 left
        Point pos = new Point(x, y);
        if(!gameState.getMap().isInGridBounds(pos))
            return;
        if(radius==0 || gameState.getMap().getGridBlockAt(x, y)==Block.SOLID)
            return;
        if(gameState.getMap().getGridBlockAt(x, y) != Block.SOFT)
            switch (direction)
            {
                case 0:
                    addBlast(x, y - 1, radius - 1, 1);
                    addBlast(x + 1, y, radius - 1, 2);
                    addBlast(x, y + 1, radius - 1, 3);
                    addBlast(x - 1, y, radius - 1, 4);
                    break;
                case 1:
                    addBlast(x, y - 1, radius - 1, 1);
                    break;
                case 2:
                    addBlast(x + 1, y, radius - 1, 2);
                    break;
                case 3:
                    addBlast(x, y + 1, radius - 1, 3);
                    break;
                case 4:
                    addBlast(x - 1, y, radius - 1, 4);
                    break;
            }

        gameState.getMap().setGridBlockAt(pos, Block.BLAST);
    }

    private void decreaseBombTimer(Bomb bomb, int milliseconds)
    {
        bomb.setTime(bomb.getTime()-milliseconds);
    }

}