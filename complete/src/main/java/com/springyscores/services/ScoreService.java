package com.springyscores.services;

import com.springyscores.ScoreRepository;
import com.springyscores.models.HistoryModel;
import com.springyscores.models.ScoreHistory;
import com.springyscores.models.ScoreModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScoreService {

    @Autowired
    ScoreRepository scoreRepository;

    public ScoreModel saveScore(ScoreModel scoreModel) {
        return scoreRepository.save(scoreModel);
    }

    public Optional<ScoreModel> getScoreById(Long id) {
        return scoreRepository.findById(id);
    }

    public ResponseEntity getPlayerHistory(String player) {
        Optional<List<ScoreModel>> history = scoreRepository.findByPlayerOrderByScore(player);
        if (history.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User has no scores");
        }
        HistoryModel playerHistory = new HistoryModel();
        List<ScoreModel> scores = history.get();
        double totalScore = 0;
        for (int index = 0; index < scores.size(); index++) {
            // Scores are retrieved from the db in order from lowest to highest. i.e. 1st index is lowest, last is highest
            if (index == 0) {
                playerHistory.setLowScore(scores.get(index).getScore(), scores.get(index).getTime());
            }
            if (index == scores.size()-1) {
                playerHistory.setTopScore(scores.get(index).getScore(), scores.get(index).getTime());
            }
            playerHistory.addScoreToHistoryList(scores.get(index).getScore(), scores.get(index).getTime());
            totalScore += scores.get(index).getScore();
        }
        playerHistory.setAverageScore(totalScore/scores.size());
        return new ResponseEntity<>(playerHistory, HttpStatus.OK);
    }

    public void deleteScore(Long id) {
        scoreRepository.deleteById(id);
    }
}
