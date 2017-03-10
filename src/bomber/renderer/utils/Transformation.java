package bomber.renderer.utils;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Transformation {

	private final Matrix4f model;
	private final Matrix4f projection;
	private final Matrix4f modelprojection;

	public Transformation() {

		model = new Matrix4f();
		projection = new Matrix4f();
		modelprojection = new Matrix4f();

	} // END OF CONSTRUCTOR

	public Matrix4f getOrthographicProjection(float left, float right, float bottom, float top) {

		return projection.identity().ortho2D(left, right, bottom, top);
	} // END OF getOrthographic METHOD

	public Matrix4f getModelMatrix(Vector3f offset, Vector3f rotation, float scale) {

		return model.identity().translate(offset)
				.rotateX((float) Math.toRadians(rotation.x))
				.rotateY((float) Math.toRadians(rotation.y))
				.rotateZ((float) Math.toRadians(rotation.z))
				.scale(scale);
	} // END OF getModelMatrix METHOD
	
	public Matrix4f getModelMatrix(Vector2f offset, float angle, float scale) {
		
		return model.identity().translate(offset.x, offset.y, 0)
				.rotateX((float) Math.toRadians(0))
				.rotateY((float) Math.toRadians(0))
				.rotateZ((float) Math.toRadians(angle))
				.scale(scale);
	} // END OF getModelMatrix METHOD
	
	public Matrix4f getModelMatrix(float x, float y, float angle, float scale) {
		
		return model.identity().translate(x, y, 0)
				.rotateX((float) Math.toRadians(0))
				.rotateY((float) Math.toRadians(0))
				.rotateZ((float) Math.toRadians(angle))
				.scale(scale);
		
	} // END OF getModelMatrix METHOD
	
	public Matrix4f getOrtoProjectionModelMatrix(Matrix4f modelMatrix, Matrix4f orthoMatrix) {
		
		return modelprojection.identity().mul(orthoMatrix).mul(modelMatrix);
		
	} // END OF getortoProjectionModelMatrix METHOD

} // END OF Transformation CLASS
