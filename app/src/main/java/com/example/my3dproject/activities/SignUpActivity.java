package com.example.my3dproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.my3dproject.Account;
import com.example.my3dproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SignUpActivity extends AppCompatActivity {

	private EditText etName, etEmail, etPassword;
	private FirebaseAuth mAuth;
	private DatabaseReference accountRef;
	private List<Account> accounts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		initializeViews();
		mAuth = FirebaseAuth.getInstance();
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		accountRef = database.getReference("accounts");
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
	}

	public void createBankAccount() {
		String name = etName.getText().toString();
		accountRef = accountRef.push();
		Account account = new Account(accountRef.getKey(), createRandomNumber(accounts), name, mAuth.getCurrentUser().getUid());
		accountRef.setValue(account);
	}

	private String createRandomNumber(List<Account> allExistingAccounts) {
		long randomNum = (int) (Math.random() * 1000000);
		String randomNumAsString = String.valueOf(randomNum);
		int numOfZerosMissing = 6 - randomNumAsString.length();
		for (int i = 0; i < numOfZerosMissing; i++) {
			randomNumAsString = '0' + randomNumAsString;
		}
		for (Account account : allExistingAccounts) {
			if (account.getNumber().equals(randomNumAsString)) {
				return createRandomNumber(allExistingAccounts);
			}
		}
		return randomNumAsString;
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
				createBankAccount();
				Intent intent = getIntent();
				intent.putExtra("userEmail", etEmail.getText().toString());
				intent.putExtra("userPassword", etPassword.getText().toString());
				setResult(RESULT_OK, intent);
				finish();
			} else {
				Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
				success.set(false);
			}
		});
		return success.get();
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