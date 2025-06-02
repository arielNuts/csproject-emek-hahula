// com.example.csproject.PlayerProfileActivity.java
package com.example.csproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Add Log import for debugging
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // Add Toast import for error messages

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

// We no longer need FirebaseAuth here as we are loading *any* player profile
// import com.google.firebase.auth.FirebaseAuth;
// import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PlayerProfileActivity extends AppCompatActivity {

    private static final String TAG = "PlayerProfileActivity"; // For logging

    private LinearLayout loadingLayout;
    private LinearLayout mainContent;

    // Declare TextViews to populate data
    private TextView usernameTextView;
    private TextView gamesPlayedTextView;
    private TextView gamesWonTextView;
    private TextView winRateTextView;
    private TextView currentStreakTextView;
    private TextView maxStreakTextView;
    private Button backButton;

    private String playerUid; // To store the UID received from the Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player_profile);

        // Initialize views
        loadingLayout = findViewById(R.id.loading_layout);
        mainContent = findViewById(R.id.main_content);

        usernameTextView = findViewById(R.id.username);
        gamesPlayedTextView = findViewById(R.id.games_played);
        gamesWonTextView = findViewById(R.id.games_won);
        winRateTextView = findViewById(R.id.win_rate);
        currentStreakTextView = findViewById(R.id.current_streak);
        maxStreakTextView = findViewById(R.id.max_streak);
        backButton = findViewById(R.id.back_button);

        // Get the player's UID from the Intent that launched this activity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("PLAYER_UID")) {
            playerUid = intent.getStringExtra("PLAYER_UID");
            Log.d(TAG, "Received Player UID: " + playerUid);
        } else {
            Log.e(TAG, "Error: No PLAYER_UID received in Intent.");
            Toast.makeText(this, "Error: Player data not found.", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no UID is provided
            return; // Exit onCreate
        }

        // Show loading initially
        showLoading(true);

        // Now, use the received playerUid to fetch their specific data
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference playerRef = databaseRef.child("users").child(playerUid);
        Log.d(TAG, "Fetching profile for: " + playerRef.toString());

        playerRef.get().addOnCompleteListener(task -> {
            // Hide loading when data is loaded
            showLoading(false);

            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    usernameTextView.setText(snapshot.child("username").getValue(String.class));

                    // Use null-safe retrieval for Long values
                    Long gamesWonValue = snapshot.child("gamesWon").getValue(Long.class);
                    gamesWonTextView.setText(String.valueOf(gamesWonValue != null ? gamesWonValue : 0));

                    Long gamesPlayedValue = snapshot.child("gamesPlayed").getValue(Long.class);
                    gamesPlayedTextView.setText(String.valueOf(gamesPlayedValue != null ? gamesPlayedValue : 0));

                    // Assuming winRate is stored as a Long (e.g., 90 for 90%)
                    Long winRateValue = snapshot.child("winRate").getValue(Long.class);
                    int winRateInt = (winRateValue != null ? winRateValue.intValue() : 0);
                    winRateTextView.setText(winRateInt + "%");

                    Long currentStreakValue = snapshot.child("streak").getValue(Long.class); // Assuming "streak" is the key
                    currentStreakTextView.setText(String.valueOf(currentStreakValue != null ? currentStreakValue : 0));

                    Long maxStreakValue = snapshot.child("maxStreak").getValue(Long.class);
                    maxStreakTextView.setText(String.valueOf(maxStreakValue != null ? maxStreakValue : 0));

                    Log.d(TAG, "Profile data loaded successfully for UID: " + playerUid);
                } else {
                    Log.w(TAG, "Profile data not found for UID: " + playerUid);
                    Toast.makeText(this, "Player profile not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Failed to load player profile for UID: " + playerUid, task.getException());
                Toast.makeText(this, "Failed to load profile. Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to the previous activity (LeaderBoards)
                onBackPressed(); // This will go back to the previous activity on the stack
                // If you explicitly want to go to MainPage, change this:
                // Intent intent = new Intent(PlayerProfileActivity.this, MainPage.class);
                // startActivity(intent);
                // finish(); // Finish this activity
            }
        });

        // The Authentication class is not used here for profile display, so it can be removed
        // Authentication auth = new Authentication();
    }

    private void showLoading(boolean show) {
        if (show) {
            loadingLayout.setVisibility(View.VISIBLE);
            mainContent.setVisibility(View.GONE);
        } else {
            loadingLayout.setVisibility(View.GONE);
            mainContent.setVisibility(View.VISIBLE);
        }
    }
}