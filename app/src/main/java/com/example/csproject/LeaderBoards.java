package com.example.csproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderBoards extends AppCompatActivity implements LeaderboardAdapter.OnItemClickListener {

    private static final String TAG = "LeaderBoardsActivity";

    private Button backButton;
    private RecyclerView leaderboardsRecyclerView;
    private LeaderboardAdapter adapter;
    private List<LeaderboardEntry> leaderboardList;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leader_boards);

        backButton = findViewById(R.id.btn_back);
        leaderboardsRecyclerView = findViewById(R.id.recycler_leaderboards);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        Log.d(TAG, "Firebase Database Reference: " + usersRef.toString());

        leaderboardList = new ArrayList<>();
        adapter = new LeaderboardAdapter(leaderboardList, this);
        leaderboardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        leaderboardsRecyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> onBackPressed());

        fetchLeaderboardData();
    }

    private void fetchLeaderboardData() {
        Query topPlayersQuery = usersRef.orderByChild("gamesWon").limitToLast(100);
        Log.d(TAG, "Firebase Query: " + topPlayersQuery.toString());

        topPlayersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange triggered. DataSnapshot exists: " + dataSnapshot.exists());
                Log.d(TAG, "Number of children in DataSnapshot: " + dataSnapshot.getChildrenCount());

                leaderboardList.clear();

                if (!dataSnapshot.exists() || dataSnapshot.getChildrenCount() == 0) {
                    Log.d(TAG, "No data found at this path or no children. Please check Firebase console and security rules.");
                    Toast.makeText(LeaderBoards.this, "No leaderboard data available.", Toast.LENGTH_SHORT).show();
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Child Key: " + snapshot.getKey() + ", Raw Value: " + snapshot.getValue());
                    LeaderboardEntry entry = snapshot.getValue(LeaderboardEntry.class);
                    if (entry != null) {
                        entry.setUid(snapshot.getKey());
                        leaderboardList.add(entry);
                        Log.d(TAG, "Successfully deserialized: Username=" + entry.getUsername() + ", GamesWon=" + entry.getGamesWon() + ", UID=" + entry.getUid());
                    } else {
                        Log.e(TAG, "Failed to deserialize entry for key: " + snapshot.getKey() + ". Check LeaderboardEntry class fields and Firebase data types.");
                    }
                }

                Log.d(TAG, "LeaderboardList size after Firebase fetch: " + leaderboardList.size());

                if (!leaderboardList.isEmpty()) {
                    Collections.reverse(leaderboardList);
                    Log.d(TAG, "LeaderboardList size after reverse: " + leaderboardList.size());

                    for (int i = 0; i < leaderboardList.size(); i++) {
                        leaderboardList.get(i).setRank("#" + (i + 1));
                        Log.d(TAG, "Ranked entry: " + leaderboardList.get(i).getRank() + " " + leaderboardList.get(i).getUsername() + " - " + leaderboardList.get(i).getGamesWon());
                    }

                    adapter.updateData(leaderboardList);
                    Log.d(TAG, "Adapter updated. RecyclerView should refresh.");
                } else {
                    Log.d(TAG, "Leaderboard list is empty, no data to display after processing.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase read cancelled/failed: " + databaseError.getMessage(), databaseError.toException());
                Toast.makeText(LeaderBoards.this, "Error loading leaderboard: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemClick(LeaderboardEntry entry, int position) {
        Log.d(TAG, "Leaderboard item clicked: " + entry.getUsername() + " (UID: " + entry.getUid() + ") at position " + position);
        Intent intent = new Intent(LeaderBoards.this, PlayerProfileActivity.class);
        intent.putExtra("PLAYER_UID", entry.getUid());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
