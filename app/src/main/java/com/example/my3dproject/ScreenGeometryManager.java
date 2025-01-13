package com.example.my3dproject;

import com.example.my3dproject.math.geometry.Point3d;

public class ScreenGeometryManager {

	private static ScreenGeometryManager instance;
	private double screenWidth;
	private double screenHeight;
	private double focalLength;

	private ScreenGeometryManager() {
	}

	public static ScreenGeometryManager getInstance() {
		if (instance == null) {
			instance = new ScreenGeometryManager();
		}
		return instance;
	}

	public void setScreenSize(double width, double height) {
		this.screenWidth = width;
		this.screenHeight = height;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	public double getScreenSizeRatio() {
		return Math.min(screenWidth, screenHeight) / Constants.IDEAL_SCREEN_WIDTH;
	}

	public double getScreenWidth() {
		return screenWidth;
	}

	public double getScreenHeight() {
		return screenHeight;
	}

	public double centerX(double x) {
		return screenWidth / 2 + x;
	}

	public double centerY(double y) {
		return screenHeight / 2 - y;
	}

	public double getProjectedX(Point3d point, double focalLength) {
		return (point.getX() / point.getZ()) * focalLength * getScreenSizeRatio();
	}

	public double getProjectedY(Point3d point, double focalLength) {
		return (point.getY() / point.getZ()) * focalLength * getScreenSizeRatio();
	}

	public double getProjectedX(Point3d point) {
		return getProjectedX(point, focalLength);
	}

	public double getProjectedY(Point3d point) {
		return getProjectedY(point, focalLength);
	}

	public double getProjectionTranslatedX(Point3d point, double focalLength) {
		return centerX(getProjectedX(point, focalLength));
	}

	public double getProjectionTranslatedY(Point3d point, double focalLength) {
		return centerY(getProjectedY(point, focalLength));
	}

	public double getProjectionTranslatedX(Point3d point) {
		return getProjectionTranslatedX(point, focalLength);
	}

	public double getProjectionTranslatedY(Point3d point) {
		return getProjectionTranslatedY(point, focalLength);
	}

}
