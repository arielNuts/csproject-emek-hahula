package com.example.csproject;

public class LeaderboardEntry {
    private String uid;
    private String username;
    private Long gamesWon;
    private String profilePicUrl;
    private String rank;

    public LeaderboardEntry() {}

    public LeaderboardEntry(String username, Long gamesWon, String profilePicUrl, String uid) {
        this.uid = uid;
        this.username = username;
        this.gamesWon = gamesWon;
        this.profilePicUrl = profilePicUrl;
    }
    public String getUid() {
        return this.uid;

    }
    public String getUsername() {
        return this.username;
    }
    public Long getGamesWon() {
        return this.gamesWon;
    }
    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getRank() {
        return this.rank;
    }

    // Setters (Firebase might use these if you don't provide a public no-arg constructor)
    public void setUid(String uid) { // <-- ADD THIS SETTER
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGamesWon(Long gamesWon) {
        this.gamesWon = gamesWon;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
