package bomber.audio;

import bomber.game.Constants;
import sun.applet.Main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Plays music
 *
 * @author Alexandru Rosu
 */
public class MusicPlayer extends Thread
{

    private Clip clip;

    /**
     * Constructs a music player
     *
     * @param volume The volume percent, ranging from 0 to 100
     */
    public MusicPlayer(float volume)
    {
        try
        {
            clip = AudioSystem.getClip();
            InputStream rawStream = Main.class.getResourceAsStream(Constants.audioFilesPath + Constants.musicFilename);
            if (rawStream == null)
                throw new IOException();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(rawStream);
            clip.open(inputStream);
            setVolume(volume);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
        {
            System.err.println("Could not load sound: " + Constants.audioFilesPath + Constants.musicFilename);
            e.printStackTrace();
        }
    }

    /**
     * Sets a new value for volume
     *
     * @param percent The volume percent, ranging from 0 to 100
     */
    public void setVolume(float percent)
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
    public void pause()
    {
        clip.stop();
    }

    /**
     * Unpauses the music
     */
    public void unpause()
    {
        clip.start();
        clip.loop(Integer.MAX_VALUE);
    }

}
