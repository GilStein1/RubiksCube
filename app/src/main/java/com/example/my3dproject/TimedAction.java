package com.example.my3dproject;

public class TimedAction {

	private Runnable action;
	private double time;

	public TimedAction(Runnable action, double time) {
		this.action = action;
		this.time = time;
	}

	public Runnable getAction() {
		return action;
	}

	public void setAction(Runnable action) {
		this.action = action;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
}
