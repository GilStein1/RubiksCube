package com.example.my3dproject;

import android.util.Pair;
import android.view.MotionEvent;
import com.example.my3dproject.drawables.Cube;
import com.example.my3dproject.drawables.Polygon;
import com.example.my3dproject.drawables.RubiksCube;
import com.example.my3dproject.math.MathUtil;
import com.example.my3dproject.math.Vec3D;
import com.example.my3dproject.math.geometry.Axis;
import com.example.my3dproject.math.geometry.Direction;
import com.example.my3dproject.math.geometry.DirectionCross;
import com.example.my3dproject.math.geometry.Point2d;
import com.example.my3dproject.math.geometry.Point3d;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class RubiksCubeManager implements UpdatableComponent{

	private final RubiksCube rubiksCube;
	private RubiksCubeState rubiksCubeState;
	private final GameController controller;
	private final TimedAnimationManager animationManager;
	private final ArrayBlockingQueue<Point2d> lastClicksQueue;
	private boolean isScreenPressed;
	private boolean hasNoticedActionUp;
	private double xRotationalVelocity;
	private double yRotationalVelocity;
	private double xRotation, yRotation;
	public final double rubiksCubeSize;
	private final double smallCubesSize;
	private final float rotationScale = 0.3f;
	private final float rotationalVelocityScale = 0.25f;
	private Point2d lastPointOfClick;
	private Optional<Polygon> selectedPolygon;
	private Optional<Polygon> selectedNotRotatedPolygon;
	private final Stack<Pair<Consumer<Double>, Double>> undoStack;
	private AtomicBoolean hasNoticedCubeSolved;

	public RubiksCubeManager(RubiksCube rubiksCube, GameController controller) {
		this.rubiksCube = rubiksCube;
		this.controller = controller;
		this.animationManager = controller.getAnimationManager();
		this.rubiksCubeState = RubiksCubeState.IDLE;
		this.rubiksCubeSize = rubiksCube.getRubiksCubeSize();
		this.smallCubesSize = rubiksCube.getSmallCubesSize();
		this.lastClicksQueue = new ArrayBlockingQueue<>(5);
		this.isScreenPressed = false;
		this.hasNoticedActionUp = false;
		this.xRotationalVelocity = 0;
		this.yRotationalVelocity = 0;
		this.xRotation = 0;
		this.yRotation = 0;
		this.lastPointOfClick = new Point2d(0, 0);
		this.selectedPolygon = Optional.empty();
		this.selectedNotRotatedPolygon = Optional.empty();
		this.undoStack = new Stack<>();
		this.hasNoticedCubeSolved = new AtomicBoolean(false);
		rubiksCube.rotate(0.001, 0.001, 0.001);
		updateRotationsFromDatabase();
	}

	private void updateRotationsFromDatabase() {
		List<RotationOperation> rotationOperations = controller.getSavedRotationOperations();
		for(RotationOperation rotationOperation : rotationOperations) {
			Cube cubeToRotateAround = new Cube(
				rotationOperation.getPointToRotateAround().getX(),
				rotationOperation.getPointToRotateAround().getY(),
				rotationOperation.getPointToRotateAround().getZ(), smallCubesSize
			);
			double angle = rotationOperation.getAngleOfRotation();
			switch (rotationOperation.getAxisOfRotation()) {
				case X: {
					rubiksCube.rotateXAroundCube(cubeToRotateAround, rotationOperation.getAngleOfRotation()
					);
					undoStack.push(new Pair<>(angleToRotate -> rubiksCube.rotateXAroundCube(cubeToRotateAround, angleToRotate), -angle));
					break;
				}
				case Y: {
					rubiksCube.rotateYAroundCube(cubeToRotateAround, rotationOperation.getAngleOfRotation()
					);
					undoStack.push(new Pair<>(angleToRotate -> rubiksCube.rotateYAroundCube(cubeToRotateAround, angleToRotate), -angle));
					break;
				}
				case Z: {
					rubiksCube.rotateZAroundCube(cubeToRotateAround, rotationOperation.getAngleOfRotation()
					);
					undoStack.push(new Pair<>(angleToRotate -> rubiksCube.rotateZAroundCube(cubeToRotateAround, angleToRotate), -angle));
					break;
				}
			}
		}
		if(rubiksCube.checkIfCubeIsSolved()) {
			hasNoticedCubeSolved.set(true);
			controller.stopTimer(true);
			controller.resetTimer();
		}
	}

	@Override
	public void update(double deltaTime, Point2d pointOfClick, int event) {
		if(event == MotionEvent.ACTION_DOWN || event == MotionEvent.ACTION_MOVE) {
			if(lastClicksQueue.remainingCapacity() == 0) {
				lastClicksQueue.remove();
			}
			lastClicksQueue.add(pointOfClick.times(1/getScreenSizeRatio()));
		}
		if (lastClicksQueue.remainingCapacity() == 0) {
			updateByClickInput(pointOfClick, event);
		}

		double scaledTime = deltaTime * 100;

		if (!isScreenPressed) {
			updateRotationsFromDecreasingVelocity(scaledTime);
		}

		rubiksCube.rotate(Math.toRadians(xRotation), Math.toRadians(yRotation), 0);
		for (Polygon polygon : rubiksCube.getAllDrawnPolygons()) {
			polygon.update(deltaTime, pointOfClick.times(getScreenSizeRatio()), event);
		}
		for (Polygon polygon : rubiksCube.getAllNotRotatedPolygons()) {
			polygon.update(deltaTime, pointOfClick.times(getScreenSizeRatio()), event);
		}
		lastPointOfClick = pointOfClick.times(1/getScreenSizeRatio());
		boolean isCubeSolved = rubiksCube.checkIfCubeIsSolved();
		if(isCubeSolved && !hasNoticedCubeSolved.get()) {
			hasNoticedCubeSolved.set(true);
			controller.noticedCubeIsSolved();
			undoStack.clear();
			controller.clearAllSavedRotations();
			controller.stopTimer(true);
		}
		if(!isCubeSolved && hasNoticedCubeSolved.get()) {
			hasNoticedCubeSolved.set(false);
		}
		selectedPolygon.ifPresent(polygon -> polygon.setSelected(true));
	}

	private void updateByClickInput(Point2d pointOfCLick, int event) {
		switch (event) {
			case MotionEvent.ACTION_DOWN:
				isScreenPressed = true;
				if (!selectedPolygon.isPresent()) {
					selectedPolygon = searchForClickedPolygon(rubiksCube.getAllDrawnPolygons(), pointOfCLick);
					selectedNotRotatedPolygon = searchForClickedPolygon(rubiksCube.getAllNotRotatedPolygons(), pointOfCLick);
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

	private void detectCubeRotationByPlayer(Point2d lastPointOfClick) {
		rubiksCubeState = RubiksCubeState.ROTATED_BY_PLAYER;
		Cube selectedCube = selectedPolygon.get().getParentCube();
		Polygon nonRotatedPolygon = selectedNotRotatedPolygon.get().getParentCube().getPolygonFromDrawPolygon(selectedNotRotatedPolygon.get());
		DirectionCross directionCross = new DirectionCross();
		directionCross.rotate(rubiksCube.getCurrentRotation());
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

	private void updateRotationsFromDecreasingVelocity(double time) {
		double absolutValueOfSumOfVelocities = Math.abs(xRotationalVelocity) + Math.abs(yRotationalVelocity);
		double xVelocityRatio = zeroIfNaN(Math.abs(xRotationalVelocity) / absolutValueOfSumOfVelocities);
		double yVelocityRatio = zeroIfNaN(Math.abs(yRotationalVelocity) / absolutValueOfSumOfVelocities);
		xRotationalVelocity -= getSignOf(xRotationalVelocity) * Math.min(Math.abs(xRotationalVelocity), time * xVelocityRatio);
		yRotationalVelocity -= getSignOf(yRotationalVelocity) * Math.min(Math.abs(yRotationalVelocity), time * yVelocityRatio);
		xRotation = xRotationalVelocity;
		yRotation = yRotationalVelocity;
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

	private double getScreenSizeRatio() {
		return Math.min(rubiksCube.screenWidth, rubiksCube.screenHeight) / Constants.IDEAL_SCREEN_WIDTH;
	}

	private static int getSignOf(double value) {
		return (value > 0 ? 1 : -1);
	}

	private static double zeroIfNaN(double value) {
		return Double.isNaN(value) ? 0 : value;
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
					animationManager.addAction(new TimedAction(() -> rubiksCube.rotateXAroundCube(
						new Cube(side * smallCubesSize, 0, 0, smallCubesSize), Math.toRadians(90.0/animationSteps*directionOfRotation)
					),j*timeToTurn/animationSteps + i*timeToTurn));
				}
				animationManager.addAction(new TimedAction(
					() -> controller.saveAnotherRotation(new RotationOperation(side * smallCubesSize, 0, 0, Axis.X, Math.toRadians(90.0*directionOfRotation))),
					i*timeToTurn*0
				));
				undoStack.push(new Pair<>(angle -> rubiksCube.rotateXAroundCube(new Cube(side * smallCubesSize, 0, 0, smallCubesSize), angle), Math.toRadians(90.0 * -directionOfRotation)));
			}
			else if(axis == 1) {
				for(int j = 0; j < animationSteps; j++) {
					animationManager.addAction(new TimedAction(() -> rubiksCube.rotateYAroundCube(
						new Cube(0, side * smallCubesSize, 0, smallCubesSize), Math.toRadians(90.0/animationSteps*directionOfRotation)
					),j*timeToTurn/animationSteps + i*timeToTurn));
				}
				animationManager.addAction(new TimedAction(
					() -> controller.saveAnotherRotation(new RotationOperation(0, side * smallCubesSize, 0, Axis.Y, Math.toRadians(90.0*directionOfRotation))),
					i*timeToTurn*0

				));
				undoStack.push(new Pair<>(angle -> rubiksCube.rotateYAroundCube(new Cube(0, side * smallCubesSize, 0, smallCubesSize), angle), Math.toRadians(90.0 * -directionOfRotation)));
			}
			else {
				for(int j = 0; j < animationSteps; j++) {
					animationManager.addAction(new TimedAction(() -> rubiksCube.rotateZAroundCube(
						new Cube(0, 0, side * smallCubesSize, smallCubesSize), Math.toRadians(90.0/animationSteps*directionOfRotation)
					),j*timeToTurn/animationSteps + i*timeToTurn));
				}
				animationManager.addAction(new TimedAction(
					() -> controller.saveAnotherRotation(new RotationOperation(0.0, 0.0, side * smallCubesSize, Axis.Z, Math.toRadians(90.0*directionOfRotation))),
					i*timeToTurn*0
				));
				undoStack.push(new Pair<>(angle -> rubiksCube.rotateZAroundCube(new Cube(0, 0, side * smallCubesSize, smallCubesSize), angle), Math.toRadians(90.0 * -directionOfRotation)));
			}
		}
	}

	public void solve() {
		if(!rubiksCubeState.isAvailableForModifications()) {
			return;
		}
		rubiksCubeState = RubiksCubeState.SOLVING;
		controller.stopTimer(true);
		int animationSteps = 10;
		double timeToTurn = 0.05;
		double index = 0;
		int stackSize = undoStack.size();
		double timeOffset = 0;
		while (!undoStack.empty()) {
			Pair<Consumer<Double>, Double> action = undoStack.pop();
			double timeWithSlowingOffset = (timeToTurn + 0.1 * Math.pow((index + 1)/stackSize, 3.5));
			for(int i = 0; i < animationSteps; i++) {
				animationManager.addAction(new TimedAction(() -> {
					hasNoticedCubeSolved.set(true);
					action.first.accept(action.second / animationSteps);
				}, timeOffset + i*timeWithSlowingOffset/animationSteps));
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
			animationManager.addAction(new TimedAction(() -> rubiksCube.rotateXAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
		undoStack.push(new Pair<>(angleToRotate -> rubiksCube.rotateXAroundCube(cubeToRotateAround, angleToRotate), -angle));
	}

	private void animateRotatingAroundY(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		controller.saveAnotherRotation(new RotationOperation(cubeToRotateAround.getPos(), Axis.Y, angle));
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rubiksCube.rotateYAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
		undoStack.push(new Pair<>(angleToRotate -> rubiksCube.rotateYAroundCube(cubeToRotateAround, angleToRotate), -angle));
	}

	private void animateRotatingAroundZ(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		controller.saveAnotherRotation(new RotationOperation(cubeToRotateAround.getPos(), Axis.Z, angle));
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rubiksCube.rotateZAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
		undoStack.push(new Pair<>(angleToRotate -> rubiksCube.rotateZAroundCube(cubeToRotateAround, angleToRotate), -angle));
	}

}
