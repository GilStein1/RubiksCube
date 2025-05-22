package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.my3dproject.ScreenGeometryManager;
import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.Point3d;

/**
 * Represents a single point in 3D space.
 * This class handles the projection from 3D coordinates to 2D screen coordinates.
 */
public class Point extends Drawable {

	// Reference to the screen geometry manager for coordinate transformations
	private final ScreenGeometryManager screenGeometryManager;

	// 3D coordinates of the point
	private double x, y, z;

	// 2D screen coordinates where the point was last drawn
	private double drawnX, drawnY;

	// Color of the point
	private int color;

	/**
	 * Creates a new point at the specified coordinates.
	 *
	 * @param x X coordinate in 3D space
	 * @param y Y coordinate in 3D space
	 * @param z Z coordinate in 3D space
	 */
	public Point(double x, double y, double z) {
		super();
		this.screenGeometryManager = ScreenGeometryManager.getInstance();
		this.x = x;
		this.y = y;
		this.z = z;

		// Calculate initial 2D screen coordinates
		this.drawnX = x * screenGeometryManager.getScreenSizeRatio();
		this.drawnY = y * screenGeometryManager.getScreenSizeRatio();

		// Default color is black
		this.color = Color.BLACK;
	}

	/**
	 * Copy constructor that creates a new point with the same coordinates as another point.
	 *
	 * @param other The point to copy coordinates from
	 */
	public Point(Point other) {
		this(other.x, other.y, other.z);
	}

	/**
	 * Moves the point to new 3D coordinates.
	 *
	 * @param x New X coordinate
	 * @param y New Y coordinate
	 * @param z New Z coordinate
	 */
	public void moveTo(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @return X coordinate of the point
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return Y coordinate of the point
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return Z coordinate of the point
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Gets the 3D position of the point as a Point3d object.
	 *
	 * @return Point3d object representing the current 3D position
	 */
	public Point3d getPose() {
		return new Point3d(x, y, z);
	}

	/**
	 * Gets the 2D screen coordinates where this point was last drawn.
	 * This uses the current 3D position and projects it to 2D screen space.
	 *
	 * @return Point2d object with the projected screen coordinates
	 */
	public Point2d getLastDrawnPoint() {
		return new Point2d(
			screenGeometryManager.getProjectionTranslatedX(getPose()),
			screenGeometryManager.getProjectionTranslatedY(getPose())
		);
	}

	/**
	 * Sets the color used to fill the point when rendered.
	 *
	 * @param color the color of the point
	 */
	public void setColor(int color) {
		this.color = color;
	}

	/**
	 * Renders the point as a filled circle on the canvas.
	 *
	 * @param canvas Android Canvas to draw on
	 * @param isDarkMode Whether dark mode is enabled
	 */
	@Override
	public void render(Canvas canvas, boolean isDarkMode) {
		// Project 3D coordinates to 2D screen coordinates
		drawnX = screenGeometryManager.getProjectionTranslatedX(getPose());
		drawnY = screenGeometryManager.getProjectionTranslatedY(getPose());

		// Create paint for drawing
		Paint paint = new Paint();

		// Draw black outline circle
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		canvas.drawCircle((float) drawnX, (float) drawnY, 10, paint);

		// Fill circle with the specified color
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(color);
		canvas.drawCircle((float) drawnX, (float) drawnY, 10, paint);
	}
}