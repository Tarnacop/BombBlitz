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

    @Before
    public void setUp() throws Exception
    {
        Block[][] blocks = {{BLANK, BLANK, BLANK, SOLID, SOFT, SOLID, SOFT, SOFT, SOFT, SOFT, SOFT},
                {BLANK, BLANK, SOFT, BLANK, SOLID, SOLID, SOLID, SOFT, BLANK, SOFT, SOLID},
                {BLANK, BLANK, BLANK, BLANK, SOFT, SOLID, SOFT, SOFT, SOFT, SOFT, SOFT},
                {SOLID, SOFT, SOLID, SOFT, SOLID, SOLID, SOFT, SOFT, SOFT, SOFT, SOFT}};
        map = new Map("Test map", blocks, null);

        players = new ArrayList<>();
        buddy = new Player("Buddy", new Point(5,5), 3, 10);
        players.add(buddy);
        gameState = new GameState(map, players);
        engine = new PhysicsEngine(gameState);
    }

    @Test
    public void addPlayer() throws Exception
    {
        Player testPlayer1 = new Player("TestPlayer1", new Point(5,5), 1, 10);
        gameState.getPlayers().add(testPlayer1);
        assertNotNull("Player was not added or does not have the given name.", testPlayer1);
        assertEquals("Added player does not have the given position", new Point(5,5), testPlayer1.getPos());
        assertEquals("Added player does not have the given number of lives", 1, testPlayer1.getLives());
        assertEquals("Added player does not have the given speed", 10.0, testPlayer1.getSpeed(), 0);

        Player testPlayer2 = new Player("TestPlayer2", new Point(3,4), 3, 14);
        gameState.getPlayers().add(testPlayer2);
        assertNotNull("Player was not added or does not have the given name.", testPlayer2);
        assertEquals("Added player does not have the given position", new Point(3, 4), testPlayer2.getPos());
        assertEquals("Added player does not have the given number of lives", 3, testPlayer2.getLives());
        assertEquals("Added player does not have the given speed", 14.0, testPlayer2.getSpeed(), 0);
    }

    @Test
    public void collisions() throws Exception
    {
        KeyboardState kState = buddy.getKeyState();
        kState.setMovement(Movement.RIGHT);

        for(int i=0; i<20; i++)
        {
            engine.update();
            //System.out.println(buddy.getPos());
            assertTrue("Collision was not detected successfully (problem at a right corner)", buddy.getPos().x+ Constants.PLAYER_WIDTH<192);
        }

        kState.setMovement(Movement.DOWN);
        for(int i=0; i<20; i++)
        {
            engine.update();
            //System.out.println(buddy.getPos());
            assertTrue("Collision was not detected successfully (problem at a down corner)", buddy.getPos().y+ Constants.PLAYER_HEIGHT<256);
        }

        kState.setMovement(Movement.LEFT);
        for(int i=0; i<20; i++)
        {
            engine.update();
            //System.out.println(buddy.getPos());
            assertTrue("Collision was not detected successfully (problem at a left corner)", buddy.getPos().x>63);
        }

        kState.setMovement(Movement.UP);
        for(int i=0; i<20; i++)
        {
            engine.update();
            //System.out.println(buddy.getPos());
            assertTrue("Collision was not detected successfully (problem at an up corner)", buddy.getPos().y>191);
        }

    }

    @Test
    public void plantBomb() throws Exception
    {
        buddy.getKeyState().setMovement(Movement.RIGHT);
        buddy.setPos(new Point(66, 8*64+1));
        buddy.setSpeed(0);
        buddy.getKeyState().setBomb(true);

        assertTrue("Bomb was not planted.", 1==gameState.getBombs().size());

        engine.update(2000);

        assertTrue("Bomb did not explode.", 0==gameState.getBombs().size());
        assertEquals("Blast expected at the place of an explosion.",BLAST, gameState.getMap().getGridBlockAt(1, 8));

        engine.update();

        assertEquals("Bomb blast did not turn to Blank after a second", BLANK, gameState.getMap().getGridBlockAt(1, 8));

        Map map = gameState.getMap();
        int width = gameState.getMap().getGridMap().length;
        int height = gameState.getMap().getGridMap()[0].length;
        for(int x=0; x<width; x++)
            for(int y=0; y<height; y++)
                assertNotEquals("Bomb blast did not clear after a frame", Block.BLAST, map.getGridBlockAt(x,y));
    }

    @Test
    public void playerKill() throws Exception
    {
        buddy.setPos(new Point(66, 8*64+1));
        buddy.setSpeed(0);
        buddy.setLives(1);
        buddy.getKeyState().setBomb(true);
        engine.update();
        assertFalse("The player was not killed by standing on a bomb", buddy.isAlive());

        buddy.setLives(3);
        buddy.setAlive(true);
        buddy.getKeyState().setBomb(true);
        engine.update();
        assertEquals("The number of lives of the player did not decrease", 2, buddy.getLives());
    }

    @Test
    public void update() throws Exception
    {
        KeyboardState kState = buddy.getKeyState();

        kState.setMovement(Movement.DOWN);
        engine.update();
        assertEquals("The position of the player after update does not match expected value", new Point(5, 15), buddy.getPos());

        kState.setMovement(Movement.RIGHT);
        buddy.setSpeed(30);
        engine.update();
        assertEquals("The position of the player after update does not match expected value", new Point(35, 15), buddy.getPos());

        kState.setMovement(Movement.UP);
        buddy.setSpeed(5);
        engine.update();
        assertEquals("The position of the player after update does not match expected value", new Point(35, 10), buddy.getPos());

        kState.setMovement(Movement.LEFT);
        buddy.setSpeed(20);
        engine.update();
        assertEquals("The position of the player after update does not match expected value", new Point(15, 10), buddy.getPos());
    }

    @Test
    public void updateWithInterval() throws Exception
    {
        buddy.getKeyState().setMovement(Movement.DOWN);
        buddy.setSpeed(100);

        // pixels - speed*milliseconds / 1000

        // TODO: look into parameterised tests
        int[] intervals = {10, 20, 30, 50, 100, 50, 30, 20, 10, 130, 500};
        int[] positionY = {6,  8,  11, 16, 26,  31, 34, 36, 37, 50,  100};

        for(int i=0; i<intervals.length; i++)
        {
            engine.update(intervals[i]);
            assertEquals("The position of the player after update does not match expected value", new Point(5, positionY[i]), buddy.getPos());
        }

    }

}