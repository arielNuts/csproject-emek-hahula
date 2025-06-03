package com.example.csproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PlayerProfileActivity extends AppCompatActivity {

    private static final String TAG = "PlayerProfileActivity";

    private LinearLayout loadingLayout;
    private LinearLayout mainContent;

    private TextView usernameTextView;
    private TextView gamesPlayedTextView;
    private TextView gamesWonTextView;
    private TextView winRateTextView;
    private TextView currentStreakTextView;
    private TextView maxStreakTextView;
    private Button backButton;

    private String playerUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player_profile);

        loadingLayout = findViewById(R.id.loading_layout);
        mainContent = findViewById(R.id.main_content);

        usernameTextView = findViewById(R.id.username);
        gamesPlayedTextView = findViewById(R.id.games_played);
        gamesWonTextView = findViewById(R.id.games_won);
        winRateTextView = findViewById(R.id.win_rate);
        currentStreakTextView = findViewById(R.id.current_streak);
        maxStreakTextView = findViewById(R.id.max_streak);
        backButton = findViewById(R.id.back_button);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("PLAYER_UID")) {
            playerUid = intent.getStringExtra("PLAYER_UID");
            Log.d(TAG, "Received Player UID: " + playerUid);
        } else {
            Log.e(TAG, "Error: No PLAYER_UID received in Intent.");
            Toast.makeText(this, "Error: Player data not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showLoading(true);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference playerRef = databaseRef.child("users").child(playerUid);
        Log.d(TAG, "Fetching profile for: " + playerRef.toString());

        playerRef.get().addOnCompleteListener(task -> {
            showLoading(false);

            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    usernameTextView.setText(snapshot.child("username").getValue(String.class));

                    Long gamesWonValue = snapshot.child("gamesWon").getValue(Long.class);
                    gamesWonTextView.setText(String.valueOf(gamesWonValue != null ? gamesWonValue : 0));

                    Long gamesPlayedValue = snapshot.child("gamesPlayed").getValue(Long.class);
                    gamesPlayedTextView.setText(String.valueOf(gamesPlayedValue != null ? gamesPlayedValue : 0));

                    Long winRateValue = snapshot.child("winRate").getValue(Long.class);
                    int winRateInt = (winRateValue != null ? winRateValue.intValue() : 0);
                    winRateTextView.setText(winRateInt + "%");

                    Long currentStreakValue = snapshot.child("streak").getValue(Long.class);
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
                onBackPressed();
            }
        });
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