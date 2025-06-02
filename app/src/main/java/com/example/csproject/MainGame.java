package com.example.csproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainGame extends AppCompatActivity implements View.OnClickListener {

    // Game settings
    private static final int WORD_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 6;

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();




    // Game state
    private String targetWord;
    private int currentRow = 0;
    private int currentCol = 0;
    private boolean gameOver = false;

    // UI Elements
    private RecyclerView recyclerView;
    private WordleAdapter adapter;
    private ArrayList<WordleCell> cellsList;

    // Word lists
    private List<String> validWords;
    private List<String> possibleTargets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_five);
        findViewById(R.id.bottom_controls).setVisibility(View.GONE);

        // Initialize word lists
        loadWordLists();

        // Select a random target word
        selectRandomWord();

        // Set up the grid
        setupGrid();

        // Set up keyboard
        setupKeyboard();

        // Setup bottom buttons
        setupBottomControls();
    }

    private void setupBottomControls() {
        Button playAgainButton = findViewById(R.id.btnPlayAgain);
        Button backButton = findViewById(R.id.btnBack);

        playAgainButton.setOnClickListener(v -> playAgain());
        backButton.setOnClickListener(v -> finish());
    }

    private void playAgain() {
        // Reset game state
        currentRow = 0;
        currentCol = 0;
        gameOver = false;

        // Clear grid
        for (WordleCell cell : cellsList) {
            cell.setLetter("");
            cell.setState(WordleCell.STATE_EMPTY);
        }
        adapter.notifyDataSetChanged();

        // Reset keyboard buttons to default style and clear their states
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            int buttonId = getResources().getIdentifier("btn" + letter, "id", getPackageName());
            if (buttonId != 0) {
                Button button = findViewById(buttonId);

                // Reset background and text color to default (adjust as needed)
                button.setBackgroundResource(R.drawable.gray_button_bg); // <-- your default button background drawable
                button.setTextColor(getResources().getColor(android.R.color.black)); // or your default color

                // Reset tag so updateKeyboardButton knows it has no special state
                button.setTag(R.id.state_tag, -1);
            }
        }

        // Select a new target word
        selectRandomWord();

        // Hide the Play Again and Back buttons if you made them visible after game end
        findViewById(R.id.bottom_controls).setVisibility(View.GONE);

        Toast.makeText(this, "New game started!", Toast.LENGTH_SHORT).show();
    }

    private void loadWordLists() {
        validWords = new ArrayList<>();
        possibleTargets = new ArrayList<>();

        // For demo purposes, adding some common 5-letter words
        // In a real app, you would load from a file in assets folder
        String[] words = WordLoader.loadWords(this);

        validWords.addAll(Arrays.asList(words));
        possibleTargets.addAll(Arrays.asList(words));

        // Try to load more words from assets
        try {
            loadWordsFromAssets();
        } catch (IOException e) {
            e.printStackTrace();
            // If we fail to load from assets, we already have the basic array
        }
    }

    private void loadWordsFromAssets() throws IOException {
        try {
            // Try to load more valid words
            InputStream is = getAssets().open("valid_words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() == WORD_LENGTH) {
                    validWords.add(line.toUpperCase());
                }
            }
            reader.close();

            // Try to load more target words
            is = getAssets().open("target_words.txt");
            reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                if (line.length() == WORD_LENGTH) {
                    possibleTargets.add(line.toUpperCase());
                }
            }
            reader.close();
        } catch (IOException e) {
            throw e;
        }
    }



    private void selectRandomWord() {
        Random random = new Random();
        targetWord = possibleTargets.get(random.nextInt(possibleTargets.size()));
        // For debugging
        System.out.println("Target word: " + targetWord);
    }

    private void setupGrid() {
        recyclerView = findViewById(R.id.recyclerView);

        cellsList = new ArrayList<>();

        // Initialize the grid with empty cells
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            for (int j = 0; j < WORD_LENGTH; j++) {
                cellsList.add(new WordleCell("", WordleCell.STATE_EMPTY));
            }
        }

        // Set up RecyclerView with Grid Layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, WORD_LENGTH);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new WordleAdapter(this, cellsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
    }

    private void setupKeyboard() {
        // First row: Q through P
        setupButton(R.id.btnQ, "Q");
        setupButton(R.id.btnW, "W");
        setupButton(R.id.btnE, "E");
        setupButton(R.id.btnR, "R");
        setupButton(R.id.btnT, "T");
        setupButton(R.id.btnY, "Y");
        setupButton(R.id.btnU, "U");
        setupButton(R.id.btnI, "I");
        setupButton(R.id.btnO, "O");
        setupButton(R.id.btnP, "P");

        // Second row: A through L
        setupButton(R.id.btnA, "A");
        setupButton(R.id.btnS, "S");
        setupButton(R.id.btnD, "D");
        setupButton(R.id.btnF, "F");
        setupButton(R.id.btnG, "G");
        setupButton(R.id.btnH, "H");
        setupButton(R.id.btnJ, "J");
        setupButton(R.id.btnK, "K");
        setupButton(R.id.btnL, "L");

        // Third row: Z through M
        setupButton(R.id.btnZ, "Z");
        setupButton(R.id.btnX, "X");
        setupButton(R.id.btnC, "C");
        setupButton(R.id.btnV, "V");
        setupButton(R.id.btnB, "B");
        setupButton(R.id.btnN, "N");
        setupButton(R.id.btnM, "M");

        // Special buttons
        Button enterButton = findViewById(R.id.btnEnter);
        enterButton.setOnClickListener(v -> onEnterPressed());

        Button deleteButton = findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(v -> onDeletePressed());
    }

    private void setupButton(int buttonId, String letter) {
        Button button = findViewById(buttonId);
        button.setTag(R.id.letter_tag, letter);  // Use a unique tag key for the letter
        button.setTag(R.id.state_tag, -1);        // Initial state as -1 (not pressed)
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (gameOver) {
            Toast.makeText(this, "Game Over! Please restart to play again", Toast.LENGTH_SHORT).show();
            return;
        }

        String letter = (String) v.getTag(R.id.letter_tag);
        onLetterPressed(letter);
    }

    private void onLetterPressed(String letter) {
        if (currentCol < WORD_LENGTH) {
            int position = currentRow * WORD_LENGTH + currentCol;
            cellsList.get(position).setLetter(letter);
            adapter.notifyItemChanged(position);
            currentCol++;
        }
    }

    private void onDeletePressed() {
        if (currentCol > 0) {
            currentCol--;
            int position = currentRow * WORD_LENGTH + currentCol;
            cellsList.get(position).setLetter("");
            adapter.notifyItemChanged(position);
        }
    }

    private void onEnterPressed() {
        if (currentCol != WORD_LENGTH) {
            Toast.makeText(this, "Not enough letters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build the current guess
        StringBuilder guess = new StringBuilder();
        for (int i = 0; i < WORD_LENGTH; i++) {
            guess.append(cellsList.get(currentRow * WORD_LENGTH + i).getLetter());
        }
        String guessWord = guess.toString();

        // Check if the guess is a valid word
        if (!validWords.contains(guessWord)) {
            Toast.makeText(this, "Not in word list", Toast.LENGTH_SHORT).show();
            return;
        }

        // Process the guess
        evaluateGuess(guessWord);

        // Check win condition
        if (guessWord.equals(targetWord)) {
            gameOver = true;
            Toast.makeText(this, "You win!", Toast.LENGTH_LONG).show();
            findViewById(R.id.bottom_controls).setVisibility(View.VISIBLE);

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                String uid = firebaseUser.getUid();
                DatabaseReference userRef = database.child("users").child(uid);

                userRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();

                        Long gamesWon = snapshot.child("gamesWon").getValue(Long.class);
                        Long gamesPlayed = snapshot.child("gamesPlayed").getValue(Long.class);
                        Long winStreak = snapshot.child("streak").getValue(Long.class);
                        Long maxStreak = snapshot.child("maxStreak").getValue(Long.class);

                        if (maxStreak == null) maxStreak = 0L;
                        if (winStreak == null) winStreak = 0L;
                        if (gamesWon == null) gamesWon = 0L;
                        if (gamesPlayed == null) gamesPlayed = 0L;

                        gamesWon += 1;
                        gamesPlayed += 1;
                        winStreak++;

                        userRef.child("gamesWon").setValue(gamesWon);
                        userRef.child("gamesPlayed").setValue(gamesPlayed);
                        userRef.child("streak").setValue(winStreak);
                        if (winStreak > maxStreak) {
                            userRef.child("maxStreak").setValue(winStreak);
                        }

                        float winRate = 0;
                        if (gamesPlayed > 0) {
                            winRate = ((float) gamesWon / gamesPlayed) * 100;
                        }
                        userRef.child("winRate").setValue(winRate);
                    }
                });
            }

            return;
        }

        // Move to next row
        currentRow++;
        currentCol = 0;

        // Check lose condition
        if (currentRow >= MAX_ATTEMPTS) {
            gameOver = true;
            findViewById(R.id.bottom_controls).setVisibility(View.VISIBLE);
            Toast.makeText(this, "Game over! The word was " + targetWord, Toast.LENGTH_LONG).show();

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                String uid = firebaseUser.getUid();
                DatabaseReference userRef = database.child("users").child(uid);

                userRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();

                        Long gamesWon = snapshot.child("gamesWon").getValue(Long.class);
                        Long gamesPlayed = snapshot.child("gamesPlayed").getValue(Long.class);
                        Long streak = snapshot.child("streak").getValue(Long.class);
                        if (streak == null) streak = 0L;
                        if (gamesWon == null) gamesWon = 0L;
                        if (gamesPlayed == null) gamesPlayed = 0L;

                        gamesPlayed += 1;
                        userRef.child("gamesPlayed").setValue(gamesPlayed);
                        userRef.child("streak").setValue(0L);


                        float winRate = 0;
                        if (gamesPlayed > 0) {
                            winRate = ((float) gamesWon / gamesPlayed) * 100;
                        }
                        userRef.child("winRate").setValue(winRate);
                    }
                });
            }
        }
    }

    private void evaluateGuess(String guess) {
        // Track which letters in the target have been matched
        boolean[] targetMatched = new boolean[WORD_LENGTH];

        // First pass: Find exact matches (correct position)
        for (int i = 0; i < WORD_LENGTH; i++) {
            int position = currentRow * WORD_LENGTH + i;
            char guessChar = guess.charAt(i);
            char targetChar = targetWord.charAt(i);

            if (guessChar == targetChar) {
                cellsList.get(position).setState(WordleCell.STATE_CORRECT);
                targetMatched[i] = true;
                updateKeyboardButton(String.valueOf(guessChar), WordleCell.STATE_CORRECT);
            }
        }

        // Second pass: Find partial matches (correct letter, wrong position)
        for (int i = 0; i < WORD_LENGTH; i++) {
            int position = currentRow * WORD_LENGTH + i;

            // Skip if this letter was already marked as correct
            if (cellsList.get(position).getState() == WordleCell.STATE_CORRECT) {
                continue;
            }

            char guessChar = guess.charAt(i);
            boolean found = false;

            // Check if this letter exists elsewhere in the target word
            for (int j = 0; j < WORD_LENGTH; j++) {
                if (!targetMatched[j] && targetWord.charAt(j) == guessChar) {
                    cellsList.get(position).setState(WordleCell.STATE_MISPLACED);
                    targetMatched[j] = true; // Mark this target letter as matched
                    found = true;
                    updateKeyboardButton(String.valueOf(guessChar), WordleCell.STATE_MISPLACED);
                    break;
                }
            }


            if (!found) {
                cellsList.get(position).setState(WordleCell.STATE_WRONG);
                updateKeyboardButton(String.valueOf(guessChar), WordleCell.STATE_WRONG);
            }
        }


        for (int i = 0; i < WORD_LENGTH; i++) {
            adapter.notifyItemChanged(currentRow * WORD_LENGTH + i);
        }
    }

    private void updateKeyboardButton(String letter, int state) {
        int buttonId = getResources().getIdentifier("btn" + letter, "id", getPackageName());
        if (buttonId != 0) {
            Button button = findViewById(buttonId);

            int currentState = (int) button.getTag(R.id.state_tag);

            // Only update if new state is more important
            if ((state == WordleCell.STATE_CORRECT) ||
                    (state == WordleCell.STATE_MISPLACED && currentState != WordleCell.STATE_CORRECT) ||
                    (state == WordleCell.STATE_WRONG && currentState != WordleCell.STATE_CORRECT && currentState != WordleCell.STATE_MISPLACED)) {

                switch (state) {
                    case WordleCell.STATE_CORRECT:
                        button.setBackgroundResource(R.drawable.green_button_bg);
                        break;
                    case WordleCell.STATE_MISPLACED:
                        button.setBackgroundResource(R.drawable.yellow_button_bg);
                        break;
                    case WordleCell.STATE_WRONG:
                        button.setBackgroundResource(R.drawable.dark_gray_button_bg);
                        break;
                }

                button.setTextColor(getResources().getColor(android.R.color.white));
                button.setTag(R.id.state_tag, state);
            }
        }
    }
}
