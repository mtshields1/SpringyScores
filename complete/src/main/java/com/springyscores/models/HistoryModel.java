package com.springyscores.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
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
    public void setLowScore(int score, Date time) { this.lowScore = new ScoreHistory(score, time); }
    public void setTopScore(int score, Date time) { this.topScore = new ScoreHistory(score, time); }
    public void addScoreToHistoryList(int score, Date time) { this.playerScores.add(new ScoreHistory(score, time)); }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    public ScoreHistory getLowScore() { return lowScore; }
    public ScoreHistory getTopScore() { return topScore; }
    public double getAverageScore() { return averageScore; }
    public List<ScoreHistory> getPlayerScores() { return playerScores; }
}
