package com.example.my3dproject.math.geometry;

public class Point2d {

	private double x, y;

	public Point2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point2d(Point2d other) {
		this.x = other.x;
		this.y = other.y;
	}

	public Point2d times(double scalar) {
		return new Point2d(x*scalar, y*scalar);
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
}
