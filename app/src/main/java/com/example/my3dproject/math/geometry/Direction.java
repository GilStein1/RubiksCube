package com.example.my3dproject.math.geometry;

/**
 * An enum representing the 6 directions in 3D space.
 */
public enum Direction {

	RIGHT(Axis.X, true),

	LEFT(Axis.X, false),

	UP(Axis.Y, true),

	DOWN(Axis.Y, false),

	FORWARD(Axis.Z, true),

	BACKWARD(Axis.Z, false);

	// The axis this direction operates on
	private Axis axis;
	// Whether this direction is positive (true) or negative (false) along the axis
	private boolean isPositiveInAxis;

	/**
	 * Creates a new Direction with the specified axis and sign.
	 *
	 * @param axis The axis this direction operates on
	 * @param isPositiveInAxis True for positive direction, false for negative
	 */
	Direction(Axis axis, boolean isPositiveInAxis) {
		this.axis = axis;
		this.isPositiveInAxis = isPositiveInAxis;
	}

	/**
	 * Gets the axis associated with this direction.
	 *
	 * @return The Axis enum value for this direction
	 */
	public Axis getAxis() {
		return axis;
	}

	/**
	 * Gets the sign (positive or negative) of this direction along its axis.
	 *
	 * @return 1 for positive directions, -1 for negative directions
	 */
	public int getSignInAxis() {
		return isPositiveInAxis ? 1 : -1;
	}

}
