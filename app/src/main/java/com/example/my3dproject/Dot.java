package com.example.my3dproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.my3dproject.math.geometry.Point2d;

public class Dot extends Drawable {

	private double x, y;
	private double drawX, drawY;
	private double time;

	public Dot(double screenWidth, double screenHeight, double x, double y) {
		super(screenWidth, screenHeight);
		this.x = x;
		this.y = y;
		this.drawX = x;
		this.drawY = y;
		this.time = 0;
	}

	public void moveTo(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public void update(double deltaTime, Point2d pointOfCLick, int event) {
		drawX = x + screenWidth/2;
		drawY = screenHeight/2 - y;
	}

	@Override
	public void render(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		canvas.drawCircle((float) drawX, (float) drawY, 10, paint);
	}
}
