package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

import com.example.my3dproject.Constants;
import com.example.my3dproject.RotationOperation;
import com.example.my3dproject.math.Vec3D;
import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.Point3d;
import com.example.my3dproject.math.geometry.Quaternion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

public class RubiksCube extends Drawable {

	private final ArrayBlockingQueue<Point2d> lastClicksQueue;
	private final double x, y, z;
	private final List<Cube> cubes;
	private final List<Point> points3d;
	private final List<Point> points3dToDraw;
	private final List<Polygon> polygons;
	private Optional<Polygon> selectedPolygon;
	private Quaternion currentRotation;
	private final double fl = 450;
	private final float rotationScale = 0.3f;
	private final float rotationalVelocityScale = 0.25f;
	private Point2d lastPointOfClick;
	private double xRotationalVelocity;
	private double yRotationalVelocity;
	private double xRotation, yRotation;
	private boolean isScreenPressed;
	private boolean hasNoticedActionUp;
	private Optional<RotationOperation> currentRotationOperation;

	public RubiksCube(double x, double y, double z, double size) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.points3d = new ArrayList<>();
		this.points3dToDraw = new ArrayList<>();
		this.polygons = new ArrayList<>();
		this.selectedPolygon = Optional.empty();
		this.cubes = new ArrayList<>();
		double sizeOfSmallCubes = size/3;
		cubes.add(new Cube(x - sizeOfSmallCubes*1, y - sizeOfSmallCubes*1, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*1, y - sizeOfSmallCubes*0, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*1, y + sizeOfSmallCubes*1, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*1, y - sizeOfSmallCubes*1, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*1, y - sizeOfSmallCubes*0, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*1, y + sizeOfSmallCubes*1, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*1, y - sizeOfSmallCubes*1, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*1, y - sizeOfSmallCubes*0, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*1, y + sizeOfSmallCubes*1, z + sizeOfSmallCubes*1, sizeOfSmallCubes));

		cubes.add(new Cube(x - sizeOfSmallCubes*0, y - sizeOfSmallCubes*1, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*0, y - sizeOfSmallCubes*0, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*0, y + sizeOfSmallCubes*1, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*0, y - sizeOfSmallCubes*1, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*0, y - sizeOfSmallCubes*0, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*0, y + sizeOfSmallCubes*1, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*0, y - sizeOfSmallCubes*1, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*0, y - sizeOfSmallCubes*0, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes*0, y + sizeOfSmallCubes*1, z + sizeOfSmallCubes*1, sizeOfSmallCubes));

		cubes.add(new Cube(x + sizeOfSmallCubes*1, y - sizeOfSmallCubes*1, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes*1, y - sizeOfSmallCubes*0, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes*1, y + sizeOfSmallCubes*1, z - sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes*1, y - sizeOfSmallCubes*1, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes*1, y - sizeOfSmallCubes*0, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes*1, y + sizeOfSmallCubes*1, z - sizeOfSmallCubes*0, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes*1, y - sizeOfSmallCubes*1, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes*1, y - sizeOfSmallCubes*0, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes*1, y + sizeOfSmallCubes*1, z + sizeOfSmallCubes*1, sizeOfSmallCubes));
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
		this.currentRotationOperation = Optional.empty();
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
		for(Cube cube : cubes) {
			Point3d p = cube.getPos();
			double tx = p.getX() - x;
			double ty = p.getY() - y;
			double tz = p.getZ() - z;
			double newX = rotationMatrix[0][0] * tx + rotationMatrix[0][1] * ty + rotationMatrix[0][2] * tz;
			double newY = rotationMatrix[1][0] * tx + rotationMatrix[1][1] * ty + rotationMatrix[1][2] * tz;
			double newZ = rotationMatrix[2][0] * tx + rotationMatrix[2][1] * ty + rotationMatrix[2][2] * tz;
			cube.updateDrawnPos(newX + x, newY + y, newZ + z);
		}
	}

	private double getScreenSizeRatio() {
		return Math.min(screenWidth, screenHeight) / Constants.IDEAL_SCREEN_WIDTH;
	}

	@Override
	public void update(double deltaTime, Point2d pointOfCLick, int event) {
		if (lastClicksQueue.remainingCapacity() == 0) {
			updateByClickInput(pointOfCLick, event);
			lastClicksQueue.remove();
		}
		lastClicksQueue.add(pointOfCLick.times(getScreenSizeRatio()));
		double scaledTime = deltaTime * 100;
		if (!isScreenPressed) {
			updateRotationsFromDecreasingVelocity(scaledTime);
		}
		else if(selectedPolygon.isPresent() && lastClicksQueue.remainingCapacity() == 0 && !currentRotationOperation.isPresent()) {
			Cube selectedCube = selectedPolygon.get().getParentCube();
			Vec3D rotationVector = currentRotation.getRotationVector();
			if(Math.abs(pointOfCLick.getX() - lastClicksQueue.peek().getX()) >
				Math.abs(pointOfCLick.getY() - lastClicksQueue.peek().getY())
			) {
				currentRotationOperation = Optional.of(new RotationOperation(selectedCube, 1));
			}
			else {
				currentRotationOperation = Optional.of(new RotationOperation(selectedCube, 0));
			}
		}
		if(selectedPolygon.isPresent() && currentRotationOperation.isPresent()) {
			Log.w("rotatingX", String.valueOf(currentRotationOperation.get().isRotatingX()));
			Log.w("rotatingY", String.valueOf(currentRotationOperation.get().isRotatingY()));
			Log.w("rotatingZ", String.valueOf(currentRotationOperation.get().isRotatingZ()));

//			currentRotationOperation = Optional.empty();
			if(currentRotationOperation.get().isRotatingX()) {
				currentRotationOperation.get().getInstanceCube().rotateX(Math.toRadians(pointOfCLick.getY() - lastClicksQueue.peek().getY())*deltaTime*50, currentRotation);
			}
			else if(currentRotationOperation.get().isRotatingY()) {
//				currentRotationOperation.get().getInstanceCube().rotateY(Math.toRadians(pointOfCLick.getX() - lastClicksQueue.peek().getX())*deltaTime*50, currentRotation);
			}
//			else if(currentRotationOperation.get().isRotatingZ()) {
//				currentRotationOperation.get().getInstanceCube().rotateZ(0.1*deltaTime, currentRotation);
//			}
//			currentRotationOperation = Optional.empty();
		}
		rotate(Math.toRadians(xRotation), Math.toRadians(yRotation), 0);
		updateAllDots(deltaTime, pointOfCLick.times(getScreenSizeRatio()), event);
		for(Cube cube : cubes) {
			cube.update(deltaTime, pointOfCLick.times(getScreenSizeRatio()), event);
		}
		lastPointOfClick = pointOfCLick.times(getScreenSizeRatio());
	}

	private void updateAllDots(double deltaTime, Point2d pointOfCLick, int event) {
		for (int i = 0; i < points3dToDraw.size(); i++) {
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
				if(!selectedPolygon.isPresent()) {
					selectedPolygon = searchForClickedPolygon(pointOfCLick);
				}
				break;
			case MotionEvent.ACTION_UP:
				if (!hasNoticedActionUp) {
					hasNoticedActionUp = true;
					isScreenPressed = false;
					currentRotationOperation = Optional.empty();
					if(!selectedPolygon.isPresent()) {
						xRotationalVelocity =
							(lastClicksQueue.peek().getY() - pointOfCLick.times(getScreenSizeRatio()).getY())
								* rotationScale * rotationalVelocityScale;
						yRotationalVelocity =
							(lastClicksQueue.peek().getX() - pointOfCLick.times(getScreenSizeRatio()).getX())
								* rotationScale * rotationalVelocityScale;
					}
				}
				selectedPolygon = Optional.empty();
				break;
			case MotionEvent.ACTION_MOVE:
				if(!selectedPolygon.isPresent()) {
					rotateCubeBasedOfNewPointOfClick(pointOfCLick);
				}
				break;
		}
		if (event != MotionEvent.ACTION_UP && hasNoticedActionUp) {
			hasNoticedActionUp = false;
		}
	}

	private Optional<Polygon> searchForClickedPolygon(Point2d pointOfClick) {
		for(int i = polygons.size() - 1; i >= 0; i--) {
			if(polygons.get(i).isPointingToPlayer()) {
				if(polygons.get(i).isPointInPolygon(pointOfClick)) {
					polygons.get(i).setSelected(true);
					return Optional.of(polygons.get(i));
				}
			}
		}
		return Optional.empty();
	}

	private void rotateCubeBasedOfNewPointOfClick(Point2d pointOfCLick) {
		xRotation = (lastPointOfClick.getY() - pointOfCLick.times(getScreenSizeRatio()).getY()) * rotationScale;
		yRotation = (lastPointOfClick.getX() - pointOfCLick.times(getScreenSizeRatio()).getX()) * rotationScale;
	}

	private static int getSignOf(double value) {
		return (value > 0 ? 1 : -1);
	}

	@Override
	public void render(Canvas canvas) {
		polygons.sort(Comparator.comparingDouble(p -> -p.getDistanceFromPlayer()));
		selectedPolygon.ifPresent(polygon -> polygon.setSelected(true));
		for(Polygon polygon : polygons) {
			if(polygon.isPointingToPlayer()) {
				polygon.render(canvas);
			}
		}
//		for(Cube c : cubes) {
//			c.render(canvas);
//		}
	}
}
