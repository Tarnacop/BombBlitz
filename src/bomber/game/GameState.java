package bomber.game;

import java.util.List;

public class GameState {

	private Map map;				
	private List<Player> players;		
	private List<Bomb> bombs;		

	public GameState(Map map, List<Player> players, List<Bomb> bombs){	
		
		this.map = map;
		this.players = players;
		this.bombs = bombs;
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
	

}
