package com.example.csproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileActivity extends AppCompatActivity {

    private LinearLayout loadingLayout;
    private LinearLayout mainContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        loadingLayout = findViewById(R.id.loading_layout);
        mainContent = findViewById(R.id.main_content);

        showLoading(true);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference userRef = database.child("users").child(uid);

        TextView username = findViewById(R.id.username);
        Button backButton = findViewById(R.id.back_button);
        Button logout = findViewById(R.id.logoutbutton);

        TextView gamesPlayed = findViewById(R.id.games_played);
        TextView gamesWon = findViewById(R.id.games_won);
        TextView winRate = findViewById(R.id.win_rate);
        TextView currentStreak = findViewById(R.id.current_streak);
        TextView maxStreak = findViewById(R.id.max_streak);

        userRef.get().addOnCompleteListener(task -> {
            showLoading(false);

            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                username.setText(snapshot.child("username").getValue(String.class));

                Long gamesWonValue = snapshot.child("gamesWon").getValue(Long.class);
                gamesWon.setText(String.valueOf(gamesWonValue != null ? gamesWonValue : 0));

                Long gamesPlayedValue = snapshot.child("gamesPlayed").getValue(Long.class);
                gamesPlayed.setText(String.valueOf(gamesPlayedValue != null ? gamesPlayedValue : 0));

                Long winRateValue = snapshot.child("winRate").getValue(Long.class);
                int winRateInt = (winRateValue != null ? winRateValue.intValue() : 0);
                winRate.setText(winRateInt + "%");


                Long currentStreakValue = snapshot.child("streak").getValue(Long.class);
                currentStreak.setText(String.valueOf(currentStreakValue != null ? currentStreakValue : 0));

                Long maxStreakValue = snapshot.child("maxStreak").getValue(Long.class);
                maxStreak.setText(String.valueOf(maxStreakValue != null ? maxStreakValue : 0));



            } else {
                // Handle error case
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MainPage.class);
                startActivity(intent);
            }
        });



        Authentication auth = new Authentication();
        logout.setOnClickListener(view -> {
            auth.logoutUser();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
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