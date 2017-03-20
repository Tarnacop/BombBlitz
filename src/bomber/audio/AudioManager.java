package bomber.audio;

import bomber.game.AudioEvent;
import bomber.game.Constants;

import javax.sound.sampled.FloatControl;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Manages all the sounds played by the game
 *
 * @author Alexandru Rosu
 */
public class AudioManager
{

    private static float musicVolume;
    private static float effectsVolume;

    private final MusicPlayer music;
    private final SoundEffectPlayer effects;

    /**
     * Constructs an audio manager
     */
    public AudioManager()
    {
        music = new MusicPlayer(musicVolume);
        effects = new SoundEffectPlayer(effectsVolume);
        effects.start();
    }

    private static boolean isValidPercent(float percent)
    {
        if (percent > 100 || percent < 0)
        {
            System.err.println("Incorrect call: setVolume(" + percent + ")");
            return false;
        }
        return true;
    }

    /**
     * Sets a new value for the volume of the music
     * Must be called before initialising the manager
     *
     * @param percent The volume percent, ranging from 0 to 100
     */
    public static void setMusicVolume(float percent)
    {
        if(isValidPercent(percent))
            musicVolume = percent;
    }

    /**
     * Sets a new value for the volume of the sound effects (all sounds but the music)
     * Must be called before initialising the manager
     *
     * @param percent The volume percent, ranging from 0 to 100
     */
    public static void setEffectsVolume(float percent)
    {
        if(isValidPercent(percent))
            effectsVolume = percent;
    }

    /**
     * Sets a new value for the overall volume (both for the sound effects and the music)
     * Must be called before initialising the manager
     *
     * @param percent The volume percent, ranging from 0 to 100
     */
    public static void setVolume(float percent)
    {
        if(isValidPercent(percent))
        {
            musicVolume = percent;
            effectsVolume = percent;
        }
    }

    /**
     * Gets the volume of the music
     *
     * @return The volume percent, ranging from 0 to 100
     */
    public static float getMusicVolume()
    {
        return musicVolume;
    }

    /**
     * Gets the volume of the sound effects
     *
     * @return The volume percent, ranging from 0 to 100
     */
    public static float getEffectsVolume()
    {
        return effectsVolume;
    }

    /**
     * Starts playing music, if not playing already
     */
    public void playMusic()
    {
    	System.out.println("AudioManager Music " + musicVolume + " Sound " + effectsVolume);
        if (!music.isAlive())
            music.start();
    }

    /**
     * Pauses the music
     */
    public void pauseMusic()
    {
        music.pause();
    }

    /**
     * Unpauses the music
     */
    public void unpauseMusic()
    {
        music.unpause();
    }

    /**
     * Stops all the sounds from playing. The sound effects cannot be restarted after this
     */
    public void stopAudio()
    {
        music.pause();
        music.interrupt();
        effects.interrupt();
    }

    /**
     * Plays a series of sound effects
     *
     * @param eventList The list of events to be played
     */
    public void playEventList(List<AudioEvent> eventList)
    {
        eventList.forEach(effects::play);
        eventList.clear();
    }

    /**
     * Plays the menu sound
     */
    public static void playMenuItemSelected()
    {
        SoundEffectPlayer effects = new SoundEffectPlayer(effectsVolume);
        effects.playSound(Constants.MENU_SOUND_FILENAME);
        effects.interrupt();
    }

    /**
     * Plays the "you won" sound
     */
    public static void playGameOverWon()
    {
        SoundEffectPlayer effects = new SoundEffectPlayer(effectsVolume);
        effects.playSound(Constants.GAME_OVER_WON_FILENAME);
        effects.interrupt();
    }

    /**
     * Plays the "you lost" sound
     */
    public static void playGameOverLost()
    {
        SoundEffectPlayer effects = new SoundEffectPlayer(effectsVolume);
        effects.playSound(Constants.GAME_OVER_LOST_FILENAME);
        effects.interrupt();
    }

    /**
     * Changes the gain of a <code>Clip</code> according to the volume percent
     *
     * @param gainControl The MASTER_GAIN control of the <code>Clip</code>
     * @param volume      The volume percent, ranging from 0 to 100
     */
    static void setControlVolume(FloatControl gainControl, float volume)
    {

        float linearMin = (float) Math.pow(10, gainControl.getMinimum() / 20);
        float linearMax = (float) Math.pow(10, gainControl.getMaximum() / 20);
        float linearVolume = linearMin + (linearMax - linearMin) * volume / 100;

        //System.out.println(linearMin + " -- " + linearVolume + " -- " + linearMax);
        //System.out.println(gainControl.getMinimum() + " -- " + (float)(20*Math.log10(linearVolume)) + " -- " + gainControl.getMaximum());

        float min = gainControl.getMinimum();
        float max = gainControl.getMaximum();

        //gainControl.setValue(min + (max-min)*volume/100); // db; decreases faster
        gainControl.setValue((float) (20 * Math.log10(linearVolume))); // linear formula; not so precise with little volume
    }

    public boolean hasOpenedMusic()
    {
        return music!=null && music.hasOpened();
    }

    public boolean isPlayingMusic()
    {
        return music.isAlive();
    }

    /*
    public static void main(String[] args) throws InterruptedException
    {
        AudioManager audioManager = new AudioManager();

        System.out.println(audioManager.hasOpenedMusic());

        audioManager.playMusic();



        //audioManager.setVolume(100);

        //AudioManager.playMenuItemSelected();


        AudioManager.playGameOverWon();

        TimeUnit.SECONDS.sleep(3);

        AudioManager.playGameOverLost();

        TimeUnit.SECONDS.sleep(3);



        Scanner sc = new Scanner(System.in);

        while(true)
            audioManager.setVolume(sc.nextInt());


        for (int i = 100000; i >= 0; i--)
        {
            try
            {
                audioManager.setVolume(2);
                System.out.println(i);
                TimeUnit.MILLISECONDS.sleep(10);

            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }


        try
        {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        audioManager.setVolume(50);
        System.out.println(50);
        try
        {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        audioManager.setVolume(25);
        System.out.println(25);

        try
        {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        List<AudioEvent> eventList = new ArrayList<>();
        eventList.add(AudioEvent.EXPLOSION);

        try
        {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }


        audioManager.setVolume(-80f);
        //audioManager.playEventList(eventList);

        try
        {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        //audioManager.pauseMusic();
        //audioManager.setVolume(0f);

        try
        {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        //audioManager.setVolume(0f);
        //audioManager.unpauseMusic();

        try
        {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }
    */
}