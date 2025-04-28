package com.example.my3dproject.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SignUpActivity extends AppCompatActivity {

	private EditText etName, etEmail, etPassword;
	private ImageView ivProfilePicture;
	private String profilePicture;
	private FirebaseAuth mAuth;
	private DatabaseReference accountRef;
	private ActivityResultLauncher<Intent> profilePictureActivityLauncher;
	private List<Account> accounts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initializeViews();
		this.profilePicture = "";
		mAuth = FirebaseAuth.getInstance();
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		accountRef = database.getReference("accounts");
		this.profilePictureActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::atReturnFromCamera);
	}

	@Override
	public void onStart() {
		super.onStart();
		getAllAccounts();
	}

	private void initializeViews() {
		etName = findViewById(R.id.etName);
		etEmail = findViewById(R.id.etEmail);
		etPassword = findViewById(R.id.etPassword);
		ivProfilePicture = findViewById(R.id.ivProfilePicture);
	}

	public void createAccount() {
		String name = etName.getText().toString();
		Account account = new Account(mAuth.getCurrentUser().getUid(), name);
		account.setProfilePicture(profilePicture);
		accountRef.child(mAuth.getCurrentUser().getUid()).setValue(account);
	}

	private void atReturnFromCamera(ActivityResult result) {
		if(result.getResultCode() == RESULT_OK) {
			Uri imageUri = result.getData().getData();
			if(imageUri != null) {
				try {
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
					ivProfilePicture.setImageBitmap(bitmap);
					profilePicture = BitmapUtil.convertTo64Base(bitmap);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public void signUpByButtonInSignUp(View view) {
		boolean successfulSignUp = createAccount(etEmail.getText().toString(), etPassword.getText().toString());
		if (successfulSignUp) {
			onStart();
		}
	}

	private boolean createAccount(String email, String password) {
		AtomicBoolean success = new AtomicBoolean(true);
		mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
			if (task.isSuccessful()) {
				Toast.makeText(this, "Authentication success.", Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor editor = getSharedPreferences("connectedUser", 0).edit();
				editor.putString("userId", mAuth.getCurrentUser().getUid());
				editor.apply();
				createAccount();
				Intent intent = getIntent();
				setResult(RESULT_OK, intent);
				finish();
			} else {
				Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
				success.set(false);
			}
		});
		return success.get();
	}

	public void setImageByButton(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		profilePictureActivityLauncher.launch(intent);
	}

	public void cancelByButton(View view) {
		setResult(RESULT_CANCELED);
		finish();
	}

	public void getAllAccounts() {
		accounts = new ArrayList<>();
		accountRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot data : dataSnapshot.getChildren()) {
					try {
						Account a = data.getValue(Account.class);
						accounts.add(a);
					} catch (DatabaseException ignored) {

					}
				}
			}

			@Override
			public void onCancelled(DatabaseError ignored) {
			}
		});
	}

}