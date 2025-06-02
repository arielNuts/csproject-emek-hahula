package com.example.csproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button BackButton = findViewById(R.id.loginBackButton);
        Button LoginButton = findViewById(R.id.loginButton);

        TextView email = findViewById(R.id.emailLogin);
        TextView password = findViewById(R.id.passwordLogin);
        Authentication authHelper = new Authentication();


        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = email.getText().toString();
                String Password = password.getText().toString();
                authHelper.loginUser(Email, Password, Login.this);
            }
        });
    }

}