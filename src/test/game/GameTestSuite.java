package test.game;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	BombTest.class,
	ControlSchemeTest.class,
	GameStateTest.class,
	KeyboardStateTest.class,
	KeyboardUpdaterTest.class,
	PlayerTest.class
	
})


public class GameTestSuite {

}
