package com.example.csproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WordleAdapter extends RecyclerView.Adapter<WordleAdapter.ViewHolder> {

    private final ArrayList<WordleCell> cellsList;
    private final Context context;

    public WordleAdapter(Context context, ArrayList<WordleCell> cellsList) {
        this.context = context;
        this.cellsList = cellsList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wordle_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WordleCell cell = cellsList.get(position);

        // Set letter
        holder.letterText.setText(cell.getLetter());

        // Apply styling based on state
        switch (cell.getState()) {
            case WordleCell.STATE_EMPTY:
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.cell_empty));
                holder.letterText.setTextColor(context.getResources().getColor(R.color.text_dark));
                break;
            case WordleCell.STATE_CORRECT:
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.cell_correct));
                holder.letterText.setTextColor(context.getResources().getColor(R.color.text_light));
                break;
            case WordleCell.STATE_MISPLACED:
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.cell_misplaced));
                holder.letterText.setTextColor(context.getResources().getColor(R.color.text_light));
                break;
            case WordleCell.STATE_WRONG:
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.cell_wrong));
                holder.letterText.setTextColor(context.getResources().getColor(R.color.text_light));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return cellsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView letterText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cellCardView);
            letterText = itemView.findViewById(R.id.cellLetter);
        }
    }
}