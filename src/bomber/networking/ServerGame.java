package bomber.networking;

import bomber.game.GameState;
import bomber.game.KeyboardState;
import bomber.physics.PhysicsEngine;

/**
 * 
 * Server side representation of a game session in a room
 *
 */
public class ServerGame implements Runnable {
	private GameState gameState;

	// TODO currently we ignore map id and always use the test map
	private int mapID = 0;

	private Thread thread = new Thread(this);

	public ServerGame(int mapID) {
		this.mapID = mapID;
	}

	public void setMapID(int mapID) {
		this.mapID = mapID;
	}

	public int getMapID() {
		return mapID;
	}

	/**
	 * Returns true if the game is in progress
	 * 
	 * @return true if the game is in progress
	 */
	public boolean isInGame() {
		if (gameState == null) {
			return false;
		}
		return !gameState.gameOver();
	}

	public void setPlayerKeyState(int playerID, KeyboardState keyState) {

	}

	public void startGame() {

	}

	public void termiate() {
	}

	@Override
	public void run() {
		PhysicsEngine physics = new PhysicsEngine(gameState);

		// tell clients the game is started

		// TODO Auto-generated method stub
		while (true) {
			// check whether game is over
			// update gameState
			physics.update(33);
			// send new gameState to clients
			// sleep according to tick rate
		}

		// tell clients the game is over
	}

}
