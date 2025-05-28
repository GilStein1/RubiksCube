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

/**
 * DefaultController is a simplified rendering controller that provides basic 3D rendering
 * capabilities without game-specific features like timers, scoring, or Firebase integration.
 * This appears to be used for menu screens, demos, or other non-game contexts where
 * 3D visualization is needed but game logic is not required.
 */
public class DefaultController extends SurfaceView implements Runnable {

	private final TimedAnimationManager animationManager;  // Manages timed animations and effects
	private final Paint background;  // Paint object for drawing the background
	private final SurfaceHolder surfaceHolder;  // Holder for the drawing surface
	private volatile boolean isRenderThreadRunning;  // Thread safety flag for render loop
	private final List<Drawable> drawables;  // List of objects that can be drawn to screen
	private final List<UpdatableComponent> updatables;  // List of objects that need regular updates
	private double lastTime;  // Timestamp of last frame, used for delta time calculations

	/**
	 * Constructor initializes the basic rendering system
	 *
	 * @param context Android context
	 * @param screenWidth Width of the screen in pixels
	 * @param screenHeight Height of the screen in pixels
	 */
	public DefaultController(
		Context context,
		int screenWidth,
		int screenHeight
	) {
		super(context);

		// Initialize animation manager
		this.animationManager = new TimedAnimationManager();

		// Setup background rendering with theme adaptation
		this.background = new Paint();
		background.setColor(isDarkMode()? Color.BLACK : Color.WHITE);

		// Initialize rendering components
		this.surfaceHolder = getHolder();
		this.isRenderThreadRunning = true;
		this.drawables = new ArrayList<>();
		this.updatables = new ArrayList<>();
		this.lastTime = System.nanoTime() / 1e9;  // Convert nanoseconds to seconds

		// Configure screen geometry for 3D projection
		ScreenGeometryManager.getInstance().setScreenSize(screenWidth, screenHeight);
		ScreenGeometryManager.getInstance().setFocalLength(Constants.FOCAL_LENGTH);

		// Start the render thread immediately
		new Thread(this).start();
	}

	/**
	 * Adds drawable objects to the list of items to be rendered each frame
	 * @param drawables Variable number of drawable objects to add
	 */
	public void addDrawables(Drawable... drawables) {
		this.drawables.addAll(Arrays.asList(drawables));
	}

	/**
	 * Adds updatable components that need to be updated each frame
	 * @param updatableComponents Variable number of updatable components to add
	 */
	public void addUpdatableComponents(UpdatableComponent... updatableComponents) {
		this.updatables.addAll(Arrays.asList(updatableComponents));
	}

	/**
	 * Main rendering method that draws one frame
	 * @param deltaTime Time elapsed since last frame in seconds
	 */
	private void drawSurface(double deltaTime) {
		Canvas canvas;
		// Only draw if surface is valid and we can lock the canvas
		if (surfaceHolder.getSurface().isValid() && (canvas = surfaceHolder.lockCanvas()) != null) {
			// Update background color based on current theme
			background.setColor(isDarkMode()? Color.BLACK : Color.WHITE);
			canvas.drawPaint(background);

			// Update all updatable components with current touch input
			for(UpdatableComponent updatableComponent : updatables) {
				updatableComponent.update(
					deltaTime,
					ScreenTouchListener.getInstance().getPos(),
					ScreenTouchListener.getInstance().getEvent()
				);
			}

			// Render all drawable objects
			for (Drawable drawable : drawables) {
				drawable.render(canvas, isDarkMode());
			}

			// Release the canvas and display the frame
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * Checks if the device is currently in dark mode
	 * @return true if in dark mode, false otherwise
	 */
	public boolean isDarkMode() {
		int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
		return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
	}

	/**
	 * Called when activity is paused. Stops the render thread.
	 */
	public void onPause() {
		isRenderThreadRunning = false;
	}

	/**
	 * Called when activity is destroyed. Stops the render thread.
	 */
	public void onDestroy() {
		isRenderThreadRunning = false;
	}

	/**
	 * Called when activity is resumed. Restarts the render thread if needed.
	 */
	public void onResume() {
		if(!isRenderThreadRunning) {
			isRenderThreadRunning = true;
			new Thread(this).start();
		}
	}

	/**
	 * @return The TimedAnimationManager instance
	 */
	public TimedAnimationManager getAnimationManager() {
		return animationManager;
	}

	/**
	 * Main rendering loop that runs in a separate thread.
	 * Handles timing, updates, and rendering.
	 */
	@Override
	public void run() {
		while (isRenderThreadRunning) {
			// Calculate delta time since last frame
			double currentTime = System.nanoTime() / 1e9;

			// Update animation manager and render the frame
			animationManager.update(currentTime - lastTime);
			drawSurface(currentTime - lastTime);

			// Store current time for next frame's delta calculation
			lastTime = currentTime;
		}
	}
}