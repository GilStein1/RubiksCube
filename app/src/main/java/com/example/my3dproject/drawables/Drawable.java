package com.example.my3dproject.drawables;

import android.graphics.Canvas;

import com.example.my3dproject.ScreenGeometryManager;
import com.example.my3dproject.UpdatableComponent;
import com.example.my3dproject.math.geometry.Point2d;

/**
 * Abstract base class for all drawable objects in the 3D project.
 * This class provides common functionality and properties that all drawable objects need,
 * such as screen dimensions and the requirement to implement a render method.
 */
public abstract class Drawable {

	// Screen dimensions
	private final double screenWidth;
	private final double screenHeight;

	/**
	 * Constructor that initializes the drawable object with current screen dimensions.
	 * Screen dimensions are taken from the ScreenGeometryManager singleton.
	 */
	public Drawable() {
		this.screenWidth = ScreenGeometryManager.getInstance().getScreenWidth();
		this.screenHeight = ScreenGeometryManager.getInstance().getScreenHeight();
	}

	/**
	 * Gets the screen width.
	 *
	 * @return Width of the screen in pixels
	 */
	public double getScreenWidth() {
		return screenWidth;
	}

	/**
	 * Gets the screen height.
	 *
	 * @return Height of the screen in pixels
	 */
	public double getScreenHeight() {
		return screenHeight;
	}

	/**
	 * Abstract method that must be implemented by all drawable objects.
	 * This method is responsible for rendering the object onto the provided canvas.
	 *
	 * @param canvas The Android Canvas object to draw on
	 * @param isDarkMode Boolean indicating whether the app is in dark mode
	 */
	public abstract void render(Canvas canvas, boolean isDarkMode);

}