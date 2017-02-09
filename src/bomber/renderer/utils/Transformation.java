package bomber.renderer.utils;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Transformation {

	private final Matrix4f model;
	private final Matrix4f projection;

	public Transformation() {

		model = new Matrix4f();
		projection = new Matrix4f();

	} // END OF CONSTRUCTOR

	public Matrix4f getOrthographicProjection(float left, float right, float bottom, float top, float zNear,
			float zFar) {

		return projection.identity().ortho(left, right, bottom, top, zNear, zFar);
	} // END OF getOrthographic METHOD

	public Matrix4f getModelMatrix(Vector3f offset, Vector3f rotation, float scale) {

		return model.identity().translate(offset)
				.rotateX((float) Math.toRadians(rotation.x))
				.rotateY((float) Math.toRadians(rotation.y))
				.rotateZ((float) Math.toRadians(rotation.z))
				.scale(scale);
	} // END OF getModelMatrix METHOD
	
	public Matrix4f getModelMatrix(Vector2f offset, float angle, float scale) {
		
		Vector3f aux_offset = new Vector3f(offset, 0);
		
		return model.identity().translate(aux_offset)
				.rotateX((float) Math.toRadians(0))
				.rotateY((float) Math.toRadians(0))
				.rotateZ((float) Math.toRadians(angle))
				.scale(scale);
	} // END OF getModelMatrix METHOD
} // END OF Transformation CLASS
