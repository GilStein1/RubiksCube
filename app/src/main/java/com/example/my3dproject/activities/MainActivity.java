package com.example.my3dproject.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.my3dproject.DefaultController;
import com.example.my3dproject.R;
import com.example.my3dproject.RubiksCubeManager;
import com.example.my3dproject.RubiksCubeManagerForSimpleBackgroundRotation;
import com.example.my3dproject.ScreenTouchListener;
import com.example.my3dproject.drawables.RubiksCube;

public class MainActivity extends AppCompatActivity {

	private FrameLayout frameLayout;
	private DefaultController controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		frameLayout = findViewById(R.id.flBackgroundCube);
	}

	private void initBackgroundAnimation() {
		if (controller == null) {
			this.controller = new DefaultController(this, frameLayout.getWidth(), frameLayout.getHeight());
			RubiksCube rubiksCube = new RubiksCube(0, 0, 0, 80);
			RubiksCubeManagerForSimpleBackgroundRotation rubiksCubeManager = new RubiksCubeManagerForSimpleBackgroundRotation(rubiksCube, controller);
			controller.addDrawables(rubiksCube);
			controller.addUpdatableComponents(rubiksCubeManager);
			frameLayout.addView(controller, 0);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocused) {
		super.onWindowFocusChanged(hasFocused);
		if(hasFocused) {
			initBackgroundAnimation();
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