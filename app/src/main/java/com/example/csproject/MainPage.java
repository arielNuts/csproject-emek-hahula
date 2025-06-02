package com.example.csproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainPage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page2);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        // 1) Play button: launches the 5‐letter game
        Button play5 = findViewById(R.id.Play5Button);
        play5.setOnClickListener(view -> {
            Intent intent = new Intent(MainPage.this, MainGame.class);
            startActivity(intent);
        });
        Button leaderboards = findViewById(R.id.Lbutton);
        leaderboards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPage.this, LeaderBoards.class);
                startActivity(intent);
            }
        });

        // 2) Profile picture: launches ProfileActivity (to be created later)
        ImageButton profileButton = findViewById(R.id.imageButtonProfile);
        profileButton.setOnClickListener(view -> {
            Intent intent;
            if(user != null) {
                intent = new Intent(MainPage.this, ProfileActivity.class);
            }
            else {
                 intent = new Intent(MainPage.this, MainActivity.class); }
            startActivity(intent);
        });
    }

}