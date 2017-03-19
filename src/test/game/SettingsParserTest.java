package test.game;

import bomber.game.Constants;
import bomber.game.SettingsParser;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Tests the settings parser
 *
 * @author Alexandru Rosu
 */
public class SettingsParserTest
{

    @Test
    public void init() throws Exception
    {
        System.out.println("A warning about settings.xml will be displayed. This is normal and should be ignored");
        File file = new File(Constants.SETTING_XML_PATH);
        file.delete();

        SettingsParser.init();
        assertEquals("The settings did not initialise.", Constants.DEFAULT_PLAYER_NAME, SettingsParser.getPlayerName());
        assertEquals("The settings did not initialise.", Constants.DEFAULT_VOLUME, SettingsParser.getEffectsVolume(), 0);
        assertEquals("The settings did not initialise.", Constants.DEFAULT_VOLUME, SettingsParser.getMusicVolume(), 0);
        assertEquals("The settings did not initialise.", Constants.DEFAULT_SERVER_IP, SettingsParser.getServerIp());
        assertEquals("The settings did not initialise.", String.valueOf(Constants.DEFAULT_SERVER_PORT), SettingsParser.getServerPort());
        assertEquals("The settings did not initialise.", true, SettingsParser.getShowTutorial());
    }

    @Test
    public void storeSettings() throws Exception
    {
        SettingsParser.init();

        SettingsParser.setPlayerName("Test name");
        SettingsParser.setEffectsVolume(80);
        SettingsParser.setMusicVolume(90);
        SettingsParser.setServer("test server", "test port");
        SettingsParser.setShowTutorial(false);

        SettingsParser.storeSettings();
        SettingsParser.init();

        assertEquals("The settings were not stored.", "Test name", SettingsParser.getPlayerName());
        assertEquals("The settings were not stored.", 80, SettingsParser.getEffectsVolume(), 0);
        assertEquals("The settings were not stored.", 90, SettingsParser.getMusicVolume(), 0);
        assertEquals("The settings were not stored.", "test server", SettingsParser.getServerIp());
        assertEquals("The settings were not stored.", "test port", SettingsParser.getServerPort());
        assertEquals("The settings were not stored.", false, SettingsParser.getShowTutorial());
    }

    @Test
    public void setPlayerName() throws Exception
    {
        SettingsParser.init();
        SettingsParser.setPlayerName("Test name");
        assertEquals("The settings were not stored.", "Test name", SettingsParser.getPlayerName());
    }

    @Test
    public void setMusicVolume() throws Exception
    {
        SettingsParser.init();
        SettingsParser.setMusicVolume(90);
        assertEquals("The settings were not stored.", 90, SettingsParser.getMusicVolume(), 0);
    }

    @Test
    public void setEffectsVolume() throws Exception
    {
        SettingsParser.init();
        SettingsParser.setEffectsVolume(80);
        assertEquals("The settings were not stored.", 80, SettingsParser.getEffectsVolume(), 0);
    }

    @Test
    public void setShowTutorial() throws Exception
    {
        SettingsParser.init();
        SettingsParser.setShowTutorial(false);
        assertEquals("The settings were not stored.", false, SettingsParser.getShowTutorial());
    }

    @Test
    public void setServer() throws Exception
    {
        SettingsParser.init();
        SettingsParser.setServer("test server", "test port");
        assertEquals("The settings were not stored.", "test server", SettingsParser.getServerIp());
        assertEquals("The settings were not stored.", "test port", SettingsParser.getServerPort());
    }

}