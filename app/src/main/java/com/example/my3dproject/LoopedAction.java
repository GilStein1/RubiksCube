package com.example.my3dproject;

public class LoopedAction extends TimedAction {

	private double timeCount;

	public LoopedAction(Runnable action, double time) {
		super(action, time);
		this.timeCount = 0;
	}

	public void updateTime(double deltaTime) {
		timeCount += deltaTime;
	}

	public boolean hasTimeCountPassedGivenTime() {
		return timeCount > time;
	}

	public void resetTimeCount() {
		timeCount = 0;
	}

}
