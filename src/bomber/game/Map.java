package bomber.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Map {

	private Block[][] gridMap;		//the grid representation of the map
	private Block[][] pixelMap;		//the pixel representation of the map
	private final int scalar = 64;
	
	public Map(Block[][] gridMap){
		
		this.gridMap = gridMap;
		
		this.pixelMap = this.convertToPixel(gridMap);
	}

	public void update(){
	
		this.pixelMap = convertToPixel(this.gridMap);
	}

	private Block[][] convertToPixel(Block[][] gridMap) {
		
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

	public void setPixelBlockAt(int x, int y, Block b) {
		pixelMap[x][y]=b;
	}

	public boolean isInGridBounds(Point p) {
		if(p.getX()>=0 && p.getY()>=0 && p.getX()<gridMap.length && p.getY()<gridMap[0].length)
			return true;
		return false;
	}
	
public String toStringWithPlayers(List<Player> players){
		
	//System.out.println("Drawing map");
		ArrayList<Point> positions = new ArrayList<>();
	
		for(Player player : players){
			
			positions.add(new Point(player.getPos().x/64, player.getPos().y/64));
			//System.out.println("Player at : " + new Point(player.getPos().x/64, player.getPos().y/64).toString());
		}
	
		String s = "";
		int i = 0;
		int j = 0;
		
		for(int y = 0; y < this.gridMap[0].length; y++){
			
			for(int x = 0; x < this.gridMap.length; x++){
			
				//System.out.println("Checking for player at: " + new Point(x, y).toString());
				if(positions.contains(new Point(i, j))){
				
					s += "PP";
				}else{
					switch(this.gridMap[x][y]){
					case BLANK: s += "  ";
						break;
					case BLAST: s += "XX";
						break;
					case SOFT:	s += "OO";
						break;
					case SOLID:	s += "HH";
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
}
