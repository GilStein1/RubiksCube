package com.example.my3dproject;

public class Account {

	public String name;
	public String profilePicture;
	public String userId;
	public double bestTime;
	public boolean resetBestScore;
	public String savedRotations;
	public double timer;
	public long timestampOfSave;

	public Account(String userId, String name) {
		this.userId = userId;
		this.name = name;
		this.profilePicture = "";
		this.bestTime = Double.MAX_VALUE;
		this.resetBestScore = false;
		this.savedRotations = "";
		this.timer = 0;
		this.timestampOfSave = 0;
	}

	public Account() {
		this.name = "";
		this.profilePicture = "";
		this.userId = "";
		this.bestTime = Double.MAX_VALUE;
		this.resetBestScore = false;
		this.savedRotations = "";
		this.timer = 0;
		this.timestampOfSave = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public boolean isResetBestScore() {
		return resetBestScore;
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

	public String getSavedRotations() {
		return savedRotations;
	}

	public void setSavedRotations(String savedRotations) {
		this.savedRotations = savedRotations;
	}

	public long getTimestampOfSave() {
		return timestampOfSave;
	}

	public void setTimestampOfSave(long timestampOfSave) {
		this.timestampOfSave = timestampOfSave;
	}

	public double getTimer() {
		return timer;
	}

	public void setTimer(double timer) {
		this.timer = timer;
	}

}
