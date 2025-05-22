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

    /**
     * Register a new user with email and password.
     */

    public void registerUser(String username, String email, String password, Context context) {

        // Step 1: Create auth user first
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = auth.getUid();
                if (userId == null) {
                    Toast.makeText(context, "Unexpected error: User ID is null", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Step 2: Now try to reserve username
                usernamesRef.child(username).runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        if (currentData.getValue() != null) {
                            // Username taken
                            return Transaction.abort();
                        } else {
                            // Reserve username for this userId
                            currentData.setValue(userId);
                            return Transaction.success(currentData);
                        }
                    }

                    @Override
                    public void onComplete(@NonNull DatabaseError error, boolean committed, DataSnapshot snapshot) {
                        if (committed) {
                            // Step 3: Save user info under users node
                            User newUser = new User(email, username);
                            usersRef.child(userId).setValue(newUser).addOnCompleteListener(userTask -> {
                                if (userTask.isSuccessful()) {
                                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, MainPage.class);
                                    context.startActivity(intent);




                                } else {
                                    // Rollback username reservation and delete auth user
                                    usernamesRef.child(username).removeValue();
                                    deleteFirebaseUser(auth.getCurrentUser());
                                    Toast.makeText(context, "Failed to save user data", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            // Username taken, rollback: delete auth user
                            deleteFirebaseUser(auth.getCurrentUser());
                            Toast.makeText(context, "Username already taken", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                // Auth registration failed (duplicate email etc)
                String errorMsg = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
            }
        });

    }
    public void sendUser(Context context) {
        Intent intent = new Intent(context, MainPage.class);
        context.startActivity(intent);
    }

    // Helper method to delete user in case of rollback
   private void deleteFirebaseUser(FirebaseUser user) {
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                // User deleted or error handled silently
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

                            // Store user data under the "users" node
                            usersRef.child(userId).setValue(newUser)
                                    .addOnCompleteListener(userTask -> {
                                        if (userTask.isSuccessful()) {
                                            // Map the username to the user ID
                                            usernamesRef.child(username).setValue(userId);
                                            Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Rollback the username reservation
                                            usernamesRef.child(username).removeValue();
                                            Toast.makeText(context, "Error saving user data", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        // Rollback the username reservation
                        usernamesRef.child(username).removeValue();
                        Toast.makeText(context, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Log in an existing user with email and password.
     */
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

    /**
     * Log out the current user.
     */
    public void logoutUser() {
        auth.signOut();
    }

    /**
     * Check if a user is currently logged in.
     */
    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    /**
     * Get the current user's UID.
     */
    public String getUserId() {
        if (isUserLoggedIn()) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }
}

