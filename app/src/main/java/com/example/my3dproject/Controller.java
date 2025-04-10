package com.example.my3dproject;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.example.my3dproject.drawables.Drawable;
import com.example.my3dproject.drawables.RubiksCube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller extends SurfaceView implements Runnable {

	private final int screenWidth, screenHeight;
	private final TextView tvTimer;
	private double timer;
	private boolean shouldTimerCount;
	private boolean isGamePaused;
	private final Paint background;
	private final SurfaceHolder surfaceHolder;
	private final Thread renderThread;
	private final List<Drawable> drawables;
	private Canvas canvas;
	private double lastTime;
	private final TimedAnimationManager animationManager;

	public Controller(Context context, int screenWidth, int screenHeight, TextView tvTimer) {
		super(context);
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.tvTimer = tvTimer;
		this.timer = 0;
		this.shouldTimerCount = false;
		this.isGamePaused = false;
		this.background = new Paint();
		background.setColor(isDarkMode()? Color.BLACK : Color.WHITE);
		this.surfaceHolder = getHolder();
		this.renderThread = new Thread(this);
		this.drawables = new ArrayList<>();
		this.lastTime = System.nanoTime() / 1e9;
		this.animationManager = new TimedAnimationManager();
		initHelpers();
		renderThread.start();
	}

	private void initHelpers() {
		ScreenGeometryManager.getInstance().setScreenSize(screenWidth, screenHeight);
		ScreenGeometryManager.getInstance().setFocalLength(Constants.FOCAL_LENGTH);
	}

	public void addDrawables(Drawable... drawables) {
		this.drawables.addAll(Arrays.asList(drawables));
	}

	private void drawSurface(double deltaTime) {
		if (surfaceHolder.getSurface().isValid() && (canvas = surfaceHolder.lockCanvas()) != null) {
			background.setColor(isDarkMode()? Color.BLACK : Color.WHITE);
			canvas.drawPaint(background);
			for (Drawable drawable : drawables) {
				drawable.update(
					deltaTime,
					ScreenTouchListener.getInstance().getPos(),
					ScreenTouchListener.getInstance().getEvent()
				);
				drawable.render(canvas, isDarkMode());
			}
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	public boolean isDarkMode() {
		int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
		return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
	}

	public TimedAnimationManager getAnimationManager() {
		return animationManager;
	}

	private void updateTimer(double deltaTime) {
		timer += deltaTime;
		String minutes = (int)(timer/60) < 10 ? "0" + (int)(timer/60) : "" + (int)(timer/60);
		String seconds = (int)(timer % 60) < 10 ? "0" + (int)(timer % 60) : "" + (int)(timer % 60);
		tvTimer.post(() -> tvTimer.setText("⏱ " + minutes + ":" + seconds));
	}

	public void resetTimer() {
		timer = 0;
		tvTimer.post(() -> tvTimer.setText("⏱ 00:00"));
	}

	public void stopTimer(boolean stopTimer) {
		this.shouldTimerCount = !stopTimer;
	}

	public void pauseGame(boolean pauseGame) {
		this.isGamePaused = pauseGame;
	}

	public boolean isGamePaused() {
		return isGamePaused;
	}

	@Override
	public void run() {
		while (true) {
			double currentTime = System.nanoTime() / 1e9;
			if(shouldTimerCount && !isGamePaused) {
				updateTimer(currentTime - lastTime);
			}
			animationManager.update();
			drawSurface(currentTime - lastTime);
			lastTime = currentTime;
		}
	}
}
