package com.example.my3dproject.math.geometry;

import com.example.my3dproject.math.Vec3D;

public class Quaternion {
	double w, x, y, z;

	public Quaternion(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3D getRotationVector() {
		return new Vec3D(x, y ,z);
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

	public Quaternion conjugate() {
		return new Quaternion(w, -x, -y, -z);
	}

	public double norm() {
		return Math.sqrt(w * w + x * x + y * y + z * z);
	}

	public Quaternion inverse() {
		double normSquared = w * w + x * x + y * y + z * z;
		Quaternion conjugate = this.conjugate();
		return new Quaternion(conjugate.w / normSquared, conjugate.x / normSquared,
			conjugate.y / normSquared, conjugate.z / normSquared);
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
