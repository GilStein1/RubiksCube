package com.example.my3dproject.math.geometry;

import com.example.my3dproject.math.Vec3D;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a 3D directional cross with six cardinal directions.
 * This class maintains reference points for all six primary directions
 * and allows rotation of the entire reference frame. It's useful for
 * determining which cardinal direction a vector most closely aligns with,
 * even in rotated coordinate systems.
 */
public class DirectionCross {

	// The six cardinal direction reference points
	private final Point3d right, left, up, down, front, back;
	// Collection of all direction points
	private final List<Point3d> allPoints;

	/**
	 * Creates a new DirectionCross with standard cardinal directions.
	 */
	public DirectionCross() {
		this.right = new Point3d(1, 0, 0);
		this.left = new Point3d(-1, 0, 0);
		this.up = new Point3d(0, 1, 0);
		this.down = new Point3d(0, -1, 0);
		this.front = new Point3d(0, 0, 1);
		this.back = new Point3d(0, 0, -1);
		this.allPoints = new ArrayList<>();
		allPoints.add(right);
		allPoints.add(left);
		allPoints.add(up);
		allPoints.add(down);
		allPoints.add(front);
		allPoints.add(back);
	}

	/**
	 * Rotates all directional reference points by the given quaternion rotation.
	 *
	 * @param rotation The quaternion representing the rotation to apply
	 */
	public void rotate(Quaternion rotation) {
		for(Point3d point : allPoints) {
			rotatePoint(point, rotation);
		}
	}

	/**
	 * Determines which cardinal direction most closely matches the given vector.
	 * Uses cosine similarity to compare the input vector with each of the six
	 * cardinal directions and returns the one with the highest similarity.
	 *
	 * @param vec3D The vector to compare against cardinal directions
	 * @return The Direction enum value that most closely matches the input vector
	 */
	public Direction getMostSimilarDirection(Vec3D vec3D) {
		double right = vec3D.cosineSimilarity(getDirectionVector(Direction.RIGHT));
		double left = vec3D.cosineSimilarity(getDirectionVector(Direction.LEFT));
		double up = vec3D.cosineSimilarity(getDirectionVector(Direction.UP));
		double down = vec3D.cosineSimilarity(getDirectionVector(Direction.DOWN));
		double forward = vec3D.cosineSimilarity(getDirectionVector(Direction.FORWARD));
		double backward = vec3D.cosineSimilarity(getDirectionVector(Direction.BACKWARD));

		double max = Math.max(Math.max(Math.max(right, left), Math.max(up, down)), Math.max(forward, backward));

		// Checks which of the directions mostly fits
		if (right == max) return Direction.RIGHT;
		if (left == max) return Direction.LEFT;
		if (up == max) return Direction.UP;
		if (down == max) return Direction.DOWN;
		if (forward == max) return Direction.FORWARD;
		return Direction.BACKWARD;
	}

	/**
	 * Rotates a single point using the given quaternion rotation.
	 *
	 * @param point The point to rotate
	 * @param rotation The quaternion representing the rotation
	 */
	private void rotatePoint(Point3d point, Quaternion rotation) {
		double[][] rotationMatrix = rotation.toRotationMatrix();
		double newX = rotationMatrix[0][0] * point.getX() + rotationMatrix[0][1] * point.getY() + rotationMatrix[0][2] * point.getZ();
		double newY = rotationMatrix[1][0] * point.getX() + rotationMatrix[1][1] * point.getY() + rotationMatrix[1][2] * point.getZ();
		double newZ = rotationMatrix[2][0] * point.getX() + rotationMatrix[2][1] * point.getY() + rotationMatrix[2][2] * point.getZ();
		point.moveTo(newX, newY, newZ);
	}

	/**
	 * Gets a normalized vector representing the specified direction.
	 *
	 * @param direction The direction to get the vector for
	 * @return A normalized Vec3D representing the specified direction
	 */
	public Vec3D getDirectionVector(Direction direction) {
		switch (direction) {
			case RIGHT: return new Vec3D(right.getX(), right.getY(), right.getZ()).normalize();
			case LEFT: return new Vec3D(left.getX(), left.getY(), left.getZ()).normalize();
			case UP: return new Vec3D(up.getX(), up.getY(), up.getZ()).normalize();
			case DOWN: return new Vec3D(down.getX(), down.getY(), down.getZ()).normalize();
			case FORWARD: return new Vec3D(front.getX(), front.getY(), front.getZ()).normalize();
			default: return new Vec3D(back.getX(), back.getY(), back.getZ()).normalize();
		}
	}

}