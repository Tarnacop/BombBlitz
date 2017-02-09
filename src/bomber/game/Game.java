package bomber.game;

import java.awt.Point;


public class Game{
	
	private String playerName;
	private Map map;

	public Game(Map map, String playerName){
		
		this.map = map;
		this.playerName = playerName;
		
		Player p1 = new Player("Player1", new Point(64,64), 5, 3);
		
		
		//AudioManager audio = new AudioManager();
	}
	
	public void update(float interval){
		
		//UI.update();
		//physics.update();
		//audio.playEventList(gameState.getAudioEvents());
	}
}
