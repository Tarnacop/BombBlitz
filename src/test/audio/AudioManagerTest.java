package test.audio;

import bomber.audio.AudioManager;
import bomber.game.AudioEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests <code>bomber.audio.AudioManager</code>
 *
 * @author Alexandru Rosu
 */
public class AudioManagerTest
{

    @Before
    public void setUp() throws Exception
    {
        AudioManager.init();
        AudioManager.setVolume(0); // making sure the tests don't produce annoying sound
    }

    @Test 
    public void Constructor() throws Exception
    {
        assertTrue("Music clip was not initialised", AudioManager.hasOpenedMusic());
    }

    @Test
    public void setMusicVolume() throws Exception
    {
        System.out.println("Two \"incorrect call\" messages were/will be displayed. This is normal and should be ignored.");

        AudioManager.setMusicVolume(40.5f);
        assertEquals("Music volume was not set",40.5f, AudioManager.getMusicVolume(), 0);

        AudioManager.setMusicVolume(92f);
        assertEquals("Music volume was not set",92f, AudioManager.getMusicVolume(), 0);

        AudioManager.setMusicVolume(-2f);
        assertEquals("Music volume setter changed the volume when given negative value",92f, AudioManager.getMusicVolume(), 0);

        AudioManager.setMusicVolume(100.1f);
        assertEquals("Music volume setter changed the volume when given value over 100",92f, AudioManager.getMusicVolume(), 0);

    }

    @Test
    public void setEffectsVolume() throws Exception
    {
        System.out.println("Two \"incorrect call\" messages were/will be displayed. This is normal and should be ignored.");

        AudioManager.setEffectsVolume(20.5f);
        assertEquals("Effects volume was not set",20.5f, AudioManager.getEffectsVolume(), 0);

        AudioManager.setEffectsVolume(98f);
        assertEquals("Effects volume was not set",98f, AudioManager.getEffectsVolume(), 0);

        AudioManager.setEffectsVolume(-5f);
        assertEquals("Effects volume setter changed the volume when given negative value",98f, AudioManager.getEffectsVolume(), 0);

        AudioManager.setEffectsVolume(120.1f);
        assertEquals("Effects volume setter changed the volume when given value over 100",98f, AudioManager.getEffectsVolume(), 0);


    }

    @Test
    public void setVolume() throws Exception
    {
        System.out.println("Two \"incorrect call\" messages were/will be displayed. This is normal and should be ignored.");

        AudioManager.setVolume(20.5f);
        assertEquals("Music volume was not set",20.5f, AudioManager.getEffectsVolume(), 0);
        assertEquals("Effects volume was not set",20.5f, AudioManager.getEffectsVolume(), 0);

        AudioManager.setVolume(98f);
        assertEquals("Music volume was not set",98f, AudioManager.getEffectsVolume(), 0);
        assertEquals("Effects volume was not set",98f, AudioManager.getEffectsVolume(), 0);

        AudioManager.setVolume(-5f);
        assertEquals("Music volume setter changed the volume when given negative value",98f, AudioManager.getEffectsVolume(), 0);
        assertEquals("Effects volume setter changed the volume when given negative value",98f, AudioManager.getEffectsVolume(), 0);

        AudioManager.setVolume(120.1f);
        assertEquals("Music volume setter changed the volume when given value over 100",98f, AudioManager.getEffectsVolume(), 0);
        assertEquals("Effects volume setter changed the volume when given value over 100",98f, AudioManager.getEffectsVolume(), 0);


    }

    @Test
    public void playMusic() throws Exception
    {
        assertFalse("Music started playing without calling the play method", AudioManager.isPlayingMusic());
        AudioManager.playMusic();
        assertTrue("Music did not start playing when play method was called", AudioManager.isPlayingMusic());
    }

    @Test
    public void pauseMusic() throws Exception
    {
        // Checking that music can be paused without errors
        AudioManager.playMusic();
        AudioManager.pauseMusic();
        AudioManager.pauseMusic();
    }

    @Test
    public void unpauseMusic() throws Exception
    {
        // Checking that music can be unpaused with no errors
        AudioManager.playMusic();
        AudioManager.pauseMusic();
        AudioManager.unpauseMusic();
        AudioManager.unpauseMusic();
    }

    @Test
    public void playEventList() throws Exception
    {
        List<AudioEvent> list = new ArrayList<>();
        list.add(AudioEvent.EXPLOSION);
        list.add(AudioEvent.POWERUP);
        list.add(AudioEvent.PLAYER_DEATH);

        list.add(AudioEvent.PLACE_BOMB);
        AudioManager.playEventList(list);

        assertTrue("EventList was not emptied after calling playEventList", list.isEmpty());

    }

}