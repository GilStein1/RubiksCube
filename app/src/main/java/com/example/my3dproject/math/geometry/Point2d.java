package com.example.my3dproject.math.geometry;

/**
 * Represents a point in 2D space with x and y coordinates.
 * Provides basic geometric operations and coordinate access methods.
 */
public class Point2d {

	// The x and y coordinates of the 2D point
	private double x, y;

	/**
	 * Creates a new 2D point with the specified coordinates.
	 *
	 * @param x The x-coordinate of the point
	 * @param y The y-coordinate of the point
	 */
	public Point2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Copy constructor - creates a new 2D point with the same coordinates as another point.
	 *
	 * @param other The point to copy coordinates from
	 */
	public Point2d(Point2d other) {
		this.x = other.x;
		this.y = other.y;
	}

	/**
	 * Creates a new point by scaling this point's coordinates by a scalar value.
	 *
	 * @param scalar The scaling factor to apply to both coordinates
	 * @return A new Point2d with scaled coordinates
	 */
	public Point2d times(double scalar) {
		return new Point2d(x*scalar, y*scalar);
	}

	/**
	 * Gets the x-coordinate of this point.
	 *
	 * @return The x-coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the x-coordinate of this point.
	 *
	 * @param x The new x-coordinate value
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Gets the y-coordinate of this point.
	 *
	 * @return The y-coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the y-coordinate of this point.
	 *
	 * @param y The new y-coordinate value
	 */
	public void setY(double y) {
		this.y = y;
	}
}