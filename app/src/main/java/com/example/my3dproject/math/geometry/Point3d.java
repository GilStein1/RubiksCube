package com.example.my3dproject.math.geometry;

public class Point3d {

	private double x, y, z;

	public Point3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point3d(Point3d other) {
		this(other.x, other.y, other.z);
	}

	public Point3d times(double scalar) {
		return new Point3d(x * scalar, y * scalar, z * scalar);
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

	public void moveTo(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

}
