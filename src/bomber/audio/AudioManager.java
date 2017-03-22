package bomber.audio;

import bomber.game.AudioEvent;

import javax.sound.sampled.FloatControl;
import java.util.List;

/**
 * Manages all the sounds played by the game
 *
 * @author Alexandru Rosu
 */
public class AudioManager
{

    private static float musicVolume;
    private static float effectsVolume;

    private static MusicPlayer music;
    private static SoundEffectPlayer effects;

    /**
     * This constructor is private because the class should not be instantiated
     */
    private AudioManager()
    {
    }

    /**
     * Initialises the manager
     */
    public static void init()
    {
        music = new MusicPlayer(musicVolume);
        effects = new SoundEffectPlayer(effectsVolume);
        effects.start();
    }

    /**
     * Checks if a percent is valid. Issues a warning to System.err if not
     *
     * @param percent The percent value
     * @return Whether the percent it is valid
     */
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
        if (isValidPercent(percent))
        {
            musicVolume = percent;
            music.setVolume(percent);
        }
    }

    /**
     * Sets a new value for the volume of the sound effects (all sounds but the music)
     * Must be called before initialising the manager
     *
     * @param percent The volume percent, ranging from 0 to 100
     */
    public static void setEffectsVolume(float percent)
    {
        if (isValidPercent(percent))
        {
            effectsVolume = percent;
            effects.setVolume(percent);
        }
    }

    /**
     * Sets a new value for the overall volume (both for the sound effects and the music)
     * Must be called before initialising the manager
     *
     * @param percent The volume percent, ranging from 0 to 100
     */
    public static void setVolume(float percent)
    {
        if (isValidPercent(percent))
        {
            musicVolume = percent;
            music.setVolume(percent);
            effectsVolume = percent;
            effects.setVolume(percent);
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
    public static void playMusic()
    {
        if (music.getState() == Thread.State.NEW)
            music.start();
        else
            music.replay();
    }

    /**
     * Pauses the music
     */
    public static void pauseMusic()
    {
        music.pause();
    }

    /**
     * Unpauses the music
     */
    public static void unpauseMusic()
    {
        music.unpause();
    }

    /**
     * Stops all the sounds from playing. The sound effects cannot be restarted after this
     */
    public static void stopAudio()
    {
        music.close();
        music.interrupt();
        effects.close();
        effects.interrupt();
    }

    /**
     * Plays a series of sound effects
     *
     * @param eventList The list of events to be played
     */
    public static void playEventList(List<AudioEvent> eventList)
    {
        eventList.forEach(effects::play);
        eventList.clear();
    }

    /**
     * Plays the menu sound
     */
    public static void playMenuItemSelected()
    {
        effects.play(AudioEvent.MENU_SOUND);

    }

    /**
     * Plays the "you won" sound
     */
    public static void playGameOverWon()
    {
        effects.play(AudioEvent.GAME_OVER_WON);
    }

    /**
     * Plays the "you lost" sound
     */
    public static void playGameOverLost()
    {
        effects.play(AudioEvent.GAME_OVER_LOST);
    }

    /**
     * Changes the gain of a Clip according to the volume percent
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

        //float min = gainControl.getMinimum();
        //float max = gainControl.getMaximum();

        //gainControl.setValue(min + (max-min)*volume/100); // db; decreases faster
        gainControl.setValue((float) (20 * Math.log10(linearVolume))); // linear formula; not so precise with little volume
    }

    /**
     * Checks if the music file has been opened
     *
     * @return Whether it has been opened or not
     */
    public static boolean hasOpenedMusic()
    {
        return music != null && music.hasOpened();
    }

    /**
     * Checks if the music in playing
     *
     * @return Whether the music is playing
     */
    public static boolean isPlayingMusic()
    {
        return music.isAlive();
    }

}