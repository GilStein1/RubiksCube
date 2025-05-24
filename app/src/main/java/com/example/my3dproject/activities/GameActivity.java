package com.example.my3dproject.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.my3dproject.GameController;
import com.example.my3dproject.R;
import com.example.my3dproject.RubiksCubeManager;
import com.example.my3dproject.ScreenTouchListener;
import com.example.my3dproject.drawables.RubiksCube;
import com.google.android.material.navigation.NavigationView;

/**
 * The game activity, displays the rubik's cube and timer and all other
 * buttons and views that are used in the game
 */
public class GameActivity extends AppCompatActivity {

	// The controller for the game
	private GameController controller;
	// The frame layout that the game is drawn onto
	private FrameLayout frameLayout;
	// The drawer layout for the popup menu from the side
	private DrawerLayout drawerLayout;
	// the navigation view with the buttons that are in the popup menu
	private NavigationView navView;
	// The button to open the popup menu
	private ImageButton menuButton;
	// The timer and high score views
	private TextView tvTimer, tvBestTime;
	// The launcher to get to the stats activity
	private ActivityResultLauncher<Intent> statsLauncher;

	/**
	 * Called when the activity is first created.
	 * Initializes UI components, Firebase instances, and the game animations.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_activities);
		// Force left-to-right layout direction and portrait orientation
		getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initViews(); // Calls for views initialization
	}

	/**
	 * A method for initializing the views in the activity.
	 */
	private void initViews() {
		frameLayout = findViewById(R.id.frame);
		frameLayout.setOnTouchListener(ScreenTouchListener.getInstance());
		drawerLayout = findViewById(R.id.drawerLayout);
		navView = findViewById(R.id.navView);
		menuButton = findViewById(R.id.btnMenu);
		menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
		tvTimer = findViewById(R.id.tvTimer);
		tvBestTime = findViewById(R.id.tvBestTime);
		statsLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});
	}

	/**
	 * A method for initializing the Rubik's Cube game.
	 */
	private void initGame() {
		if (controller == null) {
			this.controller = new GameController(this,getIntent(), frameLayout.getWidth(), frameLayout.getHeight(), tvTimer, tvBestTime);
			RubiksCube rubiksCube = new RubiksCube(0, 0, 0, 50);
			RubiksCubeManager rubiksCubeManager = new RubiksCubeManager(rubiksCube, controller);
			navView.setNavigationItemSelectedListener(item -> { // Starts the navigation view
				int id = item.getItemId();
				if (id == R.id.nav_stats) {
					Intent intent = new Intent(this, StatsActivity.class);
					statsLauncher.launch(intent);
				} else if (id == R.id.nav_reset) {
					rubiksCubeManager.solve();
				} else if (id == R.id.nav_exit) {
					finish();
				}

				drawerLayout.closeDrawer(GravityCompat.START);
				return true;
			});
			// Sets the shuffle button's OnClickListener to shuffle the cube
			findViewById(R.id.btnShuffle).setOnClickListener(view -> rubiksCubeManager.shuffle());
			controller.addDrawables(rubiksCube);
			controller.addUpdatableComponents(rubiksCubeManager);
			frameLayout.addView(controller, 0);
		}
	}

	/**
	 * Called when the window focus changes.
	 * Initializes the game when the activity gains focus.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocused) {
		super.onWindowFocusChanged(hasFocused);
		if(hasFocused) {
			initGame();
		}
	}

	/**
	 * Called when the activity is paused.
	 * Stops the game
	 */
	@Override
	public void onPause() {
		super.onPause();
		if(controller != null) {
			controller.onPause();
		}
	}

	/**
	 * Called when the activity is destroyed.
	 * Stops the game.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(controller != null) {
			controller.onDestroy();
		}
	}

	/**
	 * Called when the activity is resumed.
	 * Resumes the game.
	 */
	@Override
	public void onResume() {
		super.onResume();
		if(controller != null) {
			controller.onResume();
		}
	}

}