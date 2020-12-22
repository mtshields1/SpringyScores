package com.springyscores.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

// Model to represent player score and the time of that score
public class ScoreHistory {
    public ScoreHistory(int score, Date time) {
        this.score = score;
        this.time = time;
    }
    @JsonProperty
    int score;
    @JsonProperty
    Date time;
    public int getScore() { return score; }
}
