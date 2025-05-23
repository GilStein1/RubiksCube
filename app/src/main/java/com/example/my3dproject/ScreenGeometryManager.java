package com.example.my3dproject;

import com.example.my3dproject.math.geometry.Point3d;

/**
 * A class responsible for all 3D to 2d transformations and translations of the screen.
 */
public class ScreenGeometryManager {

	// The static instance of the class - for singleton purposes
	private static ScreenGeometryManager instance;
	// The width of the screen in pixels
	private double screenWidth;
	// The height of the screen in pixels
	private double screenHeight;
	// The focal length of the camera in the scene
	private double focalLength;

	/**
	 * The private constructor
	 */
	private ScreenGeometryManager() {
	}

	/**
	 * A method for getting the static instance of the class.
	 *
	 * @return the static instance of the class
	 */
	public static ScreenGeometryManager getInstance() {
		if (instance == null) { // If the instance is null, construct a new one
			instance = new ScreenGeometryManager();
		}
		return instance;
	}

	/**
	 * A method for setting new screen dimensions
	 *
	 * @param width The width of the screen in pixels
	 * @param height The height of the screen in pixels
	 */
	public void setScreenSize(double width, double height) {
		this.screenWidth = width;
		this.screenHeight = height;
	}

	/**
	 * A method for setting a new focal length for the camera
	 *
	 * @param focalLength The new focal length
	 */
	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	/**
	 * A method for getting the screen size ratio from the ideal screen size of the emulator
	 * that I used in the creation of this app. It is used to make sure that all graphics are
	 * in the same size in all devices
	 *
	 * @return the screen size ratio
	 */
	public double getScreenSizeRatio() {
		return Math.min(screenWidth, screenHeight) / Constants.IDEAL_SCREEN_WIDTH;
	}

	/**
	 * @return the screen width
	 */
	public double getScreenWidth() {
		return screenWidth;
	}

	/**
	 * @return the screen height
	 */
	public double getScreenHeight() {
		return screenHeight;
	}

	/**
	 * A method that gets a 2d X coordinate and transforms it so
	 * the (0, 0) is in the center of the screen
	 */
	public double centerX(double x) {
		return screenWidth / 2 + x;
	}

	/**
	 * A method that gets a 2d Y coordinate and transforms it so
	 * the (0, 0) is in the center of the screen
	 */
	public double centerY(double y) {
		return screenHeight / 2 - y;
	}

	/**
	 * A method that gets a 3d point and focal length and project in into a 2d screen
	 *
	 * @return the X value of the projected pos
	 */
	public double getProjectedX(Point3d point, double focalLength) {
		return (point.getX() / (point.getZ() + Constants.EnvironmentConstants.PLAYER_POSITION.getZ())) * focalLength * getScreenSizeRatio();
	}

	/**
	 * A method that gets a 3d point and focal length and project in into a 2d screen
	 *
	 * @return the Y value of the projected pos
	 */
	public double getProjectedY(Point3d point, double focalLength) {
		return (point.getY() / (point.getZ() + Constants.EnvironmentConstants.PLAYER_POSITION.getZ())) * focalLength * getScreenSizeRatio();
	}

	/**
	 * A method that gets a 3d point and focal length and project in into a centered 2d screen
	 *
	 * @return the X value of the projected pos
	 */
	public double getProjectionTranslatedX(Point3d point, double focalLength) {
		return centerX(getProjectedX(point, focalLength));
	}

	/**
	 * A method that gets a 3d point and focal length and project in into a centered 2d screen
	 *
	 * @return the Y value of the projected pos
	 */
	public double getProjectionTranslatedY(Point3d point, double focalLength) {
		return centerY(getProjectedY(point, focalLength));
	}

	/**
	 * A method that gets a 3d point and focal length and project in into a centered 2d screen
	 *
	 * @return the projected pos
	 */
	public Point3d getProjectionTranslatedPoint3d(Point3d point, double focalLength) {
		return new Point3d(getProjectionTranslatedX(point, focalLength), getProjectionTranslatedY(point, focalLength), 0);
	}

	/**
	 * A method that gets a 3d point and project in into a 2d screen
	 *
	 * @return the X value of the projected pos
	 */
	public double getProjectionTranslatedX(Point3d point) {
		return getProjectionTranslatedX(point, focalLength);
	}

	/**
	 * A method that gets a 3d point and project in into a 2d screen
	 *
	 * @return the Y value of the projected pos
	 */
	public double getProjectionTranslatedY(Point3d point) {
		return getProjectionTranslatedY(point, focalLength);
	}

}
