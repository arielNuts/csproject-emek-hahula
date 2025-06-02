package com.example.csproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class Register extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        TextView regiTextView = findViewById(R.id.textView2);
        TextView username = findViewById(R.id.username);
        TextView email = findViewById(R.id.email);
        TextView password = findViewById(R.id.password);
        TextView confirmPassword = findViewById(R.id.confrim_password);
        Button RegisterButton = findViewById(R.id.regiButton2);
        Authentication authHelper = new Authentication();

        Button BackButton = findViewById(R.id.regiBackButton);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String Username = username.getText().toString();
                String Email = email.getText().toString();
                String Password = password.getText().toString();
                String ConfirmPassword = confirmPassword.getText().toString();


                if (Password.equals(ConfirmPassword)) {
                    authHelper.registerUser(Username, Email, Password, Register.this);

                }


            }

        });

    }

}