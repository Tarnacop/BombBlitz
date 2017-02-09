package bomber.game;

import java.util.ArrayList;
import java.util.List;

public class GameState {

	private Map map;				
	private List<Player> players;		
	private List<Bomb> bombs;
	private List<AudioEvent> audioEvents;

	public GameState(Map map, List<Player> players){	
		
		this.map = map;
		this.players = players;
		this.bombs = new ArrayList<Bomb>();
		this.audioEvents = new ArrayList<AudioEvent>();
	}

	public boolean gameOver(){
		
		int living = 0;
		for(Player p : this.players){
			
			if(p.isAlive()){
				living++;
			}
		}
		
		if(living <= 1){
			
			return true;
		}
		
		return false;
	}
	
	public List<AudioEvent> getAudioEvents() {
		return audioEvents;
	}

	public void setAudioEvents(List<AudioEvent> audioEvents) {
		this.audioEvents = audioEvents;
	}

	public Map getMap(){
		
		return this.map;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public List<Bomb> getBombs() {
		return bombs;
	}

	public void setBombs(List<Bomb> bombs) {
		this.bombs = bombs;
	}

	public void setMap(Map map) {
		this.map = map;
	}
	
	@Override
	public String toString(){
		
		String s = "Gamestate of: \nPlayers:\n";
		
		for(Player player : this.players){
			
			s += "\nName: " + player.getName() + ", Pos: " + player.getPos() + ", Speed: " + player.getSpeed() + ", Lives: " + player.getLives() + ", Bomb Range: " + player.getBombRange();
			s += "\nWith Keyboard State = " + (player.getKeyState().isBomb()?"BOMB":"NO BOMB") + ", Current Movement: " + player.getKeyState().getMovement();
		}
		
		s += "\nBombs:\n";
		
		for(Bomb bomb : this.bombs){
			
			s += "\nOwner: " + bomb.getPlayerName() + "Pos: " + bomb.getPos() + ", Radius: " + bomb.getRadius() + ", Detonation Time: " + bomb.getTime(); 
		}
		
		s += "\nAnd Map:\n" + this.map.toStringWithPlayersBombs(this.players, this.bombs);
		
		return s;
	}
}
