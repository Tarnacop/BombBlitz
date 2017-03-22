package bomber.audio;

import bomber.game.Constants;
import sun.applet.Main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Plays music
 * Only to be used by <code>AudioManager</code>
 *
 * @author Alexandru Rosu
 */
class MusicPlayer extends Thread
{

    private Clip clip;

    /**
     * Constructs a music player
     *
     * @param volume The volume percent, ranging from 0 to 100
     */
    MusicPlayer(float volume)
    {
        try
        {
            clip = AudioSystem.getClip();
            InputStream rawStream = Main.class.getResourceAsStream(Constants.AUDIO_FILES_PATH + Constants.MUSIC_FILENAME);
            if(rawStream == null)
                throw new IOException();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(rawStream);
            clip.open(inputStream);
            inputStream.close();
            setVolume(volume);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Could not load sound: " + Constants.AUDIO_FILES_PATH + Constants.MUSIC_FILENAME);
            e.printStackTrace();
        }
    }

    /**
     * Sets a new value for volume
     *
     * @param percent The volume percent, ranging from 0 to 100
     */
    void setVolume(float percent)
    {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        AudioManager.setControlVolume(gainControl, percent);
    }

    /**
     * Starts the player and the music
     */
    public void run()
    {
        super.run();
        unpause();
    }

    /**
     * Pauses the music
     */
    void pause()
    {
        clip.stop();
    }

    /**
     * Unpauses the music
     */
    void unpause()
    {
        clip.start();
        clip.loop(Integer.MAX_VALUE);
    }

    void replay()
    {
        clip.setFramePosition(0);
        unpause();
    }

    void close()
    {
        clip.stop();
        clip.close();
    }

    boolean hasOpened()
    {
        return clip.isOpen();
    }

}
