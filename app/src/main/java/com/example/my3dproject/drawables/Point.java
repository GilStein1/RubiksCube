package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.my3dproject.ScreenGeometryManager;
import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.Point3d;

public class Point extends Drawable {

	private final ScreenGeometryManager screenGeometryManager;
	private double x, y, z;
	private double drawnX, drawnY;
	private int color;

	public Point(double x, double y, double z) {
		super();
		this.screenGeometryManager = ScreenGeometryManager.getInstance();
		this.x = x;
		this.y = y;
		this.z = z;
		this.drawnX = x * screenGeometryManager.getScreenSizeRatio();
		this.drawnY = y * screenGeometryManager.getScreenSizeRatio();
		this.color = Color.BLACK;
	}

	public Point(Point other) {
		this(other.x, other.y, other.z);
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

	public Point3d getPose() {
		return new Point3d(x, y, z);
	}

	public Point2d getLastDrawnPoint() {
		return new Point2d(
			screenGeometryManager.getProjectionTranslatedX(getPose()),
			screenGeometryManager.getProjectionTranslatedY(getPose())
		);
	}

	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public void render(Canvas canvas, boolean isDarkMode) {
		drawnX = screenGeometryManager.getProjectionTranslatedX(getPose());
		drawnY = screenGeometryManager.getProjectionTranslatedY(getPose());
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		canvas.drawCircle((float) drawnX, (float) drawnY, 10, paint);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(color);
		canvas.drawCircle((float) drawnX, (float) drawnY, 10, paint);
	}
}
