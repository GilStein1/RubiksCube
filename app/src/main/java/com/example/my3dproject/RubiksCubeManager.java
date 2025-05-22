package com.example.my3dproject;

import android.util.Pair;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.my3dproject.drawables.Cube;
import com.example.my3dproject.drawables.Polygon;
import com.example.my3dproject.drawables.RubiksCube;
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

/**
 * Main manager class for handling Rubik's Cube interactions, animations, and state management.
 * This class manages user input, cube rotations, solving logic, and visual updates.
 * Implements UpdatableComponent to receive regular update calls from the game loop.
 */
public class RubiksCubeManager implements UpdatableComponent{

	// The 3D Rubik's cube
	private final RubiksCube rubiksCube;
	// Current state of the cube
	private RubiksCubeState rubiksCubeState;
	// The Controller
	private final GameController controller;
	// The animation manager
	private final TimedAnimationManager animationManager;
	// Queue to store recent touch points for gesture detection
	private final ArrayBlockingQueue<Point2d> lastClicksQueue;
	// Flag indicating if screen is currently being touched
	private boolean isScreenPressed;
	// Flag to handle ACTION_UP event only once per touch sequence
	private boolean hasNoticedActionUp;
	// Angular velocity on the X-axis
	private double xRotationalVelocity;
	// Angular velocity on the Y-axis
	private double yRotationalVelocity;
	// Current rotation angles for X and Y axes
	private double xRotation, yRotation;
	// size of the Rubik's cube
	private final double rubiksCubeSize;
	// Size of the individual small cubes
	private final double smallCubesSize;
	// Scaling factor for touch-to-rotation conversion
	private final float rotationScale = 0.3f;
	// Scaling factor for rotational velocity
	private final float rotationalVelocityScale = 0.25f;
	// Last point that was clicked
	private Point2d lastPointOfClick;
	// Currently selected polygon
	private Optional<Polygon> selectedPolygon;
	// Selected polygon in non-rotated coordinate system
	private Optional<Polygon> selectedNotRotatedPolygon;
	// Stack storing all player operations
	private final Stack<Pair<Consumer<Double>, Double>> undoStack;
	// Flag to handle solved cube events only once per solve
	private AtomicBoolean hasNoticedCubeSolved;

	/**
	 * Constructor initializes the Rubik's Cube Manager with all necessary components.
	 * Sets up initial state, loads saved rotations from database, and performs initial cube rotation.
	 *
	 * @param rubiksCube The 3D Rubik's cube
	 * @param controller The main game controller
	 */
	public RubiksCubeManager(RubiksCube rubiksCube, GameController controller) {
		this.rubiksCube = rubiksCube;
		this.controller = controller;
		this.animationManager = controller.getAnimationManager();
		this.rubiksCubeState = RubiksCubeState.IDLE;
		this.rubiksCubeSize = rubiksCube.getRubiksCubeSize();
		this.smallCubesSize = rubiksCube.getSmallCubesSize();
		this.lastClicksQueue = new ArrayBlockingQueue<>(5); // Store last 5 touch points
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

		// Small initial rotation to ensure proper 3D rendering setup
		rubiksCube.rotate(0.001, 0.001, 0.001);

		// Load and apply any previously saved rotations from firebase storage
		retrieveRotationsFromDatabase();
	}

	/**
	 * Retrieves saved rotation operations from the database and applies them to restore
	 * the cube to its previous state.
	 */
	private void retrieveRotationsFromDatabase() {
		List<RotationOperation> rotationOperations = controller.getSavedRotationOperations();

		// Apply each saved rotation operation
		for(RotationOperation rotationOperation : rotationOperations) {
			// Create a cube representing the axis of rotation
			Cube cubeToRotateAround = new Cube(
				rotationOperation.getPointToRotateAround().getX(),
				rotationOperation.getPointToRotateAround().getY(),
				rotationOperation.getPointToRotateAround().getZ(), smallCubesSize
			);
			double angle = rotationOperation.getAngleOfRotation();

			// Apply rotation based on the axis and add inverse operation to undo stack
			switch (rotationOperation.getAxisOfRotation()) {
				case X: {
					rubiksCube.rotateXAroundCube(cubeToRotateAround, rotationOperation.getAngleOfRotation());
					undoStack.push(new Pair<>(angleToRotate -> rubiksCube.rotateXAroundCube(cubeToRotateAround, angleToRotate), -angle));
					break;
				}
				case Y: {
					rubiksCube.rotateYAroundCube(cubeToRotateAround, rotationOperation.getAngleOfRotation());
					undoStack.push(new Pair<>(angleToRotate -> rubiksCube.rotateYAroundCube(cubeToRotateAround, angleToRotate), -angle));
					break;
				}
				case Z: {
					rubiksCube.rotateZAroundCube(cubeToRotateAround, rotationOperation.getAngleOfRotation());
					undoStack.push(new Pair<>(angleToRotate -> rubiksCube.rotateZAroundCube(cubeToRotateAround, angleToRotate), -angle));
					break;
				}
			}
		}

		// Check if cube is already solved after loading rotations
		if(rubiksCube.checkIfCubeIsSolved()) {
			hasNoticedCubeSolved.set(true);
			controller.stopTimer(true);
			controller.resetTimer();
		}
	}

	/**
	 * Main update method called every frame. Handles input processing, rotation updates,
	 * polygon updates, and cube solved state detection.
	 *
	 * @param deltaTime Time elapsed since last update
	 * @param pointOfClick Current touch point coordinates
	 * @param event Current motion event type (ACTION_DOWN, ACTION_MOVE, ACTION_UP)
	 */
	@Override
	public void update(double deltaTime, Point2d pointOfClick, int event) {
		// Store recent touch points for gesture detection (scaled for screen size)
		if(event == MotionEvent.ACTION_DOWN || event == MotionEvent.ACTION_MOVE) {
			if(lastClicksQueue.remainingCapacity() == 0) {
				lastClicksQueue.remove(); // Remove oldest point if queue is full
			}
			lastClicksQueue.add(pointOfClick.times(1/getScreenSizeRatio()));
		}

		// Only process input when we have enough touch history
		if (lastClicksQueue.remainingCapacity() == 0) {
			updateByClickInput(pointOfClick, event);
		}

		// Scale delta time for appropriate rotation speed
		double scaledTime = deltaTime * 100;

		// Apply inertial rotation when screen is not being touched
		if (!isScreenPressed) {
			updateRotationsFromDecreasingVelocity(scaledTime);
		}

		// Apply current rotation to the cube
		rubiksCube.rotate(Math.toRadians(xRotation), Math.toRadians(yRotation), 0);

		// Update all visible polygons (rotated coordinate system)
		for (Polygon polygon : rubiksCube.getAllDrawnPolygons()) {
			polygon.update(deltaTime, pointOfClick.times(getScreenSizeRatio()), event);
		}

		// Update all reference polygons in original coordinate system
		for (Polygon polygon : rubiksCube.getAllNotRotatedPolygons()) {
			polygon.update(deltaTime, pointOfClick.times(getScreenSizeRatio()), event);
		}

		// Update last click point for next frame
		lastPointOfClick = pointOfClick.times(1/getScreenSizeRatio());

		// Check for cube solved state
		boolean isCubeSolved = rubiksCube.checkIfCubeIsSolved();
		if(isCubeSolved && !hasNoticedCubeSolved.get()) {
			// Cube just became solved
			hasNoticedCubeSolved.set(true);
			controller.noticedCubeIsSolved();
			undoStack.clear(); // Clear undo history when solved
			controller.clearAllSavedRotations();
			controller.stopTimer(true);
			// Show message
			controller.post(() -> Toast.makeText(controller.getContext(), "The cube is solved!", Toast.LENGTH_SHORT).show());
		}
		if(!isCubeSolved && hasNoticedCubeSolved.get()) {
			// Cube is no longer solved
			hasNoticedCubeSolved.set(false);
		}

		// Highlight currently selected polygon
		selectedPolygon.ifPresent(polygon -> polygon.setSelected(true));
	}

	/**
	 * Processes touch input events
	 * @param pointOfCLick Current touch point coordinates
	 * @param event Motion event type
	 */
	private void updateByClickInput(Point2d pointOfCLick, int event) {
		switch (event) {
			case MotionEvent.ACTION_DOWN:
				isScreenPressed = true;
				// Try to select a polygon if none is currently selected
				if (!selectedPolygon.isPresent()) {
					selectedPolygon = searchForClickedPolygon(rubiksCube.getAllDrawnPolygons(), pointOfCLick);
					selectedNotRotatedPolygon = searchForClickedPolygon(rubiksCube.getAllNotRotatedPolygons(), pointOfCLick);
				}
				break;
			case MotionEvent.ACTION_UP:
				if (!hasNoticedActionUp) {
					hasNoticedActionUp = true;
					isScreenPressed = false;
					// Calculate inertial velocity if no polygon was selected
					if (!selectedPolygon.isPresent()) {
						xRotationalVelocity =
							(lastClicksQueue.peek().getY() - pointOfCLick.times(1/getScreenSizeRatio()).getY())
								* rotationScale * rotationalVelocityScale;
						yRotationalVelocity =
							(lastClicksQueue.peek().getX() - pointOfCLick.times(1/getScreenSizeRatio()).getX())
								* rotationScale * rotationalVelocityScale;
					}
				}
				// Handle cube face rotation if a polygon was selected
				if (rubiksCubeState.isAvailableForModifications() && selectedPolygon.isPresent() && lastClicksQueue.remainingCapacity() == 0) {
					detectCubeRotationByPlayer(pointOfCLick);
				}
				// Clear selections after touch ends
				selectedPolygon = Optional.empty();
				selectedNotRotatedPolygon = Optional.empty();
				break;
			case MotionEvent.ACTION_MOVE:
				// Rotate entire cube if no specific polygon is selected
				if (!selectedPolygon.isPresent()) {
					rotateCubeBasedOfNewPointOfClick(pointOfCLick);
				}
				break;
		}
		// Reset ACTION_UP flag for subsequent touch events
		if (event != MotionEvent.ACTION_UP && hasNoticedActionUp) {
			hasNoticedActionUp = false;
		}
	}

	/**
	 * Analyzes player's swipe gesture on a selected cube face and determines the appropriate
	 * rotation axis and direction.
	 *
	 * @param lastPointOfClick The final touch point of the swipe gesture
	 */
	private void detectCubeRotationByPlayer(Point2d lastPointOfClick) {
		rubiksCubeState = RubiksCubeState.ROTATED_BY_PLAYER;

		// Get the selected cube and its corresponding non-rotated polygon
		Cube selectedCube = selectedPolygon.get().getParentCube();
		Polygon nonRotatedPolygon = selectedNotRotatedPolygon.get().getParentCube().getNotRotatedPolygonFromDrawnPolygon(selectedNotRotatedPolygon.get());

		// Create direction vectors adjusted for current cube rotation
		DirectionCross directionCross = new DirectionCross();
		directionCross.rotate(rubiksCube.getCurrentRotation());

		// Calculate swipe vector from touch movement
		Vec3D swipeVector = new Vec3D(
			lastPointOfClick.times(1/getScreenSizeRatio()).getX() - lastClicksQueue.peek().getX(),
			(lastPointOfClick.times(1/getScreenSizeRatio()).getY() - lastClicksQueue.peek().getY()),
			0
		).normalize();

		// Get all directional vectors in current cube orientation
		Vec3D vecRight = directionCross.getDirectionVector(Direction.RIGHT);
		Vec3D vecLeft = directionCross.getDirectionVector(Direction.LEFT);
		Vec3D vecUp = directionCross.getDirectionVector(Direction.UP);
		Vec3D vecDown = directionCross.getDirectionVector(Direction.DOWN);
		Vec3D vecForward = directionCross.getDirectionVector(Direction.FORWARD);
		Vec3D vecBackward =	directionCross.getDirectionVector(Direction.BACKWARD);

		Direction directionOfNormalOfPolygon = new DirectionCross().getMostSimilarDirection(nonRotatedPolygon.updateNormalVector());

		// Convert 3D direction vectors to 2D screen projections
		Vec3D[] directionVectors = {
			vecRight, vecLeft, vecUp, vecDown, vecForward, vecBackward
		};
		translateVectorsToProjections(directionVectors);

		// Filter direction vectors based on the selected face's axis
		// (only allow rotations that make sense for the selected face)
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

		// Find which direction vector most closely matches the swipe
		Vec3D.sortVectorsByMostSimilarity(swipeVector, directionVectors);
		Vec3D mostSimilarVec = directionVectors[directionVectors.length - 1];

		// Execute appropriate rotation based on swipe direction and selected face
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

		// Return to idle state after rotation animation completes
		animationManager.addAction(new TimedAction(() -> rubiksCubeState = RubiksCubeState.IDLE, 0.5));
	}

	/**
	 * Converts a 3D direction vector to its 2D screen projection.
	 *
	 * @param vec3D The 3D vector to project onto the screen
	 */
	private void translateVectorToProjection(Vec3D vec3D) {
		vec3D.normalize();
		// Create two points: origin and vector endpoint
		Point3d p1 = new Point3d(0, 0, 0);
		Point3d p2 = new Point3d(vec3D.getX() * rubiksCubeSize*2, vec3D.getY() * rubiksCubeSize*2, vec3D.getZ() * rubiksCubeSize*2);

		// Project both points to screen coordinates
		p1 = ScreenGeometryManager.getInstance().getProjectionTranslatedPoint3d(p1, Constants.FOCAL_LENGTH);
		p2 = ScreenGeometryManager.getInstance().getProjectionTranslatedPoint3d(p2, Constants.FOCAL_LENGTH);

		// Calculate projected vector and update the original vector
		Vec3D finalVec = Vec3D.fromDifferenceInPos(p2, p1).normalize();
		vec3D.setX(finalVec.getX());
		vec3D.setY(finalVec.getY());
		vec3D.setZ(0); // Projected vector has no Z component on screen
	}

	/**
	 *  A method to project multiple 3D vectors to screen coordinates.
	 *
	 * @param vectors Array of 3D vectors to project
	 */
	private void translateVectorsToProjections(Vec3D... vectors) {
		for (Vec3D vector : vectors) {
			translateVectorToProjection(vector);
		}
	}

	/**
	 * Updates cube rotation based on decreasing rotational velocity.
	 * Gradually slows down rotation when user is not actively touching the screen.
	 *
	 * @param time Scaled time value for velocity decrease calculation
	 */
	private void updateRotationsFromDecreasingVelocity(double time) {
		// Calculate total velocity magnitude
		double absolutValueOfSumOfVelocities = Math.abs(xRotationalVelocity) + Math.abs(yRotationalVelocity);

		// Calculate proportional velocity ratios for each axis
		double xVelocityRatio = zeroIfNaN(Math.abs(xRotationalVelocity) / absolutValueOfSumOfVelocities);
		double yVelocityRatio = zeroIfNaN(Math.abs(yRotationalVelocity) / absolutValueOfSumOfVelocities);

		// Decrease velocities proportionally, maintaining direction
		xRotationalVelocity -= getSignOf(xRotationalVelocity) * Math.min(Math.abs(xRotationalVelocity), time * xVelocityRatio);
		yRotationalVelocity -= getSignOf(yRotationalVelocity) * Math.min(Math.abs(yRotationalVelocity), time * yVelocityRatio);

		// Apply current velocities as rotation values
		xRotation = xRotationalVelocity;
		yRotation = yRotationalVelocity;
	}

	/**
	 * Searches through a list of polygons to find which one was clicked.
	 * @param polygons List of polygons to search through
	 * @param pointOfClick The screen coordinate that was clicked
	 * @return Optional containing the clicked polygon, or empty if none found
	 */
	private Optional<Polygon> searchForClickedPolygon(List<Polygon> polygons, Point2d pointOfClick) {
		// Search from back to front (top-most polygons first)
		for (int i = polygons.size() - 1; i >= 0; i--) {
			// Only consider polygons facing the player
			if (polygons.get(i).isPointingToPlayer()) {
				if (polygons.get(i).isPointInPolygon(pointOfClick)) {
					polygons.get(i).setSelected(true);
					return Optional.of(polygons.get(i));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Calculates rotation angles based on the difference between current and previous touch points.
	 * @param pointOfCLick Current touch point coordinates
	 */
	private void rotateCubeBasedOfNewPointOfClick(Point2d pointOfCLick) {
		xRotation = (lastPointOfClick.getY() - pointOfCLick.times(1/getScreenSizeRatio()).getY()) * rotationScale;
		yRotation = (lastPointOfClick.getX() - pointOfCLick.times(1/getScreenSizeRatio()).getX()) * rotationScale;
	}

	/**
	 * Calculates the screen size ratio for scaling touch coordinates appropriately.
	 * Based on the smaller dimension to maintain aspect ratio.
	 *
	 * @return Ratio of actual screen size to ideal screen width
	 */
	private double getScreenSizeRatio() {
		return Math.min(rubiksCube.getScreenWidth(), rubiksCube.getScreenHeight()) / Constants.IDEAL_SCREEN_WIDTH;
	}

	/**
	 * Utility method to determine the sign of a numeric value.
	 *
	 * @param value The numeric value to check
	 * @return 1 for positive values, -1 for negative values (including zero)
	 */
	private static int getSignOf(double value) {
		return (value > 0 ? 1 : -1);
	}

	/**
	 * Utility method to convert NaN values to zero
	 *
	 * @param value The value to check
	 * @return The original value if not NaN, otherwise 0
	 */
	private static double zeroIfNaN(double value) {
		return Double.isNaN(value) ? 0 : value;
	}

	/**
	 * Shuffles the Rubik's cube by performing a series of random rotations.
	 */
	public void shuffle() {
		if(!rubiksCubeState.isAvailableForModifications()) { // Checks if the cube is available for shuffling
			return;
		}
		rubiksCubeState = RubiksCubeState.SHUFFLE; // Sets the Rubik's Cube state to SHUFFLE
		controller.stopTimer(true); // Stops the timer
		int amountOfTurns = 50; // Amount of random rotations
		int animationSteps = 25; // Steps of rotation (the more steps, the smoother the animation is)
		double timeToTurn = 0.12; // Amount of seconds a rotation would take

		int lastAxis = (int)(Math.random()*3); // The last axis that was rotated
		int lastSide = (int)(Math.random()*3) - 1; // The last size that was rotated
		int direction = 1; // direction of turn
		animationManager.addAction(new TimedAction(() -> { // Adding a timed action that would make the cube state idle again after the animation stopes
			rubiksCubeState = RubiksCubeState.IDLE;
			controller.stopTimer(false);
		}, timeToTurn * amountOfTurns));
		for(int i = 0; i < amountOfTurns; i++) { // Randomize each step
			int axis = (int)(Math.random()*3); // The axis
			int side = (int)(Math.random()*3) - 1; // The side
			if(axis != lastAxis || side != lastSide) { // If the axis or the side is different than the previous then flip the direction
				direction *= -1;
			}
			lastAxis = axis;
			lastSide = side;
			int directionOfRotation = direction;
			if(axis == 0) { // Add and animation for rotation around the X axis
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
			else if(axis == 1) { // Add and animation for rotation around the Y axis
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
			else { // Add and animation for rotation around the Z axis
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

	/**
	 * Solves the rubik's cube by playing all of the steps in reverse
	 */
	public void solve() {
		if(!rubiksCubeState.isAvailableForModifications()) { // Checks if the cube is available for this animation
			return;
		}
		rubiksCubeState = RubiksCubeState.SOLVING; // Sets the state to be SOLVING
		controller.stopTimer(true);
		int animationSteps = 10; // Amount of steps in a turn animation (the more steps, the smoother the animation)
		double timeToTurn = 0.05; // Amount of seconds it would take to make a single turn
		double index = 0;
		int stackSize = undoStack.size(); // amount of rotations needed to reset
		double timeOffset = 0;
		while (!undoStack.empty()) { // For each of the steps in the undo stack
			Pair<Consumer<Double>, Double> action = undoStack.pop(); // removing the action from the undo stack
			double timeWithSlowingOffset = (timeToTurn + 0.1 * Math.pow((index + 1)/stackSize, 3.5)); // Time to wait for the animation (with a slowing effect)
			for(int i = 0; i < animationSteps; i++) { // Calculating the animation
				animationManager.addAction(new TimedAction(() -> {
					hasNoticedCubeSolved.set(true);
					action.first.accept(action.second / animationSteps);
				}, timeOffset + i*timeWithSlowingOffset/animationSteps));
			}
			index++;
			timeOffset += timeWithSlowingOffset;
		}
		animationManager.addAction(new TimedAction(() -> { // Adding a timed action to return the cube to IDLE after it is solved
			rubiksCubeState = RubiksCubeState.IDLE;
			controller.resetTimer();
			controller.clearAllSavedRotations();
		}, timeOffset));
	}

	/**
	 * A method to send an animation to the animation manager that rotates a side of the cube around the X axis
	 */
	private void animateRotatingAroundX(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		controller.saveAnotherRotation(new RotationOperation(cubeToRotateAround.getPos(), Axis.X, angle));
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rubiksCube.rotateXAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
		undoStack.push(new Pair<>(angleToRotate -> rubiksCube.rotateXAroundCube(cubeToRotateAround, angleToRotate), -angle));
	}

	/**
	 * A method to send an animation to the animation manager that rotates a side of the cube around the Y axis
	 */
	private void animateRotatingAroundY(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		controller.saveAnotherRotation(new RotationOperation(cubeToRotateAround.getPos(), Axis.Y, angle));
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rubiksCube.rotateYAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
		undoStack.push(new Pair<>(angleToRotate -> rubiksCube.rotateYAroundCube(cubeToRotateAround, angleToRotate), -angle));
	}

	/**
	 * A method to send an animation to the animation manager that rotates a side of the cube around the Z axis
	 */
	private void animateRotatingAroundZ(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		controller.saveAnotherRotation(new RotationOperation(cubeToRotateAround.getPos(), Axis.Z, angle));
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rubiksCube.rotateZAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
		undoStack.push(new Pair<>(angleToRotate -> rubiksCube.rotateZAroundCube(cubeToRotateAround, angleToRotate), -angle));
	}

}
