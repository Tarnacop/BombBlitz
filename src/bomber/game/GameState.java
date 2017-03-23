package bomber.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import bomber.AI.GameAI;
import bomber.physics.BlastTimer;

/**
 *
 * @author Owen Jenkins
 * @version 1.4
 * @since 2017-03-23
 * 
 *        GameState class for "Bomb Blitz" Game Application (2017 Year 2 Team
 *        Project, Team B1). Represents the state of the current game.
 */
public class GameState {

	private Map map;
	private List<Player> players;
	private List<Bomb> bombs;
	private List<AudioEvent> audioEvents;
	private List<BlastTimer> blastList;
	private int gameCounter;
	private int holeCounter;

	/**
	 * Create a new GameState object.
	 * 
	 * @param map
	 *            the map to play on
	 * @param players
	 *            the list of players
	 */
	public GameState(Map map, List<Player> players) {

		this.map = map;
		this.players = players;
		this.bombs = new ArrayList<Bomb>();
		this.audioEvents = new ArrayList<AudioEvent>();
		this.blastList = new LinkedList<>();
	}

	/**
	 * Check if the game is over.
	 * 
	 * @return true if the game is over
	 */
	public boolean gameOver() {

		int livingHumans = 0;
		int livingAis = 0;
		boolean result = false;
		for (Player p : this.players) {

			if (p.isAlive()) {
				if (p instanceof GameAI) {
					livingAis++;
				} else {
					livingHumans++;
				}
			}
		}

		// The game is over if there's only 1 human left playing if there are no
		// AIs, or if theres still AIs living but no humans.
		if (((livingHumans == 1) && (livingAis == 0)) || (livingHumans == 0)) {

			result = true;
		}

		return result;
	}

	/**
	 * Get the list of audio events.
	 * 
	 * @return the list of audio events
	 */
	public List<AudioEvent> getAudioEvents() {
		return audioEvents;
	}

	/**
	 * Set the list of audio events.
	 * 
	 * @param audioEvents
	 *            the new list of audio events
	 */
	public void setAudioEvents(List<AudioEvent> audioEvents) {
		this.audioEvents = audioEvents;
	}

	/**
	 * Get the current map.
	 * 
	 * @return the map
	 */
	public Map getMap() {

		return this.map;
	}

	/**
	 * Set the map.
	 * 
	 * @param map
	 *            the new map
	 */
	public void setMap(Map map) {
		this.map = map;
	}

	/**
	 * Get the list of players.
	 * 
	 * @return the list of players
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * Set the list of players.
	 * 
	 * @param players
	 *            the new list of players
	 */
	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	/**
	 * Get the list of bombs.
	 * 
	 * @return the new list of bombs
	 */
	public List<Bomb> getBombs() {
		return bombs;
	}

	/**
	 * Set the list of bombs.
	 * 
	 * @param bombs
	 *            the new list of bombs
	 */
	public void setBombs(List<Bomb> bombs) {
		this.bombs = bombs;
	}

	/**
	 * Get the list of explosions.
	 * 
	 * @return the list of explosions
	 */
	public List<BlastTimer> getBlastList() {
		return blastList;
	}

	/**
	 * Get the game counter (time from beginning of game).
	 * 
	 * @return the game counter
	 */
	public int getGameCounter() {
		return gameCounter;
	}

	/**
	 * Set the game counter.
	 * 
	 * @param gameCounter
	 *            the new game counter value
	 */
	public void setGameCounter(int gameCounter) {
		this.gameCounter = gameCounter;
	}

	/**
	 * Get the hole counter (time since the last hole was placed).
	 * 
	 * @return the hold counter
	 */
	public int getHoleCounter() {
		return holeCounter;
	}

	/**
	 * Set the hole counter.
	 * 
	 * @param holeCounter
	 *            the new hole counter value
	 */
	public void setHoleCounter(int holeCounter) {
		this.holeCounter = holeCounter;
	}

	/**
	 * toString method.
	 */
	@Override
	public String toString() {

		String s = "Gamestate of: \nPlayers:\n";

		for (Player player : this.players) {

			s += "\nName: " + player.getName() + ", Pos: " + player.getPos()
					+ ", Speed: " + player.getSpeed() + ", Lives: "
					+ player.getLives() + ", Bomb Range: "
					+ player.getBombRange();
			s += "\nWith Keyboard State = "
					+ (player.getKeyState().isBomb() ? "BOMB" : "NO BOMB")
					+ ", Current Movement: "
					+ player.getKeyState().getMovement();
		}

		s += "\nBombs:\n";

		for (Bomb bomb : this.bombs) {

			s += "\nOwner: " + bomb.getPlayerName() + "Pos: " + bomb.getPos()
					+ ", Radius: " + bomb.getRadius() + ", Detonation Time: "
					+ bomb.getTime();
		}

		s += "\nAnd Map:\n"
				+ this.map.toStringWithPlayersBombs(this.players, this.bombs);

		return s;
	}
}
