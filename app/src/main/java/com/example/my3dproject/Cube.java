package com.example.my3dproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.Quaternion;

import java.util.concurrent.ArrayBlockingQueue;

public class Cube extends Drawable {

	private final ArrayBlockingQueue<Point2d> lastClicksQueue;
	private final double x, y, z;
	private Dot[] dots;
	private final double[][] points3d;
	private final double[][] points3dToDraw;
	private Quaternion currentRotation;
	private final double fl = 600;
	private final float rotationScale = 0.5f;
	private final float rotationalVelocityScale = 0.25f;
	private Point2d lastPointOfClick;
	private double xRotationalVelocity;
	private double yRotationalVelocity;
	private double xRotation, yRotation;
	private boolean isScreenPressed;
	private boolean hasNoticedActionUp;

	public Cube(int screenWidth, int screenHeight, double x, double y, double z, double width, double height, double depth) {
		super(screenWidth, screenHeight);
		this.x = x;
		this.y = y;
		this.z = z;
		this.lastClicksQueue = new ArrayBlockingQueue<>(5);
		this.lastPointOfClick = new Point2d(0, 0);
		this.dots = new Dot[]{new Dot(screenWidth, screenHeight, 0, 0), new Dot(screenWidth, screenHeight, 0, 0), new Dot(screenWidth, screenHeight, 0, 0), new Dot(screenWidth, screenHeight, 0, 0), new Dot(screenWidth, screenHeight, 0, 0), new Dot(screenWidth, screenHeight, 0, 0), new Dot(screenWidth, screenHeight, 0, 0), new Dot(screenWidth, screenHeight, 0, 0)};
		this.points3d = new double[][]{{x + width / 2, y + height / 2, z + depth / 2}, {x + width / 2, y - height / 2, z + depth / 2}, {x - width / 2, y + height / 2, z + depth / 2}, {x - width / 2, y - height / 2, z + depth / 2}, {x + width / 2, y + height / 2, z - depth / 2}, {x + width / 2, y - height / 2, z - depth / 2}, {x - width / 2, y + height / 2, z - depth / 2}, {x - width / 2, y - height / 2, z - depth / 2}};
		this.points3dToDraw = new double[][]{{x + width / 2, y + height / 2, z + depth / 2}, {x + width / 2, y - height / 2, z + depth / 2}, {x - width / 2, y + height / 2, z + depth / 2}, {x - width / 2, y - height / 2, z + depth / 2}, {x + width / 2, y + height / 2, z - depth / 2}, {x + width / 2, y - height / 2, z - depth / 2}, {x - width / 2, y + height / 2, z - depth / 2}, {x - width / 2, y - height / 2, z - depth / 2}};
		this.currentRotation = new Quaternion(1, 0, 0, 0);
		this.xRotationalVelocity = 0;
		this.yRotationalVelocity = 0;
		this.xRotation = 0;
		this.yRotation = 0;
		this.isScreenPressed = false;
		this.hasNoticedActionUp = false;
	}

	private void rotate(double xAngle, double yAngle, double zAngle) {
		Quaternion xRotation = Quaternion.fromAxisAngle(xAngle, 1, 0, 0);
		Quaternion yRotation = Quaternion.fromAxisAngle(yAngle, 0, 1, 0);
		Quaternion zRotation = Quaternion.fromAxisAngle(zAngle, 0, 0, 1);
		currentRotation = xRotation.multiply(yRotation).multiply(zRotation).multiply(currentRotation);
		double[][] rotationMatrix = currentRotation.toRotationMatrix();
		for (int i = 0; i < points3d.length; i++) {
			double[] p = points3d[i];
			double tx = p[0] - x;
			double ty = p[1] - y;
			double tz = p[2] - z;
			double newX = rotationMatrix[0][0] * tx + rotationMatrix[0][1] * ty + rotationMatrix[0][2] * tz;
			double newY = rotationMatrix[1][0] * tx + rotationMatrix[1][1] * ty + rotationMatrix[1][2] * tz;
			double newZ = rotationMatrix[2][0] * tx + rotationMatrix[2][1] * ty + rotationMatrix[2][2] * tz;
			points3dToDraw[i] = new double[]{newX + x, newY + y, newZ + z};
		}
	}

	@Override
	public void update(double deltaTime, Point2d pointOfCLick, int event) {
		if (lastClicksQueue.remainingCapacity() == 0) {
			updateByClickInput(pointOfCLick, event);
			lastClicksQueue.remove();
		}
		lastClicksQueue.add(pointOfCLick);
		double scaledTime = deltaTime * 100;
		if (!isScreenPressed) {
			updateRotationsFromDecreasingVelocity(scaledTime);
		}
		rotate(Math.toRadians(xRotation), Math.toRadians(yRotation), 0);
		updateAllDots(deltaTime, pointOfCLick, event);
		lastPointOfClick = pointOfCLick;
	}

	private Point2d translate3dPoseToProjection(double[] point3d, double focalLength) {
		if (point3d[2] == 0) return new Point2d(0, 0);
		return new Point2d(
			(point3d[0] / point3d[2]) * focalLength,
			(point3d[1] / point3d[2]) * focalLength
		);
	}

	private void updateAllDots(double deltaTime, Point2d pointOfCLick, int event) {
		for (int i = 0; i < dots.length; i++) {
			Point2d translatedPoint = translate3dPoseToProjection(points3dToDraw[i], fl);
			dots[i].moveTo(translatedPoint.getX(), translatedPoint.getY());
			dots[i].update(deltaTime, pointOfCLick, event);
		}
	}

	private void updateRotationsFromDecreasingVelocity(double time) {
		double absolutValueOfSumOfVelocities = Math.abs(xRotationalVelocity) + Math.abs(yRotationalVelocity);
		double xVelocityRatio = zeroIfNaN(Math.abs(xRotationalVelocity) / absolutValueOfSumOfVelocities);
		double yVelocityRatio = zeroIfNaN(Math.abs(yRotationalVelocity) / absolutValueOfSumOfVelocities);
		xRotationalVelocity -= getSignOf(xRotationalVelocity) * Math.min(Math.abs(xRotationalVelocity), time * xVelocityRatio);
		yRotationalVelocity -= getSignOf(yRotationalVelocity) * Math.min(Math.abs(yRotationalVelocity), time * yVelocityRatio);
		xRotation = xRotationalVelocity;
		yRotation = yRotationalVelocity;
	}

	private static double zeroIfNaN(double value) {
		return Double.isNaN(value) ? 0 : value;
	}

	private void updateByClickInput(Point2d pointOfCLick, int event) {
		switch (event) {
			case MotionEvent.ACTION_DOWN:
				isScreenPressed = true;
				break;
			case MotionEvent.ACTION_UP:
				if (!hasNoticedActionUp) {
					hasNoticedActionUp = true;
					isScreenPressed = false;
					xRotationalVelocity =
						(lastClicksQueue.peek().getY() - pointOfCLick.getY())
							* rotationScale * rotationalVelocityScale;
					yRotationalVelocity =
						(lastClicksQueue.peek().getX() - pointOfCLick.getX())
							* rotationScale * rotationalVelocityScale;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				rotateCubeBasedOfNewPointOfClick(pointOfCLick);
				break;
		}
		if (event != MotionEvent.ACTION_UP && hasNoticedActionUp) {
			hasNoticedActionUp = false;
		}
	}

	private void rotateCubeBasedOfNewPointOfClick(Point2d pointOfCLick) {
		xRotation = (lastPointOfClick.getY() - pointOfCLick.getY()) * rotationScale;
		yRotation = (lastPointOfClick.getX() - pointOfCLick.getX()) * rotationScale;
	}

	private static int getSignOf(double value) {
		return (value > 0 ? 1 : -1);
	}

	@Override
	public void render(Canvas canvas) {
		Paint linePaint = new Paint();
		linePaint.setColor(Color.BLACK);
		canvas.drawLine((float) (dots[0].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[0].getY()), (float) (dots[1].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[1].getY()), linePaint);
		canvas.drawLine((float) (dots[0].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[0].getY()), (float) (dots[2].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[2].getY()), linePaint);
		canvas.drawLine((float) (dots[1].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[1].getY()), (float) (dots[3].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[3].getY()), linePaint);
		canvas.drawLine((float) (dots[2].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[2].getY()), (float) (dots[3].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[3].getY()), linePaint);

		canvas.drawLine((float) (dots[4].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[4].getY()), (float) (dots[5].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[5].getY()), linePaint);
		canvas.drawLine((float) (dots[4].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[4].getY()), (float) (dots[6].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[6].getY()), linePaint);
		canvas.drawLine((float) (dots[5].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[5].getY()), (float) (dots[7].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[7].getY()), linePaint);
		canvas.drawLine((float) (dots[6].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[6].getY()), (float) (dots[7].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[7].getY()), linePaint);

		canvas.drawLine((float) (dots[0].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[0].getY()), (float) (dots[4].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[4].getY()), linePaint);
		canvas.drawLine((float) (dots[1].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[1].getY()), (float) (dots[5].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[5].getY()), linePaint);
		canvas.drawLine((float) (dots[2].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[2].getY()), (float) (dots[6].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[6].getY()), linePaint);
		canvas.drawLine((float) (dots[7].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[7].getY()), (float) (dots[3].getX() + screenWidth / 2), (float) (screenHeight / 2 - dots[3].getY()), linePaint);

//		for (Dot p : dots) {
//			p.render(canvas);
//		}
	}
}
