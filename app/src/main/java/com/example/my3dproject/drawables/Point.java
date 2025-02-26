package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.my3dproject.Constants;
import com.example.my3dproject.ScreenGeometryManager;
import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.Point3d;

public class Point extends Drawable {

	private final ScreenGeometryManager screenGeometryManager;
	private double x, y, z;
	private double drawX, drawY;

	public Point(double x, double y, double z) {
		super();
		this.screenGeometryManager = ScreenGeometryManager.getInstance();
		this.x = x;
		this.y = y;
		this.z = z;
		this.drawX = x * screenGeometryManager.getScreenSizeRatio();
		this.drawY = y * screenGeometryManager.getScreenSizeRatio();
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

	@Override
	public void update(double deltaTime, Point2d pointOfCLick, int event) {
		drawX = screenGeometryManager.getProjectionTranslatedX(getPose());
		drawY = screenGeometryManager.getProjectionTranslatedY(getPose());
	}

	@Override
	public void render(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		canvas.drawCircle((float) drawX, (float) drawY, 10, paint);
	}
}
