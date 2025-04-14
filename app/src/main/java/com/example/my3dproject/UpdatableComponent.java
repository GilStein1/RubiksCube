package com.example.my3dproject;

import com.example.my3dproject.math.geometry.Point2d;

public interface UpdatableComponent {

	void update(double deltaTime, Point2d pointOfClick, int event);

}
