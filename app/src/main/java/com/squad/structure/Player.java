package com.squad.structure;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class Player {
    public Player(String userId, long score, HashMap<String, Player> pairedPlayers) {
        this.userId = userId;
        this.score = score;
        this.pairedPlayers = pairedPlayers;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }


    
    private String userId;
    private long score;

    public HashMap<String, Player> getPairedPlayers() {
        return pairedPlayers;
    }

    public void setPairedPlayers(HashMap<String, Player> pairedPlayers) {
        this.pairedPlayers = pairedPlayers;
    }

    // Here String is TaskID
    private HashMap<String, Player> pairedPlayers;

    @NonNull
    @Override
    public String toString() {
        return userId + " " + score;
    }
}
