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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import android.widget.Toast;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button BackButton = findViewById(R.id.loginBackButton);
        Button LoginButton = findViewById(R.id.loginButton);

        TextView email = findViewById(R.id.emailLogin);
        TextView password = findViewById(R.id.passwordLogin);


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
               String emailString = email.getText().toString().trim();
               String passwordString = password.getText().toString().trim();
               if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                   email.setError("Invalid email format");
                   return;
               }
               email.setError(null);

               if (passwordString.length() < 6) {
                   password.setError("password must be at least 6 characters");
                   return;
               }
               password.setError(null);

               User user = new User(emailString, passwordString);


               DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");

               database.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       boolean userFound = false;

                       for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                           User userFromDatabase = userSnapshot.getValue(User.class);

                           if (userFromDatabase != null && userFromDatabase.getEmail().equals(emailString) && userFromDatabase.getPassword().equals(passwordString)) {
                               userFound = true;
                               break;
                           }
                       }
                       if (userFound) {
                           Toast.makeText(Login.this, "user logged in successfully", Toast.LENGTH_SHORT).show();
                       }
                       else {
                           Toast.makeText(Login.this, "user not found", Toast.LENGTH_SHORT).show();
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {
                       Toast.makeText(Login.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               });


           }
       });
    }
}