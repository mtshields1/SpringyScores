package com.springyscores.models;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Locale;

@Entity
public class ScoreModel {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @JsonProperty
    private String player;
    @JsonProperty
    private int score;
    @JsonProperty
    private Date time; // ISO format
    public int getScore() { return score;}
    public Long getId() { return id; }
    public Date getTime() { return time; }
    public void caseInsensitive() { this.player = this.player.toLowerCase(Locale.ROOT); }
    public ScoreModel(String player, int score, Date time) {
        this.player = player;
        this.time = time;
        this.score = score;
    }
    public void setId (Long id) { this.id = id; }
    public ScoreModel() {}
}
