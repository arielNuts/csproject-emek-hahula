package com.example.csproject;

public class User {
    private String username;
    private float winRate;
    private int gamesPlayed;
    private int gamesWon;
    private String email;

    private int streak;

    public User() {}

    public User(String username,  String email) {
        this.username = username;
        this.email = email;
    }

    public User(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getGamesWon() {return gamesWon;}

    public float getWinRate() { return winRate;}

    public void setGamesPlayed() {
        gamesPlayed++;
    }
    public void setGamesWon() {gamesWon++;}
    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void UpdateWR() {this.winRate = (int) (gamesWon / gamesPlayed) * 100 ;}

    public void UpdateStreak() {this.streak += 1; }
}
