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

/**
 * Created by Alexandruro on 26.01.2017.
 */
public class PhysicsEngineTest
{

    private Map map;
    private ArrayList<Player> players;
    private GameState gameState;
    private PhysicsEngine engine;


    @Before
    public void setUp() throws Exception
    {
        Block[][] blocks = {{BLANK, BLANK, BLANK, SOLID, SOFT}, {BLANK, BLANK, SOFT, BLANK, SOLID}, {BLANK, BLANK, BLANK, BLANK, SOFT}, {SOLID, SOFT, SOLID, SOFT, SOLID}};
        map = new Map(blocks);
        players = new ArrayList<>();
        players.add(new Player("Buddy", new Point(5,5), 3, 10));
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
        Player buddy = engine.getPlayerNamed("Buddy");
        KeyboardState kState = buddy.getKeyState();
        kState.setKey(Movement.RIGHT);

        for(int i=0; i<20; i++)
        {
            engine.updateAll();
            System.out.println(buddy.getPos());
            assertTrue("Collision was not detected successfully (problem at a right corner)", buddy.getPos().x+PhysicsEngine.playerPixelWidth<192);
        }

        kState.setKey(Movement.DOWN);
        for(int i=0; i<20; i++)
        {
            engine.updateAll();
            System.out.println(buddy.getPos());
            assertTrue("Collision was not detected successfully (problem at a down corner)", buddy.getPos().y+PhysicsEngine.playerPixelHeight<256);
        }

        kState.setKey(Movement.LEFT);
        for(int i=0; i<20; i++)
        {
            engine.updateAll();
            System.out.println(buddy.getPos());
            assertTrue("Collision was not detected successfully (problem at a left corner)", buddy.getPos().x>63);
        }

        kState.setKey(Movement.UP);
        for(int i=0; i<20; i++)
        {
            engine.updateAll();
            System.out.println(buddy.getPos());
            assertTrue("Collision was not detected successfully (problem at an up corner)", buddy.getPos().y>191);
        }



    }

    @Test
    public void plantBomb() throws Exception {}

    @Test
    public void updateAll() throws Exception
    {
        Player buddy = engine.getPlayerNamed("Buddy");
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