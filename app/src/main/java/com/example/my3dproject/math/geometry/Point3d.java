package com.example.my3dproject.math.geometry;

/**
 * Represents a point in 3D space with x, y, and z coordinates.
 * Provides methods for coordinate manipulation, movement, and string conversion.
 */
public class Point3d {

	// The x, y, and z coordinates of the 3D point
	private double x, y, z;

	/**
	 * Creates a new 3D point with the specified coordinates.
	 *
	 * @param x The x-coordinate of the point
	 * @param y The y-coordinate of the point
	 * @param z The z-coordinate of the point
	 */
	public Point3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Default constructor - creates a new 3D point at the origin (0,0,0).
	 */
	public Point3d() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	/**
	 * Copy constructor - creates a new 3D point with the same coordinates as another point.
	 *
	 * @param other The point to copy coordinates from
	 */
	public Point3d(Point3d other) {
		this(other.x, other.y, other.z);
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

	/**
	 * Gets the z-coordinate of this point.
	 *
	 * @return The z-coordinate
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Sets the z-coordinate of this point.
	 *
	 * @param z The new z-coordinate value
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Moves this point to the specified coordinates.
	 *
	 * @param x The new x-coordinate
	 * @param y The new y-coordinate
	 * @param z The new z-coordinate
	 */
	public void moveTo(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Parses a string representation of a 3D point and creates a new Point3d instance.
	 *
	 * @param value The string representation of the point
	 * @return A new Point3d parsed from the string
	 */
	public static Point3d valueOf(String value) {
		value = value.substring(1, value.length() - 1);
		String[] parts = value.split(",");
		return new Point3d(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
	}

	/**
	 * Returns a string representation of this point in the format (x,y,z).
	 *
	 * @return String representation of the point
	 */
	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}

}