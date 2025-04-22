package com.example.my3dproject.math.geometry;

import com.example.my3dproject.drawables.Point;

public class Point3d {

	private double x, y, z;

	public Point3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point3d() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
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

	public double getDistanceFrom(Point3d other) {
		return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2) + Math.pow(z - other.z, 2));
	}

	public static Point3d valueOf(String value) {
		value = value.substring(1, value.length() - 1);
		String[] parts = value.split(",");
		return new Point3d(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}

}
