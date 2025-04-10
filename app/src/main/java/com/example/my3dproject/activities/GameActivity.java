package com.example.my3dproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.my3dproject.Controller;
import com.example.my3dproject.R;
import com.example.my3dproject.ScreenTouchListener;
import com.example.my3dproject.TimedAction;
import com.example.my3dproject.drawables.RubiksCube;
import com.google.android.material.navigation.NavigationView;

public class GameActivity extends AppCompatActivity {

	private FrameLayout frameLayout;
	private DrawerLayout drawerLayout;
	private NavigationView navView;
	private ImageButton menuButton;
	private TextView tvTimer;
	private Controller controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_activities);
		frameLayout = findViewById(R.id.frame);
		frameLayout.setOnTouchListener(ScreenTouchListener.getInstance());
		drawerLayout = findViewById(R.id.drawerLayout);
		navView = findViewById(R.id.navView);
		menuButton = findViewById(R.id.btnMenu);

		menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

		tvTimer = findViewById(R.id.tvTimer);

		navView.setNavigationItemSelectedListener(item -> {
			int id = item.getItemId();

			if (id == R.id.nav_stats) {
//					Intent intent = new Intent(GameActivity.this, StatsActivity.class);
//					startActivity(intent);
			} else if (id == R.id.nav_settings) {
				// Placeholder – launch settings screen or show a dialog
			} else if (id == R.id.nav_exit) {
				finish();
			}

			drawerLayout.closeDrawer(GravityCompat.START);
			return true;
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocused) {
		super.onWindowFocusChanged(hasFocused);
		if (hasFocused && controller == null) {
			this.controller = new Controller(this, frameLayout.getWidth(), frameLayout.getHeight(), tvTimer);
			RubiksCube rubiksCube = new RubiksCube(0, 0, 0, 50, controller);
			findViewById(R.id.btnShuffle).setOnClickListener(view -> rubiksCube.shuffle());
			findViewById(R.id.btnReset).setOnClickListener(view -> rubiksCube.solve());
			controller.addDrawables(rubiksCube);
			frameLayout.addView(controller, 0);
		}
	}

}