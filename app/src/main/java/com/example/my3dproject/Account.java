package com.example.my3dproject;

import java.util.ArrayList;
import java.util.List;

public class Account {

	public String name;
	public String userId;
	public List<RotationOperation> rotationOperations;
	public double timer;
	public double bestTime;

	public Account(String userId, String name) {
		this.userId = userId;
		this.name = name;
		this.rotationOperations = new ArrayList<>();
		this.timer = 0;
		this.bestTime = Double.MAX_VALUE;
	}

	public Account() {
		this.name = "";
		this.userId = "";
		this.rotationOperations = new ArrayList<>();
		this.timer = 0;
		this.bestTime = Double.MAX_VALUE;
	}

	public void setRotationOperationList(List<RotationOperation> rotationOperations) {
		this.rotationOperations = rotationOperations;
	}

	public List<RotationOperation> getRotationOperationList() {
		return rotationOperations;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public double getTimer() {
		return timer;
	}

	public void setTimer(double timer) {
		this.timer = timer;
	}

	public double getBestTime() {
		return bestTime;
	}

	public void setBestTime(double bestTime) {
		this.bestTime = bestTime;
	}

}
