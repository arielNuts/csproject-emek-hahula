package com.example.csproject;

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
        holder.letterText.setText(cell.getLetter());

        switch (cell.getState()) {
            case WordleCell.STATE_EMPTY:
                // ─── Empty cell: keep the gray border on the TextView ───
                holder.cardView.setCardBackgroundColor(
                        context.getResources().getColor(R.color.cell_empty)
                );
                holder.letterText.setTextColor(
                        context.getResources().getColor(R.color.text_dark)
                );
                // put the gray border drawable back
                holder.letterText.setBackgroundResource(R.drawable.gray_border);
                break;

            case WordleCell.STATE_CORRECT:
                // ─── Guessed correct: show green fill and NO border ───
                holder.cardView.setCardBackgroundColor(
                        context.getResources().getColor(R.color.cell_correct)
                );
                holder.letterText.setTextColor(
                        context.getResources().getColor(R.color.text_light)
                );
                // remove the border drawable
                holder.letterText.setBackground(null);
                break;

            case WordleCell.STATE_MISPLACED:
                // ─── Guessed wrong position: show yellow fill and NO border ───
                holder.cardView.setCardBackgroundColor(
                        context.getResources().getColor(R.color.cell_misplaced)
                );
                holder.letterText.setTextColor(
                        context.getResources().getColor(R.color.text_light)
                );
                holder.letterText.setBackground(null);
                break;

            case WordleCell.STATE_WRONG:
                // ─── Letter not in word: show gray fill (darker) and NO border ───
                holder.cardView.setCardBackgroundColor(
                        context.getResources().getColor(R.color.cell_wrong)
                );
                holder.letterText.setTextColor(
                        context.getResources().getColor(R.color.text_light)
                );
                holder.letterText.setBackground(null);
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