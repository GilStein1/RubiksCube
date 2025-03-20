package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;

import com.example.my3dproject.RubiksCubeState;
import com.example.my3dproject.TimedAction;
import com.example.my3dproject.Constants;
import com.example.my3dproject.RotationOperation;
import com.example.my3dproject.TimedAnimationManager;
import com.example.my3dproject.math.Vec3D;
import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.Point3d;
import com.example.my3dproject.math.geometry.Quaternion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class RubiksCube extends Drawable {

	private final ArrayBlockingQueue<Point2d> lastClicksQueue;
	private final TimedAnimationManager animationManager;
	private final double x, y, z;
	private final List<Cube> cubes;
	private final List<Point> points3d;
	private final List<Point> points3dToDraw;
	private final List<Polygon> polygons;
	private Optional<Polygon> selectedPolygon;
	private Quaternion currentRotation;
	private final float rotationScale = 0.3f;
	private final float rotationalVelocityScale = 0.25f;
	private Point2d lastPointOfClick;
	private double xRotationalVelocity;
	private double yRotationalVelocity;
	private double xRotation, yRotation;
	private boolean isScreenPressed;
	private boolean hasNoticedActionUp;
	private RubiksCubeState rubiksCubeState;
	private Optional<RotationOperation> currentRotationOperation;
	private final Stack<Pair<Consumer<Double>, Double>> undoStack;
	private final double rubiksCubeSize;
	private final double smallCubesSize;

	public RubiksCube(double x, double y, double z, double size, TimedAnimationManager animationManager) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.rubiksCubeSize = size;
		this.animationManager = animationManager;
		this.points3d = new ArrayList<>();
		this.points3dToDraw = new ArrayList<>();
		this.polygons = new ArrayList<>();
		this.selectedPolygon = Optional.empty();
		this.cubes = new ArrayList<>();
		double sizeOfSmallCubes = size / 3;
		this.smallCubesSize = sizeOfSmallCubes;
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubes.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		for (Cube cube : cubes) {
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
		this.rubiksCubeState = RubiksCubeState.IDLE;
		this.currentRotationOperation = Optional.empty();
		this.undoStack = new Stack<>();
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
		for (Cube cube : cubes) {
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
			if(!lastClicksQueue.isEmpty()) {
				lastClicksQueue.remove();
			}
		}
		if(event == MotionEvent.ACTION_DOWN || event == MotionEvent.ACTION_MOVE) {
			lastClicksQueue.add(pointOfCLick.times(1/getScreenSizeRatio()));
		}

		double scaledTime = deltaTime * 100;

		double distance = (lastClicksQueue.remainingCapacity() == 0)? Math.sqrt(Math.pow((pointOfCLick.times(1/getScreenSizeRatio()).getX() - lastClicksQueue.peek().getX()), 2)
			+ Math.pow((pointOfCLick.times(1/getScreenSizeRatio()).getY() - lastClicksQueue.peek().getY()), 2)) : 0;

		if (!isScreenPressed) {
			updateRotationsFromDecreasingVelocity(scaledTime);
		} else if (rubiksCubeState.isAvailableForModifications() && selectedPolygon.isPresent() && lastClicksQueue.remainingCapacity() == 0 && distance > 10) {
			rubiksCubeState = RubiksCubeState.ROTATED_BY_PLAYER;
			Cube selectedCube = selectedPolygon.get().getParentCube();
			Vec3D rotationUpVector = new Vec3D(
				currentRotation.toRotationMatrix()[0][1],
				currentRotation.toRotationMatrix()[1][1],
				currentRotation.toRotationMatrix()[2][1]
			);
			Vec3D rotationForwardVector = new Vec3D(
				currentRotation.toRotationMatrix()[0][2],
				currentRotation.toRotationMatrix()[1][2],
				currentRotation.toRotationMatrix()[2][2]
			);
			boolean isMostlyRotatingToSide = Math.abs(pointOfCLick.times(1/getScreenSizeRatio()).getX() - lastClicksQueue.peek().getX()) >
					Math.abs(pointOfCLick.times(1/getScreenSizeRatio()).getY() - lastClicksQueue.peek().getY());
			int directionOfSwipe = isMostlyRotatingToSide ?
				(pointOfCLick.times(1/getScreenSizeRatio()).getX() - lastClicksQueue.peek().getX()) > 0 ? 1 : -1 :
				(pointOfCLick.times(1/getScreenSizeRatio()).getY() - lastClicksQueue.peek().getY()) > 0 ? 1 : -1;
			if (Math.abs(rotationUpVector.getX()) > Math.abs(rotationUpVector.getY()) && Math.abs(rotationUpVector.getX()) > Math.abs(rotationUpVector.getZ())) {
				if(Math.abs(rotationForwardVector.getZ()) > Math.abs(rotationForwardVector.getY()) && Math.abs(rotationForwardVector.getZ()) > Math.abs(rotationForwardVector.getX())) {
					if(isMostlyRotatingToSide) {
						animateRotatingAroundX(25, 0.12, Math.toRadians(90*directionOfSwipe), selectedCube);
					}
					else {
						animateRotatingAroundY(25, 0.12, Math.toRadians(90*directionOfSwipe), selectedCube);
					}
				}
				else {
					if(isMostlyRotatingToSide) {
						animateRotatingAroundZ(25, 0.12, Math.toRadians(90*directionOfSwipe), selectedCube);
					}
					else {
						animateRotatingAroundY(25, 0.12, Math.toRadians(90*directionOfSwipe), selectedCube);
					}
				}
			} else if (Math.abs(rotationUpVector.getY()) > Math.abs(rotationUpVector.getX()) && Math.abs(rotationUpVector.getY()) > Math.abs(rotationUpVector.getZ())) {
				if(Math.abs(rotationForwardVector.getZ()) > Math.abs(rotationForwardVector.getY()) && Math.abs(rotationForwardVector.getZ()) > Math.abs(rotationForwardVector.getX())) {
					if(isMostlyRotatingToSide) {
						animateRotatingAroundY(25, 0.12, Math.toRadians(90*directionOfSwipe), selectedCube);
					}
					else {
						animateRotatingAroundX(25, 0.12, Math.toRadians(90*directionOfSwipe), selectedCube);
					}
				}
				else {
					if(isMostlyRotatingToSide) {
						animateRotatingAroundY(25, 0.12, Math.toRadians(90*directionOfSwipe), selectedCube);
					}
					else {
						animateRotatingAroundZ(25, 0.12, Math.toRadians(90*directionOfSwipe), selectedCube);
					}
				}
			} else {
				if(Math.abs(rotationForwardVector.getY()) > Math.abs(rotationForwardVector.getZ()) && Math.abs(rotationForwardVector.getY()) > Math.abs(rotationForwardVector.getX())) {
					if(isMostlyRotatingToSide) {
						animateRotatingAroundZ(25, 0.12, Math.toRadians(90*directionOfSwipe), selectedCube);
					}
					else {
						animateRotatingAroundX(25, 0.12, Math.toRadians(90*directionOfSwipe), selectedCube);
					}
				}
				else {
					if(isMostlyRotatingToSide) {
						animateRotatingAroundX(25, 0.12, Math.toRadians(90*directionOfSwipe), selectedCube);
					}
					else {
						animateRotatingAroundZ(25, 0.12, Math.toRadians(90*directionOfSwipe), selectedCube);
					}
				}
			}
			animationManager.addAction(new TimedAction(() -> rubiksCubeState = RubiksCubeState.IDLE, 0.5));
		}
//		else if(lastClicksQueue.remainingCapacity() != 0) {
			Log.w("the queue is less then z", String.valueOf(lastClicksQueue.remainingCapacity()));
//		}
		rotate(Math.toRadians(xRotation), Math.toRadians(yRotation), 0);
		updateAllDots(deltaTime, pointOfCLick.times(getScreenSizeRatio()), event);
		for (Cube cube : cubes) {
			cube.update(deltaTime, pointOfCLick.times(getScreenSizeRatio()), event);
		}
		lastPointOfClick = pointOfCLick.times(getScreenSizeRatio());
	}

	private void addRotationToUndoListForPlayerMove(double angleRotated) {
		Log.w("AngleRotatedToUndo", String.valueOf(angleRotated));
		Cube instanceCube = currentRotationOperation.get().getInstanceCube();
		if(currentRotationOperation.get().isRotatingX()) {
			undoStack.push(new Pair<>(angle -> rotateXAroundCube(instanceCube, angle), -angleRotated));
		}
		else if(currentRotationOperation.get().isRotatingY()) {
			undoStack.push(new Pair<>(angle -> rotateYAroundCube(instanceCube, angle), -angleRotated));
		}
		else {
			undoStack.push(new Pair<>(angle -> rotateZAroundCube(instanceCube, angle), -angleRotated));
		}
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
				if (!selectedPolygon.isPresent()) {
					selectedPolygon = searchForClickedPolygon(pointOfCLick);
				}
				break;
			case MotionEvent.ACTION_UP:
				if (currentRotationOperation.isPresent() && !currentRotationOperation.get().shouldLockIn()) {
					currentRotationOperation.get().setLockIn(true);
				}
				if (!hasNoticedActionUp) {
					hasNoticedActionUp = true;
					isScreenPressed = false;
					if (!selectedPolygon.isPresent()) {
						xRotationalVelocity =
							(lastClicksQueue.peek().getY() - pointOfCLick.times(1/getScreenSizeRatio()).getY())
								* rotationScale * rotationalVelocityScale;
						yRotationalVelocity =
							(lastClicksQueue.peek().getX() - pointOfCLick.times(1/getScreenSizeRatio()).getX())
								* rotationScale * rotationalVelocityScale;
					}
					lastClicksQueue.clear();
				}
				selectedPolygon = Optional.empty();
				break;
			case MotionEvent.ACTION_MOVE:
				if (!selectedPolygon.isPresent()) {
					rotateCubeBasedOfNewPointOfClick(pointOfCLick);
				}
				break;
		}
		if (event != MotionEvent.ACTION_UP && hasNoticedActionUp) {
			hasNoticedActionUp = false;
		}
	}

	private Optional<Polygon> searchForClickedPolygon(Point2d pointOfClick) {
		for (int i = polygons.size() - 1; i >= 0; i--) {
			if (polygons.get(i).isPointingToPlayer()) {
				if (polygons.get(i).isPointInPolygon(pointOfClick)) {
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

	public void randomize() {
		if(!rubiksCubeState.isAvailableForModifications()) {
			return;
		}
		rubiksCubeState = RubiksCubeState.RANDOMIZING;
		int amountOfTurns = 50;
		int animationSteps = 25;
		double timeToTurn = 0.12;

		int lastAxis = (int)(Math.random()*3);
		int lastSide = (int)(Math.random()*3) - 1;
		int direction = 1;
		animationManager.addAction(new TimedAction(() -> rubiksCubeState = RubiksCubeState.IDLE, timeToTurn * amountOfTurns));
		for(int i = 0; i < amountOfTurns; i++) {
			int axis = (int)(Math.random()*3);
			int side = (int)(Math.random()*3) - 1;
			if(axis != lastAxis || side != lastSide) {
				direction *= -1;
			}
			lastAxis = axis;
			lastSide = side;
			int directionOfRotation = direction;
			if(axis == 0) {
				for(int j = 0; j < animationSteps; j++) {
					animationManager.addAction(new TimedAction(() -> rotateXAroundCube(
						new Cube(side * smallCubesSize, 0, 0, smallCubesSize), Math.toRadians(90.0/animationSteps*directionOfRotation)
					),j*timeToTurn/animationSteps + i*timeToTurn));
				}
				undoStack.push(new Pair<>(angle -> rotateXAroundCube(new Cube(side * smallCubesSize, 0, 0, smallCubesSize), angle), Math.toRadians(90.0 * -directionOfRotation)));
			}
			else if(axis == 1) {
				for(int j = 0; j < animationSteps; j++) {
					animationManager.addAction(new TimedAction(() -> rotateYAroundCube(
						new Cube(0, side * smallCubesSize, 0, smallCubesSize), Math.toRadians(90.0/animationSteps*directionOfRotation)
					),j*timeToTurn/animationSteps + i*timeToTurn));
				}
				undoStack.push(new Pair<>(angle -> rotateYAroundCube(new Cube(0, side * smallCubesSize, 0, smallCubesSize), angle), Math.toRadians(90.0 * -directionOfRotation)));
			}
			else {
				for(int j = 0; j < animationSteps; j++) {
					animationManager.addAction(new TimedAction(() -> rotateZAroundCube(
						new Cube(0, 0, side * smallCubesSize, smallCubesSize), Math.toRadians(90.0/animationSteps*directionOfRotation)
					),j*timeToTurn/animationSteps + i*timeToTurn));
				}
				undoStack.push(new Pair<>(angle -> rotateZAroundCube(new Cube(0, 0, side * smallCubesSize, smallCubesSize), angle), Math.toRadians(90.0 * -directionOfRotation)));
			}
		}
	}

	public void solve() {
		if(!rubiksCubeState.isAvailableForModifications()) {
			return;
		}
		rubiksCubeState = RubiksCubeState.SOLVING;
		int animationSteps = 20;
		double timeToTurn = 0.05;
		double index = 0;
		int stackSize = undoStack.size();
		double timeOffset = 0;
		while (!undoStack.empty()) {
			Pair<Consumer<Double>, Double> action = undoStack.pop();
			double timeWithSlowingOffset = (timeToTurn + 0.1 * Math.pow((index + 1)/stackSize, 3.5));
			for(int i = 0; i < animationSteps; i++) {
				animationManager.addAction(new TimedAction(() -> action.first.accept(action.second/animationSteps), timeOffset + i*timeWithSlowingOffset/animationSteps));
			}
			index++;
			timeOffset += timeWithSlowingOffset;
		}
		animationManager.addAction(new TimedAction(() -> rubiksCubeState = RubiksCubeState.IDLE, timeOffset));
	}

	private void animateRotatingAroundX(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rotateXAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
		undoStack.push(new Pair<>(angleToRotate -> rotateXAroundCube(cubeToRotateAround, angleToRotate), -angle));
	}

	private void animateRotatingAroundY(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rotateYAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
		undoStack.push(new Pair<>(angleToRotate -> rotateYAroundCube(cubeToRotateAround, angleToRotate), -angle));
	}

	private void animateRotatingAroundZ(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rotateZAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
		undoStack.push(new Pair<>(angleToRotate -> rotateZAroundCube(cubeToRotateAround, angleToRotate), -angle));
	}

	public void rotateXAroundCube(Cube cubeToRotateAround, double angle) {
		for (Cube cube : cubes) {
			if (Math.abs(cube.getPos().getX() - cubeToRotateAround.getPos().getX()) < RotationOperation.CUBE_ROTATION_TOLERANCE) {
				cube.rotateX(
					(angle),
					new Point3d(cubeToRotateAround.getPos().getX(), 0,  0)
				);
			}
		}
	}

	public void rotateYAroundCube(Cube cubeToRotateAround, double angle) {
		for (Cube cube : cubes) {
			if (Math.abs(cube.getPos().getY() - cubeToRotateAround.getPos().getY()) < RotationOperation.CUBE_ROTATION_TOLERANCE) {
				cube.rotateY(
					(angle),
					new Point3d(0, cubeToRotateAround.getPos().getY(),  0)
				);
			}
		}
	}

	public void rotateZAroundCube(Cube cubeToRotateAround, double angle) {
		for (Cube cube : cubes) {
			if (Math.abs(cube.getPos().getZ() - cubeToRotateAround.getPos().getZ()) < RotationOperation.CUBE_ROTATION_TOLERANCE) {
				cube.rotateZ(
					(angle),
					new Point3d(0, 0, cubeToRotateAround.getPos().getZ())
				);
			}
		}
	}

	@Override
	public void render(Canvas canvas) {
		polygons.sort(Comparator.comparingDouble(Polygon::getDistanceFromPlayer));
		selectedPolygon.ifPresent(polygon -> polygon.setSelected(true));
		for (Polygon polygon : polygons) {
			if (polygon.isPointingToPlayer()) {
				polygon.render(canvas);
			}
		}
	}
}
