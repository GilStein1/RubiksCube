package com.example.my3dproject.math;

public class MathUtil {

	public static double roundValue(double value, int amountOfRounding) {
		return ((int) (value * amountOfRounding)) / (double) amountOfRounding;
	}

}
