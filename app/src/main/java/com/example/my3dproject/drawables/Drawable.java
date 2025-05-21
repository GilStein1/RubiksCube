package com.example.my3dproject.drawables;

import android.graphics.Canvas;

import com.example.my3dproject.ScreenGeometryManager;
import com.example.my3dproject.UpdatableComponent;
import com.example.my3dproject.math.geometry.Point2d;

public abstract class Drawable {

	private final double screenWidth;
	private final double screenHeight;

	public Drawable() {
		this.screenWidth = ScreenGeometryManager.getInstance().getScreenWidth();
		this.screenHeight = ScreenGeometryManager.getInstance().getScreenHeight();
	}

	public double getScreenWidth() {
		return screenWidth;
	}

	public double getScreenHeight() {
		return screenHeight;
	}

	public abstract void render(Canvas canvas, boolean isDarkMode);

}
