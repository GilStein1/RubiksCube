package com.example.my3dproject.math;

import com.example.my3dproject.drawables.Point;
import com.example.my3dproject.math.geometry.Point3d;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Represents a 3D vector with x, y, and z components.
 * Provides mathematical operations for handling vectors
 */
public class Vec3D {

	// The three components of the 3D vector
	private double x, y, z;

	/**
	 * Creates a new 3D vector with the specified components.
	 *
	 * @param x The x-component of the vector
	 * @param y The y-component of the vector
	 * @param z The z-component of the vector
	 */
	public Vec3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Normalizes this vector to unit length (magnitude of 1).
	 * Modifies the current vector instance and returns it.
	 *
	 * @return This vector after normalization
	 */
	public Vec3D normalize() {
		double size = Math.sqrt(x*x + y*y + z*z);
		x /= size;
		y /= size;
		z /= size;
		return this;
	}

	/**
	 * Calculates the dot product between this vector and another vector.
	 *
	 * @param other The other vector to calculate dot product with
	 * @return The dot product as a double value
	 */
	public double dotProduct(Vec3D other) {
		return this.getX() * other.getX() +
			this.getY() * other.getY() +
			this.getZ() * other.getZ();
	}

	/**
	 * Calculates the cosine similarity between this vector and another vector.
	 * Cosine similarity ranges from -1 (opposite direction) to 1 (same direction),
	 * with 0 indicating perpendicular vectors.
	 *
	 * @param other The other vector to compare with (null returns -1)
	 * @return Cosine similarity value between -1 and 1, or 0 if either vector has zero magnitude
	 */
	public double cosineSimilarity(Vec3D other) {
		if(other == null) {
			return -1;
		}
		double dot = this.dotProduct(other);
		double normA = Math.sqrt(this.dotProduct(this));
		double normB = Math.sqrt(other.dotProduct(other));
		return (normA == 0 || normB == 0) ? 0 : dot / (normA * normB);
	}

	/**
	 * Creates a vector representing the difference between two 3D points.
	 * The resulting vector points from p2 to p1.
	 *
	 * @param p1 The first point (destination)
	 * @param p2 The second point (origin)
	 * @return A new Vec3D representing the vector from p2 to p1
	 */
	public static Vec3D fromDifferenceInPos(Point3d p1, Point3d p2) {
		return new Vec3D(p1.getX() - p2.getX(), p1.getY() - p2.getY(), p1.getZ() - p2.getZ());
	}

	/**
	 * Creates a vector representing the difference between two drawable points.
	 * The resulting vector points from p2 to p1.
	 *
	 * @param p1 The first point (destination)
	 * @param p2 The second point (origin)
	 * @return A new Vec3D representing the vector from p2 to p1
	 */
	public static Vec3D fromDifferenceInPos(Point p1, Point p2) {
		return new Vec3D(p1.getX() - p2.getX(), p1.getY() - p2.getY(), p1.getZ() - p2.getZ());
	}

	/**
	 * Gets the x-component of this vector.
	 *
	 * @return The x-component
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the x-component of this vector.
	 *
	 * @param x The new x-component value
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Gets the y-component of this vector.
	 *
	 * @return The y-component
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the y-component of this vector.
	 *
	 * @param y The new y-component value
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Gets the z-component of this vector.
	 *
	 * @return The z-component
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Sets the z-component of this vector.
	 *
	 * @param z The new z-component value
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Returns a string representation of this vector in the format (x,y,z).
	 *
	 * @return String representation of the vector
	 */
	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}

	/**
	 * Sorts an array of vectors by their similarity to a reference vector.
	 * Vectors are sorted in ascending order of similarity (least similar first).
	 * The similarity is calculated using cosine similarity.
	 *
	 * @param vectorToCompareTo The reference vector to compare against
	 * @param vectors Variable number of vectors to sort
	 */
	public static void sortVectorsByMostSimilarity(Vec3D vectorToCompareTo, Vec3D... vectors) {
		Arrays.sort(vectors, Comparator.comparingDouble(vec -> (vectorToCompareTo.cosineSimilarity(vec) + 1)/2.0));
	}

}
