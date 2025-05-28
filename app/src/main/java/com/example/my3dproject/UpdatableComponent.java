package com.example.my3dproject;

import com.example.my3dproject.math.geometry.Point2d;

/**
 * An Interface for objects that need constant updates
 */
public interface UpdatableComponent {

	/**
	 * Main update method called every frame - every iteration of the main game loop.
	 *
	 * @param deltaTime Time elapsed since last update
	 * @param pointOfClick Current touch point coordinates
	 * @param event Current motion event type (ACTION_DOWN, ACTION_MOVE, ACTION_UP)
	 */
	void update(double deltaTime, Point2d pointOfClick, int event);

}
