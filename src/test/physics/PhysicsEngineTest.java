package test.physics;

import bomber.game.*;
import bomber.physics.PhysicsEngine;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;

import static bomber.game.Block.*;
import static org.junit.Assert.*;

/**
 * Tests the physics engine
 *
 * @author Alexandru Rosu
 */
public class PhysicsEngineTest
{

    private Map map;
    private ArrayList<Player> players;
    private GameState gameState;
    private PhysicsEngine engine;
    private Player buddy;

    // locations
    private Point centerForBombs;
    private Point leftOfCenter;
    private Point collisionWithSoft;
    private Point collisionWithSolid;
    private Point collisionWithHole;
    private Point powerups1;
    private Point powerups2;
    private Point powerups3;

    @Before
    public void setUp()
    {
        int mult = Constants.MAP_BLOCK_TO_GRID_MULTIPLIER;
        Block[][] blocks = {
                {BLANK, BLANK, BLANK, BLANK, BLANK,        SOLID, BLANK, BLANK, BLANK, SOLID},
                {BLANK, BLANK, BLANK, BLANK, BLANK,        BLANK, BLANK, BLANK, BLANK, SOLID},
                {BLANK, BLANK, BLANK, BLANK, BLANK,        BLAST, BLANK, BLANK, BLANK, SOLID},
                {SOLID, BLANK,  HOLE, BLAST, BLANK,        BLANK, BLANK,  SOFT, SOLID, SOLID},
                {BLANK, BLANK, BLANK, BLANK, BLANK,        BLANK, BLANK, BLANK, BLANK, SOLID},
                {BLANK, BLANK, BLANK, BLANK, BLANK,    PLUS_BOMB, BLANK, BLANK, BLANK, SOLID},
                {BLANK, BLANK, BLANK, BLANK, BLANK,   PLUS_SPEED, BLANK, BLANK, BLANK, SOLID},
                {BLANK, BLANK, BLANK, BLANK, BLANK,   MINUS_BOMB, BLANK, BLANK, BLANK, SOLID},
                {BLANK, BLANK, BLANK, BLANK, BLANK,  MINUS_RANGE, BLANK, BLANK, BLANK, SOLID},
                {BLANK, BLANK, BLANK, BLANK, BLANK,   PLUS_RANGE, BLANK, BLANK, BLANK, SOLID},
                {BLANK, BLANK, BLANK, BLANK, BLANK,  MINUS_SPEED, BLANK, BLANK, BLANK, SOLID},
                {BLANK, BLANK, BLANK, BLANK, BLANK,        BLANK, BLANK, BLANK, BLANK, SOLID},
                {BLANK, BLANK, BLANK, BLANK, BLANK,        BLANK, BLANK, BLANK, BLANK, SOLID},
                {BLANK, BLANK, BLANK, BLANK, BLANK,        SOLID, BLANK, BLANK, BLANK, SOLID}
        };
        map = new Map("Test map", blocks, null);

        centerForBombs = new Point(3*mult, 5*mult);
        leftOfCenter = new Point(3*mult, 4*mult);
        collisionWithSoft = new Point(2*mult+Constants.MAP_BLOCK_TO_GRID_MULTIPLIER-Constants.PLAYER_HEIGHT-1, 7*mult);
        collisionWithSolid = new Point(2*mult+Constants.MAP_BLOCK_TO_GRID_MULTIPLIER-Constants.PLAYER_HEIGHT-1, 8*mult);
        collisionWithHole = new Point(2*mult+Constants.MAP_BLOCK_TO_GRID_MULTIPLIER-Constants.PLAYER_HEIGHT-1, 2*mult);
        powerups1 = new Point(10*mult-10, 5*mult+40);
        powerups2 = new Point(8*mult-10, 5*mult-10);
        powerups3 = new Point(6*mult-10, 5*mult-10);


        buddy = new Player("Buddy", null, 3, 10);
        buddy.getKeyState().setMovement(Movement.NONE);
        buddy.setSpeed(0);
        players = new ArrayList<>();
        players.add(buddy);

        gameState = new GameState(map, players);
        engine = new PhysicsEngine(gameState);
    }

    @Test
    public void movement()
    {
        buddy.setPos(new Point(centerForBombs));
        int x = centerForBombs.x;
        int y= centerForBombs.y;
        Point pos = buddy.getPos();

        engine.update(10);
        assertEquals("The player moved even though speed=0 and movement=None", centerForBombs, pos);

        buddy.setSpeed(1000);
        engine.update(10);
        assertEquals("The player moved even though movement=None", centerForBombs, pos);

        buddy.getKeyState().setMovement(Movement.RIGHT);
        engine.update(10);
        assertTrue("The player did not move correctly", pos.x==x+10 && pos.y==y);

        buddy.getKeyState().setMovement(Movement.LEFT);
        engine.update(10);
        assertTrue("The player did not move correctly", pos.x==x && pos.y==y);

        buddy.getKeyState().setMovement(Movement.UP);
        engine.update(10);
        assertTrue("The player did not move correctly", pos.x==x && pos.y==y-10);

        buddy.getKeyState().setMovement(Movement.DOWN);
        engine.update(10);
        assertTrue("The player did not move correctly", pos.x==x && pos.y==y);
    }

    @Test
    public void collisionWithSoftAndSolid()
    {
        buddy.setSpeed(10);
        buddy.getKeyState().setMovement(Movement.RIGHT);

        buddy.setPos(new Point(collisionWithSoft));
        engine.update();
        assertEquals("The collision was not detected correctly.", collisionWithSoft, buddy.getPos());

        buddy.setPos(new Point(collisionWithSolid));
        engine.update();
        assertEquals("The collision was not detected correctly.", collisionWithSolid, buddy.getPos());
    }

    /**
     * Tests if the bombs can be planted, explode at the right time, create blast only where needed and the blast is cleared
     */
    @Test
    public void bombPhysics()
    {

        buddy.setPos(centerForBombs);
        buddy.getKeyState().setBomb(true);
        buddy.setBombRange(9);

        engine.update();
        buddy.getKeyState().setBomb(false);
        assertTrue("Bomb was not planted.", 1==gameState.getBombs().size());

        engine.update(Constants.DEFAULT_BOMB_TIME-10);
        assertTrue("Bomb exploded too early.", 1==gameState.getBombs().size());

        engine.update(10);
        assertTrue("Bomb did not explode.", 0==gameState.getBombs().size());
        assertEquals("Blast expected at the place of an explosion.",BLAST, gameState.getMap().getGridBlockAt(3, 5));


        // check the way the blocks are changed
        Block[][] gridMap = gameState.getMap().getGridMap();
        int[] blankXs = {0, 0, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 12};
        int[] blankYs = {4, 6, 4, 6, 0, 1, 2, 3, 4, 6, 7, 8, 0, 1, 2, 3, 4, 6, 7, 8, 4, 6, 4, 6, 4, 6, 4, 6, 4, 6, 5};
        int[] blastXs = {1, 2, 3, 3, 3, 3, 3, 3, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        int[] blastYs = {5, 5, 1, 2, 3, 4, 5, 6, 7, 5, 5, 5, 5, 5, 5,  5,  5};
        int[] solidXs = {0, 3, 3, 13};
        int[] solidYs = {5, 0, 8,  5};


        for(int i=0;i<blankXs.length;i++)
            assertEquals("The blast did not propagate the right way. [" + blankXs[i] + "," + blankYs[i] + "]",BLANK, gridMap[blankXs[i]][blankYs[i]]);
        for(int i=0;i<blastXs.length;i++)
            assertEquals("The blast did not propagate the right way. [" + blastXs[i] + "," + blastYs[i] + "]",BLAST, gridMap[blastXs[i]][blastYs[i]]);
        for(int i=0;i<solidXs.length;i++)
            assertEquals("The blast did not propagate the right way. [" + solidXs[i] + "," + solidYs[i] + "]",SOLID, gridMap[solidXs[i]][solidYs[i]]);

        engine.update(Constants.EXPLOSION_LENGTH);

        assertEquals("Bomb blast removed Hole", HOLE, gameState.getMap().getGridBlockAt(3, 2));
        for(int i=0;i<blastXs.length;i++)
        {
            assertNotEquals("Bomb blast did not clear the right way. [" + blastXs[i] + "," + blastYs[i] + "]", BLAST, gridMap[blastXs[i]][blastYs[i]]);
            assertNotEquals("Bomb blast did not clear the right way. [" + blastXs[i] + "," + blastYs[i] + "]", SOFT, gridMap[blastXs[i]][blastYs[i]]);
            assertNotEquals("Bomb blast did not clear the right way. [" + blastXs[i] + "," + blastYs[i] + "]", SOLID, gridMap[blastXs[i]][blastYs[i]]);
        }

    }

    @Test
    public void tooManyBombs()
    {
        buddy.setPos(centerForBombs);
        buddy.setMaxNrOfBombs(1);
        buddy.getKeyState().setBomb(true);
        engine.update(1);
        buddy.getKeyState().setBomb(false);
        engine.update(1);
        buddy.getKeyState().setBomb(true);
        engine.update(1);
        assertEquals("The bomb limit did not work correcly", 1, gameState.getBombs().size());
    }

    @Test
    public void collisionWithBomb()
    {
        buddy.setPos(new Point(centerForBombs));
        buddy.getKeyState().setBomb(true);
        engine.update(1);
        int x = centerForBombs.x;
        int y = centerForBombs.y;
        Point pos = buddy.getPos();
        buddy.setSpeed(1000);


        buddy.getKeyState().setMovement(Movement.DOWN);
        engine.update(10);
        assertTrue("The collision with the bomb was not computed correctly", pos.x==x && pos.y==y+10);

        buddy.getKeyState().setMovement(Movement.UP);
        engine.update(10);
        assertTrue("The collision with the bomb was not computed correctly", pos.x==x && pos.y==y);

        buddy.getKeyState().setMovement(Movement.LEFT);
        engine.update(10);
        assertTrue("The collision with the bomb was not computed correctly", pos.x==x-10 && pos.y==y);

        buddy.getKeyState().setMovement(Movement.RIGHT);
        engine.update(70);
        assertTrue("The collision with the bomb was not computed correctly", pos.x==x+60 && pos.y==y);

        buddy.getKeyState().setMovement(Movement.LEFT);
        engine.update(70);
        assertTrue("The collision with the bomb was not computed correctly", pos.x==249 && pos.y==320);
    }

    @Test
    public void bombOverlap()
    {
        buddy.setPos(centerForBombs);
        buddy.setBombRange(2);
        buddy.getKeyState().setBomb(true);
        engine.update(1); // B1
        buddy.getKeyState().setBomb(false);
        engine.update(50); // B1 + 50
        buddy.setPos(leftOfCenter);
        buddy.getKeyState().setBomb(true);
        engine.update(1); // B2 = B1 + 51
        engine.update(Constants.DEFAULT_BOMB_TIME-51); //B2+1949 = E1
        engine.update(51); // E2 = E1 + 51

        Block[][] gridMap = gameState.getMap().getGridMap();
        assertEquals("Second explosion did not overlap correctly with the first one", HOLE, gridMap[3][2]);
        assertEquals("Second explosion did not overlap correctly with the first one", BLAST, gridMap[3][3]);
        assertEquals("Second explosion did not overlap correctly with the first one", BLAST, gridMap[3][4]);
        assertEquals("Second explosion did not overlap correctly with the first one", BLAST, gridMap[3][5]);
        assertEquals("Second explosion did not overlap correctly with the first one", BLAST, gridMap[3][6]);

        engine.update(Constants.EXPLOSION_LENGTH-51); // E2 + 1949 = E1 + 2000
        gridMap = gameState.getMap().getGridMap();
        assertEquals("First explosion did not clear correctly", BLANK, gridMap[3][6]);
        assertEquals("First explosion did not clear correctly", BLAST, gridMap[3][3]);
        assertEquals("First explosion did not clear correctly", BLAST, gridMap[3][4]);
        assertEquals("First explosion did not clear correctly", BLAST, gridMap[3][5]);

        engine.update(51); // E2 + 2000
        gridMap = gameState.getMap().getGridMap();
        assertEquals("Second explosion did not clear correctly", BLANK, gridMap[3][3]);
        assertEquals("Second explosion did not clear correctly", BLANK, gridMap[3][4]);
        assertEquals("Second explosion did not clear correctly", BLANK, gridMap[3][5]);

    }

    @Test
    public void playerKill()
    {
        buddy.setPos(new Point(66, 8*64+1));
        buddy.setSpeed(0);
        buddy.setLives(1);
        buddy.getKeyState().setBomb(true);
        engine.update(); // planting bomb
        buddy.getKeyState().setBomb(false);
        engine.update(2000); // exploding bomb
        assertFalse("The player was not killed by standing on a bomb", buddy.isAlive());

        buddy.setLives(3);
        buddy.setAlive(true);
        buddy.getKeyState().setBomb(true);
        engine.update();
        engine.update(2000);
        assertEquals("The number of lives of the player did not decrease", 2, buddy.getLives());

        buddy.getKeyState().setBomb(false);
        engine.update();
        buddy.getKeyState().setBomb(true);
        engine.update(1); // B1
        buddy.getKeyState().setBomb(false);
        engine.update(Constants.DEFAULT_BOMB_TIME-5); // B1 + 1995
        buddy.getKeyState().setBomb(true);
        engine.update(1); // B2 = B1 + 1996
        buddy.getKeyState().setBomb(false);
        engine.update(4); // B2 + 4 = B1 + 2000 = E1
        buddy.getKeyState().setBomb(true);
        engine.update(1); // B3 = B2 + 5 = E1 + 1
        engine.update(Constants.DEFAULT_BOMB_TIME-5); // B3+1995 = B2 + 2000 = E2 = E1 + 1996
        assertEquals("The invulnerability of the player did not manifest correctly.", 1, buddy.getLives());
        engine.update(5);
        assertEquals("The invulnerability of the player did not end.", 0, buddy.getLives());

    }

    @Test
    public void collisionWithHole()
    {
        buddy.setLives(1);
        buddy.setPos(new Point(collisionWithHole));
        buddy.setSpeed(1000);
        buddy.getKeyState().setMovement(Movement.RIGHT);

        engine.update(20);
        assertEquals("The hole did not decrease the lives of the player", 0, buddy.getLives());

        buddy.setLives(2);

        engine.update(20);
        assertEquals("The hole decreased the lives of the player twice", 2, buddy.getLives());

        engine.update(60);
        buddy.getKeyState().setMovement(Movement.LEFT);
        engine.update(60);
        assertEquals("The hole decreased the lives of the player twice", 2, buddy.getLives());
    }

    @Test
    public void powerUps()
    {
        buddy.setSpeed(30);
        buddy.setBombRange(3);
        buddy.setMaxNrOfBombs(3);

        buddy.setPos(powerups1);
        engine.update(10);
        assertEquals("The PLUS_RANGE powerup did not work", 4, buddy.getBombRange());
        assertEquals("The MINUS_SPEED powerup did not work", Constants.LOW_PLAYER_SPEED, buddy.getSpeed(), 0);

        buddy.setPos(powerups2);
        engine.update(10);
        assertEquals("The MINUS_RANGE powerup did not work", 3, buddy.getBombRange());
        assertEquals("The MINUS_BOMB powerup did not work", 2, buddy.getMaxNrOfBombs());

        buddy.setPos(powerups3);
        engine.update(10);
        assertEquals("The PLUS_SPEED powerup did not work", Constants.DEFAULT_PLAYER_SPEED, buddy.getSpeed(), 0);
        assertEquals("The PLUS_BOMB powerup did not work", 3, buddy.getMaxNrOfBombs());

        buddy.setPos(centerForBombs);
        map.setGridBlockAt(new Point(3,5), PLUS_SPEED);
        engine.update(10);
        assertEquals("The PLUS_SPEED powerup did not work", Constants.HIGH_PLAYER_SPEED, buddy.getSpeed(), 0);

        map.setGridBlockAt(new Point(3,5), MINUS_SPEED);
        engine.update(10);
        assertEquals("The MINUS_SPEED powerup did not work", Constants.DEFAULT_PLAYER_SPEED, buddy.getSpeed(), 0);
    }

    @Test
    public void powerupGeneration()
    {
        buddy.setPos(centerForBombs);
        buddy.setBombRange(2);
        buddy.setLives(10000);
        int nrOfPowerups = 0;
        int i;

        for(i=0; i<100; i++)
        {
            map.setGridBlockAt(new Point(3, 4), SOFT);
            map.setGridBlockAt(new Point(3, 6), SOFT);
            buddy.getKeyState().setBomb(true);
            engine.update();
            buddy.getKeyState().setBomb(false);
            engine.update(Constants.DEFAULT_BOMB_TIME);
            engine.update(Constants.EXPLOSION_LENGTH);
            if(map.getGridBlockAt(3,4)!=BLANK)
                nrOfPowerups++;
            if(map.getGridBlockAt(3,6)!=BLANK)
                nrOfPowerups++;
        }

        System.out.println("The power-up generation test got " + nrOfPowerups + " power-ups out of " + i*2 + " exploded blocks. This means " + (50.0*nrOfPowerups/i) + "%");

    }

}