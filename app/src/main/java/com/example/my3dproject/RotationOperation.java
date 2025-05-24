package com.example.my3dproject;

import com.example.my3dproject.math.geometry.Axis;
import com.example.my3dproject.math.geometry.Point3d;

import java.util.ArrayList;
import java.util.List;

/**
 * RotationOperation represents a single rotation operation that can be performed on the Rubik's cube by the player.
 * Each operation defines a 3D rotation around a specific point and axis by a given angle.
 */
public class RotationOperation {

	private final Point3d pointToRotateAround;  // 3D point that serves as the center of rotation
	private final Axis axisOfRotation;  // Axis around which the rotation occurs
	private final double angleOfRotation;  // Angle of rotation

	/**
	 * Tolerance value for determining if cube pieces are close enough to a rotation point
	 * to be considered part of the same face or layer being rotated
	 */
	public static final double CUBE_POSITION_TOLERANCE_FOR_ROTATION = 0.1;

	/**
	 * Main constructor for creating a rotation operation
	 * @param pointToRotateAround The 3D point that serves as the center of rotation
	 * @param axisOfRotation The axis around which to rotate
	 * @param angleOfRotation The angle of rotation
	 */
	public RotationOperation(Point3d pointToRotateAround, Axis axisOfRotation, double angleOfRotation) {
		this.pointToRotateAround = pointToRotateAround;
		this.axisOfRotation = axisOfRotation;
		this.angleOfRotation = angleOfRotation;
	}

	/**
	 * Constructor that creates a Point3d from individual coordinates
	 * @param x X-coordinate of the rotation center
	 * @param y Y-coordinate of the rotation center
	 * @param z Z-coordinate of the rotation center
	 * @param axisOfRotation The axis around which to rotate
	 * @param angleOfRotation The angle of rotation
	 */
	public RotationOperation(double x, double y, double z, Axis axisOfRotation, double angleOfRotation) {
		this(new Point3d(x, y, z), axisOfRotation, angleOfRotation);
	}

	/**
	 * Default constructor that creates an "empty" rotation operation. It is used by Firebase
	 */
	public RotationOperation() {
		pointToRotateAround = new Point3d(0, 0, 0);
		axisOfRotation = Axis.X;
		angleOfRotation = 0;
	}

	/**
	 * Gets the 3D point around which the rotation occurs
	 * @return Point3d representing the center of rotation
	 */
	public Point3d getPointToRotateAround() {
		return pointToRotateAround;
	}

	/**
	 * Gets the axis of rotation
	 * @return Axis enum value
	 */
	public Axis getAxisOfRotation() {
		return axisOfRotation;
	}

	/**
	 * Gets the angle of rotation
	 * @return Rotation angle as a double
	 */
	public double getAngleOfRotation() {
		return angleOfRotation;
	}

	/**
	 * Parses a string representation back into a RotationOperation object
	 * @param value String representation of a rotation operation
	 * @return RotationOperation object parsed from the string
	 */
	public static RotationOperation valueOf(String value) {
		String[] parts = value.split("_");
		return new RotationOperation(Point3d.valueOf(parts[0]), Axis.valueOf(parts[1]), Double.parseDouble(parts[2]));
	}

	/**
	 * Parses a string containing multiple rotation operations separated by '~'
	 * @param value String containing multiple rotation operations separated by '~'
	 * @return List of RotationOperation objects parsed from the string
	 */
	public static List<RotationOperation> valuesOf(String value) {
		// Return empty list if input is empty
		if(value.isEmpty()) {
			return new ArrayList<>();
		}

		List<RotationOperation> list = new ArrayList<>();
		// Remove the trailing '~' if present
		value = value.substring(0, value.length()-1);
		// Split by '~' separator and parse each rotation operation
		String[] parts = value.split("~");
		for(String part : parts) {
			list.add(RotationOperation.valueOf(part));
		}
		return list;
	}

	/**
	 * Converts the rotation operation to a string representation.
	 * @return String representation suitable for saving/loading
	 */
	@Override
	public String toString() {
		return pointToRotateAround.toString() + "_" + axisOfRotation.toString() + "_" + angleOfRotation;
	}

}