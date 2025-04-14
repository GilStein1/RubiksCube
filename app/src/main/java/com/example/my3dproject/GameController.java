package com.example.my3dproject;

import android.content.Context;
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

public class GameController extends SurfaceView implements Runnable {

	private final int screenWidth, screenHeight;
	private final TextView tvTimer, tvBestTime;
	private double timer;
	private double bestTime;
	private boolean shouldTimerCount;
	private boolean isGamePaused;
	private final Paint background;
	private final SurfaceHolder surfaceHolder;
	private Thread renderThread;
	private volatile boolean isRenderThreadRunning;
	private final List<Drawable> drawables;
	private final List<UpdatableComponent> updatables;
	private Canvas canvas;
	private double lastTime;
	private final TimedAnimationManager animationManager;
	private List<RotationOperation> rotationOperations;
	private FirebaseAuth mAuth;
	private DatabaseReference accountRef;
	private final SharedPreferences sharedPreferences;
	private Account currentAccount;

	public GameController(
		Context context,
		int screenWidth,
		int screenHeight,
		TextView tvTimer,
		TextView tvBestTime
	) {
		super(context);
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.tvTimer = tvTimer;
		this.tvBestTime = tvBestTime;
		this.timer = 0;
		this.bestTime = Double.MAX_VALUE;
		this.shouldTimerCount = true;
		this.isGamePaused = false;
		this.background = new Paint();
		background.setColor(isDarkMode()? Color.BLACK : Color.WHITE);
		this.surfaceHolder = getHolder();
		this.renderThread = new Thread(this);
		this.isRenderThreadRunning = true;
		this.drawables = new ArrayList<>();
		this.updatables = new ArrayList<>();
		this.lastTime = System.nanoTime() / 1e9;
		this.animationManager = new TimedAnimationManager();
		this.mAuth = FirebaseAuth.getInstance();
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		this.accountRef = database.getReference("accounts");
		this.sharedPreferences = context.getSharedPreferences(findLastConnectedUser(), 0);
//		this.sharedPreferences.edit().putString("bestTime", "1000").apply();
		animationManager.addLoopedAction(new LoopedAction(this::updateSavesInPreferences, 1.0));
		getAllSavedValuesFromSharedPreferences();
		findCurrentAccount();
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

	public void addUpdatableComponents(UpdatableComponent... updatableComponents) {
		this.updatables.addAll(Arrays.asList(updatableComponents));
	}

	private void drawSurface(double deltaTime) {
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

	private void updateBestTime(double newBestTime) {
		if(newBestTime != Double.MAX_VALUE && newBestTime != 0.0) {
			String minutes = (int)(newBestTime/60) < 10 ? "0" + (int)(newBestTime/60) : "" + (int)(newBestTime/60);
			String seconds = (int)(newBestTime % 60) < 10 ? "0" + (int)(newBestTime % 60) : "" + (int)(newBestTime % 60);
			tvBestTime.post(() -> tvBestTime.setText("\uD83C\uDFC6 Best Time: " + minutes + ":" + seconds));
		}
	}

	public void noticedCubeIsSolved() {
		if(timer > 0) {
			if(bestTime > timer) {
				bestTime = timer;
				updateBestTime(timer);
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

	public void pauseGame(boolean pauseGame) {
		this.isGamePaused = pauseGame;
	}

	public boolean isGamePaused() {
		return isGamePaused;
	}

	public void saveAnotherRotation(RotationOperation rotationOperation) {
		rotationOperations.add(rotationOperation);
		Log.d("RotationOps", "Current list size: " + rotationOperations.size());
	}

	public void clearAllSavedRotations() {
		rotationOperations.clear();
	}

	private void getAllSavedValuesFromSharedPreferences() {
		if(sharedPreferences.contains("timer")) {
			this.timer = Double.parseDouble(sharedPreferences.getString("timer", null));
		}
		if(timer == 0) {
			shouldTimerCount = false;
		}
		if(sharedPreferences.contains("bestTime")) {
			this.bestTime = Double.parseDouble(sharedPreferences.getString("bestTime", null));
		}
		updateBestTime(bestTime);
		this.rotationOperations = RotationOperation.valuesOf(sharedPreferences.getString("rotations", ""));
	}

	private String findLastConnectedUser() {
		if(mAuth.getCurrentUser() != null) {
			return mAuth.getCurrentUser().getUid();
		}
		SharedPreferences sharedPreferences = getContext().getSharedPreferences("connectedUser", 0);
		return sharedPreferences.getString("userId", "anonymous");
	}

	private void updateSavedAccountInDatabase() {
		accountRef.child(mAuth.getCurrentUser().getUid()).setValue(currentAccount);
		Log.w("Saved the account in database", "Saved the account in database");
	}

	private void updateSavesInPreferences() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("timer", String.valueOf(timer));
		editor.putString("bestTime", String.valueOf(bestTime));
		editor.putString("rotations", makeRotationOperationsAString(rotationOperations));
		editor.apply();
	}

	private String makeRotationOperationsAString(List<RotationOperation> rotationOperations) {
		StringBuilder output = new StringBuilder();
		for(RotationOperation rotationOperation : rotationOperations) {
			output.append(rotationOperation.toString()).append("~");
		}
		return output.toString();
	}

	public List<RotationOperation> getSavedRotationOperations() {
		return rotationOperations;
	}

	public void findCurrentAccount() {
		accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot data : dataSnapshot.getChildren()) {
					Account account = data.getValue(Account.class);
					if (mAuth.getCurrentUser() != null && account.getUserId().equals(mAuth.getCurrentUser().getUid())) {
						currentAccount = account;
						if(account.shouldResetBestScore()) {
							account.setResetBestScore(false);
							account.setBestTime(Double.MAX_VALUE);
							bestTime = Double.MAX_VALUE;
							updateSavesInPreferences();
							updateSavedAccountInDatabase();
						}
						if(account.getBestTime() < bestTime) {
							bestTime = account.getBestTime();
							updateSavedAccountInDatabase();
							updateBestTime(bestTime);
						}
						else {
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

	public void onPause() {
		isRenderThreadRunning = false;
	}

	public void onDestroy() {
		isRenderThreadRunning = false;
	}

	public void onResume() {
		if(!isRenderThreadRunning) {
			isRenderThreadRunning = true;
			renderThread = new Thread(this);
			renderThread.start();
		}
	}

	@Override
	public void run() {
		while (isRenderThreadRunning) {
			double currentTime = System.nanoTime() / 1e9;
			if(shouldTimerCount && !isGamePaused) {
				updateTimer(currentTime - lastTime);
			}
			animationManager.update(currentTime - lastTime);
			drawSurface(currentTime - lastTime);
			lastTime = currentTime;
		}
	}
}
