package bomber.audio;

import sun.applet.Main;

import javax.sound.sampled.*;
import java.io.IOException;

/**
 * Created by Alexandruro on 05.02.2017.
 */
public class MusicPlayer extends Thread
{

    private Clip clip;

    public MusicPlayer()
    {
        try {
            clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream("/bomber/audio/01_A_Night_Of_Dizzy_Spells.wav"));
            clip.open(inputStream);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
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

    /*
    public boolean isPlaying()
    {
        return clip.isRunning();
        //return clip.isActive();
        //return clip.isOpen();
    }
    */



}
