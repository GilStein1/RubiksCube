package com.example.my3dproject;

public class Account {

	public String name;
	public String userId;
	public double bestTime;

	public Account(String userId, String name) {
		this.userId = userId;
		this.name = name;
		this.bestTime = Double.MAX_VALUE;
	}

	public Account() {
		this.name = "";
		this.userId = "";
		this.bestTime = Double.MAX_VALUE;
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

	public double getBestTime() {
		return bestTime;
	}

	public void setBestTime(double bestTime) {
		this.bestTime = bestTime;
	}

}
