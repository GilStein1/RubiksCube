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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.my3dproject.Account;
import com.example.my3dproject.DefaultController;
import com.example.my3dproject.R;
import com.example.my3dproject.RubiksCubeManagerForSimpleBackgroundRotation;
import com.example.my3dproject.drawables.RubiksCube;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Optional;

/**
 * MainActivity serves as the main entry point for the 3D Rubik's Cube application.
 * This activity handles user authentication, displays a background 3D cube animation,
 * and provides navigation to other activities (game, statistics, sign up).
 */
public class MainActivity extends AppCompatActivity {

	// Firebase authentication instance for user login/logout operations
	private FirebaseAuth mAuth;

	// Firebase database reference pointing to the "accounts"
	private DatabaseReference accountRef;

	// User's saved game state retrieved from Firebase
	private String rotationsRetrievedFromFirebase;
	private double timerRetrievedFromFirebase;
	private long timestampOfSave;

	// UI components for background animation and login dialog
	private FrameLayout frameLayout;  // Container for the 3D background cube
	private DefaultController controller;  // Manages the 3D rendering and animation

	// Activity result launchers for handling results from other activities
	private ActivityResultLauncher<Intent> signUpActivityLauncher;
	private ActivityResultLauncher<Intent> statisticsActivityLauncher;
	private ActivityResultLauncher<Intent> gameActivityLauncher;

	// Login dialog and its UI components
	private Dialog logInDialog;
	private EditText etEmail, etPassword;
	private TextView tvSignUp;

	/**
	 * Called when the activity is first created.
	 * Initializes Firebase, sets up the UI, and retrieves user account data.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Force left-to-right layout direction and portrait orientation
		getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Initialize Firebase authentication and database references
		this.mAuth = FirebaseAuth.getInstance();
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		this.accountRef = database.getReference("accounts");

		// Initialize game state variables with default values
		this.rotationsRetrievedFromFirebase = "";
		this.timerRetrievedFromFirebase = 0;
		this.timestampOfSave = 0;

		// Load current user's account data and initialize UI components
		findCurrentAccount();
		initViews();
	}

	/**
	 * Initializes UI components and sets up activity result launchers.
	 */
	private void initViews() {
		// Get reference to the frame layout that will contain the background cube
		frameLayout = findViewById(R.id.flBackgroundCube);

		// Set up launcher for sign up activity
		this.signUpActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
			if(result.getResultCode() == RESULT_CANCELED) {
				Toast.makeText(MainActivity.this, "sign up was canceled", Toast.LENGTH_SHORT).show();
			}
		});

		// Set up launchers for statistics and game activities (no specific result handling needed)
		this.statisticsActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});
		this.gameActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {});

		// Initialize the login dialog
		initDialog();
	}

	/**
	 * Creates and configures the login dialog with email/password fields
	 * and a clickable "Sign up here" link.
	 */
	private void initDialog() {
		// Create and configure the login dialog
		logInDialog = new Dialog(this);
		logInDialog.setContentView(R.layout.login_dialog);
		logInDialog.setTitle("Log In");
		logInDialog.setCancelable(true);

		// Get references to dialog UI components
		etEmail = logInDialog.findViewById(R.id.etEmail);
		etPassword = logInDialog.findViewById(R.id.etPassword);
		tvSignUp = logInDialog.findViewById(R.id.tvSignUp);

		// Create clickable "Sign up here" text
		String fullText = "Don't have an account? Sign up here";
		String clickablePart = "Sign up here";

		SpannableString spannableString = new SpannableString(fullText);

		// Define the click behavior for the "Sign up here" text
		ClickableSpan clickableSpan = new ClickableSpan() {
			@Override
			public void onClick(View view) {
				// Close dialog, clean up controller, and launch sign up activity
				logInDialog.cancel();
				controller.onDestroy();
				controller = null;
				Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
				signUpActivityLauncher.launch(intent);
			}

			/**
			 * Customizes the appearance of the clickable text (black color with underline)
			 */
			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setColor(ContextCompat.getColor(MainActivity.this, R.color.black));
				ds.setUnderlineText(true);
			}
		};

		// Apply the clickable span to the "Sign up here" portion of the text
		int startIndex = fullText.indexOf(clickablePart);
		int endIndex = startIndex + clickablePart.length();
		spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Configure the TextView to handle clicks and remove highlight color
		tvSignUp.setText(spannableString);
		tvSignUp.setMovementMethod(LinkMovementMethod.getInstance());
		tvSignUp.setHighlightColor(Color.TRANSPARENT);
	}

	/**
	 * Initializes the 3D background cube animation.
	 * Creates a DefaultController with a RubiksCube and animation manager.
	 */
	private void initBackgroundAnimation() {
		if (controller == null) {
			// Create the 3D controller with frame layout dimensions
			this.controller = new DefaultController(this, frameLayout.getWidth(), frameLayout.getHeight());

			// Create a Rubik's cube at origin with size 80
			RubiksCube rubiksCube = new RubiksCube(0, 0, 0, 80);

			// Create a rubiks cube manager for simple background rotation
			RubiksCubeManagerForSimpleBackgroundRotation rubiksCubeManager = new RubiksCubeManagerForSimpleBackgroundRotation(rubiksCube, controller);

			// Add cube and rubiks cube manager to the controller
			controller.addDrawables(rubiksCube);
			controller.addUpdatableComponents(rubiksCubeManager);

			// Add the controller view to the frame layout
			frameLayout.addView(controller, 0);
		}
	}

	/**
	 * Called when the window focus changes.
	 * Initializes the background animation when the activity gains focus.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocused) {
		super.onWindowFocusChanged(hasFocused);
		if(hasFocused) {
			initBackgroundAnimation();
		}
	}

	/**
	 * Called when the activity is paused.
	 * Stops the background animation
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
	 * Stops the background animation
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
	 * Resumes the 3D cube animation.
	 */
	@Override
	public void onResume() {
		super.onResume();
		if(controller != null) {
			controller.onResume();
		}
	}

	/**
	 * Attempts to log in a user with the provided email and password.
	 * On successful login, saves the user ID to SharedPreferences.
	 *
	 * @param email User's email address
	 * @param password User's password
	 */
	private void logUserIn(String email, String password) {
		if(email.isEmpty() || password.isEmpty()) { //if the email or password are not valid, send an error message
			Toast.makeText(MainActivity.this, "Please write valid email and password", Toast.LENGTH_SHORT).show();
			return;
		}
		mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
			if (task.isSuccessful()) {
				// Save the logged-in user's ID to SharedPreferences
				SharedPreferences.Editor editor = getSharedPreferences("connectedUser", 0).edit();
				editor.putString("userId", mAuth.getCurrentUser().getUid());
				editor.apply();

				// Close dialog and show success message
				logInDialog.cancel();
				Toast.makeText(MainActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
			} else {
				// Show error message on login failure
				Toast.makeText(MainActivity.this, "Error while logging in", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * Retrieves the current user's account data from Firebase.
	 * Searches through all accounts to find the one matching the current user's ID,
	 * then loads their saved game state (rotations, timer, timestamp).
	 */
	public void findCurrentAccount() {
		accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// Iterate through all accounts in the database
				for (DataSnapshot data : dataSnapshot.getChildren()) {
					Account account = data.getValue(Account.class);

					// Check if this account belongs to the current authenticated user
					if (mAuth.getCurrentUser() != null && account.getUserId().equals(mAuth.getCurrentUser().getUid())) {
						// Load the user's saved game state
						rotationsRetrievedFromFirebase = account.getSavedRotations();
						timerRetrievedFromFirebase = account.getTimer();
						timestampOfSave = account.getTimestampOfSave();
					}
				}
			}

			@Override
			public void onCancelled(DatabaseError ignored) {
				// Handle database errors (currently ignored)
			}
		});
	}

	/**
	 * Button click handler to show the login dialog.
	 */
	public void logInByButton(View view) {
		logInDialog.show();
	}

	/**
	 * Button click handler to open the statistics activity.
	 */
	public void openStatsByButton(View view) {
		Intent intent = new Intent(this, StatsActivity.class);
		statisticsActivityLauncher.launch(intent);
	}

	/**
	 * Button click handler to start the game activity.
	 * Cleans up the background controller and passes saved game data to the game.
	 */
	public void startGameByButton(View view) {
		// Clean up the background animation controller
		controller.onDestroy();
		controller = null;

		// Create intent and pass saved game data
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("rotations", rotationsRetrievedFromFirebase);
		intent.putExtra("timer", timerRetrievedFromFirebase);
		intent.putExtra("timestampOfSave", timestampOfSave);
		gameActivityLauncher.launch(intent);
	}

	/**
	 * Button click handler for the login button inside the dialog.
	 * Retrieves email and password from input fields and attempts login.
	 */
	public void logInByButtonInDialog(View view) {
		String email = etEmail.getText().toString();
		String password = etPassword.getText().toString();
		logUserIn(email, password);
	}

	/**
	 * Button click handler to cancel the login dialog.
	 */
	public void cancelLoginDialogByButton(View view) {
		logInDialog.cancel();
		Toast.makeText(MainActivity.this, "log in was canceled", Toast.LENGTH_SHORT).show();
	}

}