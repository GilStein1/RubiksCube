package com.example.my3dproject;

import com.example.my3dproject.math.geometry.Axis;
import com.example.my3dproject.math.geometry.Point3d;

import java.util.ArrayList;
import java.util.List;

public class RotationOperation {

	private final Point3d pointToRotateAround;
	private final Axis axisOfRotation;
	private final double angleOfRotation;

	public static final double CUBE_POSITION_TOLERANCE_FOR_ROTATION = 0.1;

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

	public static RotationOperation valueOf(String value) {
		String[] parts = value.split("_");
		return new RotationOperation(Point3d.valueOf(parts[0]), Axis.valueOf(parts[1]), Double.parseDouble(parts[2]));
	}

	public static List<RotationOperation> valuesOf(String value) {
		if(value.isEmpty()) {
			return new ArrayList<>();
		}
		List<RotationOperation> list = new ArrayList<>();
		value = value.substring(0, value.length()-1);
		String[] parts = value.split("~");
		for(String part : parts) {
			list.add(RotationOperation.valueOf(part));
		}
		return list;
	}

	@Override
	public String toString() {
		return pointToRotateAround.toString() + "_" + axisOfRotation.toString() + "_" + angleOfRotation;
	}

}
