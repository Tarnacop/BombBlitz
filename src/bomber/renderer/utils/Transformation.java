package bomber.renderer.utils;

import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * Class to handle graphics transformation
 * 
 * @author Alexandru Blinda
 *
 */
public class Transformation {

	private final Matrix4f model;
	private final Matrix4f projection;
	private final Matrix4f modelprojection;

	/**
	 * Create a new Transformation object
	 */
	public Transformation() {

		model = new Matrix4f();
		projection = new Matrix4f();
		modelprojection = new Matrix4f();

	}

	/**
	 * Get an Orthographic Projection Matrix with the given left, right, bottom
	 * and top
	 * 
	 * @param left
	 *            The given left coordinate
	 * @param right
	 *            The given right coordinate
	 * @param bottom
	 *            The given bottom coordinate
	 * @param top
	 *            The given top coordinate
	 * @return The Orthographic Projection Matrix
	 */
	public Matrix4f getOrthographicProjection(float left, float right, float bottom, float top) {

		return projection.identity().ortho2D(left, right, bottom, top);
	}

	/**
	 * Get a Model Matrix with the given offset, rotation and scale Used for 2D
	 * coordinate system
	 * 
	 * @param offset
	 *            The given offset in (x, y) coordinates as a Vector2f
	 * @param angle
	 *            The given angle rotation
	 * @param scale
	 *            The given scale
	 * @return The Model Matrix
	 */
	public Matrix4f getModelMatrix(Vector2f offset, float angle, float scale) {

		return model.identity().translate(offset.x, offset.y, 0).rotateX((float) Math.toRadians(0))
				.rotateY((float) Math.toRadians(0)).rotateZ((float) Math.toRadians(angle)).scale(scale);
	}

	/**
	 * Get a Model Matrix with the given offset, rotation and scale Used for 2D
	 * coordinate system
	 * 
	 * @param x
	 *            The given offset oN the x
	 * @param y
	 *            The given offset on the y
	 * @param angle
	 *            The given angle rotation
	 * @param scale
	 *            The given scale
	 * @return The Model Matrix
	 */
	public Matrix4f getModelMatrix(float x, float y, float angle, float scale) {

		return model.identity().translate(x, y, 0).rotateX((float) Math.toRadians(0)).rotateY((float) Math.toRadians(0))
				.rotateZ((float) Math.toRadians(angle)).scale(scale);

	}

	/**
	 * Get a Model Projection Matrix from the given Model Matrix and the given
	 * Orthographic Projection Matrix
	 * 
	 * @param modelMatrix
	 *            The given Model Matrix
	 * @param orthoMatrix
	 *            The given Orthographic Projection Matrix
	 * @return The Model Projection Matrix
	 */
	public Matrix4f getOrtoProjectionModelMatrix(Matrix4f modelMatrix, Matrix4f orthoMatrix) {

		return modelprojection.identity().mul(orthoMatrix).mul(modelMatrix);

	}

}