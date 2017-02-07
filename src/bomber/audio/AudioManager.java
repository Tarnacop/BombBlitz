package bomber.audio;

import java.util.concurrent.TimeUnit;

/**
 * Created by Alexandruro on 05.02.2017.
 */
public class AudioManager
{

    public static final String audioFilesPath = "/bomber/audio/";

    private MusicPlayer music;
    private SoundEffectPlayer effects;

    public AudioManager()
    {
        music = new MusicPlayer();
        effects = new SoundEffectPlayer();
        effects.start();
    }


    public void playMusic()
    {
        music.start();
    }

    public void pauseMusic()
    {
        music.pause();
    }

    public void unpauseMusic()
    {
        music.unpause();
    }

    public void stopMusic()
    {
        music.interrupt();
    }

    public void playExplosion()
    {
        effects.playExplosion();
    }

    /*
    public boolean isMusicPlaying()
    {
        return music.isAlive() && music.isPlaying();
    }
    */

    public static void main(String[] args) {
        AudioManager audioManager = new AudioManager();
        audioManager.playMusic();

        try
        {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        audioManager.playExplosion();

        try
        {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        audioManager.pauseMusic();

        try
        {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        audioManager.unpauseMusic();

        try
        {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }

}