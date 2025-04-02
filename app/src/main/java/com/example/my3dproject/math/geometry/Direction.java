package com.example.my3dproject.math.geometry;

public enum Direction {

	RIGHT(Axis.X),
	LEFT(Axis.X),
	UP(Axis.Y),
	DOWN(Axis.Y),
	FORWARD(Axis.Z),
	BACKWARD(Axis.Z);

	private Axis axis;

	Direction(Axis axis) {
		this.axis = axis;
	}

	public Axis getAxis() {
		return axis;
	}

}
