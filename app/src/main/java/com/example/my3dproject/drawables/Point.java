package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.my3dproject.Constants;
import com.example.my3dproject.math.geometry.Point2d;

public class Point extends Drawable {

	private double x, y, z;
	private double fl;
	private double drawX, drawY;

	public Point(double screenWidth, double screenHeight, double x, double y, double z, double focalLength) {
		super(screenWidth, screenHeight);
		this.x = x;
		this.y = y;
		this.z = z;
		this.fl = focalLength;
		this.drawX = x* getScreenSizeRatio();
		this.drawY = y* getScreenSizeRatio();
	}

	public Point(Point other) {
		this(other.screenWidth, other.screenHeight, other.x, other.y, other.z, other.fl);
	}

	public void moveTo(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	private double getScreenSizeRatio() {
		return Math.min(screenWidth, screenHeight) / Constants.IDEAL_SCREEN_WIDTH;
	}

	public double getTranslatedX() {
		return (x + screenWidth / 2);
	}

	public double getTranslatedY() {
		return (screenHeight / 2 - y);
	}

	public double getProjectionTranslatedX() {
		double projectedX = (x / z) * fl * getScreenSizeRatio();
		return (projectedX + screenWidth / 2);
	}

	public Point2d getLastDrawnPoint() {
		return new Point2d(getProjectionTranslatedX(), getProjectionTranslatedY());
	}

	public double getProjectionTranslatedY() {
		double projectedY = (y / z) * fl * getScreenSizeRatio();
		return (screenHeight / 2 - projectedY);
	}

	public void setFocalLength(double focalLength) {
		this.fl = focalLength;
	}

	@Override
	public void update(double deltaTime, Point2d pointOfCLick, int event) {
		double projectedX = (x / z) * fl;
		double projectedY = (y / z) * fl;
		drawX = (projectedX + screenWidth / 2)* getScreenSizeRatio();
		drawY = (screenHeight / 2 - projectedY)* getScreenSizeRatio();
	}

	@Override
	public void render(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		canvas.drawCircle((float) drawX, (float) drawY, 10, paint);
	}
}
