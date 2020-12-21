package com.springyscores.services;

import com.springyscores.ScoreRepository;
import com.springyscores.models.HistoryModel;
import com.springyscores.models.ScoreModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public List<ScoreModel> getListOfScores(Map<String, String> filters) throws ParseException {
        // Assumption: pagination WILL be requested
        List<ScoreModel> results;
        if (filters.containsKey("datebefore") && filters.containsKey("dateafter")) {
            results = findBetweenDates(filters);
        }
        else if (filters.containsKey("datebefore")) {
            results = findBeforeOrAfterDates(filters, "datebefore");
        }
        else if (filters.containsKey("dateafter")) {
            results = findBeforeOrAfterDates(filters, "dateafter");
        }
        else {
            // No dates requested: just get list of requested player names based on pages requested
            List<String> namesRequested = Arrays.asList(filters.get("players").split("\\s*,\\s*"));
            Pageable paging = PageRequest.of(Integer.parseInt(filters.get("pageno")), Integer.parseInt(filters.get("pagesize")));
            Slice<ScoreModel> result = scoreRepository.findByPlayerIn(namesRequested, paging);
            results = result.getContent();
        }
        return results;
    }

    public List<ScoreModel> findBetweenDates(Map<String, String> filters) throws ParseException {
        List<ScoreModel> results;
        if (filters.containsKey("players")) {
            // Find between dates for these players
            List<String> namesRequested = Arrays.asList(filters.get("players").split("\\s*,\\s*"));
            Pageable paging = PageRequest.of(Integer.parseInt(filters.get("pageno")), Integer.parseInt(filters.get("pagesize")));
            Slice<ScoreModel> result = scoreRepository.findByPlayerInAndTimeAfterAndTimeBefore(namesRequested,
                    getProperDate(filters.get("dateafter")),
                    getProperDate(filters.get("datebefore")),
                    paging);
            results = result.getContent();
        }
        else {
            Pageable paging = PageRequest.of(Integer.parseInt(filters.get("pageno")), Integer.parseInt(filters.get("pagesize")));
            Slice<ScoreModel> result = scoreRepository.findByTimeAfterAndTimeBefore(getProperDate(filters.get("dateafter")), getProperDate(filters.get("datebefore")), paging);
            results = result.getContent();
        }
        return results;
    }

    public List<ScoreModel> findBeforeOrAfterDates(Map<String, String> filters, String dateFilter) throws ParseException {
        List<ScoreModel> results = new ArrayList<>();
        if (filters.containsKey("players")) {
            // Find before dates for these players
            List<String> namesRequested = Arrays.asList(filters.get("players").split("\\s*,\\s*"));
            Pageable paging = PageRequest.of(Integer.parseInt(filters.get("pageno")), Integer.parseInt(filters.get("pagesize")));
            if (dateFilter.equals("datebefore")) {
                Slice<ScoreModel> result = scoreRepository.findByPlayerInAndTimeBefore(namesRequested,
                        getProperDate(filters.get(dateFilter)),
                        paging);
                results = result.getContent();
            }
            else if (dateFilter.equals("dateafter")) {
                Slice<ScoreModel> result = scoreRepository.findByPlayerInAndTimeAfter(namesRequested,
                        getProperDate(filters.get(dateFilter)),
                        paging);
                results = result.getContent();
            }
        }
        else {
            Pageable paging = PageRequest.of(Integer.parseInt(filters.get("pageno")), Integer.parseInt(filters.get("pagesize")));
            if (dateFilter.equals("datebefore")) {
                Slice<ScoreModel> result = scoreRepository.findByTimeBefore(getProperDate(filters.get(dateFilter)), paging);
                results = result.getContent();
            }
            else if (dateFilter.equals("dateafter")) {
                Slice<ScoreModel> result = scoreRepository.findByTimeAfter(getProperDate(filters.get(dateFilter)), paging);
                results = result.getContent();
            }
        }
        return results;
    }

    public Date getProperDate(String date) throws ParseException {
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        return inputFormat.parse(date);
    }
}
