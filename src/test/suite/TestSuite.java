package test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.AI.AITestSuite;
import test.audio.AudioManagerTest;
import test.game.GameTestSuite;
import test.networking.NetworkingTestSuite;
import test.physics.PhysicsEngineTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
		NetworkingTestSuite.class,
		AudioManagerTest.class,
		AITestSuite.class,
		GameTestSuite.class,
		PhysicsEngineTest.class,
		//UITestSuite.class
})



public class TestSuite {

}
