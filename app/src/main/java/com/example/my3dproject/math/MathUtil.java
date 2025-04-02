package com.example.my3dproject.math;

import com.example.my3dproject.math.geometry.Axis;

public class MathUtil {

	public static double roundValue(double value, int amountOfRounding) {
		return ((int) (value * amountOfRounding)) / (double) amountOfRounding;
	}

	public static boolean isVectorMostlyInAxis(Vec3D vector, Axis axis) {
		switch (axis) {
			case X: return Math.abs(vector.getX()) > Math.max(Math.abs(vector.getY()), Math.abs(vector.getZ()));
			case Y: return Math.abs(vector.getY()) > Math.max(Math.abs(vector.getX()), Math.abs(vector.getZ()));
			case Z: return Math.abs(vector.getZ()) > Math.max(Math.abs(vector.getY()), Math.abs(vector.getX()));
		}
		return false;
	}

}
