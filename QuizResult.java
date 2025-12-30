package com.server;

import java.io.Serializable;

public class QuizResult implements Serializable {
    private String username;
    private int score;

    public QuizResult(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() { return username; }
    public int getScore() { return score; }
}