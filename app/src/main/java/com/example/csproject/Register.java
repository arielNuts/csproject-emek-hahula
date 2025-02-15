package com.example.csproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.Toast;


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


        Button BackButton = findViewById(R.id.regiBackButton);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button RegisterButton = findViewById(R.id.regiButton2);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString().trim();
                String passwordString = password.getText().toString().trim();
                String confirmPasswordString = confirmPassword.getText().toString().trim();
                String usernameString = username.getText().toString().trim();

                if (usernameString.length() < 3) {
                    username.setError("username must be at least 3 characters");
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                    email.setError("Invalid email format");
                    return;
                }
                if (passwordString.length() < 6) {
                    password.setError("password must be at least 6 characters");
                    return;
                }
                if (!passwordString.equals(confirmPasswordString)) {
                    confirmPassword.setError("passwords do not match");
                    return;
                }

                confirmPassword.setError(null);
                password.setError(null);
                username.setError(null);
                email.setError(null);

                User user = new User(usernameString, passwordString, emailString);

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                database.child("users").push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "user registered successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(Register.this, "user registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}