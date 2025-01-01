package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.PointUtils;
import com.example.my3dproject.math.geometry.Quaternion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class RubiksCube extends Drawable {

	private final ArrayBlockingQueue<Point2d> lastClicksQueue;
	private final double x, y, z;
	private final List<Cube> cubes;
	private final List<Point> points3d;
	private final List<Point> points3dToDraw;
	private final List<Polygon> polygons;
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

	public RubiksCube(int screenWidth, int screenHeight, double x, double y, double z, double size) {
		super(screenWidth, screenHeight);
		this.x = x;
		this.y = y;
		this.z = z;
		this.points3d = new ArrayList<>();
		this.points3dToDraw = new ArrayList<>();
		this.polygons = new ArrayList<>();
		this.cubes = new ArrayList<>();
		double sizeOfSmallCubes = size/3;
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*1, y - sizeOfSmallCubes*1, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*1, y - sizeOfSmallCubes*0, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*1, y + sizeOfSmallCubes*1, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*1, y - sizeOfSmallCubes*1, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*1, y - sizeOfSmallCubes*0, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*1, y + sizeOfSmallCubes*1, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*1, y - sizeOfSmallCubes*1, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*1, y - sizeOfSmallCubes*0, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*1, y + sizeOfSmallCubes*1, z + sizeOfSmallCubes*1, sizeOfSmallCubes));

		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*0, y - sizeOfSmallCubes*1, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*0, y - sizeOfSmallCubes*0, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*0, y + sizeOfSmallCubes*1, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*0, y - sizeOfSmallCubes*1, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*0, y - sizeOfSmallCubes*0, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*0, y + sizeOfSmallCubes*1, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*0, y - sizeOfSmallCubes*1, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*0, y - sizeOfSmallCubes*0, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x - sizeOfSmallCubes*0, y + sizeOfSmallCubes*1, z + sizeOfSmallCubes*1, sizeOfSmallCubes));

		cubes.add(new Cube(screenWidth, screenHeight, x + sizeOfSmallCubes*1, y - sizeOfSmallCubes*1, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x + sizeOfSmallCubes*1, y - sizeOfSmallCubes*0, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x + sizeOfSmallCubes*1, y + sizeOfSmallCubes*1, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x + sizeOfSmallCubes*1, y - sizeOfSmallCubes*1, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x + sizeOfSmallCubes*1, y - sizeOfSmallCubes*0, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x + sizeOfSmallCubes*1, y + sizeOfSmallCubes*1, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x + sizeOfSmallCubes*1, y - sizeOfSmallCubes*1, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x + sizeOfSmallCubes*1, y - sizeOfSmallCubes*0, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(screenWidth, screenHeight, x + sizeOfSmallCubes*1, y + sizeOfSmallCubes*1, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
		for(Cube cube : cubes) {
			points3d.addAll(Arrays.asList(cube.getAll3dPoints()));
			points3dToDraw.addAll(Arrays.asList(cube.getAll3dPointsToDraw()));
			polygons.addAll(cube.getAllPolygons());
		}
		this.lastClicksQueue = new ArrayBlockingQueue<>(5);
		this.lastPointOfClick = new Point2d(0, 0);
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
		for (int i = 0; i < points3d.size(); i++) {
			Point p = points3d.get(i);
			double tx = p.getX() - x;
			double ty = p.getY() - y;
			double tz = p.getZ() - z;
			double newX = rotationMatrix[0][0] * tx + rotationMatrix[0][1] * ty + rotationMatrix[0][2] * tz;
			double newY = rotationMatrix[1][0] * tx + rotationMatrix[1][1] * ty + rotationMatrix[1][2] * tz;
			double newZ = rotationMatrix[2][0] * tx + rotationMatrix[2][1] * ty + rotationMatrix[2][2] * tz;
			points3dToDraw.get(i).moveTo(newX + x, newY + y, newZ + z);
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
		for(Cube cube : cubes) {
			cube.update(deltaTime, pointOfCLick, event);
		}
		lastPointOfClick = pointOfCLick;
	}

	private void updateAllDots(double deltaTime, Point2d pointOfCLick, int event) {
		for (int i = 0; i < points3dToDraw.size(); i++) {
			points3dToDraw.get(i).setFocalLength(fl);
			points3dToDraw.get(i).update(deltaTime, pointOfCLick, event);
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
		polygons.sort(Comparator.comparingDouble(p -> -p.getDistanceFromPlayer()));
		for(Polygon polygon : polygons) {
			if(polygon.isPointingToPlayer()) {
				polygon.render(canvas);
			}
		}
	}
}
