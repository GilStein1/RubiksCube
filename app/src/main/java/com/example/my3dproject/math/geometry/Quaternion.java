package com.example.my3dproject.math.geometry;

/**
 * Represents a quaternion
 */
public class Quaternion {
	// Quaternion components: w is the scalar part, x/y/z form the vector part
	private double w, x, y, z;

	/**
	 * Creates a new quaternion with the specified components.
	 *
	 * @param w The scalar (real) component of the quaternion
	 * @param x The x-component of the vector part
	 * @param y The y-component of the vector part
	 * @param z The z-component of the vector part
	 */
	public Quaternion(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a quaternion from an axis-angle representation.
	 *
	 * @param angle The rotation angle in radians
	 * @param x The x-component of the rotation axis
	 * @param y The y-component of the rotation axis
	 * @param z The z-component of the rotation axis
	 * @return A new quaternion representing the rotation
	 */
	public static Quaternion fromAxisAngle(double angle, double x, double y, double z) {
		double halfAngle = angle / 2;
		double sinHalfAngle = Math.sin(halfAngle);
		return new Quaternion(
			Math.cos(halfAngle),
			x * sinHalfAngle,
			y * sinHalfAngle,
			z * sinHalfAngle
		);
	}

	/**
	 * Multiplies this quaternion with another quaternion (quaternion composition).
	 *
	 * @param q The quaternion to multiply with
	 * @return A new quaternion representing the combined rotation
	 */
	public Quaternion multiply(Quaternion q) {
		return new Quaternion(
			w * q.w - x * q.x - y * q.y - z * q.z,
			w * q.x + x * q.w + y * q.z - z * q.y,
			w * q.y - x * q.z + y * q.w + z * q.x,
			w * q.z + x * q.y - y * q.x + z * q.w
		);
	}

	/**
	 * Converts this quaternion to a 3x3 rotation matrix.
	 * The resulting matrix can be used to rotate 3D points and vectors.
	 *
	 * @return A 3x3 rotation matrix as a double[][] array
	 */
	public double[][] toRotationMatrix() {
		return new double[][]{
			{1 - 2 * (y * y + z * z), 2 * (x * y - z * w), 2 * (x * z + y * w)},
			{2 * (x * y + z * w), 1 - 2 * (x * x + z * z), 2 * (y * z - x * w)},
			{2 * (x * z - y * w), 2 * (y * z + x * w), 1 - 2 * (x * x + y * y)}
		};
	}
}
