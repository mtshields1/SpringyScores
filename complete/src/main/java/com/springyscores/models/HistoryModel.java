package com.springyscores.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class HistoryModel {
    @JsonProperty
    ScoreHistory lowScore;
    @JsonProperty
    ScoreHistory topScore;
    @JsonProperty
    double averageScore;
    @JsonProperty
    List<ScoreHistory> playerScores = new ArrayList<>();
    public void setLowScore(int score, String time) { this.lowScore = new ScoreHistory(score, time); }
    public void setTopScore(int score, String time) { this.topScore = new ScoreHistory(score, time); }
    public void addScoreToHistoryList(int score, String time) { this.playerScores.add(new ScoreHistory(score, time)); }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
}
