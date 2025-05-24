package com.example.my3dproject;

/**
 * LoopedAction extends TimedAction to provide repeating/looping functionality.
 * This class represents an action that should be executed repeatedly at regular intervals.
 */
public class LoopedAction extends TimedAction {

	// Accumulator that tracks how much time has passed since last execution
	private double timeCount;

	/**
	 * Constructor creates a looped action
	 * @param action The Runnable to execute each time the interval elapses
	 * @param time The time interval (in seconds) between executions
	 */
	public LoopedAction(Runnable action, double time) {
		super(action, time);  // Initialize parent TimedAction with action and time
		this.timeCount = 0;   // Start with zero elapsed time
	}

	/**
	 * Updates the internal time counter with the elapsed frame time
	 * This is called each frame/update cycle by the animation manager.
	 * @param deltaTime Time elapsed since last update
	 */
	public void updateTime(double deltaTime) {
		timeCount += deltaTime;
	}

	/**
	 * Checks if enough time has passed to trigger the next execution
	 * @return true if the time interval has elapsed and action should be executed
	 */
	public boolean hasTimeCountPassedGivenTime() {
		return timeCount > time;
	}

	/**
	 * Resets the time counter to zero, typically called after executing the action
	 * This allows the looped action to start counting toward the next execution
	 */
	public void resetTimeCount() {
		timeCount = 0;
	}

}