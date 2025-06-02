// com.example.csproject.LeaderboardAdapter.java
package com.example.csproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private List<LeaderboardEntry> leaderboardList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(LeaderboardEntry entry, int position);
    }

    public LeaderboardAdapter(List<LeaderboardEntry> leaderboardList, OnItemClickListener listener) {
        this.leaderboardList = leaderboardList;
        this.listener = listener;
    }

    public void updateData(List<LeaderboardEntry> newList) {
        this.leaderboardList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_item_row, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        LeaderboardEntry entry = leaderboardList.get(position);

        holder.usernameTextView.setText(entry.getUsername());
        holder.rankTextView.setText(entry.getRank());

        // Set the number of wins (removed " Wins" text)
        if (entry.getGamesWon() != null) {
            holder.winsTextView.setText(String.valueOf(entry.getGamesWon())); // <-- CHANGED HERE
        } else {
            holder.winsTextView.setText("0"); // Default if gamesWon is null
        }

        // Handle profile image loading
        if (entry.getProfilePicUrl() != null && !entry.getProfilePicUrl().isEmpty()) {
            // Use Glide/Picasso here if you have them for image loading from URL
            // Glide.with(holder.profileImageView.getContext())
            //      .load(entry.getProfilePicUrl())
            //      .placeholder(R.drawable.ic_profile_wordle)
            //      .error(R.drawable.ic_profile_wordle)
            //      .into(holder.profileImageView);
        } else {
            holder.profileImageView.setBackgroundResource(R.drawable.ic_profile_wordle);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(entry, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return leaderboardList.size();
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView usernameTextView;
        TextView rankTextView;
        TextView winsTextView;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.img_profile);
            usernameTextView = itemView.findViewById(R.id.txt_username);
            rankTextView = itemView.findViewById(R.id.txt_rank);
            winsTextView = itemView.findViewById(R.id.txt_wins);
        }
    }
}