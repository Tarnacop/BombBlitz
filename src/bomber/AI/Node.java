package bomber.AI;

import java.awt.Point;
import java.util.Comparator;

/**
 * The Class Node for implementing A* algorithm.
 */
public class Node implements Comparator<Node>, Comparable<Node>{
	
	/** The g value. */
	private int gValue;
	
	/** The f value. */
	private int fValue;
	
	/** The parent. */
	private Node parent;
	
	/** The coordinate. */
	private Point coord;

	/**
	 * Instantiates a new node.
	 *
	 * @param gValue the g value
	 * @param hValue the h value
	 * @param parent the parent
	 * @param coordinates the coordinates
	 */
	public Node(int gValue, int hValue, Node parent, Point coordinates) {

		this.coord = coordinates;
		this.gValue = gValue;
		this.parent = parent;
		this.fValue = gValue + hValue;
	}
	
	/**
	 * Instantiates a new node.
	 *
	 * @param parent the parent
	 * @param coordinates the coordinates
	 */
	public Node(Node parent, Point coordinates)
	{
		this.parent = parent;
		this.coord = coordinates;
	}

	/**
	 * Gets the coordinate.
	 *
	 * @return the coordinate
	 */
	public Point getCoord() {
		return coord;
	}
	
	/**
	 * Gets the g value.
	 *
	 * @return the g value
	 */
	public int getgValue() {
		return gValue;
	}

	
	/**
	 * Gets the f value.
	 *
	 * @return the f value
	 */
	public int getfValue() {
		return fValue;
	}
	
	
	/**
	 * Gets the parent.
	 *
	 * @return the parent node
	 */
	public Node getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Node o1, Node o2) {
		if(o1.getfValue() > o2.getfValue()) return 1;
		if(o1.getfValue() == o2.getfValue()) return 0;
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Node o) {
		return compare(this, o);
	}
	

}
