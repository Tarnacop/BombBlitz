package bomber.renderer.utils;

import org.joml.Vector2f;

import bomber.renderer.shaders.Mesh;

public class GameEntity {
	
	private final Mesh mesh;
	private final Vector2f pos;
	private float angle;
	private float scale;
	
	public GameEntity(Mesh mesh) {
		
		this.mesh = mesh;
		pos = new Vector2f(0f, 0f);
		angle = 0f;
		scale = 1f;
	} // END OF CONSTRUCTOR
	
	public Vector2f getPosition() {
		
		return pos;
	} // END OF getPosition METHOD
	
	public void setPosition(float x, float y) {
		
		pos.x = x;
		pos.y = y;
	} // END OF setPosition METHOD
	
	public float getScale() {
		
		return scale;
	} // END OF getScale METHOD
	
	public void setScale(float scale) {
		
		this.scale = scale;
	} // END OF scale METHOD
	
	public float getRotation() {
		
		return angle;
	} // END OF getRotation METHOD
	
	public void setRotation(float angle) {
		
		this.angle = angle;
	} // END OF setRotation METHOD
	
	public Mesh getMesh() {
		
		return mesh;
	} // END OF getMesh METHOD
} // END OF GameEntity METHOD
