// com.example.csproject.LeaderBoards.java
package com.example.csproject;

import android.content.Intent; // Required for starting new activities
import android.os.Bundle;
import android.util.Log; // Required for logging messages
import android.widget.Button;
import android.widget.Toast; // Required for showing brief messages to the user

import androidx.activity.EdgeToEdge; // For edge-to-edge display
import androidx.annotation.NonNull; // For non-null annotations
import androidx.appcompat.app.AppCompatActivity; // Base activity class
import androidx.recyclerview.widget.LinearLayoutManager; // Layout manager for RecyclerView
import androidx.recyclerview.widget.RecyclerView; // RecyclerView widget

// Firebase Realtime Database imports
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query; // For ordering and limiting data
import com.google.firebase.database.ValueEventListener; // For listening to data changes

import java.util.ArrayList; // For dynamic list
import java.util.Collections; // For reversing the list
import java.util.List; // For list interface

// The LeaderBoards activity implements LeaderboardAdapter.OnItemClickListener
// This means it promises to provide an implementation for the onItemClick method
public class LeaderBoards extends AppCompatActivity implements LeaderboardAdapter.OnItemClickListener {

    // TAG for Logcat messages, useful for filtering logs
    private static final String TAG = "LeaderBoardsActivity";

    // UI elements declarations
    private Button backButton;
    private RecyclerView leaderboardsRecyclerView;
    private LeaderboardAdapter adapter;
    private List<LeaderboardEntry> leaderboardList; // List to hold player data

    // Firebase Database Reference to the "users" node
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable edge-to-edge display for a modern look
        EdgeToEdge.enable(this);
        // Set the activity's content view from the XML layout
        setContentView(R.layout.activity_leader_boards);

        // 1. Initialize UI elements by referencing their IDs from activity_leader_boards.xml
        backButton = findViewById(R.id.btn_back);
        leaderboardsRecyclerView = findViewById(R.id.recycler_leaderboards);

        // 2. Initialize Firebase Database instance and get a reference to your "users" node
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // "users" is the parent node where your player data (username, gamesWon, etc.) resides
        usersRef = database.getReference("users");
        // Log the Firebase database reference path for debugging
        Log.d(TAG, "Firebase Database Reference: " + usersRef.toString());

        // 3. Setup RecyclerView:
        // Initialize the list that will hold the leaderboard data
        leaderboardList = new ArrayList<>();
        // Initialize the adapter, passing the list and 'this' (the activity itself) as the listener
        // 'this' works because LeaderBoards implements LeaderboardAdapter.OnItemClickListener
        adapter = new LeaderboardAdapter(leaderboardList, this);
        // Set the layout manager for the RecyclerView (e.g., a vertical list)
        leaderboardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Set the adapter for the RecyclerView
        leaderboardsRecyclerView.setAdapter(adapter);

        // 4. Set up the back button click listener
        // When clicked, it will simulate pressing the device's back button
        backButton.setOnClickListener(v -> onBackPressed());

        // 5. Initiate the process to fetch and display leaderboard data from Firebase
        fetchLeaderboardData();
    }

    /**
     * Fetches leaderboard data from Firebase Realtime Database.
     * It queries the "users" node, orders by "gamesWon" (descending),
     * and updates the RecyclerView.
     */
    private void fetchLeaderboardData() {
        // Create a Firebase Query:
        // 1. usersRef: Start from the "users" node.
        // 2. orderByChild("gamesWon"): Sort the users based on their 'gamesWon' child property.
        //    Firebase Realtime Database sorts numeric values in ASCENDING order by default.
        // 3. limitToLast(100): Get the last 100 results after sorting.
        //    Since sorting is ascending, 'limitToLast' effectively gives you the 100 highest 'gamesWon' values.
        //    Adjust '100' to your desired number of top players.
        Query topPlayersQuery = usersRef.orderByChild("gamesWon").limitToLast(100);

        // Log the Firebase query string for debugging
        Log.d(TAG, "Firebase Query: " + topPlayersQuery.toString());

        // Attach a ValueEventListener to the query.
        // This listener will be triggered once initially and then every time the data changes.
        topPlayersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Log whether the DataSnapshot exists and the number of children found
                Log.d(TAG, "onDataChange triggered. DataSnapshot exists: " + dataSnapshot.exists());
                Log.d(TAG, "Number of children in DataSnapshot: " + dataSnapshot.getChildrenCount());

                // Clear the existing list to avoid duplicate entries on subsequent data changes
                leaderboardList.clear();

                // Check if any data was found. If not, log a message.
                if (!dataSnapshot.exists() || dataSnapshot.getChildrenCount() == 0) {
                    Log.d(TAG, "No data found at this path or no children. Please check Firebase console and security rules.");
                    // Optionally, inform the user that no data is available
                    Toast.makeText(LeaderBoards.this, "No leaderboard data available.", Toast.LENGTH_SHORT).show();
                }

                // Iterate through each child (which represents a user) in the DataSnapshot
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Log each child's key (UID) and its raw value for debugging
                    Log.d(TAG, "Child Key: " + snapshot.getKey() + ", Raw Value: " + snapshot.getValue());

                    // Convert the Firebase data for each user into a LeaderboardEntry object
                    LeaderboardEntry entry = snapshot.getValue(LeaderboardEntry.class);
                    if (entry != null) {
                        // Crucially, set the UID from the snapshot's key
                        entry.setUid(snapshot.getKey());
                        leaderboardList.add(entry); // Add the populated entry to our local list
                        // Log the successfully deserialized entry details
                        Log.d(TAG, "Successfully deserialized: Username=" + entry.getUsername() + ", GamesWon=" + entry.getGamesWon() + ", UID=" + entry.getUid());
                    } else {
                        // Log an error if deserialization fails for an entry
                        Log.e(TAG, "Failed to deserialize entry for key: " + snapshot.getKey() + ". Check LeaderboardEntry class fields and Firebase data types.");
                    }
                }

                // Log the size of the leaderboard list after initial Firebase data fetch
                Log.d(TAG, "LeaderboardList size after Firebase fetch: " + leaderboardList.size());

                // If the list is not empty, proceed with sorting, ranking, and updating the adapter
                if (!leaderboardList.isEmpty()) {
                    // IMPORTANT: Firebase `orderByChild` sorts numbers in ascending order.
                    // Since we used `limitToLast` to get the highest scores,
                    // the list is currently ordered from lowest of the top N to highest of the top N.
                    // We need to reverse it to display the highest score first (rank #1).
                    Collections.reverse(leaderboardList);
                    // Log the size after reversing
                    Log.d(TAG, "LeaderboardList size after reverse: " + leaderboardList.size());

                    // Assign ranks based on the sorted order (highest gamesWon gets #1)
                    for (int i = 0; i < leaderboardList.size(); i++) {
                        leaderboardList.get(i).setRank("#" + (i + 1));
                        // Log each ranked entry
                        Log.d(TAG, "Ranked entry: " + leaderboardList.get(i).getRank() + " " + leaderboardList.get(i).getUsername() + " - " + leaderboardList.get(i).getGamesWon());
                    }

                    // Update the RecyclerView adapter with the new, sorted, and ranked data
                    adapter.updateData(leaderboardList);
                    // Log that the adapter has been updated
                    Log.d(TAG, "Adapter updated. RecyclerView should refresh.");
                } else {
                    // Log if the list remains empty after processing Firebase data
                    Log.d(TAG, "Leaderboard list is empty, no data to display after processing.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // This method is called if there's an error (e.g., permission denied, no internet connection)
                // Log the error message and the exception stack trace
                Log.e(TAG, "Firebase read cancelled/failed: " + databaseError.getMessage(), databaseError.toException());
                // Show a user-friendly error message using a Toast
                Toast.makeText(LeaderBoards.this, "Error loading leaderboard: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Implementation of the onItemClick method from LeaderboardAdapter.OnItemClickListener.
     * This method is called when a user clicks on a leaderboard item (player row).
     *
     * @param entry    The LeaderboardEntry object of the clicked player.
     * @param position The adapter position of the clicked item.
     */
    @Override
    public void onItemClick(LeaderboardEntry entry, int position) {
        // Log the details of the clicked item for debugging
        Log.d(TAG, "Leaderboard item clicked: " + entry.getUsername() + " (UID: " + entry.getUid() + ") at position " + position);

        // Create an Intent to start the PlayerProfileActivity
        Intent intent = new Intent(LeaderBoards.this, PlayerProfileActivity.class);

        // Pass the Unique User ID (UID) of the clicked player to the PlayerProfileActivity
        // This UID will be used by PlayerProfileActivity to fetch the specific player's data
        intent.putExtra("PLAYER_UID", entry.getUid());

        // Start the PlayerProfileActivity
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // It's good practice to remove Firebase listeners when the activity is destroyed
        // to prevent memory leaks and unnecessary data synchronization.
        // If you stored a reference to 'topPlayersQuery' and the 'ValueEventListener' instance,
        // you would remove it here, e.g.:
        // topPlayersQuery.removeEventListener(myValueEventListenerInstance);
        // For simplicity in this example, we're not explicitly storing the listener instance,
        // but for more complex apps, explicit management is recommended.
    }
}