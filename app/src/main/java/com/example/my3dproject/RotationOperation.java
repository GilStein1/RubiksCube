package com.example.my3dproject;

import com.example.my3dproject.drawables.Cube;

public class RotationOperation {

	private Cube instanceCube;
	private boolean isRotatingX, isRotatingY, isRotatingZ;

	public RotationOperation(Cube instanceCube, boolean isRotatingX, boolean isRotatingY, boolean isRotatingZ) {
		this.instanceCube = instanceCube;
		this.isRotatingX = isRotatingX;
		this.isRotatingY = isRotatingY;
		this.isRotatingZ = isRotatingZ;
	}

	public RotationOperation(Cube instanceCube, int rotationAxis) {
		this.instanceCube = instanceCube;
		this.isRotatingX = false;
		this.isRotatingY = false;
		this.isRotatingZ = false;
		if(rotationAxis == 0) {
			isRotatingX = true;
		}
		else if(rotationAxis == 1) {
			isRotatingY = true;
		}
		else if(rotationAxis == 2) {
			isRotatingZ = true;
		}
	}

	public Cube getInstanceCube() {
		return instanceCube;
	}

	public void setInstanceCube(Cube instanceCube) {
		this.instanceCube = instanceCube;
	}

	public boolean isRotatingX() {
		return isRotatingX;
	}

	public void setRotatingX(boolean rotatingX) {
		isRotatingX = rotatingX;
	}

	public boolean isRotatingY() {
		return isRotatingY;
	}

	public void setRotatingY(boolean rotatingY) {
		isRotatingY = rotatingY;
	}

	public boolean isRotatingZ() {
		return isRotatingZ;
	}

	public void setRotatingZ(boolean rotatingZ) {
		isRotatingZ = rotatingZ;
	}
}
