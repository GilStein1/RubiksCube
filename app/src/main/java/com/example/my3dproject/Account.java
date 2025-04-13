package com.example.my3dproject;

public class Account {

	public String name;
	public String userId;
	public double bestTime;
	public boolean resetBestScore;

	public Account(String userId, String name) {
		this.userId = userId;
		this.name = name;
		this.bestTime = Double.MAX_VALUE;
		this.resetBestScore = false;
	}

	public Account() {
		this.name = "";
		this.userId = "";
		this.bestTime = Double.MAX_VALUE;
		this.resetBestScore = false;
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

	public boolean shouldResetBestScore() {
		return resetBestScore;
	}

	public void setResetBestScore(boolean shouldResetBestScore) {
		this.resetBestScore = shouldResetBestScore;
	}
}
