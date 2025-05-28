package com.example.my3dproject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A class responsible for all animation managing and timed actions execution.
 */
public class TimedAnimationManager {

	// List of all TimedActions
	private final List<TimedAction> queue;
	// List of all LoopedActions
	private final List<LoopedAction> loopedActions;

	/**
	 * The constructor that constructs the TimedAnimation manager
	 */
	public TimedAnimationManager() {
		this.queue = new ArrayList<>();
		this.loopedActions = new ArrayList<>();
	}

	/**
	 * A method for adding a timed action.
	 *
	 * @param action The action to be added
	 */
	public void addAction(TimedAction action) {
		synchronized (queue) { // Using the list of actions in a thread - safe way
			action.setTime(action.getTime() + System.nanoTime() / 1e9); // Changing the timer on the action to a timestamp in the future
			queue.add(action); // Adding the action
		}
	}

	/**
	 * A method for adding a looped action.
	 *
	 * @param action The action to be added
	 */
	public void addLoopedAction(LoopedAction action) {
		synchronized (loopedActions) { // Using the list of actions in a thread - safe way
			loopedActions.add(action); // Adding the action
		}
	}

	/**
	 * The update method. Called each iteration of the main
	 * game loop.
	 *
	 * @param deltaTime Time elapsed since last update
	 */
	public synchronized void update(double deltaTime) {
		for(LoopedAction action : loopedActions) { // For each of the looped actions
			action.updateTime(deltaTime); // Update the action with the deltaTime
			if(action.hasTimeCountPassedGivenTime()) { // If the timer on the action finished it's count
				action.resetTimeCount(); // Reset the timer
				action.getAction().run(); // Run the action
			}
		}
		if(queue.isEmpty()) { // If the list of TimedActions is empty, exit the method
			return;
		}
		// A list of actions, saved outside for later removal
		List<TimedAction> removeLater = new ArrayList<>();
		synchronized (queue) { // Using the list in a thread - safe way
			// Sorting the list by the time they have left in order
			// to execute the "finished" actions in the right order
			queue.sort(Comparator.comparingDouble(TimedAction::getTime));
		}
		// For each of the actions, if it's time has come, execute it's action and remove from the list
		for(int i = 0; i < queue.size(); i++) {
			double currentTime = System.nanoTime() / 1e9;
			if(queue.get(i) != null && queue.get(i).getTime() <= currentTime) {
				queue.get(i).getAction().run();
				removeLater.add(queue.get(i));
			}
		}
		// For each of the actions in the list to remove, remove from the original list
		for(TimedAction action : removeLater) {
			queue.remove(action);
		}
		removeLater.clear(); // Clear the list of actions to remove
	}

}
