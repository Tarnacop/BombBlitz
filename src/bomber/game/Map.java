package bomber.game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Owen Jenkins
 * @version 1.4
 * @since 2017-03-23
 * 
 *        Map class for "Bomb Blitz" Game Application (2017 Year 2 Team Project,
 *        Team B1). Represents a map in the game.
 */
public class Map {

	private Block[][] gridMap; // the grid representation of the map
	private Block[][] pixelMap; // the pixel representation of the map
	private final int scalar = 64;
	private List<Point> spawnPoints;
	private String name;

	/**
	 * Create a new Map object.
	 * 
	 * @param name
	 *            the name of the map
	 * @param gridMap
	 *            the grid of the blocks that make up the map
	 * @param spawnPoints
	 *            the spawn points of the map
	 */
	public Map(String name, Block[][] gridMap, List<Point> spawnPoints) {

		this.name = name;
		this.gridMap = gridMap;
		this.spawnPoints = spawnPoints;
		this.pixelMap = this.convertToPixel(gridMap);
	}

	/**
	 * Get the spawn points of the map
	 * 
	 * @return the list of spawn points
	 */
	public List<Point> getSpawnPoints() {
		return spawnPoints;
	}

	/**
	 * Set the spawn points of the map
	 * 
	 * @param spawnPoints
	 *            the new list of spawn points
	 */
	public void setSpawnPoints(List<Point> spawnPoints) {
		this.spawnPoints = spawnPoints;
	}

	/**
	 * Update the pixel representation of the map to match the grid one.
	 */
	public void update() {

		this.pixelMap = convertToPixel(this.gridMap);
	}

	/**
	 * Convert a grid representation of the map into a pixel one.
	 * 
	 * @param gridMap
	 *            the grid representation
	 * @return the pixel representation
	 */
	public Block[][] convertToPixel(Block[][] gridMap) {

		int gridWidth = gridMap[0].length;
		int gridHeight = gridMap.length;
		int pixelWidth = gridWidth * scalar;
		int pixelHeight = gridHeight * scalar;
		Block pixelMap[][] = new Block[pixelHeight][pixelWidth];
		for (int i = 0; i < pixelHeight; i++) {
			for (int j = 0; j < pixelWidth; j++) {
				pixelMap[i][j] = gridMap[i / scalar][j / scalar];
			}
		}
		return pixelMap;
	}

	/**
	 * Get the grid representation of the map.
	 * 
	 * @return the grid representation
	 */
	public Block[][] getGridMap() {

		return this.gridMap;// get the small grid representation of the map
	}

	/**
	 * Get the pixel representation of the map.
	 * 
	 * @return the pixel representation
	 */
	public Block[][] getPixelMap() {

		return this.pixelMap;// get the larger pixel representation of the map
	}

	/**
	 * Get the block at a certain point in the grid representation.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return the block type at the point
	 */
	public Block getGridBlockAt(int x, int y) {

		return this.gridMap[x][y];
	} // get the block value of a grid space at the specified coords

	/**
	 * Get the block at a certain point in the pixel representation.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return the block type at the point
	 */
	public Block getPixelBlockAt(int x, int y) {

		return this.pixelMap[x][y];
	}

	/**
	 * Set the block at a certain point in the grid representation.
	 * 
	 * @param p
	 *            the coordinates of the block
	 * @param b
	 *            the new block type
	 */
	public void setGridBlockAt(Point p, Block b) {
		gridMap[p.x][p.y] = b;
		update();
	}

	/**
	 * Set the block at a certain point in the pixel representation.
	 * 
	 * @param p
	 *            the coordinates of the block
	 * @param b
	 *            the new block type
	 */
	public void setPixelBlockAt(Point p, Block b) {
		pixelMap[p.x][p.y] = b;
	}

	/**
	 * Check if a point is within the bounds of the grid representation.
	 * 
	 * @param p
	 *            the point to check
	 * @return true if is within the bounds
	 */
	public boolean isInGridBounds(Point p) {
		if (p.getX() >= 0 && p.getY() >= 0 && p.getX() < gridMap.length
				&& p.getY() < gridMap[0].length)
			return true;
		return false;
	}

	/**
	 * toString method which displays the location of players and bombs too.
	 * 
	 * @param players
	 *            the players to display
	 * @param bombs
	 *            the bombs to display
	 * @return the string representation of the map with the players and bombs
	 *         given
	 */
	public String toStringWithPlayersBombs(List<Player> players,
			List<Bomb> bombs) {

		ArrayList<Point> playerPositions = new ArrayList<>();

		for (Player player : players) {

			playerPositions.add(new Point(player.getPos().x / 64, player
					.getPos().y / 64));
		}

		ArrayList<Point> bombPositions = new ArrayList<>();

		for (Bomb bomb : bombs) {

			bombPositions.add(new Point(bomb.getPos().x / 64,
					bomb.getPos().y / 64));
		}

		String s = "";
		int i = 0;
		int j = 0;

		for (int y = 0; y < this.gridMap[0].length; y++) {

			for (int x = 0; x < this.gridMap.length; x++) {

				if (playerPositions.contains(new Point(i, j))) {

					s += "PP";
				} else if (bombPositions.contains(new Point(i, j))) {

					s += "o*";
				} else {
					switch (this.gridMap[x][y]) {
					case BLAST:
						s += "XX";
						break;
					case SOFT:
						s += "OO";
						break;
					case SOLID:
						s += "HH";
						break;
					case HOLE:
						s += "__";
						break;
					case MINUS_BOMB:
						s += "-b";
						break;
					case MINUS_RANGE:
						s += "-r";
						break;
					case MINUS_SPEED:
						s += "-s";
						break;
					case PLUS_BOMB:
						s += "+b";
						break;
					case PLUS_RANGE:
						s += "+r";
						break;
					case PLUS_SPEED:
						s += "+s";
						break;
					default:
						s += "  ";
						break;
					}
				}
				i++;
			}
			j++;
			i = 0;
			s += "\n";
		}

		return s;
	}

	/**
	 * Get the name of the map.
	 * 
	 * @return the name of the map
	 */
	public String getName() {
		return this.name;
	}
}
