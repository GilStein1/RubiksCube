package com.example.my3dproject.math.geometry;

import android.graphics.Color;

import androidx.annotation.ColorInt;

public class CubeColors {

	private @ColorInt int[] colors;

	public CubeColors(@ColorInt int... colors) {
		this.colors = new int[6];
		for(int i = 0; i < colors.length && i < this.colors.length; i++) {
			this.colors[i] = colors[i];
		}
	}

	public @ColorInt int[] getColors() {
		return colors;
	}

	public static CubeColors getColorsFromPos(double xValue, double yValue, double zValue) {
		int[] colors = new int[6];
		if(xValue > 0) {
			colors[2] = Color.YELLOW;
			colors[4] = Color.DKGRAY;
		}
		else if (xValue < 0){
			colors[4] = Color.WHITE;
			colors[2] = Color.DKGRAY;
		}
		else {
			colors[2] = Color.DKGRAY;
			colors[4] = Color.DKGRAY;
		}
		if(yValue > 0) {
			colors[0] = Color.RED;
			colors[1] = Color.DKGRAY;
		}
		else if (yValue < 0){
			colors[1] = Color.rgb(255, 140, 0);
			colors[0] = Color.DKGRAY;
		}
		else {
			colors[1] = Color.DKGRAY;
			colors[0] = Color.DKGRAY;
		}
		if(zValue > 0) {
			colors[5] = Color.GREEN;
			colors[3] = Color.DKGRAY;
		}
		else if (zValue < 0){
			colors[3] = Color.BLUE;
			colors[5] = Color.DKGRAY;
		}
		else {
			colors[3] = Color.DKGRAY;
			colors[5] = Color.DKGRAY;
		}
		return new CubeColors(colors);
	}

}
