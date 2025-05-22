package com.example.my3dproject;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.my3dproject.drawables.Drawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultController extends SurfaceView implements Runnable {

	private final TimedAnimationManager animationManager;
	private final Paint background;
	private final SurfaceHolder surfaceHolder;
	private volatile boolean isRenderThreadRunning;
	private final List<Drawable> drawables;
	private final List<UpdatableComponent> updatables;
	private double lastTime;

	public DefaultController(
		Context context,
		int screenWidth,
		int screenHeight
	) {
		super(context);
		this.animationManager = new TimedAnimationManager();
		this.background = new Paint();
		background.setColor(isDarkMode()? Color.BLACK : Color.WHITE);
		this.surfaceHolder = getHolder();
		this.isRenderThreadRunning = true;
		this.drawables = new ArrayList<>();
		this.updatables = new ArrayList<>();
		this.lastTime = System.nanoTime() / 1e9;
		ScreenGeometryManager.getInstance().setScreenSize(screenWidth, screenHeight);
		ScreenGeometryManager.getInstance().setFocalLength(Constants.FOCAL_LENGTH);
		new Thread(this).start();
	}

	public void addDrawables(Drawable... drawables) {
		this.drawables.addAll(Arrays.asList(drawables));
	}

	public void addUpdatableComponents(UpdatableComponent... updatableComponents) {
		this.updatables.addAll(Arrays.asList(updatableComponents));
	}

	private void drawSurface(double deltaTime) {
		Canvas canvas;
		if (surfaceHolder.getSurface().isValid() && (canvas = surfaceHolder.lockCanvas()) != null) {
			background.setColor(isDarkMode()? Color.BLACK : Color.WHITE);
			canvas.drawPaint(background);
			for(UpdatableComponent updatableComponent : updatables) {
				updatableComponent.update(
					deltaTime,
					ScreenTouchListener.getInstance().getPos(),
					ScreenTouchListener.getInstance().getEvent()
				);
			}
			for (Drawable drawable : drawables) {
				drawable.render(canvas, isDarkMode());
			}
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	public boolean isDarkMode() {
		int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
		return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
	}

	public void onPause() {
		isRenderThreadRunning = false;
	}

	public void onDestroy() {
		isRenderThreadRunning = false;
	}

	public void onResume() {
		if(!isRenderThreadRunning) {
			isRenderThreadRunning = true;
			new Thread(this).start();
		}
	}

	public TimedAnimationManager getAnimationManager() {
		return animationManager;
	}

	@Override
	public void run() {
		while (isRenderThreadRunning) {
			double currentTime = System.nanoTime() / 1e9;
			animationManager.update(currentTime - lastTime);
			drawSurface(currentTime - lastTime);
			lastTime = currentTime;
		}
	}
}
