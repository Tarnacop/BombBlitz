package bomber.audio;

import bomber.game.Constants;
import sun.applet.Main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Alexandru Rosu on 05.02.2017.
 */
public class MusicPlayer extends Thread
{

    private Clip clip;

    public MusicPlayer(float volume)
    {
        try {
            clip = AudioSystem.getClip();
            InputStream rawStream = Main.class.getResourceAsStream(Constants.AUDIO_FILES_PATH + Constants.MUSIC_FILENAME);
            if(rawStream == null)
                throw new IOException();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(rawStream);
            clip.open(inputStream);
            setVolume(volume);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Could not load sound: " + Constants.AUDIO_FILES_PATH + Constants.MUSIC_FILENAME);
            e.printStackTrace();
        }
    }


    public void setVolume(float percent)
    {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        AudioManager.setControlVolume(gainControl, percent);
    }

    public void run()
    {
        super.run();
        unpause();
    }

    public void pause()
    {
        clip.stop();
    }

    public void unpause()
    {
        clip.start();
        clip.loop(Integer.MAX_VALUE);
    }

}
