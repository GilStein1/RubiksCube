package com.example.my3dproject.activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.my3dproject.Controller;
import com.example.my3dproject.R;
import com.example.my3dproject.ScreenTouchListener;
import com.example.my3dproject.drawables.RubiksCube;

public class GameActivity extends AppCompatActivity {

	private FrameLayout frameLayout;
	private Controller controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_activities);
		frameLayout = findViewById(R.id.frame);
		frameLayout.setOnTouchListener(ScreenTouchListener.getInstance());
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocused) {
		super.onWindowFocusChanged(hasFocused);
		if (hasFocused && controller == null) {
			this.controller = new Controller(this, frameLayout.getWidth(), frameLayout.getHeight());
			RubiksCube rubiksCube = new RubiksCube(0, 0, 0, 50, controller.getAnimationManager());
			findViewById(R.id.btnRandomize).setOnClickListener(view -> rubiksCube.randomize());
			findViewById(R.id.btnSolve).setOnClickListener(view -> rubiksCube.solve());
			controller.addDrawables(rubiksCube);
			frameLayout.addView(controller, 0);
		}
	}

}