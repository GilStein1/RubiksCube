package com.example.my3dproject.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.my3dproject.Account;
import com.example.my3dproject.R;
import com.example.my3dproject.ScoresAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * StatsActivity displays a leaderboard showing user rankings based on their high scores.
 */
public class StatsActivity extends AppCompatActivity {

	// Firebase authentication instance for managing user authentication
	private FirebaseAuth mAuth;

	// Firebase database reference to the "accounts" node
	private DatabaseReference accountRef;

	// TextView to display the current user's ranking position
	private TextView tvUserPlace;

	// Account object representing the currently logged-in user
	private Account connectedAccount;

	// List containing all user accounts fetched from Firebase
	private List<Account> accounts;

	// ListView to display the leaderboard
	private ListView lvScore;

	// Custom adapter for populating the scores ListView
	private ScoresAdapter adapter;

	/**
	 * Called when the activity is first created.
	 * Initializes UI components, Firebase instances, and loads the leaderboard data.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		// Initialize Firebase authentication instance
		this.mAuth = FirebaseAuth.getInstance();

		// Get Firebase database instance and reference to "accounts" node
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		this.accountRef = database.getReference("accounts");

		// Initialize UI components
		this.tvUserPlace = findViewById(R.id.tvUserPlace);
		this.lvScore = findViewById(R.id.lvScore);

		// Initialize the accounts list and adapter
		this.accounts = new ArrayList<>();
		this.adapter = new ScoresAdapter(this, 0, 0, accounts);
		this.lvScore.setAdapter(adapter);

		// Load all accounts from Firebase
		getAllAccounts();
	}

	/**
	 * Fetches all user accounts from Firebase database and populates the leaderboard.
	 * Sorts accounts by best time and identifies the current user's account.
	 */
	public void getAllAccounts() {
		// Clear existing accounts
		accounts.clear();

		// Add a one-time listener to fetch all accounts
		accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// Iterate through each account in the database
				for (DataSnapshot data : dataSnapshot.getChildren()) {
					// Convert Firebase data to Account object
					Account account = data.getValue(Account.class);

					// Check if this account belongs to the currently logged-in user
					if(account.getUserId().equals(mAuth.getCurrentUser().getUid())) {
						connectedAccount = account;
					}

					// Add account to the list
					accounts.add(account);
				}

				// Sort accounts by best time (ascending order - best times first)
				accounts.sort(Comparator.comparingDouble(Account::getBestTime));

				// Notify the adapter that data has changed to refresh the ListView
				adapter.notifyDataSetChanged();

				// Update the display showing the current user's ranking
				updateConnectedUsersPlace();
			}

			@Override
			public void onCancelled(DatabaseError ignored) {
			}
		});
	}

	/**
	 * Updates the TextView showing the current user's position in the leaderboard.
	 */
	private void updateConnectedUsersPlace() {
		// Only update if we found the connected user's account
		if(connectedAccount != null) {
			// Calculate user's position (add 1 since list is 0-indexed)
			int place = (accounts.indexOf(connectedAccount) + 1);

			// Determine the appropriate ordinal suffix
			String placeText = place + (place == 1 ? "st" : (place == 2) ? "nd" : (place == 3) ? "rd" : "th");

			// Update the TextView with the user's ranking
			tvUserPlace.setText("You are at " + placeText + " place!");
		}
	}

	/**
	 * Called when the close button is clicked.
	 * Closes the current activity and returns to the previous screen.
	 *
	 * @param view The view that was clicked (close button)
	 */
	public void closeActivityByButton(View view) {
		finish();
	}

}