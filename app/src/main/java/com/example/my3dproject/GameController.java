package com.example.my3dproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.example.my3dproject.drawables.Drawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * GameController is the main game engine class that handles rendering and timing.
 * It extends SurfaceView for custom drawing
 * and implements Runnable to run the game loop in a separate thread.
 */
public class GameController extends SurfaceView implements Runnable {

	// Intent and UI components
	private final Intent intentFromMainMenu;  // Intent passed from the main menu activity
	private final TextView tvTimer, tvBestTime;  // UI elements to display timer and best time

	// Timer and scoring system
	private double timer;  // Current game timer in seconds
	private double bestTime;  // The player's high score
	private boolean shouldTimerCount;  // Flag to control whether timer is actively counting

	// Rendering components
	private final Paint background;  // Paint object for drawing the background
	private final SurfaceHolder surfaceHolder;  // Holder for the drawing surface
	private Thread renderThread;  // Thread that runs the game loop
	private volatile boolean isRenderThreadRunning;  // Thread safety flag determining if the game thread should run
	private final List<Drawable> drawables;  // List of objects that can be drawn to screen
	private final List<UpdatableComponent> updatables;  // List of objects that need regular updates
	private Canvas canvas;  // Canvas for drawing operations
	private double lastTime;  // Timestamp of last frame, used for delta time calculations

	// Animation and game mechanics
	private final TimedAnimationManager animationManager;  // Manages timed animations and periodic actions
	private List<RotationOperation> rotationOperations;  // List of cube rotations performed by player

	// Firebase and data persistence
	private final FirebaseAuth mAuth;  // Firebase authentication instance
	private final DatabaseReference accountRef;  // Reference to accounts in Firebase database
	private final SharedPreferences sharedPreferences;  // Local storage for game data
	private Account currentAccount;  // Current user's account data

	/**
	 * Constructor initializes the game controller with all necessary components
	 *
	 * @param context Android context
	 * @param intentFromMainMenu Intent containing the firebase data that was retrieved in the main menu
	 * @param screenWidth Width of the screen in pixels
	 * @param screenHeight Height of the screen in pixels
	 * @param tvTimer TextView to display the current timer
	 * @param tvBestTime TextView to display the best time
	 */
	public GameController(
		Context context,
		Intent intentFromMainMenu,
		int screenWidth,
		int screenHeight,
		TextView tvTimer,
		TextView tvBestTime
	) {
		super(context);

		// Store references to UI components and intent data
		this.intentFromMainMenu = intentFromMainMenu;
		this.tvTimer = tvTimer;
		this.tvBestTime = tvBestTime;

		// Initialize timer and scoring
		this.timer = 0;
		this.bestTime = Double.MAX_VALUE;  // Use max value as "no best time set" indicator
		this.shouldTimerCount = true;

		// Setup rendering components
		this.background = new Paint();
		background.setColor(isDarkMode()? Color.BLACK : Color.WHITE);  // Adapt to system theme
		this.surfaceHolder = getHolder();
		this.renderThread = new Thread(this);
		this.isRenderThreadRunning = true;
		this.drawables = new ArrayList<>();
		this.updatables = new ArrayList<>();
		this.lastTime = System.nanoTime() / 1e9;  // Convert nanoseconds to seconds

		// Initialize animation mamager
		this.animationManager = new TimedAnimationManager();

		// Setup Firebase components
		this.mAuth = FirebaseAuth.getInstance();
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		this.accountRef = database.getReference("accounts");
		this.sharedPreferences = context.getSharedPreferences(findLastConnectedUser(), 0);

		// Setup periodic save operations (every 1 second)
		animationManager.addLoopedAction(new LoopedAction(this::updateSavesInPreferences, 1.0));
		animationManager.addLoopedAction(new LoopedAction(this::updateSavesInAccount, 1.0));

		// Load saved game state
		getAllSavedValuesFromSharedPreferences();
		findCurrentAccount();

		// Setup screen geometry for 3D projection
		ScreenGeometryManager.getInstance().setScreenSize(screenWidth, screenHeight);
		ScreenGeometryManager.getInstance().setFocalLength(Constants.FOCAL_LENGTH);

		// Start the render thread
		renderThread.start();
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
	 * Main rendering method that draws one frame of the game
	 * @param deltaTime Time elapsed since last frame in seconds
	 */
	private void drawSurface(double deltaTime) {
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
	 * Provides access to the animation manager for other components
	 * @return The TimedAnimationManager instance
	 */
	public TimedAnimationManager getAnimationManager() {
		return animationManager;
	}

	/**
	 * Updates the game timer and refreshes the timer display
	 * @param deltaTime Time elapsed since last update in seconds
	 */
	private void updateTimer(double deltaTime) {
		timer += deltaTime;

		// Format time as MM:SS
		String minutes = (int)(timer/60) < 10 ? "0" + (int)(timer/60) : "" + (int)(timer/60);
		String seconds = (int)(timer % 60) < 10 ? "0" + (int)(timer % 60) : "" + (int)(timer % 60);

		// Update UI on main thread
		tvTimer.post(() -> tvTimer.setText("⏱ " + minutes + ":" + seconds));
	}

	/**
	 * Resets the timer to zero and updates the display
	 */
	public void resetTimer() {
		timer = 0;
		tvTimer.post(() -> tvTimer.setText("⏱ 00:00"));
	}

	/**
	 * Controls whether the timer should be counting or paused
	 * @param stopTimer true to stop the timer, false to resume counting
	 */
	public void stopTimer(boolean stopTimer) {
		this.shouldTimerCount = !stopTimer;
	}

	/**
	 * Updates the best time display with a new time
	 * @param newBestTime The new best time to display
	 */
	private void updateBestTime(double newBestTime) {
		// Only update if we have a valid time
		if(newBestTime != Double.MAX_VALUE && newBestTime != 0.0) {
			// Format time as MM:SS
			String minutes = (int)(newBestTime/60) < 10 ? "0" + (int)(newBestTime/60) : "" + (int)(newBestTime/60);
			String seconds = (int)(newBestTime % 60) < 10 ? "0" + (int)(newBestTime % 60) : "" + (int)(newBestTime % 60);

			// Update UI on main thread with trophy emoji
			tvBestTime.post(() -> tvBestTime.setText("\uD83C\uDFC6 " + minutes + ":" + seconds));
		}
	}

	/**
	 * Called when the cube is solved. Updates best time if current time is better.
	 */
	public void noticedCubeIsSolved() {
		if(timer > 0) {
			// Check if this is a new personal best
			if(bestTime > timer) {
				bestTime = timer;
				updateBestTime(timer);

				// Update Firebase account if logged in and this beats their best time
				if(currentAccount != null && currentAccount.getBestTime() > bestTime) {
					currentAccount.setBestTime(bestTime);
					updateSavedAccountInDatabase();
				}
				timer = 0;
				updateSavesInPreferences();
			}
			timer = 0;
		}
	}

	/**
	 * Records a rotation operation performed by the player
	 * @param rotationOperation The rotation operation to save
	 */
	public void saveAnotherRotation(RotationOperation rotationOperation) {
		rotationOperations.add(rotationOperation);
	}

	/**
	 * Clears all saved rotation operations
	 */
	public void clearAllSavedRotations() {
		rotationOperations.clear();
	}

	/**
	 * Loads saved game state from SharedPreferences and Intent data.
	 */
	private void getAllSavedValuesFromSharedPreferences() {
		// Load timer from local storage
		if(sharedPreferences.contains("timer")) {
			this.timer = Double.parseDouble(sharedPreferences.getString("timer", null));
		}

		// Compare timestamps to determine which data is more recent
		long localTimestamp = Long.parseLong(sharedPreferences.getString("timestampOfSave", "0"));
		long firebaseTimestamp = intentFromMainMenu.getExtras().getLong("timestampOfSave");

		// Use Firebase data if it's more recent
		if(firebaseTimestamp > localTimestamp) {
			timer = intentFromMainMenu.getExtras().getDouble("timer");
		}

		// If timer is 0, don't count (game hasn't started)
		if(timer == 0) {
			shouldTimerCount = false;
		}

		// Load best time
		if(sharedPreferences.contains("bestTime")) {
			this.bestTime = Double.parseDouble(sharedPreferences.getString("bestTime", null));
		}
		updateBestTime(bestTime);

		// Load rotation operations, preferring Firebase data if more recent
		String rotations = firebaseTimestamp > localTimestamp ?
			intentFromMainMenu.getExtras().getString("rotations") :
			sharedPreferences.getString("rotations", "");
		this.rotationOperations = RotationOperation.valuesOf(rotations != null ? rotations : "");
	}

	/**
	 * Determines the user ID for SharedPreferences storage.
	 * Uses Firebase UID if logged in, otherwise uses saved anonymous ID.
	 * @return User ID string for preferences key
	 */
	private String findLastConnectedUser() {
		if(mAuth.getCurrentUser() != null) {
			return mAuth.getCurrentUser().getUid();
		}
		SharedPreferences sharedPreferences = getContext().getSharedPreferences("connectedUser", 0);
		return sharedPreferences.getString("userId", "anonymous");
	}

	/**
	 * Saves the current account data to Firebase database
	 */
	private void updateSavedAccountInDatabase() {
		accountRef.child(mAuth.getCurrentUser().getUid()).setValue(currentAccount);
	}

	/**
	 * Saves current game state to local SharedPreferences
	 */
	private void updateSavesInPreferences() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("timer", String.valueOf(timer));
		editor.putString("bestTime", String.valueOf(bestTime));
		editor.putString("rotations", makeRotationOperationsAString(rotationOperations));
		editor.putString("timestampOfSave", String.valueOf(System.currentTimeMillis()));
		editor.apply();
	}

	/**
	 * Updates the current account with latest game state and saves to Firebase
	 */
	private void updateSavesInAccount() {
		if(currentAccount != null) {
			currentAccount.setSavedRotations(makeRotationOperationsAString(rotationOperations));
			currentAccount.setTimer(timer);
			currentAccount.setTimestampOfSave(System.currentTimeMillis());
			updateSavedAccountInDatabase();
		}
	}

	/**
	 * Converts a list of rotation operations to a string format for storage
	 * @param rotationOperations List of rotation operations to convert
	 * @return a String that represents the rotations
	 */
	private String makeRotationOperationsAString(List<RotationOperation> rotationOperations) {
		StringBuilder output = new StringBuilder();
		for(RotationOperation rotationOperation : rotationOperations) {
			output.append(rotationOperation.toString()).append("~");
		}
		return output.toString();
	}

	/**
	 * @return List of saved rotation operations
	 */
	public List<RotationOperation> getSavedRotationOperations() {
		return rotationOperations;
	}

	/**
	 * Retrieves the current user's account from Firebase database.
	 * Also handles account synchronization and best score reset functionality.
	 */
	public void findCurrentAccount() {
		accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// Search through all accounts to find current user's account
				for (DataSnapshot data : dataSnapshot.getChildren()) {
					Account account = data.getValue(Account.class);
					if (mAuth.getCurrentUser() != null && account.getUserId().equals(mAuth.getCurrentUser().getUid())) {
						currentAccount = account;

						// Handle best score reset if requested
						if(account.shouldResetBestScore()) {
							account.setResetBestScore(false);
							account.setBestTime(Double.MAX_VALUE);
							bestTime = Double.MAX_VALUE;
							updateSavesInPreferences();
							updateSavedAccountInDatabase();
						}

						// Synchronize best times between local and Firebase
						if(account.getBestTime() < bestTime) {
							// Firebase has better time, update local
							bestTime = account.getBestTime();
							updateSavedAccountInDatabase();
							updateBestTime(bestTime);
						}
						else {
							// Local has better time, update Firebase
							account.setBestTime(bestTime);
							updateSavedAccountInDatabase();
						}
					}
				}
			}

			@Override
			public void onCancelled(DatabaseError ignored) {
			}
		});
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
			renderThread = new Thread(this);
			renderThread.start();
		}
	}

	/**
	 * Main game loop that runs in a separate thread.
	 * Handles timing, updates, and rendering
	 */
	@Override
	public void run() {
		while (isRenderThreadRunning) {
			// Calculate delta time since last frame
			double currentTime = System.nanoTime() / 1e9;

			// Update timer if it should be counting
			if(shouldTimerCount) {
				updateTimer(currentTime - lastTime);
			}

			// Update animation manager and render the frame
			animationManager.update(currentTime - lastTime);
			drawSurface(currentTime - lastTime);

			// Store current time for next frame's delta calculation
			lastTime = currentTime;
		}
	}
}