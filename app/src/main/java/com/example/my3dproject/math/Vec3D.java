package com.example.my3dproject.math;

import com.example.my3dproject.math.geometry.Point3d;

public class Vec3D {

	private double x, y, z;

	public Vec3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3D normalize() {
		double size = Math.sqrt(x*x + y*y + z*z);
		x /= size;
		y /= size;
		z /= size;
		return this;
	}

	public double dotProduct(Vec3D other) {
		return this.getX() * other.getX() +
			this.getY() * other.getY() +
			this.getZ() * other.getZ();
	}

	public double cosineSimilarity(Vec3D other) {
		double dot = this.dotProduct(other);
		double normA = Math.sqrt(this.dotProduct(this));
		double normB = Math.sqrt(other.dotProduct(other));
		return (normA == 0 || normB == 0) ? 0 : dot / (normA * normB);
	}

	public static Vec3D fromDifferenceInPos(Point3d p1, Point3d p2) {
		return new Vec3D(p1.getX() - p2.getX(), p1.getY() - p2.getY(), p1.getZ() - p2.getZ());
	}

	public double getValueInMainAxis() {
		if(Math.abs(x) > Math.max(Math.abs(y), Math.abs(z))) {
			return x;
		}
		else {
			if(Math.abs(y) > Math.abs(z)) {
				return y;
			}
		}
		return z;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}

}
