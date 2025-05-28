package com.example.my3dproject;

/**
 * A class that represents an action and a time to execute the action.
 * The class is mainly sent to the TimedAnimationManager
 */
public class TimedAction {

	// The action to be executed
	private Runnable action;
	// The timer to the execution
	protected double time;

	/**
	 * The constructor to construct a TimedAction
	 *
	 * @param action The action to execute
	 * @param time The timer of the execution
	 */
	public TimedAction(Runnable action, double time) {
		this.action = action;
		this.time = time;
	}

	/**
	 * @return the action
	 */
	public Runnable getAction() {
		return action;
	}
	/**
	 * @param action The action
	 */
	public void setAction(Runnable action) {
		this.action = action;
	}

	/**
	 * @return the time left on the timer
	 */
	public double getTime() {
		return time;
	}

	/**
	 * @param time The time for the timer
	 */
	public void setTime(double time) {
		this.time = time;
	}
}
