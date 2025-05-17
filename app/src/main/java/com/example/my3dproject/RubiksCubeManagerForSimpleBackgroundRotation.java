package com.example.my3dproject;

import com.example.my3dproject.drawables.Cube;
import com.example.my3dproject.drawables.Polygon;
import com.example.my3dproject.drawables.RubiksCube;
import com.example.my3dproject.math.geometry.Point2d;

public class RubiksCubeManagerForSimpleBackgroundRotation implements UpdatableComponent {

	private final RubiksCube rubiksCube;
	private final TimedAnimationManager animationManager;
	private double timeCount;

	public RubiksCubeManagerForSimpleBackgroundRotation(RubiksCube rubiksCube, DefaultController controller) {
		this.rubiksCube = rubiksCube;
		this.animationManager = controller.getAnimationManager();
		this.timeCount = 0;
		rubiksCube.rotate(0.001, 0.001, Math.toRadians(45));
	}

	@Override
	public void update(double deltaTime, Point2d pointOfClick, int event) {
		timeCount += deltaTime;
		if(timeCount > 0.3) {
			timeCount = 0;
			randomlyRotateSide();
		}
		rubiksCube.rotate(-deltaTime*0.2, deltaTime*0.3, 0);
		for(Polygon polygon : rubiksCube.getAllDrawnPolygons()) {
			polygon.update(deltaTime, pointOfClick, event);
		}
		for(Polygon polygon : rubiksCube.getAllNotRotatedPolygons()) {
			polygon.update(deltaTime, pointOfClick, event);
		}
	}

	private void randomlyRotateSide() {
		int axis = (int)(Math.random()*3);
		int side = (int)(Math.random()*3) - 1;
		if(axis == 0) {
			animateRotatingAroundX(
				15, 0.28, Math.toRadians(90),
				new Cube(side* rubiksCube.getSmallCubesSize(), 0, 0, rubiksCube.getSmallCubesSize())
			);
		}
		else if(axis == 1) {
			animateRotatingAroundY(
				15, 0.28, Math.toRadians(90),
				new Cube(0, side* rubiksCube.getSmallCubesSize(), 0, rubiksCube.getSmallCubesSize())
			);
		}
		else {
			animateRotatingAroundZ(
				15, 0.28, Math.toRadians(90),
				new Cube(0, 0, side* rubiksCube.getSmallCubesSize(), rubiksCube.getSmallCubesSize())
			);
		}
	}

	private void animateRotatingAroundX(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rubiksCube.rotateXAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
	}

	private void animateRotatingAroundY(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rubiksCube.rotateYAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
	}

	private void animateRotatingAroundZ(int animationSteps, double timeToTurn, double angle, Cube cubeToRotateAround) {
		for(int i = 0; i < animationSteps; i++) {
			animationManager.addAction(new TimedAction(() -> rubiksCube.rotateZAroundCube(cubeToRotateAround, angle/animationSteps), i*timeToTurn/animationSteps));
		}
	}

}
