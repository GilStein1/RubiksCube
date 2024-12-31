package com.example.my3dproject.math.geometry;

public class Quaternion {
	double w, x, y, z;

	public Quaternion(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

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

	public Quaternion multiply(Quaternion q) {
		return new Quaternion(
			w * q.w - x * q.x - y * q.y - z * q.z,
			w * q.x + x * q.w + y * q.z - z * q.y,
			w * q.y - x * q.z + y * q.w + z * q.x,
			w * q.z + x * q.y - y * q.x + z * q.w
		);
	}

	public double[][] toRotationMatrix() {
		return new double[][]{
			{1 - 2 * (y * y + z * z), 2 * (x * y - z * w), 2 * (x * z + y * w)},
			{2 * (x * y + z * w), 1 - 2 * (x * x + z * z), 2 * (y * z - x * w)},
			{2 * (x * z - y * w), 2 * (y * z + x * w), 1 - 2 * (x * x + y * y)}
		};
	}
}
