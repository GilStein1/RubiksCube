package com.example.my3dproject;

import android.view.MotionEvent;
import android.view.View;

import com.example.my3dproject.math.geometry.Point2d;

public class ScreenTouchListener implements View.OnTouchListener {

	private static ScreenTouchListener instance;
	private final Point2d pos;
	private int event;

	private ScreenTouchListener() {
		this.pos = new Point2d(0, 0);
		this.event = MotionEvent.ACTION_UP;
	}

	public static ScreenTouchListener getInstance() {
		if(instance == null) {
			instance = new ScreenTouchListener();
		}
		return instance;
	}

	public Point2d getPos() {
		return new Point2d(pos);
	}

	public int getEvent() {
		return event;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		pos.setX(event.getX());
		pos.setY(event.getY());
		this.event = event.getAction();
		return true;
	}
}
