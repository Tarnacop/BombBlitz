package bomber.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Map {

	private Block[][] gridMap;		//the grid representation of the map
	private Block[][] pixelMap;		//the pixel representation of the map
	private final int scalar = 64;
	private List<Point> spawnPoints;
	private String name;
	
	public Map(String name, Block[][] gridMap, List<Point> spawnPoints){
		
		this.name = name;
		this.gridMap = gridMap;
		this.spawnPoints = spawnPoints;
		this.pixelMap = this.convertToPixel(gridMap);
	}

	public List<Point> getSpawnPoints() {
		return spawnPoints;
	}

	public void setSpawnPoints(List<Point> spawnPoints) {
		this.spawnPoints = spawnPoints;
	}

	public void update(){
	
		this.pixelMap = convertToPixel(this.gridMap);
	}

	public Block[][] convertToPixel(Block[][] gridMap) {
		
		int gridWidth = gridMap[0].length;
	    	int gridHeight = gridMap.length;
		int pixelWidth = gridWidth*scalar;
	    	int pixelHeight = gridHeight*scalar;
	    	Block pixelMap[][] = new Block[pixelHeight][pixelWidth];
	    	for(int i=0;i<pixelHeight;i++){
	        	for(int j=0;j<pixelWidth;j++){
	            		pixelMap[i][j] = gridMap[i/scalar][j/scalar];
	        	}
	       	}
		return pixelMap;
	}
	
	
	public Block[][] getGridMap(){
		
		return this.gridMap;//get the small grid representation of the map
	}
	
	public Block[][] getPixelMap(){
		
		return this.pixelMap;//get the larger pixel representation of the map
	}
	
	public Block getGridBlockAt(int x, int y){
		
		return this.gridMap[x][y];
	}	//get the block value of a grid space at the specified coords
	
	public Block getPixelBlockAt(int x, int y){
		
		return this.pixelMap[x][y];
	}

	public void setGridBlockAt(Point p, Block b) {
		gridMap[p.x][p.y]=b;
		update();
	}

	public void setPixelBlockAt(Point p, Block b) {
		pixelMap[p.x][p.y]=b;
	}

	public boolean isInGridBounds(Point p) {
		if(p.getX()>=0 && p.getY()>=0 && p.getX()<gridMap.length && p.getY()<gridMap[0].length)
			return true;
		return false;
	}
	
public String toStringWithPlayersBombs(List<Player> players, List<Bomb> bombs){
		
		ArrayList<Point> playerPositions = new ArrayList<>();
	
		for(Player player : players){
			
			playerPositions.add(new Point(player.getPos().x/64, player.getPos().y/64));
		}
		
		ArrayList<Point> bombPositions = new ArrayList<>();
		
		for(Bomb bomb : bombs){
			
			bombPositions.add(new Point(bomb.getPos().x/64, bomb.getPos().y/64));
		}
	
		String s = "";
		int i = 0;
		int j = 0;
		
		for(int y = 0; y < this.gridMap[0].length; y++){
			
			for(int x = 0; x < this.gridMap.length; x++){
			
				if(playerPositions.contains(new Point(i, j))){
				
					s += "PP";
				}else if(bombPositions.contains(new Point(i, j))){
				
					s += "o*";
				}else{
					switch(this.gridMap[x][y]){
					case BLAST: s += "XX";
						break;
					case SOFT:	s += "OO";
						break;
					case SOLID:	s += "HH";
						break;
					case HOLE: s += "__";
						break;
					case MINUS_BOMB: s+= "-b";
						break;
					case MINUS_RANGE: s+= "-r";
						break;
					case MINUS_SPEED: s += "-s";
						break;
					case PLUS_BOMB: s += "+b";
						break;
					case PLUS_RANGE: s += "+r";
						break;
					case PLUS_SPEED: s += "+s";
						break;
					default: s += "  "; 
						break;
					}
				}
				i++;
			}
			j++;
			i=0;
			s += "\n";
		}
		
		return s;
	}

public String getName() {
	return this.name;
}
}
