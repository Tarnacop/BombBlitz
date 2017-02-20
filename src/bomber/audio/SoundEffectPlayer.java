package bomber.audio;

import bomber.game.AudioEvent;
import bomber.game.Constants;
import sun.applet.Main;

import javax.sound.sampled.*;
import java.io.IOException;

/**
 * Created by Alexandruro on 05.02.2017.
 */
public class SoundEffectPlayer extends Thread
{

    private void playSound(String fileName)
    {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    Main.class.getResourceAsStream(Constants.audioFilesPath + fileName));
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

    public void play(AudioEvent event)
    {
        switch (event)
        {
            case PLACE_BOMB:
                playSound(Constants.bombPlaceFilename);
                break;
            case EXPLOSION:
                playSound(Constants.explosionFilename);
                break;
            case PLAYER_DEATH:
                playSound(Constants.playerDeathFilename);
                break;
            case MOVEMENT:
                playSound(Constants.movementFilename);
                break;
            case POWERUP:
                playSound(Constants.powerupFilename);
                break;
        }
    }
}
