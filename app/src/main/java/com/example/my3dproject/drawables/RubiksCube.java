package com.example.my3dproject.drawables;

import android.graphics.Canvas;
import android.util.Pair;
import android.view.MotionEvent;

import com.example.my3dproject.Account;
import com.example.my3dproject.Controller;
import com.example.my3dproject.RubiksCubeState;
import com.example.my3dproject.ScreenGeometryManager;
import com.example.my3dproject.TimedAction;
import com.example.my3dproject.Constants;
import com.example.my3dproject.RotationOperation;
import com.example.my3dproject.TimedAnimationManager;
import com.example.my3dproject.math.MathUtil;
import com.example.my3dproject.math.Vec3D;
import com.example.my3dproject.math.geometry.Axis;
import com.example.my3dproject.math.geometry.Direction;
import com.example.my3dproject.math.geometry.DirectionCross;
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
import java.util.function.Consumer;

public class RubiksCube extends Drawable {

	private final ArrayBlockingQueue<Point2d> lastClicksQueue;
	private final Controller controller;
	private final TimedAnimationManager animationManager;
	private final double x, y, z;
	private final List<Cube> cubes;
	private final List<Cube> cubesThatDoNotRotate;
	private final List<Point> points3d;
	private final List<Point> notRotatedPoints3d;
	private final List<Point> points3dToDraw;
	private final List<Point> notRotatedPointsToDraw;
	private final List<Polygon> drawnPolygons;
	private final List<Polygon> notRotatedPolygons;
	private Optional<Polygon> selectedPolygon;
	private Optional<Polygon> selectedNotRotatedPolygon;
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
	private final Stack<Pair<Consumer<Double>, Double>> undoStack;
	private final double rubiksCubeSize;
	private final double smallCubesSize;

	public RubiksCube(double x, double y, double z, double size, Controller controller) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.rubiksCubeSize = size;
		this.controller = controller;
		this.animationManager = controller.getAnimationManager();
		this.points3d = new ArrayList<>();
		this.notRotatedPoints3d = new ArrayList<>();
		this.points3dToDraw = new ArrayList<>();
		this.notRotatedPointsToDraw = new ArrayList<>();
		this.drawnPolygons = new ArrayList<>();
		this.notRotatedPolygons = new ArrayList<>();
		this.selectedPolygon = Optional.empty();
		this.selectedNotRotatedPolygon = Optional.empty();
		this.cubes = new ArrayList<>();
		this.cubesThatDoNotRotate = new ArrayList<>();
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

		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x - sizeOfSmallCubes * 0, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z - sizeOfSmallCubes * 0, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y - sizeOfSmallCubes * 0, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));
		cubesThatDoNotRotate.add(new Cube(x + sizeOfSmallCubes * 1, y + sizeOfSmallCubes * 1, z + sizeOfSmallCubes * 1, sizeOfSmallCubes));

		for (Cube cube : cubes) {
			points3d.addAll(Arrays.asList(cube.getAll3dPoints()));
			points3dToDraw.addAll(Arrays.asList(cube.getAll3dPointsToDraw()));
			drawnPolygons.addAll(cube.getAllPolygons());
		}

		for(Cube cube : cubesThatDoNotRotate) {
			notRotatedPolygons.addAll(cube.getAllPolygons());
			notRotatedPoints3d.addAll(Arrays.asList(cube.getAll3dPoints()));
			notRotatedPointsToDraw.addAll(Arrays.asList(cube.getAll3dPointsToDraw()));
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
		this.undoStack = new Stack<>();
		rotate(0.001, 0.001, 0.001);
		controller.addTaskToDoWhenAccountIsLogged(this::updateRotationsFromDatabase);
	}

	private void updateRotationsFromDatabase(Account account) {
		List<RotationOperation> rotationOperations = account.getRotationOperationList();
		for(RotationOperation rotationOperation : rotationOperations) {
			Cube cubeToRotateAround = new Cube(
				rotationOperation.getPointToRotateAround().getX(),
				rotationOperation.getPointToRotateAround().getY(),
				rotationOperation.getPointToRotateAround().getZ(), smallCubesSize
			);
			double angle = rotationOperation.getAngleOfRotation();
			switch (rotationOperation.getAxisOfRotation()) {
				case X: {
					rotateXAroundCube(cubeToRotateAround, rotationOperation.getAngleOfRotation()
					);
					undoStack.push(new Pair<>(angleToRotate -> rotateXAroundCube(cubeToRotateAround, angleToRotate), -angle));
					break;
				}
				case Y: {
					rotateYAroundCube(cubeToRotateAround, rotationOperation.getAngleOfRotation()
					);
					undoStack.push(new Pair<>(angleToRotate -> rotateYAroundCube(cubeToRotateAround, angleToRotate), -angle));
					break;
				}
				case Z: {
					rotateZAroundCube(cubeToRotateAround, rotationOperation.getAngleOfRotation()
					);
					undoStack.push(new Pair<>(angleToRotate -> rotateZAroundCube(cubeToRotateAround, angleToRotate), -angle));
					break;
				}
			}
		}
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
		for (int i = 0; i < notRotatedPoints3d.size(); i++) {
			Point p = notRotatedPoints3d.get(i);
			double tx = p.getX() - x;
			double ty = p.getY() - y;
			double tz = p.getZ() - z;
			double newX = rotationMatrix[0][0] * tx + rotationMatrix[0][1] * ty + rotationMatrix[0][2] * tz;
			double newY = rotationMatrix[1][0] * tx + rotationMatrix[1][1] * ty + rotationMatrix[1][2] * tz;
			double newZ = rotationMatrix[2][0] * tx + rotationMatrix[2][1] * ty + rotationMatrix[2][2] * tz;
			notRotatedPointsToDraw.get(i).moveTo(newX + x, newY + y, newZ + z);
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
		for (Cube cube : cubesThatDoNotRotate) {
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
		if(event == MotionEvent.ACTION_DOWN || event == MotionEvent.ACTION_MOVE) {
			if(lastClicksQueue.remainingCapacity() == 0) {
				lastClicksQueue.remove();
			}
			lastClicksQueue.add(pointOfCLick.times(1/getScreenSizeRatio()));
		}
		if (lastClicksQueue.remainingCapacity() == 0) {
			updateByClickInput(pointOfCLick, event);
		}

		double scaledTime = deltaTime * 100;

		double distance = (lastClicksQueue.remainingCapacity() == 0)? Math.sqrt(Math.pow((pointOfCLick.times(1/getScreenSizeRatio()).getX() - lastClicksQueue.peek().getX()), 2)
			+ Math.pow((pointOfCLick.times(1/getScreenSizeRatio()).getY() - lastClicksQueue.peek().getY()), 2)) : 0;

		if (!isScreenPressed) {
			updateRotationsFromDecreasingVelocity(scaledTime);
		}

		rotate(Math.toRadians(xRotation), Math.toRadians(yRotation), 0);
		updateAllDots(deltaTime, pointOfCLick.times(getScreenSizeRatio()), event);
		for (Cube cube : cubes) {
			cube.update(deltaTime, pointOfCLick.times(getScreenSizeRatio()), event);
		}
		for (Cube cube : cubesThatDoNotRotate) {
			cube.update(deltaTime, pointOfCLick.times(getScreenSizeRatio()), event);
		}
		lastPointOfClick = pointOfCLick.times(1/getScreenSizeRatio());
	}

	private void detectCubeRotationByPlayer(Point2d lastPointOfClick) {
		rubiksCubeState = RubiksCubeState.ROTATED_BY_PLAYER;
		Cube selectedCube = selectedPolygon.get().getParentCube();
		Polygon nonRotatedPolygon = selectedNotRotatedPolygon.get().getParentCube().getPolygonFromDrawPolygon(selectedNotRotatedPolygon.get());
		DirectionCross directionCross = new DirectionCross();
		directionCross.rotate(currentRotation);
		Vec3D swipeVector = new Vec3D(
			lastPointOfClick.times(1/getScreenSizeRatio()).getX() - lastClicksQueue.peek().getX(),
			(lastPointOfClick.times(1/getScreenSizeRatio()).getY() - lastClicksQueue.peek().getY()),
			0
		).normalize();

		Vec3D vecRight = directionCross.getDirectionVector(Direction.RIGHT);
		Vec3D vecLeft = directionCross.getDirectionVector(Direction.LEFT);
		Vec3D vecUp = directionCross.getDirectionVector(Direction.UP);
		Vec3D vecDown = directionCross.getDirectionVector(Direction.DOWN);
		Vec3D vecForward = directionCross.getDirectionVector(Direction.FORWARD);
		Vec3D vecBackward =	directionCross.getDirectionVector(Direction.BACKWARD);

		Direction directionOfNormalOfPolygon = new DirectionCross().getMostSimilarDirection(nonRotatedPolygon.updateNormalVector());

		Vec3D[] directionVectors = {
			vecRight, vecLeft, vecUp, vecDown, vecForward, vecBackward
		};
		translateVectorsToProjections(directionVectors);
		if(directionOfNormalOfPolygon.getAxis() == Axis.X) {
			directionVectors = new Vec3D[] {
				vecUp, vecDown, vecForward, vecBackward
			};
		}
		else if(directionOfNormalOfPolygon.getAxis() == Axis.Y) {
			directionVectors = new Vec3D[] {
				vecRight, vecLeft, vecForward, vecBackward
			};
		}
		else {
			directionVectors = new Vec3D[] {
				vecRight, vecLeft, vecUp, vecDown
			};
		}
		MathUtil.sortVectorsByMostSimilarity(swipeVector, directionVectors);
		Vec3D mostSimilarVec = directionVectors[directionVectors.length - 1];
		if(mostSimilarVec == vecRight) {
			if(directionOfNormalOfPolygon.getAxis() == Axis.Z) {
				animateRotatingAroundY(25, 0.12, -(directionOfNormalOfPolygon.getSignInAxis()) * Math.toRadians(90), selectedCube);
			}
			else if(directionOfNormalOfPolygon.getAxis() == Axis.Y) {
				animateRotatingAroundZ(25, 0.12, (directionOfNormalOfPolygon.getSignInAxis()) * Math.toRadians(90), selectedCube);
			}
		}
		else if(mostSimilarVec == vecLeft) {
			if(directionOfNormalOfPolygon.getAxis() == Axis.Z) {
				animateRotatingAroundY(25, 0.12, -(directionOfNormalOfPolygon.getSignInAxis()) * -Math.toRadians(90), selectedCube);
			}
			else if(directionOfNormalOfPolygon.getAxis() == Axis.Y) {
				animateRotatingAroundZ(25, 0.12, (directionOfNormalOfPolygon.getSignInAxis()) * -Math.toRadians(90), selectedCube);
			}
		}
		else if(mostSimilarVec == vecUp) {
			if(directionOfNormalOfPolygon.getAxis() == Axis.Z) {
				animateRotatingAroundX(25, 0.12, (directionOfNormalOfPolygon.getSignInAxis()) * Math.toRadians(90), selectedCube);
			}
			else if(directionOfNormalOfPolygon.getAxis() == Axis.X) {
				animateRotatingAroundZ(25, 0.12, -(directionOfNormalOfPolygon.getSignInAxis()) * Math.toRadians(90), selectedCube);
			}
		}
		else if(mostSimilarVec == vecDown) {
			if(directionOfNormalOfPolygon.getAxis() == Axis.Z) {
				animateRotatingAroundX(25, 0.12, (directionOfNormalOfPolygon.getSignInAxis()) * -Math.toRadians(90), selectedCube);
			}
			else if(directionOfNormalOfPolygon.getAxis() == Axis.X) {
				animateRotatingAroundZ(25, 0.12, -(directionOfNormalOfPolygon.getSignInAxis()) * -Math.toRadians(90), selectedCube);
			}
		}
		else if(mostSimilarVec == vecForward) {
			if(directionOfNormalOfPolygon.getAxis() == Axis.X) {
				animateRotatingAroundY(25, 0.12, (directionOfNormalOfPolygon.getSignInAxis()) * Math.toRadians(90), selectedCube);
			}
			else if(directionOfNormalOfPolygon.getAxis() == Axis.Y) {
				animateRotatingAroundX(25, 0.12, -(directionOfNormalOfPolygon.getSignInAxis()) * Math.toRadians(90), selectedCube);
			}
		}
		else if(mostSimilarVec == vecBackward){
			if(directionOfNormalOfPolygon.getAxis() == Axis.X) {
				animateRotatingAroundY(25, 0.12, (directionOfNormalOfPolygon.getSignInAxis()) * -Math.toRadians(90), selectedCube);
			}
			else if(directionOfNormalOfPolygon.getAxis() == Axis.Y) {
				animateRotatingAroundX(25, 0.12, -(directionOfNormalOfPolygon.getSignInAxis()) * -Math.toRadians(90), selectedCube);
			}
		}
		animationManager.addAction(new TimedAction(() -> rubiksCubeState = RubiksCubeState.IDLE, 0.5));
	}

	private void translateVectorToProjection(Vec3D vec3D) {
		vec3D.normalize();
		Point3d p1 = new Point3d(0, 0, 0);
		Point3d p2 = new Point3d(vec3D.getX() * rubiksCubeSize*2, vec3D.getY() * rubiksCubeSize*2, vec3D.getZ() * rubiksCubeSize*2);
		p1 = ScreenGeometryManager.getInstance().getProjectionTranslatedPoint3d(p1, Constants.FOCAL_LENGTH);
		p2 = ScreenGeometryManager.getInstance().getProjectionTranslatedPoint3d(p2, Constants.FOCAL_LENGTH);
		Vec3D finalVec = Vec3D.fromDifferenceInPos(p2, p1).normalize();
		vec3D.setX(finalVec.getX());
		vec3D.setY(finalVec.getY());
		vec3D.setZ(0);
	}

	private void translateVectorsToProjections(Vec3D... vectors) {
		for (Vec3D vector : vectors) {
			translateVectorToProjection(vector);
		}
	}

	private void updateAllDots(double deltaTime, Point2d pointOfCLick, int event) {
		for (int i = 0; i < points3dToDraw.size(); i++) {
			points3dToDraw.get(i).update(deltaTime, pointOfCLick, event);
		}
		for (int i = 0; i < notRotatedPointsToDraw.size(); i++) {
			notRotatedPointsToDraw.get(i).update(deltaTime, pointOfCLick, event);
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
					selectedPolygon = searchForClickedPolygon(drawnPolygons, pointOfCLick);
					selectedNotRotatedPolygon = searchForClickedPolygon(notRotatedPolygons, pointOfCLick);
				}
				break;
			case MotionEvent.ACTION_UP:
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
				}
				if (rubiksCubeState.isAvailableForModifications() && selectedPolygon.isPresent() && lastClicksQueue.remainingCapacity() == 0) {
					detectCubeRotationByPlayer(pointOfCLick);
				}
				selectedPolygon = Optional.empty();
				selectedNotRotatedPolygon = Optional.empty();
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

	private Optional<Polygon> searchForClickedPolygon(List<Polygon> polygons, Point2d pointOfClick) {
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
		xRotation = (lastPointOfClick.getY() - pointOfCLick.times(1/getScreenSizeRatio()).getY()) * rotationScale;
		yRotation = (lastPointOfClick.getX() - pointOfCLick.times(1/getScreenSizeRatio()).getX()) * rotationScale;
	}

	private static int getSignOf(double value) {
		return (value > 0 ? 1 : -1);
	}

	public void shuffle() {
		if(!rubiksCubeState.isAvailableForModifications()) {
			return;
		}
		rubiksCubeState = RubiksCubeState.SHUFFLE;
		controller.stopTimer(true);
		int amountOfTurns = 50;
		int animationSteps = 25;
		double timeToTurn = 0.12;

		int lastAxis = (int)(Math.random()*3);
		int lastSide = (int)(Math.random()*3) - 1;
		int direction = 1;
		animationManager.addAction(new TimedAction(() -> {
			rubiksCubeState = RubiksCubeState.IDLE;
			controller.stopTimer(false);
		}, timeToTurn * amountOfTurns));
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
				animationManager.addAction(new TimedAction(
					() -> controller.saveAnotherRotation(new RotationOperation(side * smallCubesSize, 0, 0, Axis.X, Math.toRadians(90.0*directionOfRotation))),
					i*timeToTurn*0
				));
				undoStack.push(new Pair<>(angle -> rotateXAroundCube(new Cube(side * smallCubesSize, 0, 0, smallCubesSize), angle), Math.toRadians(90.0 * -directionOfRotation)));
			}
			else if(axis == 1) {
				for(int j = 0; j < animationSteps; j++) {
					animationManager.addAction(new TimedAction(() -> rotateYAroundCube(
						new Cube(0, side * smallCubesSize, 0, smallCubesSize), Math.toRadians(90.0/animationSteps*directionOfRotation)
					),j*timeToTurn/animationSteps + i*timeToTurn));
				}
				animationManager.addAction(new TimedAction(
					() -> controller.saveAnotherRotation(new RotationOperation(0, side * smallCubesSize, 0, Axis.Y, Math.toRadians(90.0*directionOfRotation))),
					i*timeToTurn*0

				));
				undoStack.push(new Pair<>(angle -> rotateYAroundCube(new Cube(0, side * smallCubesSize, 0, smallCubesSize), angle), Math.toRadians(90.0 * -directionOfRotation)));
			}
			else {
				for(int j = 0; j < animationSteps; j++) {
					animationManager.addAction(new TimedAction(() -> rotateZAroundCube(
						new Cube(0, 0, side * smallCubesSize, smallCubesSize), Math.toRadians(90.0/animationSteps*directionOfRotation)
					),j*timeToTurn/animationSteps + i*timeToTurn));
				}
				animationManager.addAction(new TimedAction(
					() -> controller.saveAnotherRotation(new RotationOperation(0.0, 0.0, side * smallCubesSize, Axis.Z, Math.toRadians(90.0*directionOfRotation))),
					i*timeToTurn*0
				));
				undoStack.push(new Pair<>(angle -> rotateZAroundCube(new Cube(0, 0, side * smallCubesSize, smallCubesSize), angle), Math.toRadians(90.0 * -directionOfRotation)));
			}
		}
	}

	public void solve() {
		if(!rubiksCubeState.isAvailableForModifications()) {
			return;
		}
		rubiksCubeState = RubiksCubeState.SOLVING;
		controller.stopTimer(true);
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
		animationManager.addAction(new TimedAction(() -> {
			rubiksCubeState = RubiksCubeState.IDLE;
			controller.resetTimer();
			controller.clearAllSavedRotations();
		}, timeOffset));
	}

	private void animateRotatingAroundX(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		controller.saveAnotherRotation(new RotationOperation(cubeToRotateAround.getPos(), Axis.X, angle));
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rotateXAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
		undoStack.push(new Pair<>(angleToRotate -> rotateXAroundCube(cubeToRotateAround, angleToRotate), -angle));
	}

	private void animateRotatingAroundY(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		controller.saveAnotherRotation(new RotationOperation(cubeToRotateAround.getPos(), Axis.Y, angle));
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rotateYAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
		undoStack.push(new Pair<>(angleToRotate -> rotateYAroundCube(cubeToRotateAround, angleToRotate), -angle));
	}

	private void animateRotatingAroundZ(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		controller.saveAnotherRotation(new RotationOperation(cubeToRotateAround.getPos(), Axis.Z, angle));
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

	public void setState(RubiksCubeState state) {
		this.rubiksCubeState = state;
	}

	public RubiksCubeState getRubiksCubeState() {
		return rubiksCubeState;
	}

	@Override
	public void render(Canvas canvas, boolean isDarkMode) {
		drawnPolygons.sort(Comparator.comparingDouble(Polygon::getDistanceFromPlayer));
		notRotatedPolygons.sort(Comparator.comparingDouble(Polygon::getDistanceFromPlayer));
		selectedPolygon.ifPresent(polygon -> polygon.setSelected(true));
		for (Polygon polygon : drawnPolygons) {
			if (polygon.isPointingToPlayer()) {
				polygon.render(canvas, isDarkMode);
			}
		}
		for (Polygon polygon : notRotatedPolygons) {
			if (polygon.isPointingToPlayer()) {
				polygon.setSelected(false);
//				polygon.render(canvas);
			}
		}
//		DirectionCross directionCross = new DirectionCross();
//		directionCross.rotate(currentRotation);
//		Point p = new Point(
//			directionCross.getDirectionVector(Direction.UP).getX() * 40,
//			directionCross.getDirectionVector(Direction.UP).getY() * 40,
//			directionCross.getDirectionVector(Direction.UP).getZ() * 40
//		);
//		p.setColor(Color.RED);
//		p.update(0, new Point2d(0, 0), 0);
//		p.render(canvas);
//
//		p = new Point(
//			directionCross.getDirectionVector(Direction.RIGHT).getX() * 40,
//			directionCross.getDirectionVector(Direction.RIGHT).getY() * 40,
//			directionCross.getDirectionVector(Direction.RIGHT).getZ() * 40
//		);
//		p.setColor(Color.YELLOW);
//		p.update(0, new Point2d(0, 0), 0);
//		p.render(canvas);
//
//		p = new Point(
//			directionCross.getDirectionVector(Direction.FORWARD).getX() * 40,
//			directionCross.getDirectionVector(Direction.FORWARD).getY() * 40,
//			directionCross.getDirectionVector(Direction.FORWARD).getZ() * 40
//		);
//		p.setColor(Color.GREEN);
//		p.update(0, new Point2d(0, 0), 0);
//		p.render(canvas);

	}
}
