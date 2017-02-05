package bomber.audio;

import sun.applet.Main;

import javax.sound.sampled.*;
import java.io.IOException;

/**
 * Created by Alexandruro on 05.02.2017.
 */
public class SoundEffectPlayer extends Thread
{

    public void playExplosion()
    {
        playSound("sfx_exp_medium3.wav");
    }

    private void playSound(String fileName)
    {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream(AudioManager.audioFilesPath + fileName));
            clip.open(inputStream);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

}
