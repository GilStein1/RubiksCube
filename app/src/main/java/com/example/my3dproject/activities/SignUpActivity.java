package com.example.my3dproject.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.my3dproject.Account;
import com.example.my3dproject.BitmapUtil;
import com.example.my3dproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SignUpActivity handles user registration for the application.
 */
public class SignUpActivity extends AppCompatActivity {

	// UI input fields for user registration data
	private EditText etName, etEmail, etPassword;

	// ImageView to display the selected profile picture
	private ImageView ivProfilePicture;

	// Base64 encoded string of the selected profile picture
	private String profilePicture;

	// Firebase authentication instance for creating new user accounts
	private FirebaseAuth mAuth;

	// Firebase database reference pointing to the "accounts" node
	private DatabaseReference accountRef;

	// Activity result launcher for handling image selection from gallery
	private ActivityResultLauncher<Intent> profilePictureActivityLauncher;

	/**
	 * Called when the activity is first created.
	 * Initializes the UI, Firebase components, and activity result launchers.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		// Force left-to-right layout direction and portrait orientation
		getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Initialize UI components
		initializeViews();

		// Initialize profile picture as empty string (no image selected initially)
		this.profilePicture = "";

		// Initialize Firebase authentication and database references
		mAuth = FirebaseAuth.getInstance();
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		accountRef = database.getReference("accounts");

		// Set up activity result launcher for image selection
		this.profilePictureActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::atReturnFromCamera);
	}

	/**
	 * Initializes references to all UI components used in the sign-up process.
	 */
	private void initializeViews() {
		etName = findViewById(R.id.etName);
		etEmail = findViewById(R.id.etEmail);
		etPassword = findViewById(R.id.etPassword);
		ivProfilePicture = findViewById(R.id.ivProfilePicture);
	}

	/**
	 * Creates a new Account object and saves it to Firebase Realtime Database.
	 * This method is called after successful Firebase authentication.
	 * The account is stored using the user's UID as the key.
	 */
	private void createAccount() {
		// Get the user's name from the input field
		String name = etName.getText().toString();

		// Create new Account object with current user's UID and name
		Account account = new Account(mAuth.getCurrentUser().getUid(), name);

		// Set the profile picture (Base64 encoded string, empty if no image selected)
		account.setProfilePicture(profilePicture);

		// Save the account to Firebase database using the user's UID as the key
		accountRef.child(mAuth.getCurrentUser().getUid()).setValue(account);
	}

	/**
	 * Handles the result from the image selection activity.
	 *
	 * @param result The result from the image picker activity
	 */
	private void atReturnFromCamera(ActivityResult result) {
		if(result.getResultCode() == RESULT_OK) {
			// Get the URI of the selected image
			Uri imageUri = result.getData().getData();

			if(imageUri != null) {
				try {
					// Convert URI to Bitmap
					Bitmap bitmap = BitmapUtil.getBitmapOutOfImageUri(imageUri, getContentResolver());

					// Scale the bitmap to standard profile picture dimensions
					bitmap = Bitmap.createScaledBitmap(bitmap, BitmapUtil.PROFILE_PICTURE_WIDTH, BitmapUtil.PROFILE_PICTURE_HEIGHT, true);

					// Display the scaled image in the ImageView
					ivProfilePicture.setImageBitmap(bitmap);

					// Convert bitmap to Base64 string
					profilePicture = BitmapUtil.convertTo64Base(bitmap);

				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * Button click handler for the sign-up button.
	 * Initiates the account creation process with email and password.
	 *
	 * @param view The button that was clicked
	 */
	public void signUpByButtonInSignUp(View view) {
		createAccount(etEmail.getText().toString(), etPassword.getText().toString());
	}

	/**
	 * Creates a new Firebase user account with the provided email and password.
	 *
	 * @param email User's email address
	 * @param password User's password
	 */
	private void createAccount(String email, String password) {
		mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
			if (task.isSuccessful()) {
				// Show success message to user
				Toast.makeText(this, "Authentication success.", Toast.LENGTH_SHORT).show();

				// Save the new user's ID to SharedPreferences
				SharedPreferences.Editor editor = getSharedPreferences("connectedUser", 0).edit();
				editor.putString("userId", mAuth.getCurrentUser().getUid());
				editor.apply();

				// Create the account record in the database
				createAccount();

				// Return success result to the calling activity
				Intent intent = getIntent();
				setResult(RESULT_OK, intent);
				finish();
			} else {
				// Show error message if authentication fails
				Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * Button click handler for selecting a profile picture.
	 * Opens the device's image gallery for the user to select an image.
	 *
	 * @param view The button that was clicked
	 */
	public void setImageByButton(View view) {
		// Create intent to pick an image from the gallery
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");  // Filter to show only images

		// Launch the image picker activity
		profilePictureActivityLauncher.launch(intent);
	}

	/**
	 * Button click handler for canceling the sign-up process.
	 * Returns RESULT_CANCELED to the calling activity and closes this activity.
	 *
	 * @param view The button that was clicked
	 */
	public void cancelByButton(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}

}