package bomber.audio;

import bomber.game.AudioEvent;
import sun.applet.Main;

import javax.sound.sampled.*;
import java.io.IOException;

/**
 * Created by Alexandruro on 05.02.2017.
 */
public class SoundEffectPlayer extends Thread
{

    public static final String explosionFilename = "sfx_exp_medium3.wav";
    public static final String bombPlaceFilename = "sfx_sound_neutral6.wav";
    public static final String movementFilename = "sfx_movement_footstepsloop4_slow.wav";
    public static final String playerDeathFilename = "sfx_deathscream_robot1.wav";

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

    public void play(AudioEvent event)
    {
        switch (event)
        {
            case PLACE_BOMB:
                playSound(bombPlaceFilename);
                break;
            case EXPLOSION:
                playSound(explosionFilename);
                break;
            case PLAYER_DEATH:
                playSound(playerDeathFilename);
                break;
        }
    }
}
