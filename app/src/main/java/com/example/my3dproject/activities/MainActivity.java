package com.example.my3dproject.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.my3dproject.R;

public class MainActivity extends AppCompatActivity {

	ActivityResultLauncher<Intent> activityResultLauncher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

		});
	}

	public void startGame(View view) {
		Intent intent = new Intent(this, GameActivities.class);
		activityResultLauncher.launch(intent);
	}

}