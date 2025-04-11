package com.example.my3dproject;

import com.example.my3dproject.drawables.Cube;
import com.example.my3dproject.math.geometry.Axis;
import com.example.my3dproject.math.geometry.Point3d;

public class RotationOperation {

	private final Point3d pointToRotateAround;
	private final Axis axisOfRotation;
	private final double angleOfRotation;

	public static final double CUBE_ROTATION_TOLERANCE = 0.1;

	public RotationOperation(Point3d pointToRotateAround, Axis axisOfRotation, double angleOfRotation) {
		this.pointToRotateAround = pointToRotateAround;
		this.axisOfRotation = axisOfRotation;
		this.angleOfRotation = angleOfRotation;
	}

	public RotationOperation(double x, double y, double z, Axis axisOfRotation, double angleOfRotation) {
		this(new Point3d(x, y, z), axisOfRotation, angleOfRotation);
	}

	public RotationOperation() {
		pointToRotateAround = new Point3d(0, 0, 0);
		axisOfRotation = Axis.X;
		angleOfRotation = 0;
	}

	public Point3d getPointToRotateAround() {
		return pointToRotateAround;
	}

	public Axis getAxisOfRotation() {
		return axisOfRotation;
	}

	public double getAngleOfRotation() {
		return angleOfRotation;
	}
}
