package com.example.csproject;

public class WordleCell {
    public static final int STATE_EMPTY = 0;
    public static final int STATE_WRONG = 1;
    public static final int STATE_MISPLACED = 2;
    public static final int STATE_CORRECT = 3;

    private String letter;
    private int state;

    public WordleCell(String letter, int state) {
        this.letter = letter;
        this.state = state;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
