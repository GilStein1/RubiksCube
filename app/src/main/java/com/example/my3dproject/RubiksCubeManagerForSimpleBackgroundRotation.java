package com.example.my3dproject;

import com.example.my3dproject.drawables.Cube;
import com.example.my3dproject.drawables.Polygon;
import com.example.my3dproject.drawables.RubiksCube;
import com.example.my3dproject.math.geometry.Point2d;

/**
 * Main manager class for handling Rubik's Cube random rotation animation in the background (used in the main activity).
 * Implements UpdatableComponent to receive regular update calls from the game loop.
 */
public class RubiksCubeManagerForSimpleBackgroundRotation implements UpdatableComponent {

	// The Rubik's Cube
	private final RubiksCube rubiksCube;
	// The animation manager to handle the random rotation animation
	private final TimedAnimationManager animationManager;
	// The time count that is used to call for a random rotation each 0.3 seconds
	private double timeCount;

	/**
	 * Constructor initializes the Rubik's Cube Manager with all necessary components.
	 *
	 * @param rubiksCube The 3D Rubik's cube
	 * @param controller The main controller
	 */
	public RubiksCubeManagerForSimpleBackgroundRotation(RubiksCube rubiksCube, DefaultController controller) {
		this.rubiksCube = rubiksCube;
		this.animationManager = controller.getAnimationManager();
		this.timeCount = 0;
		// Small initial rotation to ensure proper 3D rendering setup
		rubiksCube.rotate(0.001, 0.001, Math.toRadians(45));
	}

	/**
	 * Main update method called every frame. Handles the random rotation in the background.
	 *
	 * @param deltaTime Time elapsed since last update
	 * @param pointOfClick Current touch point coordinates
	 * @param event Current motion event type (ACTION_DOWN, ACTION_MOVE, ACTION_UP)
	 */
	@Override
	public void update(double deltaTime, Point2d pointOfClick, int event) {
		timeCount += deltaTime; // Counts the time passed
		if(timeCount > 0.3) { // If 0.3 seconds have passed, the count resets and a random side is rotated
			timeCount = 0;
			randomlyRotateSide();
		}
		rubiksCube.rotate(-deltaTime*0.2, deltaTime*0.3, 0); // Rotates the cube slowly in the background
		for(Polygon polygon : rubiksCube.getAllDrawnPolygons()) { // Updates all polygons
			polygon.update(deltaTime, pointOfClick, event);
		}
		for(Polygon polygon : rubiksCube.getAllNotRotatedPolygons()) { // Updates all reference polygons
			polygon.update(deltaTime, pointOfClick, event);
		}
	}

	/**
	 * A method that randomly rotates a side of a cube
	 */
	private void randomlyRotateSide() {
		int axis = (int)(Math.random()*3); // The axis, picked randomly (0 = X, 1 = Y, 2 = Z)
		int side = (int)(Math.random()*3) - 1; // The side that would be rotated (the middle or the sides)
		if(axis == 0) {
			animateRotatingAroundX( // Rotate a size around the X axis. The rotation takes 0.28 seconds and takes 15 steps
				15, 0.28, Math.toRadians(90),
				new Cube(side * rubiksCube.getSmallCubesSize(), 0, 0, rubiksCube.getSmallCubesSize())
			);
		}
		else if(axis == 1) {
			animateRotatingAroundY( // Rotate a size around the Y axis. The rotation takes 0.28 seconds and takes 15 steps
				15, 0.28, Math.toRadians(90),
				new Cube(0, side * rubiksCube.getSmallCubesSize(), 0, rubiksCube.getSmallCubesSize())
			);
		}
		else {
			animateRotatingAroundZ( // Rotate a size around the Z axis. The rotation takes 0.28 seconds and takes 15 steps
				15, 0.28, Math.toRadians(90),
				new Cube(0, 0, side * rubiksCube.getSmallCubesSize(), rubiksCube.getSmallCubesSize())
			);
		}
	}

	/**
	 * A method to send an animation to the animation manager that rotates a side of the cube around the X axis
	 */
	private void animateRotatingAroundX(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rubiksCube.rotateXAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
	}

	/**
	 * A method to send an animation to the animation manager that rotates a side of the cube around the Y axis
	 */
	private void animateRotatingAroundY(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rubiksCube.rotateYAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
	}

	/**
	 * A method to send an animation to the animation manager that rotates a side of the cube around the Z axis
	 */
	private void animateRotatingAroundZ(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rubiksCube.rotateZAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
	}

}
