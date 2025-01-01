package com.example.my3dproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.my3dproject.drawables.Drawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller extends SurfaceView implements Runnable{

	private final int screenWidth, screenHeight;
	private final Paint background;
	private final SurfaceHolder surfaceHolder;
	private final Thread renderThread;
	private final List<Drawable> drawables;
	private Canvas canvas;
	private double lastTime;

	public Controller(Context context, int screenWidth, int screenHeight) {
		super(context);
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.background = new Paint();
		background.setColor(Color.WHITE);
		this.surfaceHolder = getHolder();
		this.renderThread = new Thread(this);
		this.drawables = new ArrayList<>();
		this.lastTime = System.nanoTime()/1e9;
		renderThread.start();
	}

	public void addDrawables(Drawable... drawables) {
		this.drawables.addAll(Arrays.asList(drawables));
	}

	private void drawSurface(double deltaTime) {
		if(surfaceHolder.getSurface().isValid()) {
			canvas = surfaceHolder.lockCanvas();
			canvas.drawPaint(background);
			for(Drawable drawable : drawables) {
				drawable.update(deltaTime, ScreenTouchListener.getInstance().getPos(), ScreenTouchListener.getInstance().getEvent());
				drawable.render(canvas);
			}
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	@Override
	public void run() {
		while (true) {
			double currentTime = System.nanoTime()/1e9;
			drawSurface(currentTime - lastTime);
			lastTime = currentTime;
		}
	}
}
