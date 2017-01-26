package bomber.game;

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

Block getGridBlockAt(int x, int y){
	
	return this.gridMap[x][y];
}	//get the block value of a grid space at the specified coords

Block getPixelBlockAt(int x, int y){
	
	return this.pixelMap[x][y];
}
}
