package com.example.my3dproject;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TimedAnimationManager {

	private final List<TimedAction> queue;
	private final List<LoopedAction> loopedActions;

	public TimedAnimationManager() {
		this.queue = new ArrayList<>();
		this.loopedActions = new ArrayList<>();
	}

	public void addAction(TimedAction action) {
		synchronized (queue) {
			action.setTime(action.getTime() + System.nanoTime() / 1e9);
			queue.add(action);
		}
	}

	public void addLoopedAction(LoopedAction action) {
		synchronized (loopedActions) {
			loopedActions.add(action);
		}
	}

	public synchronized void update(double deltaTime) {
		for(LoopedAction action : loopedActions) {
			action.updateTime(deltaTime);
			if(action.hasTimeCountPassedGivenTime()) {
				action.resetTimeCount();
				action.getAction().run();
			}
		}
		if(queue.isEmpty()) {
			return;
		}
		List<TimedAction> removeLater = new ArrayList<>();
		synchronized (queue) {
			queue.sort(Comparator.comparingDouble(TimedAction::getTime));
		}
		for(int i = 0; i < queue.size(); i++) {
			double currentTime = System.nanoTime() / 1e9;
			if(queue.get(i) != null && queue.get(i).getTime() <= currentTime) {
				queue.get(i).getAction().run();
				removeLater.add(queue.get(i));
			}
		}
		for(TimedAction action : removeLater) {
			queue.remove(action);
		}
		removeLater.clear();
	}

}
