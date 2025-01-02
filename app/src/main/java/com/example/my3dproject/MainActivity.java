package com.example.my3dproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.my3dproject.drawables.RubiksCube;

public class MainActivity extends AppCompatActivity {

	private FrameLayout frameLayout;
	private Controller controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		frameLayout = findViewById(R.id.frame);
		frameLayout.setOnTouchListener(ScreenTouchListener.getInstance());
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocused) {
		super.onWindowFocusChanged(hasFocused);
		if (hasFocused) {
			this.controller = new Controller(this, frameLayout.getWidth(), frameLayout.getHeight());
			controller.addDrawables(
				new RubiksCube(frameLayout.getWidth(), frameLayout.getHeight(), 0, 0, 100, 50)
			);
			frameLayout.addView(controller);
		}
	}
}