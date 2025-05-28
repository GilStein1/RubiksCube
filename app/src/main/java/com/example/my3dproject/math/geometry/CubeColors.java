package com.example.my3dproject.math.geometry;

import android.graphics.Color;

import androidx.annotation.ColorInt;

/**
 * A class that is used to represent all colors of the 6 sized of a cube
 * in the rubik's cube.
 */
public class CubeColors {

	// Array storing the color values for each of the six cube faces
	private @ColorInt int[] colors;

	/**
	 * Private constructor to create a CubeColors instance with specified colors.
	 *
	 * @param colors Variable number of color integers
	 */
	private CubeColors(@ColorInt int... colors) {
		this.colors = new int[6];
		for(int i = 0; i < colors.length && i < this.colors.length; i++) {
			this.colors[i] = colors[i];
		}
	}

	/**
	 * Gets the array of colors for all six cube faces.
	 *
	 * @return Array of 6 color integers representing each face's color
	 */
	public @ColorInt int[] getColors() {
		return colors;
	}

	/**
	 * A method for generating a CubeColors object to fit a pos of a cube
	 * relative to the rubik's cube coordinate system.
	 *
	 * @param xValue The X-coordinate position of the cube
	 * @param yValue The Y-coordinate position of the cube
	 * @param zValue The Z-coordinate position of the cube
	 * @return A new CubeColors instance with position-based coloring
	 */
	public static CubeColors getColorsFromPos(double xValue, double yValue, double zValue) {
		int[] colors = new int[6];

		// X-axis coloring (right/left faces)
		if(xValue > 0) {
			colors[2] = Color.YELLOW;    // Right face: yellow for positive X
			colors[4] = Color.DKGRAY;    // Left face: dark gray
		}
		else if (xValue < 0){
			colors[4] = Color.WHITE;     // Left face: white for negative X
			colors[2] = Color.DKGRAY;    // Right face: dark gray
		}
		else {
			colors[2] = Color.DKGRAY;    // Both faces dark gray when X = 0
			colors[4] = Color.DKGRAY;
		}

		// Y-axis coloring (top/bottom faces)
		if(yValue > 0) {
			colors[0] = Color.RED;       // Top face: red for positive Y
			colors[1] = Color.DKGRAY;    // Bottom face: dark gray
		}
		else if (yValue < 0){
			colors[1] = Color.rgb(255,165,0);  // Bottom face: orange for negative Y
			colors[0] = Color.DKGRAY;    // Top face: dark gray
		}
		else {
			colors[1] = Color.DKGRAY;    // Both faces dark gray when Y = 0
			colors[0] = Color.DKGRAY;
		}

		// Z-axis coloring (front/back faces)
		if(zValue > 0) {
			colors[5] = Color.rgb(0,197,0);    // Front face: green for positive Z
			colors[3] = Color.DKGRAY;    // Back face: dark gray
		}
		else if (zValue < 0){
			colors[3] = Color.BLUE;      // Back face: blue for negative Z
			colors[5] = Color.DKGRAY;    // Front face: dark gray
		}
		else {
			colors[3] = Color.DKGRAY;    // Both faces dark gray when Z = 0
			colors[5] = Color.DKGRAY;
		}

		return new CubeColors(colors);
	}

}
