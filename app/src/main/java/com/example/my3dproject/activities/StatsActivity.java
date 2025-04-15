package com.example.my3dproject.activities;

import android.os.Bundle;
import android.widget.ListView;

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

public class StatsActivity extends AppCompatActivity {

	private FirebaseAuth mAuth;
	private DatabaseReference accountRef;
	private List<Account> accounts;
	private ListView lvScore;
	private ScoresAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);
		this.mAuth = FirebaseAuth.getInstance();
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		this.accountRef = database.getReference("accounts");
		this.accounts = new ArrayList<>();
		this.lvScore = findViewById(R.id.lvScore);
		this.adapter = new ScoresAdapter(this, 0, 0, accounts);
		this.lvScore.setAdapter(adapter);
		getAllAccounts();
	}

	public void getAllAccounts() {
		accounts.clear();
		accountRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot data : dataSnapshot.getChildren()) {
					Account account = data.getValue(Account.class);
					accounts.add(account);
					accounts.sort(Comparator.comparingDouble(Account::getBestTime));
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onCancelled(DatabaseError ignored) {
			}
		});
	}

}