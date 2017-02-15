package bomber.audio;

import bomber.game.AudioEvent;

import java.util.ArrayList;
import java.util.List;
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
        music.pause();
        music.interrupt();
    }

    public void playEventList(List<AudioEvent> eventList)
    {
        eventList.forEach(event -> {
            //SoundEffectPlayer effects = new SoundEffectPlayer();
            //effects.start();
            effects.play(event);
            //effects.interrupt();
        });
        eventList.clear();
    }

    public static void main(String[] args) {
        AudioManager audioManager = new AudioManager();
        //audioManager.playMusic();

        List<AudioEvent> eventList = new ArrayList<>();
        eventList.add(AudioEvent.EXPLOSION);

        try
        {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        audioManager.playEventList(eventList);

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