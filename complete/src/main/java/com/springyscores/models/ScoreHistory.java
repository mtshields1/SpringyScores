package com.springyscores.models;

import com.fasterxml.jackson.annotation.JsonProperty;

// Model to represent player score and the time of that score
public class ScoreHistory {
    public ScoreHistory(int score, String time) {
        this.score = score;
        this.time = time;
    }
    @JsonProperty
    int score;
    @JsonProperty
    String time;
}
