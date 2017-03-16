package bomber.physics;

import bomber.game.*;
import bomber.game.Map;

import java.awt.*;
import java.util.*;

/**
 * Created by Alexandru Rosu on 15.01.2017.
 */
public class PhysicsEngine
{

    private GameState gameState;

    private HashMap<String, Boolean> okToPlaceBomb;

    private Stack<Point> powerUpPossibleLocations;

    /**
     * Creates an engine using a GameState
     * @param gameState The GameState object
     */
    public PhysicsEngine(GameState gameState)
    {
        this.gameState = gameState;
        okToPlaceBomb = new HashMap<>();
        powerUpPossibleLocations = new Stack<>();
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
        int xOffset = (Constants.MAP_BLOCK_TO_GRID_MULTIPLIER - Constants.BOMB_WIDTH)/2;
        int YOffset = (Constants.MAP_BLOCK_TO_GRID_MULTIPLIER - Constants.BOMB_HEIGHT)/2;
        return new Point((playerPosition.x+ Constants.PLAYER_WIDTH/2)/64 * 64+xOffset, (playerPosition.y+ Constants.PLAYER_HEIGHT/2)/64*64+YOffset);
    }

    public synchronized void update(int milliseconds)
    {
        // update map after blast
        Map map = gameState.getMap();
        // generate power-ups
        while(!powerUpPossibleLocations.isEmpty())
        {
            Point p = powerUpPossibleLocations.pop();
            //System.out.println("Possible power-up at " + p.toString());
            map.setGridBlockAt(p, getRandomBlock());
        }
        // clear the blast
        int width = gameState.getMap().getGridMap().length;
        int height = gameState.getMap().getGridMap()[0].length;
        for(int x=0; x<width; x++)
            for(int y=0; y<height; y++)
                if (map.getGridBlockAt(x,y) == Block.BLAST)
                {
                    map.setGridBlockAt(new Point(x,y), Block.BLANK);
                }

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

        // -------- Movement --------
        Movement movement = player.getKeyState().getMovement();
        if (movement != Movement.NONE)
        {

            // Play sound effects
            gameState.getAudioEvents().add(AudioEvent.MOVEMENT);

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
            // If the dimensions ever change, this should be checked
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
        if (player.getKeyState().isBomb() && okToPlaceBomb.get(player.getName()) != null && okToPlaceBomb.get(player.getName()))
        {
            int bombCount = 0;
            for (Bomb bomb : gameState.getBombs())
                if (bomb.getPlayerName().equals(player.getName()))
                    bombCount++;
            if (bombCount < player.getMaxNrOfBombs())
            {
                plantBomb(player, Constants.DEFAULT_BOMB_TIME);
                okToPlaceBomb.put(player.getName(), false);
            }
        }
        if (!player.getKeyState().isBomb())
            okToPlaceBomb.put(player.getName(), true);


        // -------- Damage --------
        if (playerTouchesBlock(pos, Block.BLAST) != null)
        {
            player.setLives(player.getLives() - 1);
            if (player.getLives() == 0)
                player.setAlive(false);
            gameState.getAudioEvents().add(AudioEvent.PLAYER_DEATH);
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
            player.setSpeed(Constants.LOW_PLAYER_SPEED);
            gameState.getAudioEvents().add(AudioEvent.POWERUP);
            gameState.getMap().setGridBlockAt(powerup, Block.BLANK);
        }
        while ((powerup = playerTouchesBlock(pos, Block.PLUS_SPEED)) != null)
        {
            player.setSpeed(Constants.HIGH_PLAYER_SPEED);
            gameState.getAudioEvents().add(AudioEvent.POWERUP);
            gameState.getMap().setGridBlockAt(powerup, Block.BLANK);
        }

    }

    private Point playerTouchesBlock(Point pos, Block block)
    {
        // Return: 0 no, 1 up-left, 2 up-right, 3 down-left, 4 down-right
        Map map = gameState.getMap();
        if (map.getPixelBlockAt(pos.x, pos.y)== block)
            return new Point(pos.x/64,pos.y/64);
        if (map.getPixelBlockAt(pos.x+ Constants.PLAYER_WIDTH,pos.y+ Constants.PLAYER_HEIGHT)== block)
            return new Point((pos.x+ Constants.PLAYER_WIDTH)/64,(pos.y+ Constants.PLAYER_HEIGHT)/64);
        if (map.getPixelBlockAt(pos.x,pos.y+ Constants.PLAYER_HEIGHT)== block)
            return new Point(pos.x/64,(pos.y+ Constants.PLAYER_HEIGHT)/64);
        if (map.getPixelBlockAt(pos.x+ Constants.PLAYER_WIDTH,pos.y)== block)
            return new Point((pos.x+ Constants.PLAYER_WIDTH)/64,pos.y/64);
        return null;
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

    private void revertPosition(Point fromDirection, Point corner, Point playerPos)
    {
        Map map = gameState.getMap();
        while(map.getPixelBlockAt(corner.x, corner.y) == Block.SOLID || map.getPixelBlockAt(corner.x, corner.y) == Block.SOFT)
        {
            translatePoint(corner, fromDirection);
            translatePoint(playerPos, fromDirection);
        }
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
        if(gameState.getMap().getGridBlockAt(x, y) == Block.SOFT)
            powerUpPossibleLocations.push(pos);
        else
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

    // --------------------------
    // Map update related methods
    // --------------------------

    private Block getRandomBlock()
    {
        Random generator = new Random();
        boolean isPowerup = generator.nextInt(100) < Constants.POWERUP_PROBABILITY;
        if(!isPowerup)
            return Block.BLANK;
        int isPositive = generator.nextInt(100) < Constants.POSITIVE_POWERUP_PROBABILITY ? 0 : 10; // 0 is positive, 10 is negative
        int powerup = generator.nextInt(3); // first, second or third power-up

        switch (isPositive+powerup)
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