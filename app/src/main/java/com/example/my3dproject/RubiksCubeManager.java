package com.example.my3dproject;

import com.example.my3dproject.drawables.RubiksCube;
import com.example.my3dproject.math.geometry.Point2d;

public class RubiksCubeManager implements UpdatableComponent{

	private final RubiksCube rubiksCube;

	public RubiksCubeManager(RubiksCube rubiksCube) {
		this.rubiksCube = rubiksCube;
	}

	@Override
	public void update(double deltaTime, Point2d pointOfClick, int event) {

	}
}
