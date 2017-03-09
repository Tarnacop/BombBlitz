package test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.physics.PhysicsEngineTest;
import test.AI.AITestSuite;
import test.game.GameTestSuite;
import test.networking.NetworkingTestSuite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	NetworkingTestSuite.class,
	AITestSuite.class,
	GameTestSuite.class,
	PhysicsEngineTest.class
	
})



public class TestSuite {

}
