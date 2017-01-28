package bomber.AI;

import java.awt.Point;
import java.util.Comparator;

public class Node implements Comparator<Node>, Comparable<Node>{
	private int gValue;
	private int hValue;
	private int fValue;
	private Node parent;
	private Point coord;
	
	public Node(int gValue, int hValue, Node parent, Point coordinates) {

		this.coord = coordinates;
		this.gValue = gValue;
		this.hValue = hValue;
		this.parent = parent;
		this.fValue = gValue + hValue;
	}
	
	public Node(Node parent, Point coordinates)
	{
		this.parent = parent;
		this.coord = coordinates;
	}
	
	
	public Point getCoord() {
		return coord;
	}
	public int getgValue() {
		return gValue;
	}
	public void setgValue(int gValue) {
		this.gValue = gValue;
	}
	public void setCoord(Point coord) {
		this.coord = coord;
	}
	
	public int getfValue() {
		return fValue;
	}
	
	public void setfValue(int fValue) {
		this.fValue = fValue;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	@Override
	public int compare(Node o1, Node o2) {
		if(o1.getfValue() > o2.getfValue()) return 1;
		if(o1.getfValue() == o2.getfValue()) return 0;
		return -1;
	}
	@Override
	public int compareTo(Node o) {
		return compare(this, o);
	}
	
	
	
	

}
