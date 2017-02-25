package test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.physics.PhysicsEngineTest;
import test.AI.AITestSuite;
import test.game.GameTestSuite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	AITestSuite.class,
	GameTestSuite.class,
	PhysicsEngineTest.class
	
})



public class TestSuite {

}
