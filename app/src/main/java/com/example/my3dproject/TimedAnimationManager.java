package com.example.my3dproject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TimedAnimationManager {

	private final List<TimedAction> queue;

	public TimedAnimationManager() {
		queue = new ArrayList<>();
	}

	public void addAction(TimedAction action) {
		synchronized (queue) {
			action.setTime(action.getTime() + System.nanoTime() / 1e9);
			queue.add(action);
		}
	}

	public synchronized void update() {
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
