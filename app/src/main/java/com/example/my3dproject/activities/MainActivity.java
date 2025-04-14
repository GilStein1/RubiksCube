package com.example.my3dproject.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.my3dproject.DefaultController;
import com.example.my3dproject.R;
import com.example.my3dproject.RubiksCubeManagerForSimpleBackgroundRotation;
import com.example.my3dproject.drawables.RubiksCube;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

	private FirebaseAuth mAuth;
	private FrameLayout frameLayout;
	private DefaultController controller;
	private ActivityResultLauncher<Intent> signUpActivityLauncher;
	private ActivityResultLauncher<Intent> gameActivityLauncher;
	private Dialog logInDialog;
	private EditText etEmail, etPassword;
	private TextView tvSignUp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		this.mAuth = FirebaseAuth.getInstance();
		initViews();
	}

	private void initViews() {
		frameLayout = findViewById(R.id.flBackgroundCube);
		this.signUpActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});
		this.gameActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});
		initDialog();
	}

	private void initDialog() {
		logInDialog = new Dialog(this);
		logInDialog.setContentView(R.layout.login_dialog);
		logInDialog.setTitle("Log In");
		logInDialog.setCancelable(false);
		etEmail = logInDialog.findViewById(R.id.etEmail);
		etPassword = logInDialog.findViewById(R.id.etPassword);
		tvSignUp = logInDialog.findViewById(R.id.tvSignUp);
		String fullText = "Don't have an account? Sign up here";
		String clickablePart = "Sign up here";

		SpannableString spannableString = new SpannableString(fullText);

		ClickableSpan clickableSpan = new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				logInDialog.cancel();
				controller.onDestroy();
				controller = null;
				Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
				signUpActivityLauncher.launch(intent);
			}

			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setColor(ContextCompat.getColor(MainActivity.this, R.color.black));
				ds.setUnderlineText(true);
			}
		};
		int startIndex = fullText.indexOf(clickablePart);
		int endIndex = startIndex + clickablePart.length();
		spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvSignUp.setText(spannableString);
		tvSignUp.setMovementMethod(LinkMovementMethod.getInstance());
		tvSignUp.setHighlightColor(Color.TRANSPARENT);
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

	private void logUserIn(String email, String password) {
		mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
			if (task.isSuccessful()) {
				SharedPreferences.Editor editor = getSharedPreferences("connectedUser", 0).edit();
				editor.putString("userId", mAuth.getCurrentUser().getUid());
				editor.apply();
				logInDialog.cancel();
				Toast.makeText(MainActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MainActivity.this, "Error while logging in", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void logInByButton(View view) {
		logInDialog.show();
	}

	public void startGameByButton(View view) {
		controller.onDestroy();
		controller = null;
		Intent intent = new Intent(this, GameActivity.class);
		gameActivityLauncher.launch(intent);
	}

	public void logInByButtonInDialog(View view) {
		String email = etEmail.getText().toString();
		String password = etPassword.getText().toString();
		logUserIn(email, password);
	}

	public void cancelLoginDialogByButton(View view) {
		logInDialog.cancel();
		Toast.makeText(MainActivity.this, "sign up was canceled", Toast.LENGTH_SHORT).show();
	}

}