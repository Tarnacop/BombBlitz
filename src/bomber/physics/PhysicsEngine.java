package bomber.physics;

import bomber.game.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * Manages the physics of the game
 *
 * @author Alexandru Rosu
 */
public class PhysicsEngine
{

    private GameState gameState;
    private HashMap<String, Boolean> okToPlaceBomb;

    /**
     * Creates an engine using a GameState
     *
     * @param gameState The GameState object
     */
    public PhysicsEngine(GameState gameState)
    {
        this.gameState = gameState;
        okToPlaceBomb = new HashMap<>();
        gameState.getPlayers().forEach(player -> okToPlaceBomb.put(player.getName(), true));
    }

    /**
     * Updates all the objects in the game world
     *
     * @param milliseconds The amount of time in milliseconds from the last update
     */
    public synchronized void update(int milliseconds)
    {
        // update map after blast
        updateMap(milliseconds);

        // update bombs
        ArrayList<Bomb> toBeDeleted = new ArrayList<>();
        gameState.getBombs().forEach(b -> updateBomb(b, toBeDeleted, milliseconds));
        toBeDeleted.forEach(b -> gameState.getBombs().remove(b));

        // update players
        gameState.getPlayers().forEach(p -> updatePlayer(p, milliseconds));
    }

    /**
     * Updates all the objects in the game world, using a default delta time of 1 second
     */
    public synchronized void update()
    {
        update(1000);
    }


    // -----------------------------
    // Player update related methods
    // -----------------------------

    /**
     * Updates a player
     *
     * @param player       The Player to be updated
     * @param milliseconds The amount of time in milliseconds from the last update
     */
    private void updatePlayer(Player player, int milliseconds)
    {

        // Only update if the player is alive
        if (!player.isAlive()) return;

        // Initialise data
        Point pos = player.getPos();
        Point initialPos = new Point(pos);

        // Update invulnerability period
        int invulnerability = player.getInvulnerability();
        if (invulnerability>0)
            player.setInvulnerability(Math.max(0, invulnerability - milliseconds));

        // -------- Movement --------
        Movement movement = player.getKeyState().getMovement();
        if (movement != Movement.NONE)
        {

            // Play sound effects
            //gameState.getAudioEvents().add(AudioEvent.MOVEMENT);

            // Initialise data
            int speed = (int) (milliseconds * player.getSpeed() / 1000);
            Rectangle initialPlayerRect = new Rectangle(pos.x, pos.y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
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

            assert (fromDirection != null);

            // Collision with bombs
            Rectangle translatedPlayerRect = new Rectangle(pos.x, pos.y, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);
            for (Bomb bomb : gameState.getBombs())
            {
                Rectangle bombRect = new Rectangle(bomb.getPos().x, bomb.getPos().y, Constants.BOMB_WIDTH, Constants.BOMB_HEIGHT);
                if (!bombRect.intersects(initialPlayerRect))
                    while (bombRect.intersects(translatedPlayerRect))
                    {
                        translatedPlayerRect.translate(fromDirection.x, fromDirection.y);
                        translatePoint(pos, fromDirection);
                    }
            }

            // Collision with solid/soft blocks
            revertPosition(fromDirection, new Point(pos), pos); // check up-left corner

            Point upRightCorner = new Point(pos.x + Constants.PLAYER_WIDTH, pos.y);
            revertPosition(fromDirection, upRightCorner, pos);

            Point downLeftCorner = new Point(pos.x, pos.y + Constants.PLAYER_HEIGHT);
            revertPosition(fromDirection, downLeftCorner, pos);

            Point downRightCorner = new Point(pos.x + Constants.PLAYER_WIDTH, pos.y + Constants.PLAYER_HEIGHT);
            revertPosition(fromDirection, downRightCorner, pos);

        }

        // -------- Planting bombs --------
        if (player.getKeyState().isBomb() && okToPlaceBomb.get(player.getName()))
        {
            int bombCount = 0;
            for (Bomb bomb : gameState.getBombs())
                if (bomb.getPlayerName().equals(player.getName()))
                    bombCount++;
            if (bombCount < player.getMaxNrOfBombs())
            {
                plantBomb(player);
                okToPlaceBomb.put(player.getName(), false);
            }
        }
        if (!player.getKeyState().isBomb())
            okToPlaceBomb.put(player.getName(), true);


        // -------- Damage --------
        if (player.getInvulnerability()==0 && playerTouchesBlock(pos, Block.BLAST) != null)
        {
            player.setInvulnerability(Constants.INVULNERABILITY_LENGTH);
            player.setLives(player.getLives() - 1);
            if (player.getLives() == 0)
                player.setAlive(false);
            gameState.getAudioEvents().add(AudioEvent.PLAYER_DEATH);
        }

        // Collision with holes
        if(player.getInvulnerability()==0 && playerTouchesBlock(pos, Block.HOLE)!=null)
        {
            player.setLives(player.getLives() - 1);
            if (player.getLives() == 0)
                player.setAlive(false);
            gameState.getAudioEvents().add(AudioEvent.PLAYER_DEATH);
            player.setInvulnerability(Constants.INVULNERABILITY_LENGTH);
        }

        // -------- Getting power-ups --------
        Point powerup;
        while ((powerup = playerTouchesBlock(pos, Block.MINUS_BOMB)) != null)
        {
            player.setMaxNrOfBombs(Math.max(1, player.getMaxNrOfBombs() - 1));
            gameState.getAudioEvents().add(AudioEvent.POWERUP);
            gameState.getMap().setGridBlockAt(powerup, Block.BLANK);
        }
        while ((powerup = playerTouchesBlock(pos, Block.PLUS_BOMB)) != null)
        {
            player.setMaxNrOfBombs(player.getMaxNrOfBombs() + 1);
            gameState.getAudioEvents().add(AudioEvent.POWERUP);
            gameState.getMap().setGridBlockAt(powerup, Block.BLANK);
        }
        while ((powerup = playerTouchesBlock(pos, Block.MINUS_RANGE)) != null)
        {
            player.setBombRange(Math.max(Constants.MINIMUM_BOMB_RANGE, player.getBombRange() - Constants.BOMB_RANGE_CHANGE));
            gameState.getAudioEvents().add(AudioEvent.POWERUP);
            gameState.getMap().setGridBlockAt(powerup, Block.BLANK);
        }
        while ((powerup = playerTouchesBlock(pos, Block.PLUS_RANGE)) != null)
        {
            player.setBombRange(Math.min(Constants.MAXIMUM_BOMB_RANGE, player.getBombRange() + Constants.BOMB_RANGE_CHANGE));
            gameState.getAudioEvents().add(AudioEvent.POWERUP);
            gameState.getMap().setGridBlockAt(powerup, Block.BLANK);
        }
        while ((powerup = playerTouchesBlock(pos, Block.MINUS_SPEED)) != null)
        {
            if(player.getSpeed()==Constants.HIGH_PLAYER_SPEED)
                player.setSpeed(Constants.DEFAULT_PLAYER_SPEED);
            else
                player.setSpeed(Constants.LOW_PLAYER_SPEED);
            gameState.getAudioEvents().add(AudioEvent.POWERUP);
            gameState.getMap().setGridBlockAt(powerup, Block.BLANK);
        }
        while ((powerup = playerTouchesBlock(pos, Block.PLUS_SPEED)) != null)
        {
            if(player.getSpeed()==Constants.LOW_PLAYER_SPEED)
                player.setSpeed(Constants.DEFAULT_PLAYER_SPEED);
            else
                player.setSpeed(Constants.HIGH_PLAYER_SPEED);
            gameState.getAudioEvents().add(AudioEvent.POWERUP);
            gameState.getMap().setGridBlockAt(powerup, Block.BLANK);
        }

    }

    /**
     * Checks if a player touches (collides with) any block of a given type on the map.
     * If so, it returns the position of one of them. Otherwise, it return null.
     *
     * @param pos   The position of the player
     * @param block The type of block
     * @return The position of the block or null
     */
    private Point playerTouchesBlock(Point pos, Block block)
    {
        Map map = gameState.getMap();
        if (map.getPixelBlockAt(pos.x, pos.y) == block)
            return new Point(pos.x / 64, pos.y / 64);
        if (map.getPixelBlockAt(pos.x + Constants.PLAYER_WIDTH, pos.y + Constants.PLAYER_HEIGHT) == block)
            return new Point((pos.x + Constants.PLAYER_WIDTH) / 64, (pos.y + Constants.PLAYER_HEIGHT) / 64);
        if (map.getPixelBlockAt(pos.x, pos.y + Constants.PLAYER_HEIGHT) == block)
            return new Point(pos.x / 64, (pos.y + Constants.PLAYER_HEIGHT) / 64);
        if (map.getPixelBlockAt(pos.x + Constants.PLAYER_WIDTH, pos.y) == block)
            return new Point((pos.x + Constants.PLAYER_WIDTH) / 64, pos.y / 64);
        return null;
    }

    /**
     * Translates a point by a vector (represented through a Point)
     *
     * @param point The point to be translated
     * @param delta The movement vector
     */
    private void translatePoint(Point point, Point delta)
    {
        point.translate(delta.x, delta.y);
    }

    /**
     * Makes a player plant a bomb that will explode in the default time (from Constants)
     *
     * @param player The player that plants the bomb
     */
    private void plantBomb(Player player)
    {
        Bomb bomb = new Bomb(player.getName(), getBombLocation(player.getPos()), Constants.DEFAULT_BOMB_TIME, player.getBombRange());
        bomb.setPlayerID(player.getPlayerID());
        gameState.getBombs().add(bomb);
        gameState.getAudioEvents().add(AudioEvent.PLACE_BOMB);
    }

    /**
     * Gets the location where a player will place a bomb
     *
     * @param playerPosition The position of the player
     * @return A Point at the location of the bomb
     */
    private Point getBombLocation(Point playerPosition)
    {
        int xOffset = (Constants.MAP_BLOCK_TO_GRID_MULTIPLIER - Constants.BOMB_WIDTH) / 2;
        int YOffset = (Constants.MAP_BLOCK_TO_GRID_MULTIPLIER - Constants.BOMB_HEIGHT) / 2;
        return new Point((playerPosition.x + Constants.PLAYER_WIDTH / 2) / 64 * 64 + xOffset, (playerPosition.y + Constants.PLAYER_HEIGHT / 2) / 64 * 64 + YOffset);
    }

    /**
     * Resolves a possible collision between a player and a block corner
     *
     * @param fromDirection The opposite of the movement vector of the player
     * @param corner        The position of the corner
     * @param playerPos     The position of the player
     */
    private void revertPosition(Point fromDirection, Point corner, Point playerPos)
    {
        Map map = gameState.getMap();
        while (map.getPixelBlockAt(corner.x, corner.y) == Block.SOLID || map.getPixelBlockAt(corner.x, corner.y) == Block.SOFT)
        {
            translatePoint(corner, fromDirection);
            translatePoint(playerPos, fromDirection);
        }
    }


    // ---------------------------
    // Bomb update related methods
    // ---------------------------

    /**
     * Updates a bomb
     *
     * @param bomb         The Bomb to be updated
     * @param toBeDeleted  A list where the bomb will be put if it needs to be deleted (it explodes)
     * @param milliseconds The amount of time in milliseconds from the last update
     */
    private void updateBomb(Bomb bomb, ArrayList<Bomb> toBeDeleted, int milliseconds)
    {
        decreaseBombTimer(bomb, milliseconds);
        if (bomb.getTime() <= 0)
        {
            toBeDeleted.add(bomb);
            Point pos = bomb.getPos();
            int radius = bomb.getRadius();
            addBlast(pos.x / 64, pos.y / 64, radius, 0);
            gameState.getAudioEvents().add(AudioEvent.EXPLOSION);
        }
    }

    /**
     * Simulates the propagation of an explosion, adding Blast blocks to the map
     *
     * @param x         The x coordinate of the blast
     * @param y         The y coordinate of the blast
     * @param radius    The "power" of the blast (it is decreased by 1 for each block the explosion travels through)
     * @param direction The direction of the blast propagation (0 all, 1 up, 2 right, 3 down, 4 left)
     */
    private void addBlast(int x, int y, int radius, int direction)
    {
        Point pos = new Point(x, y);
        if (!gameState.getMap().isInGridBounds(pos))
            return;
        if (radius == 0 || gameState.getMap().getGridBlockAt(x, y) == Block.SOLID)
            return;
        if (gameState.getMap().getGridBlockAt(x, y) == Block.SOFT)
            gameState.getBlastList().add(new BlastTimer(pos));
        else
        {
            if (gameState.getMap().getGridBlockAt(x, y) == Block.HOLE)  // this can't be destroyed but does not stop the blast
                gameState.getBlastList().add(new BlastTimer(pos, Block.HOLE));
            else
                updateBlastList(pos, Block.BLANK);
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
        }

        gameState.getMap().setGridBlockAt(pos, Block.BLAST);

    }

    /**
     * Updates the blast list entry corresponding to a position.
     * If an entry is already there, it updates the timer. If not, it adds a new entry.
     * @param position
     * @param reveal
     */
    private void updateBlastList(Point position, Block reveal)
    {
        Optional<BlastTimer> blastTimer = gameState.getBlastList().stream()
                .filter(timer -> timer.getLocation().equals(position)).findAny();
        if(blastTimer.isPresent())
            blastTimer.get().setTimer(Constants.EXPLOSION_LENGTH);
        else
            gameState.getBlastList().add(new BlastTimer(position, reveal));
    }

    /**
     * Updates the timer of a bomb
     *
     * @param bomb         The bomb to be updated
     * @param milliseconds The amount of time in milliseconds from the last update
     */
    private void decreaseBombTimer(Bomb bomb, int milliseconds)
    {
        bomb.setTime(bomb.getTime() - milliseconds);
    }

    // --------------------------
    // Map update related methods
    // --------------------------

    /**
     * Updates the map
     *
     * @param milliseconds The amount of time in milliseconds from the last update
     */
    private void updateMap(int milliseconds)
    {
        Map map = gameState.getMap();

        // clear the blast
        gameState.getBlastList().forEach(blastTimer ->
        {
            blastTimer.decreaseTimer(milliseconds);
            if (blastTimer.isDone())
                map.setGridBlockAt(blastTimer.getLocation(), blastTimer.getReveal());
        });
        gameState.getBlastList().removeIf(BlastTimer::isDone);
    }

}