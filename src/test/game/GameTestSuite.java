package test.game;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	BombTest.class,
	GameStateTest.class,
	KeyboardStateTest.class,
	KeyboardUpdaterTest.class,
	PlayerTest.class,
	MapTest.class,
	SettingsParserTest.class
})


public class GameTestSuite {

}
