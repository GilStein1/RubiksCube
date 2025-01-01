package com.example.my3dproject.math.geometry;

public class Point3d extends Point2d{

	private double z;

	public Point3d(double x, double y, double z) {
		super(x, y);
		this.z = z;
	}

	public Point3d(Point3d other) {
		this(other.x, other.y, other.z);
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
}
