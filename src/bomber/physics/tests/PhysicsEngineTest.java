package bomber.physics.tests;

import bomber.game.*;
import bomber.physics.PhysicsEngine;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;

import static bomber.game.Block.*;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Alexandruro on 26.01.2017.
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
        map = new Map(blocks);
        players = new ArrayList<>();
        buddy = new Player("Buddy", new Point(5,5), 3, 10);
        players.add(buddy);
        gameState = new GameState(map, players, new ArrayList<>());
        engine = new PhysicsEngine(gameState);
    }

    @Test
    public void addPlayer() throws Exception
    {
        engine.addPlayer("TestPlayer1", new Point(5,5), 1, 10);
        Player testPlayer1 = engine.getPlayerNamed("TestPlayer1");
        assertNotNull("Player was not added or does not have the given name.", testPlayer1);
        assertEquals("Added player does not have the given position", new Point(5,5), testPlayer1.getPos());
        assertEquals("Added player does not have the given number of lives", 1, testPlayer1.getLives());
        assertEquals("Added player does not have the given speed", 10.0, testPlayer1.getSpeed());

        engine.addPlayer("TestPlayer2", new Point(3,4), 3, 14);
        Player testPlayer2 = engine.getPlayerNamed("TestPlayer2");
        assertNotNull("Player was not added or does not have the given name.", testPlayer2);
        assertEquals("Added player does not have the given position", new Point(3, 4), testPlayer2.getPos());
        assertEquals("Added player does not have the given number of lives", 3, testPlayer2.getLives());
        assertEquals("Added player does not have the given speed", 14.0, testPlayer2.getSpeed());
    }

    @Test
    public void collisions() throws Exception
    {
        KeyboardState kState = buddy.getKeyState();
        kState.setKey(Movement.RIGHT);

        for(int i=0; i<20; i++)
        {
            engine.updateAll();
            //System.out.println(buddy.getPos());
            assertTrue("Collision was not detected successfully (problem at a right corner)", buddy.getPos().x+PhysicsEngine.playerPixelWidth<192);
        }

        kState.setKey(Movement.DOWN);
        for(int i=0; i<20; i++)
        {
            engine.updateAll();
            //System.out.println(buddy.getPos());
            assertTrue("Collision was not detected successfully (problem at a down corner)", buddy.getPos().y+PhysicsEngine.playerPixelHeight<256);
        }

        kState.setKey(Movement.LEFT);
        for(int i=0; i<20; i++)
        {
            engine.updateAll();
            //System.out.println(buddy.getPos());
            assertTrue("Collision was not detected successfully (problem at a left corner)", buddy.getPos().x>63);
        }

        kState.setKey(Movement.UP);
        for(int i=0; i<20; i++)
        {
            engine.updateAll();
            //System.out.println(buddy.getPos());
            assertTrue("Collision was not detected successfully (problem at an up corner)", buddy.getPos().y>191);
        }

    }

    @Test
    public void plantBomb() throws Exception
    {

        buddy.getKeyState().setKey(Movement.RIGHT);
        buddy.setPos(new Point(66, 8*64+1));
        buddy.setSpeed(0);
        engine.plantBomb("Buddy", 0, 3);

        assertTrue("Bomb was not planted.", 1==gameState.getBombs().size());

        engine.updateAll();

        assertTrue("Bomb did not explode.", 0==gameState.getBombs().size());
        assertEquals("Blast expected at the place of an explosion.",BLAST, gameState.getMap().getGridBlockAt(1, 8));

        engine.updateAll();

        assertEquals("Bomb blast did not turn to Blank after a frame", BLANK, gameState.getMap().getGridBlockAt(1, 8));

        Map map = gameState.getMap();
        int width = gameState.getMap().getGridMap().length;
        int height = gameState.getMap().getGridMap()[0].length;
        for(int x=0; x<width; x++)
            for(int y=0; y<height; y++)
                assertNotEquals("Bomb blast did not clear after a frame", Block.BLAST, map.getGridBlockAt(x,y));
    }

    @Test
    public void updateAll() throws Exception
    {
        KeyboardState kState = buddy.getKeyState();

        kState.setKey(Movement.DOWN);
        engine.updateAll();
        assertEquals("The position of the player after update does not match expected value", new Point(5, 15), buddy.getPos());

        kState.setKey(Movement.RIGHT);
        buddy.setSpeed(30);
        engine.updateAll();
        assertEquals("The position of the player after update does not match expected value", new Point(35, 15), buddy.getPos());

        kState.setKey(Movement.UP);
        buddy.setSpeed(5);
        engine.updateAll();
        assertEquals("The position of the player after update does not match expected value", new Point(35, 10), buddy.getPos());

        kState.setKey(Movement.LEFT);
        buddy.setSpeed(20);
        engine.updateAll();
        assertEquals("The position of the player after update does not match expected value", new Point(15, 10), buddy.getPos());
    }

}