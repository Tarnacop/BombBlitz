package bomber.audio;

import bomber.game.AudioEvent;
import bomber.game.Constants;
import sun.applet.Main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Alexandru Rosu on 05.02.2017.
 */
public class SoundEffectPlayer extends Thread
{

    private float volume;

    public SoundEffectPlayer()
    {
        volume = Constants.defaultVolume;
    }

    private void playSound(String fileName)
    {
        try {
            Clip clip = AudioSystem.getClip();
            InputStream rawStream = Main.class.getResourceAsStream(Constants.audioFilesPath + fileName);
            if(rawStream == null)
                throw new IOException();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(rawStream);
            clip.open(inputStream);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            AudioManager.setControlVolume(gainControl, volume);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
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

    public void setVolume(float percent)
    {
        this.volume = percent;
    }
}
