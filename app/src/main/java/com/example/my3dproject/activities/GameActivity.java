package com.example.my3dproject.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.my3dproject.GameController;
import com.example.my3dproject.R;
import com.example.my3dproject.RubiksCubeManager;
import com.example.my3dproject.ScreenTouchListener;
import com.example.my3dproject.drawables.RubiksCube;
import com.google.android.material.navigation.NavigationView;

public class GameActivity extends AppCompatActivity {

	private FrameLayout frameLayout;
	private DrawerLayout drawerLayout;
	private NavigationView navView;
	private ImageButton menuButton;
	private TextView tvTimer, tvBestTime;
	private GameController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_activities);
		getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		frameLayout = findViewById(R.id.frame);
		frameLayout.setOnTouchListener(ScreenTouchListener.getInstance());
		drawerLayout = findViewById(R.id.drawerLayout);
		navView = findViewById(R.id.navView);

		menuButton = findViewById(R.id.btnMenu);
		menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

		tvTimer = findViewById(R.id.tvTimer);
		tvBestTime = findViewById(R.id.tvBestTime);
	}

	private void initGame() {
		if (controller == null) {
			this.controller = new GameController(this,getIntent(), frameLayout.getWidth(), frameLayout.getHeight(), tvTimer, tvBestTime);
			RubiksCube rubiksCube = new RubiksCube(0, 0, 0, 50);
			RubiksCubeManager rubiksCubeManager = new RubiksCubeManager(rubiksCube, controller);
			navView.setNavigationItemSelectedListener(item -> {
				int id = item.getItemId();
				if (id == R.id.nav_stats) {

				} else if (id == R.id.nav_reset) {
					rubiksCubeManager.solve();
				} else if (id == R.id.nav_exit) {
					finish();
				}

				drawerLayout.closeDrawer(GravityCompat.START);
				return true;
			});
			findViewById(R.id.btnShuffle).setOnClickListener(view -> rubiksCubeManager.shuffle());
			controller.addDrawables(rubiksCube);
			controller.addUpdatableComponents(rubiksCubeManager);
			frameLayout.addView(controller, 0);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocused) {
		super.onWindowFocusChanged(hasFocused);
		if(hasFocused) {
			initGame();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if(controller != null) {
			controller.onPause();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(controller != null) {
			controller.onDestroy();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(controller != null) {
			controller.onResume();
		}
	}

}