package com.example.my3dproject.math.geometry;

public enum Direction {

	RIGHT(Axis.X, true),
	LEFT(Axis.X, false),
	UP(Axis.Y, true),
	DOWN(Axis.Y, false),
	FORWARD(Axis.Z, true),
	BACKWARD(Axis.Z, false);

	private Axis axis;
	private boolean isPositiveInAxis;

	Direction(Axis axis, boolean isPositiveInAxis) {
		this.axis = axis;
		this.isPositiveInAxis = isPositiveInAxis;
	}

	public Axis getAxis() {
		return axis;
	}

	public int getSignInAxis() {
		return isPositiveInAxis ? 1 : -1;
	}

}
