package com.example.csproject;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class Authentication {

    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    private DatabaseReference usernamesRef;

    public Authentication() {
        auth = FirebaseAuth.getInstance();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        usersRef = database.child("users");
        usernamesRef = database.child("usernames");
    }

    public void registerUser(String username, String email, String password, Context context) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = auth.getUid();
                if (userId == null) {
                    Toast.makeText(context, "Unexpected error: User ID is null", Toast.LENGTH_SHORT).show();
                    return;
                }

                usernamesRef.child(username).runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        if (currentData.getValue() != null) {
                            return Transaction.abort();
                        } else {
                            currentData.setValue(userId);
                            return Transaction.success(currentData);
                        }
                    }

                    @Override
                    public void onComplete(@NonNull DatabaseError error, boolean committed, DataSnapshot snapshot) {
                        if (committed) {
                            User newUser = new User(email, username);
                            usersRef.child(userId).setValue(newUser).addOnCompleteListener(userTask -> {
                                if (userTask.isSuccessful()) {
                                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, MainPage.class);
                                    context.startActivity(intent);
                                } else {
                                    usernamesRef.child(username).removeValue();
                                    deleteFirebaseUser(auth.getCurrentUser());
                                    Toast.makeText(context, "Failed to save user data", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            deleteFirebaseUser(auth.getCurrentUser());
                            Toast.makeText(context, "Username already taken", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                String errorMsg = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendUser(Context context) {
        Intent intent = new Intent(context, MainPage.class);
        context.startActivity(intent);
    }

    private void deleteFirebaseUser(FirebaseUser user) {
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
            });
        }
    }

    private void createUser(String username, String email, String password, Context context) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getUid();

                        if (userId != null) {
                            User newUser = new User(email, username);

                            usersRef.child(userId).setValue(newUser)
                                    .addOnCompleteListener(userTask -> {
                                        if (userTask.isSuccessful()) {
                                            usernamesRef.child(username).setValue(userId);
                                            Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            usernamesRef.child(username).removeValue();
                                            Toast.makeText(context, "Error saving user data", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        usernamesRef.child(username).removeValue();
                        Toast.makeText(context, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void loginUser(String email, String password, Context context) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, MainPage.class);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void logoutUser() {
        auth.signOut();
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public String getUserId() {
        if (isUserLoggedIn()) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }
}
