package com.example.my3dproject;

import android.graphics.Canvas;

import com.example.my3dproject.math.geometry.Point2d;

public abstract class Drawable {

	protected double screenWidth, screenHeight;

	public Drawable(double screenWidth, double screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	public abstract void update(double deltaTime, Point2d pointOfClick, int event);

	public abstract void render(Canvas canvas);

}
