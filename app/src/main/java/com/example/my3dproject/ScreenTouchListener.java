package com.example.my3dproject;

import android.view.MotionEvent;
import android.view.View;

import com.example.my3dproject.math.geometry.Point2d;

/**
 * A singleton class responsible for managing all screen-touch actions
 */
public class ScreenTouchListener implements View.OnTouchListener {

	// A static instance of the class - for singleton usages
	private static ScreenTouchListener instance;
	// The 2d pos of the last touch on the screen
	private final Point2d pos;
	// The event of the last touch gesture on the screen
	private int event;

	/**
	 * A private constructor that initializes the pos and the event
	 */
	private ScreenTouchListener() {
		this.pos = new Point2d(0, 0);
		this.event = MotionEvent.ACTION_UP;
	}

	/**
	 * A static method to get the static instance of the class
	 */
	public static ScreenTouchListener getInstance() {
		if(instance == null) { // If the instance is null, create a new one
			instance = new ScreenTouchListener();
		}
		return instance;
	}

	/**
	 * @return the 2d pos of the last touch on the screen
	 */
	public Point2d getPos() {
		return new Point2d(pos);
	}

	/**
	 * @return the current motion event type (ACTION_DOWN, ACTION_MOVE, ACTION_UP)
	 */
	public int getEvent() {
		return event;
	}

	/**
	 * A method, implemented from the OnTouchListener interface,
	 * that is called whenever a screen gesture is detected
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Updates the pos of the last screen gesture
		pos.setX(event.getX());
		pos.setY(event.getY());
		// Updates the current motion event type
		this.event = event.getAction();
		return true;
	}
}
