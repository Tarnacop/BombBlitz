package bomber.audio;

import bomber.game.AudioEvent;
import bomber.game.Constants;
import sun.applet.Main;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Plays sound effects
 * Only to be used by <code>AudioManager</code>
 *
 * @author Alexandru Rosu
 */
class SoundEffectPlayer extends Thread
{

    private float volume;

    /**
     * Constructs a sound effect player
     *
     * @param volume The volume percent, ranging from 0 to 100
     */
    SoundEffectPlayer(float volume)
    {
        this.volume = volume;
    }

    /**
     * Plays a single sound
     *
     * @param fileName The name of the file inside the audio resources folder (<code>Constants.audioFilesPath</code>)
     */
    void playSound(String fileName)
    {
        try
        {
            Clip clip = AudioSystem.getClip();
            InputStream rawStream = Main.class.getResourceAsStream(Constants.audioFilesPath + fileName);
            if (rawStream == null)
                throw new IOException();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(rawStream);
            clip.open(inputStream);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            AudioManager.setControlVolume(gainControl, volume);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
        {
            System.err.println("Could not load sound: " + Constants.audioFilesPath + fileName);
            e.printStackTrace();
        }
    }

    /**
     * Plays the sound associated with an audio event
     *
     * @param event The audio event
     */
    void play(AudioEvent event)
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
                //playSound(Constants.movementFilename);
                break;
            case POWERUP:
                playSound(Constants.powerupFilename);
                break;
        }
    }


//    /**
//     * Sets a new value for volume
//     *
//     * @param percent The volume percent, ranging from 0 to 100
//     */
//    void setVolume(float percent)
//    {
//        this.volume = percent;
//    }

}
